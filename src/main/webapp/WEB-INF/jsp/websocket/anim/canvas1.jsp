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
button:hover {
	cursor: pointer;
}

canvas {
	border: 3px solid green;
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

td {
	/* 	border: solid 1px blue; */
	word-wrap: break-all;
}

.table-fixed {
	table-layout: fixed;
}
 .control-btn{
 	width: 400px;
 	font-size: 2em;
 	border-radius: 5px;
 	height:100px;
 }
.control-btn:hover { 
	background-color: gray;
	color: white;
}
/*
#touchpad-control {
	width: 500px;
	height: 500px;
	border: solid 1px orange;
	border-radius: 10px;
	background-color: yellow;
}
*/
#content{
	padding:10px;
}
#velocity-info{
	padding:20px; 
}
</style>
<link type="text/css" rel="stylesheet"
	href="<c:url value="/res/style/bootstrap.min.css"></c:url >" />
</head>
<body onload="disconnect()">
	<div id="content">
		<h1 align="center">Racing Game <small>online</small>
		</h1>
		<p id="info" align="center"></p> 

		<table class="table-fixed" >
			<tr>
				<td colspan="3"><span class="badge badge-secondary">Health</span>
					<div id="life-bar-wrapper" class="progress">
						<div id="life-bar" class="progress-bar progress-bar-striped"></div>
					</div></td>
				<td></td>
			</tr> 
			<tr valign="top">
				<td colspan="3" style="width: ${winW}px">
					<div
						style="background-image:url('<c:url value="/res/img/layout1.png" />'); background-repeat: no-repeat">
						<canvas id="tutorial" width="${winW}" height="${winH}"> </canvas>
					</div>
				</td>

				<td style="width: 300px">
					<p id="circuit-info"></p>
					<p id="entity-info"></p>
					<p id="realtime-info"></p>
					<p id="msg-info"></p>
				</td>
			</tr>
		</table>
		<p></p>
		<!-- CONTROLS -->
		<table id="controls-pad" class="table-fixed" valign="top">
			<tr valign="top">
				<td><div><button class="btn btn-secondary btn-sm" onclick="closeControlPad()">Close Control Pad</button></div></td>
				<td><button class="control-btn" move-role="w" id="btn-up">UP -w</button>
				</td>
				<td></td> 
			</tr>
			<tr valign="top">
				<td><button class="control-btn" move-role="a" id="btn-left">LEFT -a</button>
				</td>
				<td><button class="control-btn" style="background-color: red"
						onclick="releaseAll()" id="btn-stop">STOP</button></td>
				<td><button class="control-btn" move-role="d" id="btn-right">RIGHT -d</button>
				</td>
			</tr>
			<tr valign="top">
				<td style="text-align: center"><p id="player-name"></p></td>
				<td><button class="control-btn" move-role="s" id="btn-down">DOWN -s</button>
				</td>
				<td style="text-align: center"><p id="player-position"></p></td>
			</tr> 
		</table> 
		<div style="display: none" id="show-control-btn"><button class="btn btn-primary btn-sm" onclick="showControlPad()">Show Control Pad</button></div>
		 
		<!-- INPUTS -->
		<p></p>
		<table  class="table-borderless" style="table-layout: fixed; width: 1000px; " valign="top">
			<tr valign="top">
				<td>Select Server</td>
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
					<button class="btn btn-danger" id="leave" onclick="leave()">Leave</button>
				</td>
			</tr>
			<tr>
				<td colspan="2"><span class="badge badge-info" id="ws-info"></span></td>
			</tr>
			<tr>
				<td colspan="3">
					<h4>Press 'O' to fire!</h4>
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
				sucessJoin()
			}, function() {
				errorJoin()
			});

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
				game.stompClient.disconnect();
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
	</script>
</body>
</html>

