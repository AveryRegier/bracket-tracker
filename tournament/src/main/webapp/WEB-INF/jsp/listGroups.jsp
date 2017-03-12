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

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
<html>
<head>
<title>Site Wide Group List</title>
<link rel="stylesheet" type="text/css" href="/basic.css">
</head>
<body>
<jsp:include page="header.jsp"/>
<!--Round Table-->
<table border="0" cellpadding="0" cellspacing="0" width="60%" align="center">
	<tr valign="top" align="right" class="round-title">
		<td width="0%" align="left"><img src="/images/boxtopleft.gif" alt="" width="8" height="25" border="0"></td>
		<td width="100%" background="/images/boxtop.gif">
			<table border="0" cellpadding="2" cellspacing="0" width="100%" align="center">
				<tr>
					<td align="left" valign="top" class="head"><b>Site Wide Group List</b></td>
				</tr>
			</table>
		</td>
		<td width="0%" align="right"><img src="/images/boxtopright.gif" alt="" width="8" height="25" border="0"></td>
	</tr>
	<tr valign="top" align="left">
		<td width="0%" background="/images/boxleft.gif"><img src="/images/boxleft.gif" alt="" width="8" height="8" border="0"></td>
		<td width="100%">
			<img src="/images/spacer.gif" width="143" height="5" border="0" alt=""><br>
			<!--Content Table-->
			
			<table class="border-table" cellpadding="0" cellspacing="0" border="0">
				<tr>
					<td>
						<table class="content-table" cellpadding="3" cellspacing="1" border="0">
							<tr class="header-row">
								<td>Administrator</td>
								<td>Group Name</td>
								<td>Invitation</td>
								<td># of Pools</td>
								<td># of Users</td>
								<td>Actions</td>
							</tr>
							<c:forEach var="Group" items="${Groups}">
							<tr class="content-row">
								<td><c:out value="${Group.admin.name}"/></td>
								<td><a href="<c:out value="${config.MyTournamentURL}"/>?request=show&type=group&id=<c:out value="${Group.oid}"/>"><c:out value="${Group.name}"/></a></td>
								<td><c:out value="${Group.invitationCode}"/></td>
								<td><c:out value="${Group.numPools}"/></td>
								<td><c:out value="${Group.numMembers}"/></td>
								<td><c:if test="${Group.deletable}">[<a onclick="return confirm('Continue to delete group \'<c:out value="${Group.name}"/>\'?');" href="<c:out value="${config.MyTournamentURL}"/>?request=delete&type=group&id=<c:out value="${Group.oid}"/>">Delete</a>]</c:if></td>
							</tr> 
							</c:forEach>
						</table>
					</td>
				</tr>
			</table>
			<!--Content Table-->
		</td>
		<td width="0%" background="/images/boxright.gif"><img src="/images/boxright.gif" alt="" width="8" height="8" border="0"></td>
	</tr>
	<tr valign="top" align="left">
		<td width="0%" align="left" valign="top"><img src="/images/boxbottomleft.gif" alt="" width="8" height="8" border="0"></td>
		<td width="100%" background="/images/boxbottom.gif"><img src="/images/boxbottom.gif" alt="" width="9" height="8" border="0"></td>
		<td width="0%" align="right" valign="top"><img src="/images/boxbottomright.gif" alt="" width="8" height="8" border="0"></td>
	</tr>
</table>
<!--Round Table-->
</body>
</html>
