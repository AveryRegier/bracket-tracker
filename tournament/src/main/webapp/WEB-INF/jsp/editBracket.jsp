<%--
Copyright (C) 2003-2011 Avery J. Regier.

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
<script>
var alertEnabled = false;
function Game(oid, selectElement, opponentOrder){
	this.feeders = new Array();
	this.next = null;
	this.id = oid;
	this.opponent = opponentOrder;
	this.selectBox = selectElement;

	function func_addFeeder(feeder) {
		this.feeders[feeder.opponent] = feeder;
		feeder.setNext(this);
		this.pull(feeder, feeder);
	}
	this.addFeeder = func_addFeeder;
	
	function func_push() {
		if(this.next) {
			var current = this.feeders[this.selectBox.selectedIndex];
			if(current == null) current = this;
			this.next.pull(current, this);
		}
	}
	this.push = func_push;
	
	function func_pull(theSeed, theGame) {
		var isSelected = (theGame.opponent == this.selectBox.selectedIndex);
		this.selectBox.options[theGame.opponent] = new Option(theSeed.getTeam(), theGame.opponent);
		if(isSelected) {
			this.selectBox.selectedIndex = theGame.opponent;
			this.push();
		}
	}
	this.pull = func_pull;
	
	function func_getTeam() {
		return this.selectBox.selectedIndex == 0 ? "Unknown" : this.selectBox[this.selectBox.selectedIndex].text;
	}
	this.getTeam = func_getTeam;
	
	function func_setNext(theGame) {
		this.next = theGame;
	}
	this.setNext = func_setNext;
}

function Seed(opponentOrder, name) {
	this.opponent = opponentOrder;
	this.team = name;
	
	function func_setNext(theGame) {
		theGame.pull(this, this);
	}
	this.setNext = func_setNext;

	function func_getTeam() {
		return name;
	}
	this.getTeam = func_getTeam;
}
var seedNext = new Array();
var gameNext = new Array();
var seeds = new Array();
var games = new Array();
</script>
</head>
<body>
<jsp:include page="header.jsp"/>
<c:if test="${!empty Pools}">
<form name="addToPool" method="POST" action="<c:out value="${config.AssignBracketToPoolPostURL}"/>">
<select name="pool">
<c:forEach var="pool" items="${Pools}">
	<option value="<c:out value="${pool.oid}"/>"><c:out value="${pool.group.name}"/>: <c:out value="${pool.name}"/></option>
</c:forEach>
</select>
<input type="hidden" name="bracket" value="<c:out value="${BracketBean.oid}"/>">
<input type="submit" name="add" value="Add Saved Bracket To Pool">
</form>
</c:if>
<form name="theForm" method="POST">
<div class="head"><c:out value="${BracketBean.bracketType}"/> Name:</div> <input type=text name="name" size="100" maxlength="70" value="<c:out value="${BracketBean.name}"/>"><input type="submit" name="save" value="Save" class="common">
<c:if test="${BracketBean.bracketType == 'Tournament'}">
<%@ include file="editDate.jsp" %>
</c:if>

<%@ include file="bracket-start.jsp" %>
<c:forEach var="game" items="${BracketBean.bracket}">
<%@ include file="game-start.jsp" %>
<!--
<c:out value="${game.level}"/> 
<c:out value="${game.opponent.name}"/>
-->
<c:choose><c:when test="${game.seedNode}">
<script>
	seeds[<c:out value="${game.seed.ID}"/>] = new Seed(<c:out value="${game.opponent.sequence}"/>, "<c:out value="${game.seed.seedNo}"/> <c:out value="${game.team.name}" escapeXml="false"/>");
	if(seedNext[<c:out value="${game.seed.ID}"/>]) {
		seedNext[<c:out value="${game.seed.ID}"/>].addFeeder(seeds[<c:out value="${game.seed.ID}"/>]);
	}
</script>
<div class="content">
<c:out value="${game.seed.seedNo}"/> <c:out value="${game.team.name}" escapeXml="false"/>
</div>
</c:when><c:otherwise>
<select  style = "font-size : xx-small;" name="game<c:out value="${game.gameNodeOid}"/>" align="absmiddle" onChange="games[<c:out value="${game.gameNodeOid}"/>].push();">
	<option value="-1">&nbsp;</option>
</select>
<script>
	//alert("<c:out value="${game.gameNodeOid}"/>"+document.theForm.game<c:out value="${game.gameNodeOid}"/>);
	games[<c:out value="${game.gameNodeOid}"/>] = 
		new Game(<c:out value="${game.gameNodeOid}"/>, 
				 document.theForm.game<c:out value="${game.gameNodeOid}"/><c:if test="${not empty game.opponent}">,
				 <c:out value="${game.opponent.sequence}"/></c:if>);
<c:forEach var="feeder" items="${game.feeders}">
	if(<c:choose><c:when test="${feeder.seedNode}">seeds</c:when><c:otherwise>games</c:otherwise></c:choose>[<c:out value="${feeder.ID}"/>]) {
		games[<c:out value="${game.gameNodeOid}"/>].addFeeder(<c:choose><c:when test="${feeder.seedNode}">seeds</c:when><c:otherwise>games</c:otherwise></c:choose>[<c:out value="${feeder.ID}"/>]);
	} else {
		<c:choose><c:when test="${feeder.seedNode}">seed</c:when><c:otherwise>game</c:otherwise></c:choose>Next[<c:out value="${feeder.ID}"/>] = games[<c:out value="${game.gameNodeOid}"/>];
	}
	if(gameNext[<c:out value="${game.gameNodeOid}"/>]) {
		gameNext[<c:out value="${game.gameNodeOid}"/>].addFeeder(games[<c:out value="${game.gameNodeOid}"/>]);
	}
</c:forEach></script>
</c:otherwise>
</c:choose>
<%@ include file="game-end.jsp" %>
</c:forEach>
<%@ include file="bracket-end.jsp" %>
</form>
<script>
<c:forEach var="game" items="${BracketBean.bracket}">
<c:if test="${not empty game.pick}">
games[<c:out value="${game.gameNodeOid}"/>].selectBox.selectedIndex = <c:out value="${game.pick.sequence}"/>;
games[<c:out value="${game.gameNodeOid}"/>].push();
</c:if>
</c:forEach>
alertEnabled=true;
</script>
</body>
</html>
