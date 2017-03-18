<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:if test="${!empty Player.games}">
    <div class="box">
        <h2>Games</h2>
        <div class="inner-content">
            <p/>
            <table class="border-table" cellpadding="0" cellspacing="0" border="0">
            <tr><td>
            <c:forEach var="game" items="${Player.games}">
                <table class="content-table" cellpadding="5" cellspacing="1" border="0">
                    <tr class="header-row">
                        <td colspan='2'><c:out value="${game.status}"/></td>
                    </tr>
                    <c:forEach var="score" items="${game.scores}">
                    <tr class="content-row">
                        <td><c:out value="${score.seed}"/> <b><c:out value="${score.team.name}"/></b></td>
                        <td><b><c:out value="${score.score}"/></b></td>
                    </tr>
                    </c:forEach>
                </table>
            </c:forEach>
            </td></tr>
            </table>
        </div>
    </div>
    <BR clear="all"/>
    <BR/>
</c:if>
