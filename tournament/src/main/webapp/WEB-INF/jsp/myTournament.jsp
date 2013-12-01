<%--
Copyright (C) 2003-2011 Avery J. Regier.

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
<head><title><c:out value="${Player.name}"/>'s Tournament</title></head>
<link rel="stylesheet" type="text/css" href="/tournament/basic.css">

<body>
<jsp:include page="header.jsp"></jsp:include>
<table border="0" width="100%">
<tr><td valign="top" width="20%" align="left">
<table border="0" cellpadding="2" cellspacing="2" align="left">
	
	<tr><td class="<c:choose><c:when test="${(empty param.tournament and empty param.archives)}">head</c:when><c:otherwise>content</c:otherwise></c:choose>"><a href="<c:out value="${config.MyTournamentURL}"/>?current=true">Current Tournaments</a></td></tr>
	<tr><td class="<c:choose><c:when test="${(empty param.tournament and !empty param.archives)}">head</c:when><c:otherwise>content</c:otherwise></c:choose>"><a href="<c:out value="${config.MyTournamentURL}"/>?archives=true">Archived Tournaments</a></td></tr>
	<tr><td class="content">&nbsp;</td></tr>
	<c:forEach var="tournament" items="${Tournaments}">
		<c:if test="${(!empty param.archives) == (tournament.archived)}"><tr><td class="<c:choose><c:when test="${param.tournament == tournament.id}">head</c:when><c:otherwise>content</c:otherwise></c:choose>">
		<a href="<c:out value="${config.MyTournamentURL}"/>?tournament=<c:out value="${tournament.id}"/><c:if test="${tournament.archived}">&archives=true</c:if>">
			<c:out value="${tournament.name}"/></a></td></tr></c:if>
	</c:forEach>
</table>
</td><td width="60%" valign="top">
<!--Round Table-->
<table border="0" cellpadding="0" cellspacing="0" width="100%" align="left">
	<tr valign="top" align="right" class="round-title">
		<td width="0%" align="left"><img src="/tournament/images/boxtopleft.gif" alt="" width="8" height="25" border="0"></td>
		<td width="100%" background="/tournament/images/boxtop.gif" class="round-title">
			<table border="0" cellpadding="2" cellspacing="0" width="100%" align="center">
				<tr><td align="left" valign="top"><b>Brackets</b></td></tr>
			</table>
		</td>
		<td width="0%" align="right"><img src="/tournament/images/boxtopright.gif" alt="" width="8" height="25" border="0"></td>
	</tr>
	<tr valign="top" align="left">
		<td width="0%" background="/tournament/images/boxleft.gif"><img src="/tournament/images/boxleft.gif" alt="" width="8" height="8" border="0"></td>
		<td width="100%">
			<img src="/tournament/images/spacer.gif" width="143" height="5" border="0" alt=""><br>
			<!--Content Table-->
			<table border="0">
				<tr class="content-row">
					<td>
						Currently own <c:out value="${Player.numBrackets}"/> 
						bracket<c:if test="${group.numBrackets != 1}">s</c:if>.
					</td>
				</tr>
				<tr><td>&nbsp;</td></tr>
				<tr>
					<td>
						<table class="border-table" cellpadding="0" cellspacing="0" border="0">
						<tr>
						<td>
						<table class="content-table" cellpadding="3" cellspacing="1" border="0">
							<tr class="header-row">
								<td>Bracket</td>
								<td>Pools</td>
							</tr>
							<c:forEach var="bracket" items="${Player.brackets}">
							<tr class="content-row">
								<td><a href="<c:out value="${app.singleton.config.BracketMaintenanceURL}"/>?request=edit&id=<c:out value="${bracket.oid}"/>"><c:out value="${bracket.name}"/></a>
									<c:if test="${bracket.deletable}"><a onclick="return confirm('Continue to delete bracket \'<c:out value="${bracket.name}"/>\'?');" href="<c:out value="${config.BracketMaintenanceURL}"/>?request=delete&id=<c:out value="${bracket.oid}"/>" class="X">X</a></c:if>
								</td>
								<td>
									<c:forEach var="pool" items="${bracket.pools}">
										<a href="?request=show&type=group&id=<c:out value="${pool.group.oid}"/>"><c:out value="${pool.group.name}"/></a> - <a href="?request=show&type=pool&id=<c:out value="${pool.oid}"/>"><c:out value="${pool.name}"/></a>
										<c:if test="${pool.removable}"><a onclick="return confirm('Continue to remove <c:out value="${bracket.name}"/> from \'<c:out value="${pool.name}"/>\'?');" href="<c:out value="${config.PoolMaintenanceURL}"/>?bracket=<c:out value="${bracket.oid}"/>&request=remove&pool=<c:out value="${pool.oid}"/>" class="X">X</a></c:if>
										<br>
									</c:forEach>
								</td>
							</tr> 
							</c:forEach>
						</table>
						</td>
						</tr>
						</table>
					</td>
				</tr>
				<tr><td>&nbsp;</td></tr>	
				<c:forEach var="tournament" items="${Tournaments}">
				<c:if test="${(empty param.tournament and (!empty param.archives) == (tournament.archived)) or param.tournament == tournament.id}">
				<tr class="content-row">
					<td><a href="<c:out value="${config.TournamentURL}"/>?tournament=<c:out value="${tournament.id}"/>">
						<c:out value="${tournament.name}"/></a><c:if test="${tournament.deletable}">
						<a onclick="return confirm('Continue to delete <c:out value="${tournament.name}"/>?');" href="<c:out value="${config.TournamentURL}"/>?tournament=<c:out value="${tournament.id}"/>&request=remove" class="X">X</a></c:if><c:if test="${tournament.readyForBrackets}">
						[<a href="?request=create&type=bracket&tournament=<c:out value="${tournament.id}"/>">Create a new Bracket</a>] 
						</c:if><c:if test="${tournament.currentUserIsAdmin}">
						[<a href="<c:out value="${config.TournamentURL}"/>?tournament=<c:out value="${tournament.id}"/>&edit=true">Edit</a>]
						[<a href="<c:out value="${config.AddAdminsToTournamentURL}"/><c:out value="${tournament.id}"/>">Admin</a>]
						</c:if>
						<c:if test="${not empty tournament.lastUpdated}"><fmt:formatDate type="both" dateStyle="short" timeStyle="short" value="${tournament.lastUpdated}" /></c:if>
					</td>
				</tr>
				</c:if>
				</c:forEach>
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
<BR clear="all"/>
<BR/>
<!--Round Table-->
<table border="0" cellpadding="0" cellspacing="0" width="100%" align="left">
	<tr valign="top" align="right" class="round-title">
		<td width="0%" align="left"><img src="/tournament/images/boxtopleft.gif" alt="" width="8" height="25" border="0"></td>
		<td width="100%" background="/tournament/images/boxtop.gif">
			<table border="0" cellpadding="2" cellspacing="0" width="100%" align="center">
				<tr><td align="left" valign="top"><b>Groups</b></td></tr>
			</table>
		</td>
		<td width="0%" align="right"><img src="/tournament/images/boxtopright.gif" alt="" width="8" height="25" border="0"></td>
	</tr>
	<tr valign="top" align="left">
		<td width="0%" background="/tournament/images/boxleft.gif"><img src="/tournament/images/boxleft.gif" alt="" width="8" height="8" border="0"></td>
		<td width="100%">
			<img src="/tournament/images/spacer.gif" width="143" height="5" border="0" alt=""><br>
			<!--Content Table-->
			<table border="0">
				<tr class="content-row">
					<td>Currently attached to <c:out value="${Player.numGroups}"/> 
					group<c:if test="${Player.numGroups != 1}">s</c:if>.</td>
				</tr>
				<c:if test="${Player.numGroups > 0}">
				<tr><td>&nbsp;</td></tr>
				<tr>
					<td>
						<table class="border-table" cellpadding="0" cellspacing="0" border="0">
						<tr>
						<td>
						<table class="content-table" cellpadding="5" cellspacing="1" border="0">
							<tr class="header-row">
								<td>Group</td>
								<td>Pools</td>
							</tr>
							<c:forEach var="group" items="${Player.groups}">
							<tr class="content-row">
								<td valign="top">
									<a href="?request=show&type=group&id=<c:out value="${group.oid}"/>"><c:out value="${group.name}"/></a>
									<c:if test="${group.deletable}">
										<a onclick="return confirm('Continue to delete group \'<c:out value="${group.name}"/>\'?');" href="<c:out value="${config.MyTournamentURL}"/>?request=delete&type=group&id=<c:out value="${group.oid}"/>" class="X">X</a> 
									</c:if>
								</td>
								<td>
									<c:forEach var="pool" items="${group.pools}">
										<a href="?request=show&type=pool&id=<c:out value="${pool.oid}"/>&groupID=<c:out value="${group.oid}"/>"><c:out value="${pool.name}"/></a><br>
									</c:forEach>
									<c:if test="${group.currentUserAdmin}">
										<a href="?request=create&type=pool&group=<c:out value="${group.oid}"/>">Create A New Pool</a>
									</c:if>
								</td>
							</tr>
							</c:forEach>
						</table>
						</td>
						</tr>
						</table>
					</td>
				</tr>
				</c:if>
				<tr><td>&nbsp;</td></tr>
				<tr class="content-row">
					<td>
						<form action="<c:out value="${config.MyTournamentURL}"/>" method="GET">
						<a href="?request=create&type=group">Create A New Group</a> &nbsp;
							Join by Invitation: <input type="text" name="invitationCode"/>
							<input type="submit" value="Submit"/>
						</form>
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
</td><td width="20%">&nbsp;</td></tr>
</table>
</body>
</html>
