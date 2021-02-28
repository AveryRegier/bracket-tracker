## Tournament Pool and Bracket Tracker
### Getting Started
The development and testing of the system has only been done in

* Tomcat 4.1.24 and Tomcat 5.5.x
* MySQL Ver 12.21 Distrib 4.0.15, for Win95/Win98 (i32)
* Eclipse 3.1
* Sysdeo Tomcat plugin v3.0

There are several prerequisites (programs you must have installed) to set the tournament bracket tracker up.

* MySQL
* Sun JDK 1.5. You can get this from java.sun.com. If you use Tomcat 5.5, you can get away with just the JRE, which you can get easily from java.com. If you are working off of Linux, be aware that gcj doesn't work. Some Linux distributions already have gcj installed and use it to run the already installed version of tomcat. This doesn't work yet, and I don't have a Linux box at the moment to figure out why. Installing the Sun JDK worked for another user in this situation.
* Tomcat or another J2EE container. I suggest Tomcat 5.5.

Once you have tomcat installed, you must make it available from your web server. How this is done is different for every web server. If you have Apache, then you need to use the mod-jk2 connector.

Put the `tournament.war` file in the tomcat `webapps` folder.

To get the database ready, run the following commands from the mysql.exe tool in the following order:
```shell
\. [path]\config\db.sql
\. [path]\config\ncaa.sql
\. [path]\config\ncaa-teams.sql
\. [path]\config\ncaa-leagues.sql
\. [path]\config\ncaa-2004.sql (optional)
\. [path]\config\11team.sql (optional - for ACC and Big 10 tournaments)
\. [path]\config\12team.sql (optional - for Big 12, Big East, and SEC tournaments)
\. [path]\config\acc-league.sql (optional)
\. [path]\config\acc-2005.sql (optional)
\. [path]\config\ncaa-2005.sql (optional)
\. [path]\config\nit.sql (optional - for National Invitation Tournament)
\. [path]\config\worldcup.sql (optional - for World Cup Stage 2)
```

To upgrade from a different version, run as appropriate
```shell
\. [path]\config\upgrade2to3.sql (if starting at 0.2.x)
\. [path]\config\upgrade3to4.sql (if starting at 0.3.x)
\. [path]\config\upgrade4to5.sql
\. [path]\config\ncaa-leagues.sql (add league data for simpler admin)
\. [path]\config\acc-league.sql (optional - add league data for simpler admin)
\. [path]\config\11team.sql (optional - for ACC and Big 10 tournaments)
\. [path]\config\12team.sql (optional - for Big 12, Big East, and SEC tournaments)
\. [path]\config\nit.sql (optional - for National Invitation Tournament)
\. [path]\config\worldcup.sql (optional - for World Cup Stage 2)
```

You will also want to override the `smtpServerHost` and `adminEmail` properties in your `jdbc.properties` file so that password change notices are sent properly.

You need to grant privileges in mysql as well:
```sql
GRANT ALL PRIViLEGES ON *.* TO 'testuser'@'localhost'
IDENTIFIED BY 'testpw' WITH GRANT OPTION;
```
You may add a file named `jdbc.properties` and override any properties you want (including userids and passwords). This file will never be included in a distribution and is safe from being overridden. Put it next to `tournament.properties` in `WEB-INF/classes/config/`. You will also want to override the smtpServerHost and adminEmail properties so that password change notices are sent properly.

Make some tweaks to the `[tomcat home]/conf/web.xml` file. Uncomment the following:
```xml
<!-- The invoker servlet -->
<servlet>
    <servlet-name>invoker</servlet-name>
    <servlet-class>
        org.apache.catalina.servlets.InvokerServlet
    </servlet-class>
    <init-param>
        <param-name>debug</param-name>
        <param-value>0</param-value>
    </init-param>
    <load-on-startup>2</load-on-startup>
</servlet>
```
and
```xml
<!-- The mapping for the invoker servlet -->
<servlet-mapping>
    <servlet-name>invoker</servlet-name>
    <url-pattern>/servlet/*</url-pattern>
</servlet-mapping>
```
This enables the autoinvoker servlet, which maps from URLs to class names which happen to be J2EE servlet classes. Some people consider this a security risk, which is why it is disabled by default. I've never had an issue so long as I know all of the servlet classes I have in the system. This can be changed. If you consider it an issue, let me know via a feature request.

Start MySQL and Tomcat

Go to http://localhost:8080/tournament/