<%--
Copyright (C) 2003-2013 Avery J. Regier.

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
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 --%>

<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head><title>Login to Tournament and Bracket Tracker</title></head>
<body onLoad="theForm.uid.focus()">
<form method="post" name="theForm">
<table border=0 align=center>
<c:if test="${loginFailed}"><tr><td colspan=2 align=center>Login Failed</td></tr></c:if>
<c:if test="${passwordReset}"><tr><td colspan=2 align=center>Reset instructions sent via email.</td></tr></c:if>
<c:if test="${resetFailed}"><tr><td colspan=2 align=center>Password reset failed.</td></tr></c:if>
<tr><th>User ID</th><td><input type="text" name="uid" maxlength="7" value="<c:out value="${uid}"/>"></td></tr>
<tr><th>Password</th><td><input type="password" maxlength="10" name="password"></td></tr>
<tr><td colspan=2 align=right><input type="submit" name="login" value="Login">
<c:if test="${emailAvailable}">
<input type="submit" name="resetPassword" value="Reset Password">
</c:if>
</td></tr>
</table>
<p align="center"><strong><a href="<c:out value="${app.singleton.config.RegistrationURL}"/>?redirect=<%=java.net.URLEncoder.encode(request.getParameter("redirect")) %>">Register</a></strong></p>
</form>
<BR>
<blockquote><pre>
Tournament Pool and Bracket Tracker version 0.7.0-SNAPSHOT, Copyright (C) 2003-2014 Avery J. Regier
Tournament Pool and Bracket Tracker comes with ABSOLUTELY NO WARRANTY.
This is free software, and you are welcome to redistribute it under certain conditions.
For details see the <a href="/tournament/LICENSE">license</a>.

<a href="http://sourceforge.net/projects/bracket-tracker/">Source</a>
</pre></blockquote>
</body>
</html>