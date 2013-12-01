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
<title>Assign Brackets to Pool <c:out value="${Pool.name}"/></title>
<link rel="stylesheet" type="text/css" href="/tournament/basic.css">
</head>
<body onLoad="theForm.bracket.focus()">
<jsp:include page="header.jsp"/>
<!--Round Table-->
<table border="0" cellpadding="0" cellspacing="0" width="60%" align="center">
	<tr class="round-title" valign="top" align="right">
		<td width="0%" align="left"><img src="/tournament/images/boxtopleft.gif" alt="" width="8" height="25" border="0"></td>
		<td width="100%" background="/tournament/images/boxtop.gif">
			<table border="0" cellpadding="2" cellspacing="0" width="100%" align="center">
				<tr>
					<td align="left" valign="top"><b>Assign Bracket To <c:out value="${Pool.name}"/> Pool<br><!-- title --></b></td>
				</tr>
			</table>
		</td>
		<td width="0%" align="right"><img src="/tournament/images/boxtopright.gif" alt="" width="8" height="25" border="0"></td>
	</tr>
	<tr valign="top" align="left">
		<td width="0%" background="/tournament/images/boxleft.gif"><img src="/tournament/images/boxleft.gif" alt="" width="8" height="8" border="0"></td>
		<td width="100%">
			<img src="/tournament/images/spacer.gif" width="143" height="5" border="0" alt=""><br>
			<!--Content Table-->
			<form method="POST" name="theForm">
			<input type=hidden name="pool" value="<c:out value="${Pool.oid}"/>">
			<table cellpadding="3" cellspacing="0" border="0">
				<tr>
					<td class="head" valign="top">Brackets:</td>
					<td>
						<select name="bracket">
							<c:forEach var="item" items="${brackets.items}">
								<option value="<c:out value="${item.value}"/>"><c:out value="${item.name}"/>
							</c:forEach>
						</select>
					</td>
				</tr>
				<c:if test="${Pool.tieBreakerType.oid != 0}">
				<tr>
					<td class="head">Tie Breaker:</td>
					<td class="content"><c:out value="${Pool.tieBreakerType.name}"/></td>
				</tr>
				<tr>
					<td class="head">Question:</td>
					<td class="content"><c:out value="${Pool.tieBreakerQuestion}"/></td>
				</tr>
				<tr>
					<td class="head">Answer:</td>
					<td class="content"><input type="text" name="tieBreakerAnswer"></td>
				</tr>
				</c:if>
				<tr><td>&nbsp;</td></tr>
				<tr>
					<td><input class="common" type="submit" value="Assign"></td>
				</tr>
			</table>
			</form>
			<!--Content Table-->
		</td>
		<td width="0%" background="/tournament/images/boxright.gif"><img src="/tournament/images/boxright.gif" alt="" width="8" height="8" border="0"></td>
	</tr>
	<tr valign="top" align="left">
		<td width="0%" align="left" valign="top"><img src="/tournament/images/boxbottomleft.gif" alt="" width="8" height="8" border="0"></td>
		<td width="100%" background="/tournament/images/boxbottom.gif"><img src="/tournament/images/boxbottom.gif" alt="" width="9" height="8" border="0"></td>
		<td width="0%" align="right" valign="top"><img src="/tournament/images/boxbottomright.gif" alt="" width="8" height="8" border="0"></td>
	</tr>
</table>
<!--Round Table-->
</body>
</html>
