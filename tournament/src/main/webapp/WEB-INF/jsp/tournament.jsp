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
<head><title><c:out value="${BracketBean.name}"/></title>
<link rel="stylesheet" type="text/css" href="/basic.css">
</head>
<body>
<jsp:include page="header.jsp"/>
<%@ include file="bracket-start.jsp" %>
<c:forEach var="game" items="${BracketBean.bracket}">
<%@ include file="game-start.jsp" %>
<!--
<c:out value="${game.level}"/> 
<c:out value="${game.opponent.name}"/>
-->
<c:choose>
<c:when test="${!empty game.team.name}">
<c:if test="${game.upset}"><strong></c:if>
<c:out value="${game.seed.seedNo}"/> 
<c:out value="${game.team.name}"/> 
<c:if test="${game.upset}"></strong></c:if>
<c:if test="${!empty game.teamScore}">(<c:out value="${game.teamScore}"/>)</c:if>
</c:when>
<c:otherwise><!--<c:out value="${game.gameNodeOid}"/>--><c:choose><c:when test="${!empty game.status}"> <c:out value="${game.status}"/></c:when>
<c:otherwise> ___________</c:otherwise></c:choose></c:otherwise>
</c:choose>
<%@ include file="game-end.jsp" %>
</c:forEach>
<%@ include file="bracket-end.jsp" %>
</body>
</html>
