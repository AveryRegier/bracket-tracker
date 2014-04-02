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
<title><c:out value="${Pool.name}"/> Pool</title>
<link rel="stylesheet" type="text/css" href="/tournament/basic.css">
</head>
<body>
<jsp:include page="header.jsp"/>
<!--Round Table-->

<div class="main">
    <div class="box">
        <h2><c:out value="${Pool.name}"/> Pool Detail</h2>
        <div class="inner-content">

			<!--Content Table-->
			<table>
				<tr>
					<td class="head">Administrator:&nbsp;</td>
					<td class="content"><c:out value="${Pool.owner.name}"/></td>
				</tr>
				<tr>
					<td class="head">Score System:&nbsp;</td>
					<td class="content"><c:out value="${Pool.scoreSystem.name}"/></td>
				</tr>
				<tr>
					<td class="head">Tie Breaker:</td>
					<td class="content"><c:out value="${Pool.tieBreakerType.name}"/></td>
				</tr>
				<c:if test="${Pool.tieBreakerType.oid != 0}">
				<tr>
					<td class="head">Tie Breaker Question:</td>
					<td class="content"><c:out value="${Pool.tieBreakerQuestion}"/></td>
				</tr>
				</c:if>
				<c:if test="${Pool.tieBreakerType.oid != 0 && !empty Pool.tieBreakerAnswer}">
				<tr>
					<td class="head">Tie Breaker Answer:</td>
					<td class="content"><c:out value="${Pool.tieBreakerAnswer}"/></td>
				</tr>
				</c:if>
				<tr>
					<td class="head">Group:&nbsp;</td>
					<td class="content"><a href="<c:out value="${config.ShowGroupURL}"/><c:out value="${Pool.group.oid}"/>"><c:out value="${Pool.group.name}"/></a></td>
				</tr>
				<tr>
					<td class="head">Tournament:&nbsp;</td>
					<td class="content"><a href="<c:out value="${config.TournamentURL}"/>?tournament=<c:out value="${Pool.tournament.id}"/>"><c:out value="${Pool.tournament.name}"/></a>
						<c:if test="${Pool.tournament.currentUserIsAdmin}">
						[<a href="<c:out value="${config.TournamentURL}"/>?tournament=<c:out value="${Pool.tournament.id}"/>&edit=true">Edit</a>]
						</c:if>
						starts <c:out value="${Pool.tournament.startTime}"/>
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
			</table>		
			<br>
			<br>
			<table class="border-table" cellpadding="0" cellspacing="0" align="center">
			<tr>
			<td>
			<table class="content-table" cellpadding="3" cellspacing="1">
				<tr class="header-row">
					<th>Rank</th>
					<th>Players</th>
					<th>Pools</th>
					<th>Current</th>
					<th>Remaining</th>
					<th>Max</th>
					<th>Max Rank</th>
					<c:if test="${Pool.tieBreakerType.oid != 0}">
					<th>Tie Breaker</th>
					</c:if>
					<th>Comment</th>
				</tr>
				<c:forEach var="bracket" items="${Pool.brackets}">
				<tr class="content-row">
					<td><c:out value="${bracket.rank}"/></td>
					<td><c:out value="${bracket.userName}"/></td>
					<td><c:if test="${Pool.showBracketsEarly}">
					<a href="<c:out value="${app.singleton.config.BracketMaintenanceURL}"/>?request=show&id=<c:out value="${bracket.oid}"/>&pool=<c:out value="${Pool.oid}"/>">
					</c:if>
					<c:if test="${bracket.out}"><strike></c:if>
					<c:if test="${Pool.showGroups}">
						<c:out value="${bracket.group.name}"/>
					</c:if>
					<c:if test="${not Pool.showGroups}"><c:out value="${bracket.name}"/></c:if>
					<c:if test="${bracket.out}"></strike></c:if>
					<c:if test="${Pool.showBracketsEarly}"></a></c:if>
					<c:if test="${bracket.removable}"><a onclick="return confirm('Continue to remove <c:out value="${bracket.name}"/> from \'<c:out value="${Pool.name}"/>\'?');" href="<c:out value="${config.PoolMaintenanceURL}"/>?bracket=<c:out value="${bracket.oid}"/>&request=remove&pool=<c:out value="${Pool.oid}"/>" class="X">X</a></c:if>
					</td>
					<td><c:out value="${bracket.score.current}"/></td>
					<td><c:out value="${bracket.score.remaining}"/></td>
					<td><c:out value="${bracket.score.max}"/></td>
					<td><c:out value="${bracket.maxRank}"/></td>
					<c:if test="${Pool.tieBreakerType.oid != 0}">
					<td><c:out value="${bracket.tieBreakerAnswer}"/></td>
					</c:if>
					<td><c:out value="${bracket.comment}"/></td>
				</tr> 
				</c:forEach>
			</table>
			</td>
			</tr>
			</table>

			<c:if test="${Pool.showGroups}">
			<br>
			<br>
			<table class="border-table" cellpadding="0" cellspacing="0" align="center">
			<tr>
			<td>
			<table class="content-table" cellpadding="3" cellspacing="1">
				<tr class="header-row">
					<th>Team</th>
					<th>Current</th>
				</tr>
				<c:forEach var="team" items="${Pool.teamScores}">
				<tr class="content-row">
					<td><a href="?request=show&type=pool&id=<c:out value="${Pool.oid}"/>&groupID=<c:out value="${team.key.oid}"/>"><c:out value="${team.key.name}"/></a></td>
					<td><c:out value="${team.value}"/></td>
				</tr>
				</c:forEach>
			</table>
			</td>
			</tr>
			</table>
			</c:if>

            <c:if test="${!Pool.closed}">
			<br>
			<table>
				<tr class="content-row">
					<td><a href="<c:out value="${config.AssignBracketToPoolURL}"/><c:out value="${Pool.oid}"/>">Assign a bracket to this pool</a></td>
				</tr>
			</table>
			</c:if>
			<c:if test="${Pool.tieBreakerNeeded}">
			<br>
			<table>
				<tr class="content-row">
					<td><a href="<c:out value="${config.PoolMaintenanceURL}"/>?pool=<c:out value="${Pool.oid}"/>">Set tiebreaker</a></td>
				</tr>
			</table>
			</c:if>
			<c:if test="${Pool.editable}">
			<br>
			<table>
				<tr class="content-row">
					<td><a href="<c:out value="${config.PoolMaintenanceURL}"/>?pool=<c:out value="${Pool.oid}"/>">Edit this pool</a></td>
				</tr>
			</table>
			</c:if>
			<c:if test="${Pool.tournament.currentUserIsAdmin}">
			<br>
			<table>
				<tr class="content-row">
					<td><a href="<c:out value="${Pool.mailTo}"/>">Email Members</a></td>
				</tr>
			</table>
			</c:if>
			<br>
			<table>
				<tr class="content-row">
					<td><a href="?<c:out value="${pageContext.request.queryString}"/>&output=csv">Export to CSV</a></td>
				</tr>
			</table>
			<!--Content Table-->
        </div>
     </div>
</div>

</body>
</html>
