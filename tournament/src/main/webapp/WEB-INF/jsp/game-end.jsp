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

</td><%--
<c:if test="${game.level gt 1 && game.level != BracketBean.maxLevels}">
<td rowspan=2 colspan="<c:out value="${BracketBean.maxLevels - game.level}"/>">&nbsp;</td>
</c:if>--%>
<c:if test="${game.level != (BracketBean.maxLevels) and (game.opponent.sequence == 1)}">
<c:choose>
<c:when test="${game.level == 5}">
	<c:set var="rows" value="34"/>
	<c:set var="pixels" value="280"/>
</c:when>
<c:when test="${game.level == 4}">
	<c:set var="rows" value="18"/>
	<c:set var="pixels" value="135"/>
</c:when>
<c:when test="${game.level == 3}">
	<c:set var="rows" value="10"/>
	<c:set var="pixels" value="65"/>
</c:when>
<c:when test="${game.level == 2}">
	<c:set var="rows" value="6"/>
	<c:set var="pixels" value="28"/>
</c:when>
<c:when test="${game.level == 1}">
	<c:set var="rows" value="4"/>
	<c:set var="pixels" value="9"/>
</c:when>
<c:when test="${game.level == 0}">
	<c:set var="rows" value="2"/>
	<c:set var="pixels" value="5"/>
</c:when>
</c:choose>
<td rowspan="<c:out value="${rows}"/>" valign="middle">
<table border=0 cellspacing=0 cellpadding=0>
<tr><td><img src="/images/bracket-top.gif"/></td></tr>
<tr><td><img src="/images/bracket-bar.gif" height="<c:out value="${pixels}"/>" width="14"/></td></tr>
<tr><td><img src="/images/bracket-middle.gif"/></td></tr>
<tr><td><img src="/images/bracket-bar.gif" height="<c:out value="${pixels}"/>" width="14"/></td></tr>
<tr><td><img src="/images/bracket-bottom.gif"/></td></tr>
</table>
</td>
</c:if>
<c:if test="${game.level != 0}">
</tr>
</c:if>