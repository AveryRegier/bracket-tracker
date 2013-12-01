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
<head><title><c:out value="${BracketBean.name}"/></title>
<link rel="stylesheet" type="text/css" href="/tournament/basic.css">
</head>
<body>
<jsp:include page="header.jsp"/>
<form name="theForm" method="POST">
<div class="head">Tournament Name:</div> <input type=text name="name" size="100" maxlength="70" value="<c:out value="${BracketBean.name}"/>"><input type="submit" name="save" value="Save" class="common">
<a href="<c:out value="${config.AddTeamsToLeagueURL}"/>?tournament=<c:out value="${BracketBean.oid}"/>">Import Existing Teams</a>
<a href="<c:out value="${config.CreateTeamURL}"/>?tournament=<c:out value="${BracketBean.oid}"/>">Create New Team</a>

<%@ include file="bracket-start.jsp" %>
<c:forEach var="game" items="${BracketBean.bracket}">
<%@ include file="game-start.jsp" %>
<!--
<c:out value="${game.level}"/> 
<c:out value="${game.opponent.name}"/>
-->
<c:choose>
<c:when test="${game.seedNode}">
	<c:out value="${game.seed.seedNo}"/> 
	<select name="team<c:out value="${game.seed.ID}"/> ">
		<option value="">Unknown
		<c:forEach var="item" items="${teams.items}">
			<option value="<c:out value="${item.value}"/>"
				<c:if test="${game.team.ID == item.value}">selected</c:if>
			><c:out value="${item.name}"/>
		</c:forEach>
	</select>
</c:when>
<c:when test="${!empty game.team.name}">
<c:if test="${game.upset}"><strong></c:if>
<c:out value="${game.seed.seedNo}"/> 
<c:out value="${game.team.name}"/> 
<c:if test="${game.upset}"></strong></c:if>
</c:when>
<c:otherwise><c:out value="${game.gameNodeOid}"/>___________</c:otherwise>
</c:choose>
<%@ include file="game-end.jsp" %>
</c:forEach>
<%@ include file="bracket-end.jsp" %>
</form>
</body>
</html>
