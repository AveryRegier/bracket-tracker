<%--
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
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 --%>

<%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head><title>Reset Password</title>
<script>
function validate(theForm) {
	if(theForm.password.value.length > 10) {
		alert("Password is too long.  Maximum length of 10 characters.");
		return false;
	}
	if(theForm.password.value != theForm.password2.value) {
		alert("Confirmation failed.");
		return false;
	}
	return true;
}
</script>
</head>
<body onLoad="theForm.uid.focus()">
<form name="theForm" method="post" onsubmit="return validate(this)">
<table border=0 align=center>
<tr><td colspan=2 align=center>Please reset your password.</td></tr>
<tr><th>User ID</th><td><input type="hidden" name="uid" value="<c:out value="${uid}"/>"><c:out value="${uid}"/></td></tr>
<tr><th>Password</th><td><input type="password" name="password" maxlength="10"></td></tr>
<tr><th>Confirm Password</th><td><input type="password" name="password2" maxlength="10"></td></tr>
<tr><th>Name</th><td><c:out value="${name}"/></td></tr>
<tr><th>Email Address</th><td><c:out value="${email}"/></td></tr>
<tr><td colspan=2 align=right><input type="submit" name="resetPassword" value="Reset"></td></tr>
</table>
<input type="hidden" name="auth" value="<c:out value="${auth}"/>">
</form>
</body>
</html>