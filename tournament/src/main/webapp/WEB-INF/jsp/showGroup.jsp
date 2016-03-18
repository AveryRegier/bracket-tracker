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
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
	<title><c:out value="${Group.name}"/> Group Detail</title>
	<style type="text/css">
		.smallBreak {
			margin: 0 0 0em; padding: 0
		}
	</style>
	<link rel="stylesheet" type="text/css" href="/tournament/basic.css">
	<style type="text/css">
		<%--Initial definitions for base column. 
		   Define the (minimum) width here, and optionally a padding --%>
		.columnized div {
			float: left;
			width: 11em;		<%-- When using a fixed number of columns, you can omit the width. Otherwise it must be set! This will be the *minimum* width of a column --%>
			text-align: left;
			margin: 0;	 		<%-- Don't use a margin! --%>
		}
	</style>
	<script type="text/javascript" src="/tournament/multicolumn.js"></script>
	<script type="text/javascript">	
		//Minimalistic settings. You can tweak the settings by re-assigning the defaults in MultiColumnSettings.
		multiColumnSettings=new MultiColumnSettings;
		multiColumnSettings.classNameScreen='columnized';					
	//	multiColumnSettings.numberOfColumns=3;		
		window.onload = function () {
			new MultiColumn(document.getElementById("container"),multiColumnSettings);
		}
	</script>
</head>
<body>
<jsp:include page="header.jsp"/>

<div class="sidebar">
	<p class="<c:choose><c:when test="${(empty param.tournament and empty param.archives)}">head</c:when><c:otherwise>content</c:otherwise></c:choose>"><a href="<c:out value="${config.MyTournamentURL}"/>?type=group&id=<c:out value="${Group.oid}"/>&current=true">Current Tournaments</a></p>
	<p class="<c:choose><c:when test="${(empty param.tournament and !empty param.archives)}">head</c:when><c:otherwise>content</c:otherwise></c:choose>"><a href="<c:out value="${config.MyTournamentURL}"/>?type=group&id=<c:out value="${Group.oid}"/>&archives=true">Archived Tournaments</a></p>
	<div class="content">&nbsp;</div>
	<c:forEach var="tournament" items="${Tournaments}">
		<c:if test="${(!empty param.archives) == (tournament.archived)}"><p class="<c:choose><c:when test="${param.tournament == tournament.id}">head</c:when><c:otherwise>content</c:otherwise></c:choose>">
		<a href="<c:out value="${config.MyTournamentURL}"/>?type=group&id=<c:out value="${Group.oid}"/>&tournament=<c:out value="${tournament.id}"/><c:if test="${tournament.archived}">&archives=true</c:if>">
			<c:out value="${tournament.name}"/></a></p></c:if>
	</c:forEach>
</div>

<div class="main">
    <div class="box">
        <h2><c:out value="${Group.name}"/> Group Detail</h2>
        <div class="inner-content">
				<!--Content Table-->
				<table>
					<tr>
						<td class="head">Administrator&nbsp;</td>
						<td class="content"><c:out value="${Group.admin.name}"/></td>
					</tr>
					<c:if test="${Group.currentUserAdmin}">
					<c:choose>
					<c:when test="${Group.invitationCode != null}">
					<tr>
						<td class="head">Invitation URL&nbsp;</td>
						<td class="content"><a href="<c:out value="${config.MyTournamentURL}"/>?invitationCode=<c:out value="${Group.invitationCode}"/>"><c:out value="${Group.invitationCode}"/></a></td>
						<td class="content"><form method="POST" action="<c:out value="${config.MyTournamentURL}"/>"><input type="hidden" name="groupID" value="<c:out value="${Group.oid}"/>"><input type="submit" name="disableInvitation" value="Disable Invitation" ></form></td>
					</tr>
					</c:when><c:otherwise>
					<tr>
						<td class="head">Invitations&nbsp;</td>
						<td class="content"><form method="POST" action="<c:out value="${config.MyTournamentURL}"/>"><input type="hidden" name="groupID" value="<c:out value="${Group.oid}"/>"><input type="submit" name="enableInvitation" value="Enable Invitation" ></form></td>
					</tr>
					</c:otherwise>
					</c:choose>
					</c:if>
                    <c:if test="${! empty Group.parent}">
                    <tr>
                        <td class="head">Parent Group:</td>
                        <td class="content"><a href="?request=show&type=group&id=<c:out value="${Group.parent.oid}"/>"><c:out value="${Group.parent.name}"/></a></td>
                    </tr>
                    </c:if>
				</table>
				<!--Content Table-->

        </div>
     </div>


	<BR/>
	<!--Round Table - Pools -->

    <div class="box">
        <h2>Pools</h2>
        <div class="inner-content">
				<!--Content Table-->
				<table cellpadding="3" cellspacing="0" border="0">
					<c:forEach var="pool" items="${Group.pools}">
					<tr class="content-row">
						<td><a href="?request=show&type=pool&id=<c:out value="${pool.oid}"/>&groupID=<c:out value="${Group.oid}"/>"><c:out value="${pool.name}"/></a>
						<c:if test="${pool.deletable}"><a onclick="return confirm('Continue to delete pool \'<c:out value="${pool.name}"/>\'?');" href="<c:out value="${config.PoolMaintenanceURL}"/>?request=delete&pool=<c:out value="${pool.oid}"/>" class="X">X</a></c:if>
						</td>
					</tr> 
					</c:forEach>
					<c:if test="${Group.currentUserAdmin}">
						<tr><td>&nbsp;</td></tr>
						<tr class="content-row"><td class="content"><a href="?request=create&type=pool&group=<c:out value="${Group.oid}"/>">Create A New Pool</a></td></tr>
					</c:if>
				</table>
	
				<!--Content Table-->

        </div>
     </div>
	<!--Round Table - Pools-->
	<BR/>
	<!--Round Table - Members-->
    <div class="box">
        <h2>Members</h2>
        <div class="inner-content">
				<!--Content -->
				<div id="container">
					<div class="content">
						<c:forEach var="player" items="${Group.members}">
						<P class="smallBreak"><c:out value="${player.name}"/><c:if test="${player.deletable}"
						>&nbsp;<a onclick="return confirm('Continue to remove <c:out value="${player.name}"/> from \'<c:out value="${Group.name}"/>\'?');" href="<c:out value="${config.PlayerMaintenanceURL}"/>?group=<c:out value="${Group.oid}"/>&request=remove&playerID=<c:out value="${player.oid}"/>" class="X">X</a> 
						</c:if></P>
						</c:forEach>
					</div>
				</div>
				<c:if test="${Group.currentUserAdmin}">
					<P class="content">
					<a href="<c:out value="${config.AddPlayersToGroupURL}"/><c:out value="${Group.oid}"/>">Add A New Member</a>
					</P>
				</c:if>
				<!--Content -->
        </div>
     </div>
	<!--Round Table - Members-->

	<c:if test="${Group.currentUserAdmin || not empty Group.subGroups}">
	<BR/>
	<!--Round Table - SubGroups-->
    <div class="box">
        <h2>Sub Groups</h2>
        <div class="inner-content">
				<!--Content -->
				<div id="container">
					<div class="content">
						<c:forEach var="subGroup" items="${Group.subGroups}">
						<P class="smallBreak"><a href="?request=show&type=group&id=<c:out value="${subGroup.oid}"/>"><c:out value="${subGroup.name}"/></a></P>
						</c:forEach>
						
						<c:if test="${Group.mayAddSubGroup}">
							<P><a href="?request=create&type=group&parentID=<c:out value="${Group.oid}"/>">Create A New Sub Group</a></P>
						</c:if>		
					</div>
				</div>
				<!--Content -->
        </div>
     </div>
	</c:if>
</div>
</body>
</html>
