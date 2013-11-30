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
<title>Close Pool <c:out value="${Pool.name}"/> in <c:out value="${Group.name}"/> Group</title>
<link rel="stylesheet" type="text/css" href="/tournament/basic.css">
</head>
<body onLoad="theForm.name.focus()">
<jsp:include page="header.jsp"/>
<!--Round Table-->
<table border="0" cellpadding="0" cellspacing="0" width="60%" align="center">
	<tr class="round-title" valign="top" align="right">
		<td width="0%" align="left"><img src="/tournament/images/boxtopleft.gif" alt="" width="8" height="25" border="0"></td>
		<td width="100%" background="/tournament/images/boxtop.gif">
			<table border="0" cellpadding="2" cellspacing="0" width="100%" align="center">
				<tr>
					<td align="left" valign="top"><b>Close Pool <c:out value="${Pool.name}"/> in <c:out value="${Group.name}"/> Group<br><!-- title --></b></td>
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
			<form method="POST" name="theForm">
			<table cellspacing="3" cellpadding="0" border="0">
				<tr>
					<td class="head">Name:</td>
					<td class="content"><c:out value="${Pool.name}"/></td>
				</tr>
				<tr>
					<td class="head">Tournament:&nbsp;</td>
					<td class="content"><a href="<c:out value="${config.TournamentURL}"/>?tournament=<c:out value="${Pool.tournament.id}"/>"><c:out value="${Pool.tournament.name}"/></a>
						<c:if test="${Pool.tournament.currentUserIsAdmin}">
						[<a href="<c:out value="${config.TournamentURL}"/>?tournament=<c:out value="${Pool.tournament.id}"/>&edit=true">Edit</a>]
						</c:if>
					</td>
				</tr>
				<tr>
					<td class="head">Scoring System:</td>
					<td class="content"><c:out value="${Pool.scoreSystem.name}"/></td>
				</tr>
				<tr>
					<td class="head">Tie Breaker:</td>
					<td class="content"><c:out value="${Pool.tieBreakerType.name}"/></td>
				</tr>
				<tr>
					<td class="head">Tie Breaker Question:</td>
					<td class="content"><c:out value="${Pool.tieBreakerQuestion}"/></td>
				</tr>
				<tr>
					<td class="head">Tie Breaker Answer:</td>
					<td class="content">
						<input type="text" name="tieBreakerAnswer" value="<c:out value="${Pool.tieBreakerAnswer}"/>">
					</td>
				</tr>
				<tr>
					<td class="head">Bracket Limit:&nbsp;</td>
					<td class="content">
						<c:if test="${Pool.bracketLimit != 0}">
							<c:out value="${Pool.bracketLimit}"/> per Player
						</c:if>
						<c:if test="${Pool.bracketLimit == 0}">None</c:if>
					</td>
				</tr>
				<tr>
					<td class="head"></td>
					<td class="content"><input type="checkbox" name="showBracketsEarly" value="true" disabled <c:if test="${Pool.showBracketsEarly}">checked</c:if>>Show brackets before tournament starts</td>
				</tr>
				<tr><td>&nbsp;</td></tr>
				<tr>
					<td colspan="2" align="center"><input class="common" type="submit" name="closePool" value="Close Pool"></td>
				</tr>
			</table>
			</form>
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
