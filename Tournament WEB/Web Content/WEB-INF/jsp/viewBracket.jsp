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
<title><c:out value="${BracketBean.name}"/></title>
<link rel="stylesheet" type="text/css" href="/tournament/basic.css">
</head>
<body>
<jsp:include page="header.jsp"/>
<c:if test="${!empty Pools}">
<form name="addToPool" method="POST" action="<c:out value="${config.AssignBracketToPoolPostURL}"/>">
<select name="pool">
<c:forEach var="pool" items="${Pools}">
	<option value="<c:out value="${pool.oid}"/>"><c:out value="${pool.name}"/>
</c:forEach>
</select>
<input type="hidden" name="bracket" value="<c:out value="${BracketBean.oid}"/>">
<input type=submit name="add" value="Add Saved Bracket To Pool">
</form>
</c:if>
<form name="theForm">
Score: <input type="text" name="score" value="0" onFocus="blur()"> 
Possible:<input type="text" name="possible" value="0" onFocus="blur()">
<script>
document.theForm.score.value = 0;
document.theForm.possible.value = 0;
</script>
</form>
<%@ include file="bracket-start.jsp" %>
<c:forEach var="game" items="${BracketBean.bracket}">
<%@ include file="game-start.jsp" %>
<!--
<c:out value="${game.level}"/> 
<c:out value="${game.opponent.name}"/>
-->
<c:choose>
<c:when test="${!empty game.team.name}">
<script>
document.theForm.score.value = parseInt(document.theForm.score.value) + <c:out value="${game.score}"/>;
document.theForm.possible.value = parseInt(document.theForm.possible.value) + <c:out value="${game.possible}"/>;
</script>
<c:if test="${game.decided}">
	<font color="<c:if test="${game.correct}">green</c:if><c:if test="${!game.correct}">red</c:if>">
</c:if>
<font size="xx-small"> 
<c:if test="${game.upset}"><strong></c:if>
<c:out value="${game.seed.seedNo}"/> <c:out value="${game.team.name}"/> 
<c:if test="${game.upset}"></strong></c:if>
</font>
<c:if test="${game.decided}">
	</font>
</c:if>
</c:when>
<c:otherwise><c:out value="${game.gameNodeOid}"/>___________</c:otherwise>
</c:choose>

<%@ include file="game-end.jsp" %>
</c:forEach>
<%@ include file="bracket-end.jsp" %>
</body>
</html>
