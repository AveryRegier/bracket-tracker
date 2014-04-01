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
 --%><%@ taglib uri="/WEB-INF/c.tld" prefix="c" %>Rank,Players,Pools,Current,Remaining,Max,Max Rank<c:if test="${Pool.tieBreakerType.oid != 0}">,Tie Breaker</c:if>
<c:forEach var="bracket" items="${Pool.brackets}"><c:out value="${bracket.rank}"/>,<c:out value="${bracket.userName}"/>,<c:if test="${Pool.showGroups}"><c:out value="${bracket.group.name}"/></c:if><c:if test="${not Pool.showGroups}"><c:out value="${bracket.name}"/></c:if>,<c:out value="${bracket.score.current}"/>,<c:out value="${bracket.score.remaining}"/>,<c:out value="${bracket.score.max}"/>,<c:out value="${bracket.maxRank}"/><c:if test="${Pool.tieBreakerType.oid != 0}">,<c:out value="${bracket.tieBreakerAnswer}"/></c:if>
</c:forEach>
