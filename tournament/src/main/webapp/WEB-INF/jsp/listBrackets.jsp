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
<head>
<title><c:out value="${Player.name}"/>'s Bracket List</title>
<link rel="stylesheet" type="text/css" href="/tournament/basic.css">
</head>
<body>
<jsp:include page="header.jsp"/>
<!--Round Table-->
<table border="0" cellpadding="0" cellspacing="0" width="60%" align="center">
	<tr valign="top" align="right" class="round-title">
		<td width="0%" align="left"><img src="/tournament/images/boxtopleft.gif" alt="" width="8" height="25" border="0"></td>
		<td width="100%" background="/tournament/images/boxtop.gif">
			<table border="0" cellpadding="2" cellspacing="0" width="100%" align="center">
				<tr>
					<td align="left" valign="top" class="head"><b><c:out value="${Player.name}"/>'s Bracket List</b></td>
				</tr>
			</table>
		</td>
		<td width="0%" align="right"><img src="/tournament/images/boxtopright.gif" alt="" width="8" height="25" border="0"></td>
	</tr>
	<tr valign="top" align="left">
		<td width="0%" background="/tournament/images/boxleft.gif"><img src="/tournament/images/boxleft.gif" alt="" width="8" height="8" border="0"></td>
		<td width="100%">
			<img src="/tournament/images/spacer.gif" width="143" height="5" border="0" alt=""><br>
			<!--Content Table-->
			
			<table class="border-table" cellpadding="0" cellspacing="0" border="0">
				<tr>
					<td>
						<table class="content-table" cellpadding="3" cellspacing="1" border="0">
							<tr class="header-row">
								<td>ID</td>
								<td>Bracket Name</td>
								<td>Tournament Name</td>
								<td># of Pools</td>
								<td>Actions</td>
							</tr>
							<c:forEach var="Bracket" items="${Player.brackets}">
							<tr class="content-row">
								<td><c:out value="${Bracket.oid}"/></td>
								<td><c:out value="${Bracket.name}"/></td>
								<td><a href="<c:out value="${config.TournamentURL}"/>?tournament=<c:out value="${Bracket.tournament.id}"/>"><c:out value="${Bracket.tournament.name}"/></a></td>
								<td><c:out value="${Bracket.numPools}"/></td>
								<td><c:if test="${Bracket.deletable}">[<a onclick="return confirm('Are you sure you want to delete \'<c:out value="${Bracket.name}"/>\'?');" href="<c:out value="${config.BracketMaintenanceURL}"/>?request=delete&id=<c:out value="${Bracket.oid}"/>">Delete</a>]</c:if></td>
							</tr> 
							</c:forEach>
						</table>
					</td>
				</tr>
			</table>
			<!--Content Table-->
		</td>
		<td width="0%" background="/tournament/images/boxright.gif"><img src="/tournament/images/boxright.gif" alt="" width="8" height="8" border="0"></td>
	</tr>
	<tr valign="top" align="left">
		<td width="0%" align="left" valign="top"><img src="/tournament/images/boxbottomleft.gif" alt="" width="8" height="8" border="0"></td>
		<td width="100%" background="/tournament/images/boxbottom.gif"><img src="/tournament/images/boxbottom.gif" alt="" width="9" height="8" border="0"></td>
		<td width="0%" align="right" valign="top"><img src="/tournament/images/boxbottomright.gif" alt="" width="8" height="8" border="0"></td>
	</tr>
</table>
<!--Round Table-->
</body>
</html>
