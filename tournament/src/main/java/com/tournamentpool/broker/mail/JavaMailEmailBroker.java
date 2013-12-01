/* 
Copyright (C) 2003-2008 Avery J. Regier.

This file is part of the Tournament Pool and Bracket Tracker.

Tournament Pool and Bracket Tracker is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

Tournament Pool and Bracket Tracker is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>. */

package com.tournamentpool.broker.mail;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.Set;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.tournamentpool.application.SingletonProvider;
import com.tournamentpool.domain.User;

/**
 * @author Avery Regier
 */
public class JavaMailEmailBroker {

	private static ThreadLocal<String[]> tl = new ThreadLocal<String[]>();

	public static void setBaseURL(String scheme, String host, int port) {
		tl.set(new String[] { scheme, host, Integer.toString(port) });
	}

	private final SingletonProvider sp;

	public JavaMailEmailBroker(SingletonProvider sp) {
		this.sp = sp;
	}

	public void sendPasswordChange(User user) throws UnsupportedEncodingException, MessagingException {
		String[] server = (String[]) tl.get();
		String scheme = server[0];
		String host = server[1];
		String port = server[2];
		final Properties config = sp.getSingleton().getConfig();

		Properties props = new Properties();
		// set some defaults to be backwards compatible with the old code
		props.put("mail.smtp.host", config.getProperty("smtpServerHost"));
		props.put("mail.smtp.port", "25");
		
		Set<String> stringPropertyNames = config.stringPropertyNames();
		for (String prop : stringPropertyNames) {
			if(prop != null && prop.startsWith("mail.smtp.")) {
				props.put(prop, config.getProperty(prop));
			}
		}
		
//		props.put("mail.smtp.auth", config.getProperty("mail.smtp.auth", "true"));
//		props.put("mail.smtp.starttls.enable", config.getProperty("mail.smtp.starttls.enable", "true"));
//		props.put("mail.smtp.host", 
//				config.getProperty("mail.smtp.host", 
//						config.getProperty("smtpServerHost", "smtp.gmail.com")));
//		props.put("mail.smtp.port", config.getProperty("mail.smtp.port", "587"));
//
//		if(false /* ssl */) {
//		props.put("mail.smtp.socketFactory.port", "465");
//		props.put("mail.smtp.socketFactory.class",
//				"javax.net.ssl.SSLSocketFactory");
//		props.put("mail.smtp.auth", "true");
//		props.put("mail.smtp.port", "465");
//		}

		
		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(
								config.getProperty("mail.userName", config.getProperty("adminEmail")), 
								config.getProperty("mail.password"));
					}
				});

		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(config.getProperty("adminEmail"),
				"Tournament and Bracket Tracker Administrator"));
		message.setRecipients(Message.RecipientType.TO,
				new InternetAddress[] { new InternetAddress(user.getEmail(),
						user.getName()) });
		message.setSubject(config.getProperty("passwordChangeSubject",
				"Tournament and Bracket Tracker Password Change"));
		message.setText(user.getName() + ",\n"
				+ "To change your password please click the following link: \n"
				+ scheme + "://" + host
				+ ("http".equals(scheme) && port.equals("80") ? "" : ":" + port)
				+ sp.getSingleton().getConfig().getProperty("ResetPasswordURL")
				+ "?uid=" + user.getID() + "&auth="
				+ URLEncoder.encode(user.getAuth(), "UTF-8") + "\n");

		Transport.send(message);

		System.out.println("Password reset sent to "+user.getEmail());

	}
}
