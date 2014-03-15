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
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<P class="content" align="right">
	<a href="<c:out value="${config.MyTournamentURL}"/>">My Tournament</a> 
	<c:if test="${Player.siteAdmin}"><a href="<c:out value="${config.AdminURL}"/>">Admin</a></c:if>
	<a href="<c:out value="${config.ProfileURL}"/>">My Profile</a>
	<a href="?logoff=true">Logoff</a><BR/>
	Last Updated: <fmt:formatDate type="both" timeStyle="short" value="${app.singleton.lastUpdated}" />   
</P>
