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
<head>
<title>Select Tournament Type for a new Tournament</title>
<link rel="stylesheet" type="text/css" href="/basic.css">
</head>
<body onLoad="theForm.year.value = new Date().getFullYear(); theForm.name.focus()">
<jsp:include page="header.jsp"/>
<!--Round Table-->
<table border="0" cellpadding="0" cellspacing="0" width="60%" align="center">
	<tr class="round-title" valign="top" align="right">
		<td width="0%" align="left"><img src="/images/boxtopleft.gif" alt="" width="8" height="25" border="0"></td>
		<td width="100%" background="/images/boxtop.gif">
			<table border="0" cellpadding="2" cellspacing="0" width="100%" align="center">
				<tr>
					<td align="left" valign="top"><b>Select Tournament Type and League for a new Tournament<br><!-- title --></b></td>
				</tr>
			</table>
		</td>
		<td width="0%" align="right"><img src="/images/boxtopright.gif" alt="" width="8" height="25" border="0"></td>
	</tr>
	<tr valign="top" align="left">
		<td width="0%" background="/images/boxleft.gif"><img src="/images/boxleft.gif" alt="" width="8" height="8" border="0"></td>
		<td width="100%">
			<img src="/images/spacer.gif" width="143" height="5" border="0" alt=""><br>
			<!--Content Table-->
			<form method="POST" name="theForm">
			<input type=hidden name="group" value="<c:out value="${group.id}"/>">
			<table cellspacing="3" cellpadding="0" border="0">
				<tr>
					<td class="head">Name:</td>
					<td class="content"><input type=text name="name"></td>
				</tr>
				<tr>
					<td class="head">Tournament Type:</td>
					<td class="content">
						<select name="tournamentType">
							<c:forEach var="item" items="${tournamentTypes.items}">
								<option value="<c:out value="${item.value}"/>"><c:out value="${item.name}"/>
							</c:forEach>
						</select>
					</td>
				</tr>
				<tr>
					<td class="head">League:</td>
					<td class="content">
						<select name="league">
							<c:forEach var="item" items="${leagues.items}">
								<option value="<c:out value="${item.value}"/>"><c:out value="${item.name}"/>
							</c:forEach>
						</select>
						<a href="<c:out value="${config.CreateLeagueURL}"/>">Create New League</a>
					</td>
				</tr>
				<tr>
					<td class="head">Start Date/Time:</td>
					<td class="content">
					<select name="month">
						<option value=""></option>
						<option value="1">Jan</option>
						<option value="2">Feb</option>
						<option value="3">Mar</option>
						<option value="4">Apr</option>
						<option value="5">May</option>
						<option value="6">Jun</option>
						<option value="7">Jul</option>
						<option value="8">Aug</option>
						<option value="9">Sep</option>
						<option value="10">Oct</option>
						<option value="11">Nov</option>
						<option value="12">Dec</option>
					</select>
					<input type="text" maxlength="2" size="2" name="day">
					<input type="text" maxlength="4" size="4" name="year"> at 
					<input type="text" maxlength="2" size="2" name="hour"> : 
					<input type="text" maxlength="2" size="2" name="minute">
					<select name="ampm">
						<option value=""></option>
						<option value="AM">AM</option>
						<option value="PM">PM</option>
					</select>
					<select name="timezone">
						<c:forEach var="item" items="${timezones}">
							<option value="<c:out value="${item}"/>"><c:out value="${item}"/>
						</c:forEach>
					</select>					
					</td>
				</tr>
				<tr><td>&nbsp;</td></tr>
				<tr>
					<td colspan="2" align="center"><input class="common" type="submit" value="Create Tournament"></td>
				</tr>
			</table>
			</form>
			<!--Content Table-->
		</td>
		<td width="0%" background="/images/boxright.gif"><img src="/images/boxright.gif" alt="" width="8" height="8" border="0"></td>
	</tr>
	<tr valign="top" align="left">
		<td width="0%" align="left" valign="top"><img src="/images/boxbottomleft.gif" alt="" width="8" height="8" border="0"></td>
		<td width="100%" background="/images/boxbottom.gif"><img src="/images/boxbottom.gif" alt="" width="9" height="8" border="0"></td>
		<td width="0%" align="right" valign="top"><img src="/images/boxbottomright.gif" alt="" width="8" height="8" border="0"></td>
	</tr>
</table>
<!--Round Table-->
</body>
</html>
