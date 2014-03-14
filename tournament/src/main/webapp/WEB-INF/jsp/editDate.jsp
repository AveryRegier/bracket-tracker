<br clear="all"/>
<div class="head">Start Date/Time:</div>
<div class="content">
    <select name="month">
        <option value=""></option>
        <option value="1" <c:if test="${1 == month}">selected</c:if>>Jan</option>
        <option value="2" <c:if test="${2 == month}">selected</c:if>>Feb</option>
        <option value="3" <c:if test="${3 == month}">selected</c:if>>Mar</option>
        <option value="4" <c:if test="${4 == month}">selected</c:if>>Apr</option>
        <option value="5" <c:if test="${5 == month}">selected</c:if>>May</option>
        <option value="6" <c:if test="${6 == month}">selected</c:if>>Jun</option>
        <option value="7" <c:if test="${7 == month}">selected</c:if>>Jul</option>
        <option value="8" <c:if test="${8 == month}">selected</c:if>>Aug</option>
        <option value="9" <c:if test="${9 == month}">selected</c:if>>Sep</option>
        <option value="10" <c:if test="${10 == month}">selected</c:if>>Oct</option>
        <option value="11" <c:if test="${11 == month}">selected</c:if>>Nov</option>
        <option value="12" <c:if test="${12 == month}">selected</c:if>>Dec</option>
    </select>
    <input type="text" maxlength="2" size="2" name="day" value="<c:out value="${day}"/>">
    <input type="text" maxlength="4" size="4" name="year" value="<c:out value="${year}"/>"> at
    <input type="text" maxlength="2" size="2" name="hour" value="<c:out value="${hour}"/>"> :
    <input type="text" maxlength="2" size="2" name="minute"  value="<c:out value="${minute}"/>">
    <select name="ampm">
        <option value=""></option>
        <option value="AM" <c:if test="${'AM' == ampm}">selected</c:if>>AM</option>
        <option value="PM" <c:if test="${'PM' == ampm}">selected</c:if>>PM</option>
    </select>
    <select name="timezone">
        <c:forEach var="item" items="${timezones}">
            <option value="<c:out value="${item}"/>" <c:if test="${timezone == item}">selected</c:if>><c:out value="${item}"/>
        </c:forEach>
    </select>

</div>
<br clear="all"/>