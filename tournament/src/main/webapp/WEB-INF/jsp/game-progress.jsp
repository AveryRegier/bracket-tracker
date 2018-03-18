<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:if test="${!empty Player.games}">
    <c:forEach var="game" items="${Player.games}">
    <table class="border-table" cellpadding="0" cellspacing="0" border="0" style="display: inline-block">
    <tr><td>
        <table class="content-table" cellpadding="5" cellspacing="1" border="0">
            <tr class="header-row">
                <td><c:out value="${game.status}"/></td>
                <td class="number-header">Score</td>
                <td class="number-header">Current</td>
                <td class="number-header">Remaining</td>
            </tr>
            <c:forEach var="score" items="${game.scores}">
            <tr class="content-row">
                <td style="color: <c:out value="${score.color}"/>"><c:out value="${score.seed}"/> <b><c:out value="${score.team.name}"/></b></td>
                <td><b><c:out value="${score.score}"/></b></td>
                <td><c:out value="${score.current}"/></td>
                <td><c:out value="${score.remaining}"/></td>
            </tr>
            </c:forEach>
        </table>
    </td></tr>
    </table>
    <BR clear="all"/>
    </c:forEach>
</c:if>
