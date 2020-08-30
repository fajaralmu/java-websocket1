<%@ page language="java" contentType="text/html; charset=windows-1256"
	pageEncoding="windows-1256"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="icon" href="<c:url value="/res/img/javaEE.ico" />"
	type="image/x-icon">
<title>Canvas Online Race</title>

<script src="<c:url value="/res/js/jquery-3.3.1.slim.min.js"></c:url >"></script>
<script src="<c:url value="/res/js/bootstrap.min.js"></c:url >"></script>
<script src="<c:url value="/res/js/sockjs-0.3.2.min.js"></c:url >"></script>
<script src="<c:url value="/res/js/stomp.js"></c:url >"></script>
<script src="<c:url value="/res/js/util.js"></c:url >"></script>
<script type="text/javascript">
	var game;
</script>
<script type="module">
	import {Game} from "<c:url value="/res/js/game.js"></c:url >";

	game = new Game();
</script>
<style type="text/css">
.header{
	color: gray;
	display: grid;
	grid-template-columns: 70% 30% ;
	background-image: url("<c:url value="/res/img/bg.png"></c:url >" );
	background-repeat: no-repeat;
	background-size: ${winW}px 300px; 
	width: ${winW}px;
	height: 300px;
	
}
 
#life-bar {  
	height: 20px;
	width: ${winW}px;
}
#life-bar-wrapper{ 
	padding:5px; 
	height: 30px; 
	width:${winW+10}px; 
}
</style>
<link type="text/css" rel="stylesheet"
	href="<c:url value="/res/style/bootstrap.min.css"></c:url >" />
<link type="text/css" rel="stylesheet"
	href="<c:url value="/res/style/style.css"></c:url >" />
</head>
<body onload="disconnect()">
	<div id="content">
	
		<!-- HEADER -->
		<jsp:include page="partials/header.jsp"></jsp:include>
		 
		<p id="info" align="center"></p> 

		<!-- CANVAS -->
		<jsp:include page="partials/canvas-layout.jsp"></jsp:include>
		<p></p>
		
		<!-- CONTROLS --> 
		<jsp:include page="partials/controls.jsp"></jsp:include> 
		 
		<!-- INPUTS -->
		<p></p>
		<table  class="table-borderless" style="table-layout: fixed; width: 1000px; " valign="top">
			<tr valign="top">
				<td>Select Room</td>
				<td><select class="form-control" id="server-list">
						<c:forEach var="serverName" items="${serverNames }">
							<option value="${serverName }">${serverName }</option>
						</c:forEach>
					</select>
				</td>
				<td style="text-align: center">Additional Info</td>
			</tr>
			<tr valign="top">
				<td>Input Name</td>
				<td><input class="form-control" id="name" type="text" required="required"/></td>
				<td rowspan="3"  ><div id="velocity-info"><h4>velX:<small>0</small></h4><h4>velY:<small>0</small></h4><p>StoppingMode: false<br>StoppingDirection: 0</p></div></td>
			</tr>
			<tr valign="top">
				<td colspan="2">
					<button class="btn btn-primary" id="join" onclick="joinGame()">Join</button>
					<button class="btn btn-primary" id="connect" onclick="connect()">Connect</button>
					<button class="btn btn-danger" id="btn-leave" >Leave</button>
					<button class="btn btn-warning" id="btn-reset-pos" >Reset Position</button>
				</td>
			</tr>
			<tr>
				<td colspan="2"><span class="badge badge-info" id="ws-info"></span></td>
			</tr>
			<tr>
				<td colspan="3">
					<h4>Press 'O' to fire!</h4>
					<h4>Press 'R' to reset position!</h4>
				</td>
			</tr>
		</table>
		<p>Connected: <span id="connect-info"></span></p>
		<!-- <h2>Touchpad-Control</h2><div id="touchpad-control"  </div> -->
	</div>

	<script type="text/javascript">
		var connectBtn = _byId('connect');
		var canvas = _byId('tutorial');
		var ctx = canvas.getContext('2d');
		var textInput = _byId("draw-text");
		var initBtn = _byId("animate");
		var joined = false;

		/**init game**/

		function initGame(){
			if(null == game){
				alert("Error initiating game, please reload");
				return;
			}
			
			game.canvas = this.canvas;
			game.ctx = this.ctx;
			game.layouts = ${layouts};
			/*declared in websocket-util.js*/ game.contextPath = "${contextPath}";
			game.playerImagePath = "<c:url value="/res/img/player/"/>";
			game. urlJoinPath = "<spring:url value="/game-app-simple/join" />";
			game.imgPath = "<c:url value="/res/img/"/>" ;
			game.WIN_W = "${winW}";
			game.WIN_H = "${winH}";
			game.rolePlayer = "${rolePlayer}";
			game.roleBonusLife =  "${roleBonusLife}";
			game.roleBonusArmor =  "${roleBonusArmor}" ;
			//CIRCUIT
			game.roleRight = "${roleRight}";
			game.roleLeft = "${roleLeft}";
			game.roleUp = "${roleUp}";
			game.roleDown = "${roleDown}";
			game.roleFinish = "${roleFinish}";	
			game.roleGeneralLayout = "${roleGeneralLayout}";
			
			game.roles = ${roles};
			game.staticImages = ${staticImages};
			game.baseHealth =  ${baseHealth} ;
			game.window = window;
			game.document = document;
			game.fullAddress = window.location.protocol + '//'  + window.location.hostname + (window.location.port ? ':' + window.location.port : '');
			game.updateConnectionInfo = updateConnectionInfo;
			console.log("INIT - GAME",game);
			
		}

		function joinGame() {
			initGame();
			var name = _byId("name").value;
			if(name == null){
				alert("Name must be provider");
				return;
			}
			var serverName = _byId("server-list").value;
			game.join(name, serverName, function() {
				sucessJoin();
			}, function() {
				errorJoin();
			});

		}
		
		function showGameCanvas(){
			_byId("game-layout").style.display = "block";
			_byId("show-game-wapper").style.display = "none";
		}

		function sucessJoin() {
			joined = true;
			setupControlBtn();
		}
		function errorJoin() {
			joined = false;
		}

		function printCircuitInfo(info) {
			_byId("circuit-info").innerHTML = info;
		}

		function updateConnectionInfo(connected) {
			showGameCanvas();
			_byId("connect-info").innerHTML = connected;
		}

		function printInfo(text) {
			_byId("realtime-info").innerHTML = text;
		}

		function printEntityInfo(entity, entities, playerPosition, game) {
			var positionHTML = "<h2>POSITION:" + (playerPosition + 1) + "/"
					+ entities.length + ", LAP:" + entity.lap + "</h2>";

			var velocityInfo = "<h4>velX:<small>" + game.velX + "</small></h4><h4>velY:<small>"
					+ game.velY + "</small></h4>" + "<p>StoppingMode: "
					+ game.stoppingMode + "<br/>StoppingDirection: "
					+ game.stoppingDir + "</p>";

			_byId("player-name").innerHTML = "<h2>Player: " + entity.name
					+ "</h2>";
			_byId("velocity-info").innerHTML = velocityInfo;
			_byId("player-position").innerHTML = positionHTML;

			_byId("entity-info").innerHTML = JSON.stringify(entity);
			_byId("entity-info").innerHTML += "<br> <b>STAGE</b>: "
					+ entity.stageId + "<br> <b>LAYOUT ID</b>: "
					+ entity.layoutId + "<br> " + positionHTML;
			+"<br> <b>LAP</b>: " + entity.lap;
		}

		function connect() {

			if (this.joined != true) {
				alert("Please Join The Game First!");
				return;
			}

			const socket = new SockJS(game.contextPath + '/game-app');
			const stompClient = Stomp.over(socket);
			game.doConnect(stompClient); 
		}

		function disconnect() {
			 
			if (game != null && game.stompClient != null) {
				 leave();
				 game.sendUpdate(game.entity);
			}
			/* setConnected(false);
			console.log("Disconnected"); */
		}

		function setConnected(connected) { 
			_byId('connect-info').innerHTML = connected;
		}

		function leave() {
			window.document.title = "0FF-PLAYER: " + game.entity.name;
			game.leaveApp(game.entity.id);
		}

		function updateEntityInfo() {
			const amount = game.entity.life / game.baseHealth * game.WIN_W;
			_byId("life-bar").style.width = amount + "px";
		}

		window.onkeydown = function(e) {
			game.move(e.key);
		}

		window.onkeyup = function(e) {
			game.release(e.key);
		}

		function setupControlBtn() {
			const controlButtons = document
					.getElementsByClassName("control-btn");
			for (let i = 0; i < controlButtons.length; i++) {
				const button = controlButtons[i];
				const moveRole = button.getAttribute("move-role");
				button.onmousedown = function() {
					game.move(moveRole);
				};
				button.onclick = function() {
					game.move(moveRole);
				};
				button.onmouseup = function() {
					game.release(moveRole);
				};
				button.onmouseout = function() {
					game.release(moveRole);
				};
			}
		}
		
		function showControlPad(){
			_byId("controls-pad").style.display = "block";
			_byId("show-control-btn").style.display = "none";
		}
		
		function closeControlPad(){
			_byId("controls-pad").style.display = "none"; 
		 	_byId("show-control-btn").style.display = "block";
		}
		
		function initEvents(){
			const icon = _byId("header-icon");
			
			icon.onmouseover = function(e){ 
				e.target.height = 300;

			}
			icon.onmouseout = function(e){ 
				e.target.height = 200;

			}
			
			_byId("btn-leave").onclick = function(e){
				if(!confirm("Do you want to leave?")){
					return;
				}
				disconnect();  
				window.location.reaload();
			}
			
			_byId("btn-reset-pos").onclick = function(e){
				if(game && confirm("Reset Position?")){
					game.resetPosition();
				}
			}
		}
		
		initEvents();
	</script>
</body>
</html>

