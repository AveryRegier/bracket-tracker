<%--
Copyright (C) 2003-2014 Avery J. Regier.

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
<head>
    <title>Update Profile</title>
    <script>
    function validate(theForm) {
    	if(!theForm.uid.value || !theForm.password.value || !theForm.name.value) {
    		alert("Profile is not complete");
    		return false;
    	}
    	if(!theForm.email.value) {
    		if(confirm(
    			"Email address is required if you want to \nuse the forgot password functionality.  \n"+
    			"Do you really want to continue without providing it?"))
    		{
    			return true;
    		}
    		return false;
    	}
    	return true;
    }
    </script>
</head>
<body onLoad="theForm.uid.focus()">
    <c:if test="${userIdTaken}"><tr><td colspan=2 align=center>New User ID is already taken. Choose another.</td></tr></c:if>
    <form name="theForm" method="post" onsubmit="return validate(this)">
        <table border=0 align=center>
        <tr><th>User ID</th><td><input type="text" name="uid" value="<c:out value="${uid}"/>" maxlength="7"></td></tr>
        <tr><th>Name</th><td><input type="text" name="name" maxlength="70" value="<c:out value="${name}"/>"></td></tr>
        <tr><th>Email Address</th><td><input type="text" name="email" maxlength="254" value="<c:out value="${email}"/>"></td><td>(In case you forget your password.)</td></tr>
        <tr><td colspan=2 align=right><input type="submit" name="update" value="Update"></td></tr>
        </table>
    </form>
</body>
</html>