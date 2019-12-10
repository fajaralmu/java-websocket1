<%@ page language="java" contentType="text/html; charset=windows-1256"
	pageEncoding="windows-1256"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script src="<c:url value="/res/js/sockjs-0.3.2.min.js"></c:url >"></script>
<script src="<c:url value="/res/js/stomp.js"></c:url >"></script>
<script src="<c:url value="/res/js/ajax.js"></c:url >"></script>
<script src="<c:url value="/res/js/util.js"></c:url >"></script>
<script src="<c:url value="/res/js/websocket-util.js"></c:url >"></script>

<script type="text/javascript">
/** GLOBAL VARIABLES **/
		var layouts = ${layouts};
		/*declared in websocket-util.js*/ contextPath = "${contextPath}";
		var currentLayoutId = 0;
		var playerImagePath = "<c:url value="/res/img/player/"/>";
		var urlJoinPath = "<spring:url value="/game-app-simple/join" />";
		var imgPath = "<c:url value="/res/img/"/>" ;
		var WIN_W = "${winW}";
		var WIN_H = "${winH}";
		var rolePlayer = ${rolePlayer};
		var roleBonusLife =  ${roleBonusLife};
		var roleBonusArmor =  ${roleBonusArmor} ;
		//CIRCUIT
		var roleRight = ${roleRight};
		var roleLeft = ${roleLeft};
		var roleUp = ${roleUp};
		var roleDown = ${roleDown};
		var roleFinish = ${roleFinish};			
		
		var roles = ${roles};
		var staticImages = ${staticImages};
		var baseHealth =  ${baseHealth} ;
		var dirUp = "u";
		var dirLeft = "l";
		var dirRight = "r";
		var dirDown = "d";
		
		var playerPosition = 0;
		var entityDirection = "r";		
		var isAnimate = false;
		var velX = 0, velY = 0;
		var x = 10, y = 10;
		var entities = new Array();
		var entity = {};
		
		var firing = false;
		var entityDirectionHistory = new Array();
		
		var fireTiming = 0;
		var entityImages = new Array();
		var allMissiles = new Array();
		var run = 0;
		var runIncrement = 0.3;
		var stoppingDir = "0";
		var stoppingSide = "0";
		var stoppingMode = false;  
</script>
<script src="<c:url value="/res/js/player.js"></c:url >"></script>

<title>Canvas Animation</title>
<style type="text/css">
button:hover{ cursor: pointer; }

canvas { border: 1px solid black; }

.btn-ok {
	background-color: green;
	font-size: 2em;
	color: white
}

.btn-danger {
	background-color: red;
	font-size: 2em;
	color: white
}

.life-bar {
	border: solid 1px black;
	background-color: rgb(100, 200, 0);
	height: 20px;
	width: ${winW}px;

}

td{
	border: solid 1px blue;
	word-wrap:break-all;
}

#layout-table{
	 
	table-layout: fixed;
}

.control-btn{
	width:100%;
	padding: 10px;
	color: yellow;
	background-color: maroon;
	font-family: arial;
	font-size: 2em; 
	margin: auto;
	text-align: center;
	border-radius: 5px;
}

.control-btn:hover{
	font-size: 2.5em;
	background-color: gray;
	 
}

#touchpad-control{
	width: 500px;
	height:500px;
	border:solid 1px orange;
	border-radius: 10px;
	background-color: yellow;
}
 
</style>
</head>
<body onload="disconnect()">
<div id="content" >
	<p id="info" align="center"></p>
	<h3 id="ws-info"></h3>

	<table id="layout-table">
		<tr>
			<td colspan="3"><h3 style="display: none">Health</h3>
				<div  style="display:none; width:${winW}px; padding:5px; border:solid 1px blue;">
					<div id="life-bar" class="life-bar"></div>
				</div>
				</td>
			 <td></td>
		</tr>

		<tr valign="top">
			<td colspan="3" style="width: ${winW}px">
			<div style="background-image:url('<c:url value="/res/img/layout1.png" />'); background-repeat: no-repeat" >
			<canvas id="tutorial" width="${winW}" height="${winH}">
				</canvas></div>
				</td>
				
			<td   style="width:300px">
				<p id="circuit-info" ></p>
				<p id="entity-info"></p>
				<p id="realtime-info"></p>
				<p id="msg-info"></p>
			</td>
		</tr>
		<tr>
			<td><p id="player-name"></p></td>
			<td><button class="control-btn" move-role="w" id="btn-up">UP</button> </td>
			<td><p id="player-position"></p></td>
			
		</tr>
		<tr>
			<td><button class="control-btn"  move-role="a" id="btn-left">LEFT</button> </td>
			<td><button class="control-btn" style="background-color: red" onclick="releaseAll()" id="btn-stop">STOP</button> </td>
			<td><button class="control-btn"  move-role="d" id="btn-right">RIGHT</button> </td>
		</tr>
		<tr>
			<td></td>
			<td><button class="control-btn"  move-role="s" id="btn-down">DOWN</button> </td>
			<td></td>
		</tr>

	</table>
	<p></p>
	<p id="velocity-info"></p>
	<label>Input Name: </label>
	<input style="height: 50px; font-size:1.5em" id="name" type="text" />
	<button class="btn-ok" id="join" onclick="join()">Join</button>
	<button class="btn-ok" id="connect" onclick="connect()">Connect</button>
	<button class="btn-danger" id="leave" onclick="leave()">Leave</button>
	<hr />
	<p>
		Connected: <span id="connect-info" />
	</p>
	<h2>Touchpad-Control</h2>
	<div id="touchpad-control" >
	</div>
</div>

<script type="text/javascript">
		
		var connectBtn = document.getElementById('connect');
		var canvas = document.getElementById('tutorial');
		var ctx = canvas.getContext('2d');
		var textInput = document.getElementById("draw-text");
		var initBtn = document.getElementById("animate");
		
	
		
		function printCircuitInfo(info){
			document.getElementById("circuit-info").innerHTML = info;
		}
		
		function getLatestDirection(){
			if(this.entityDirectionHistory.length >0){
				return this.entityDirectionHistory[this.entityDirectionHistory.length-1];
			}
			return null;
		}
		
 		function printInfo(text) {
			document.getElementById("realtime-info").innerHTML = text;
		}
 		
 		function printEntityInfo(entity){
 			var positionHTML = "<h2>POSITION:"+(this.playerPosition+1)+"/"+this.entities.length+", LAP:"+entity.lap+"</h2>";
 			
 			var velocityInfo = "<h3>velX: "+velX+", velY: "+velY+"</h3>"+
 			"<p>StoppingMode: "+stoppingMode+", StoppingDirection: "+stoppingDir+"</p>";
 			
 			document.getElementById("player-name").innerHTML = "<h2>Player: "+entity.name+"</h2>";
 			document.getElementById("velocity-info").innerHTML = velocityInfo;
 			document.getElementById("player-position").innerHTML = positionHTML;
 			
 			document.getElementById("entity-info").innerHTML = JSON
			.stringify(entity);
 			document.getElementById("entity-info").innerHTML+=
 				"<br> <b>STAGE</b>: "+entity.stageId
 				+"<br> <b>LAYOUT ID</b>: "+entity.layoutId
 				+"<br> "+positionHTML;
 				+"<br> <b>LAP</b>: "+entity.lap;
 		}

		function connect() { doConnect(entity); }

		function setConnected(connected) { document.getElementById('connect-info').innerHTML = connected; }

		function leave() { window.document.title = "0FF-PLAYER: " + entity.name; leaveApp(entity.id); }
	</script>
	<script type="text/javascript">
		

		function updateEntityInfo() {
			var amount = this.entity.life / this.baseHealth * this.WIN_W;
			document.getElementById("life-bar").style.width = amount + "px";
		}

		window.onkeydown = function(e) { move(e.key); }

		window.onkeyup = function(e) { release(e.key); }
		
		function releaseAll(){
			release('w');
			release('a');
			release('s');
			release('d');
		}

		function release(key) {
			run = 0;
			this.entity.physical.lastUpdated = new Date();
		 	/**
			* stopping object wil be handled in the loooooop
			*/
			this.stoppingMode = true; 
		 	switch (key) {
			case 'w':
				this.stoppingDir = this.dirUp;
				this.stoppingSide = "v";
				break;
			case 's':
				this.stoppingDir = this.dirDown;	
				this.stoppingSide = "v";
				break;
			case 'a':
				this.stoppingDir = this.dirLeft;
				this.stoppingSide = "h";
				break;
			case 'd':
				this.stoppingDir = this.dirRight;
				this.stoppingSide = "h";
				break;
			default:
				//default value
			//	stoppingSide = "0";
			//	stoppingDir = 123456;
				break;
			}
		 	
		 	this.entityDirectionHistory.push(this.stoppingDir);
		}

		function update(){ }
		
		function move(key) {
			this.entity.physical.lastUpdated = new Date(); 
			
			const pressW = key == 'w';
			const pressS = key == 's';
			const pressA = key == 'a';
			const pressD = key == 'd';
			const stoppingDec = speedDec;
			
			if (this.stoppingMode && this.stoppingDir == this.dirUp && !pressW) {
				this.velY -= stoppingDec;
			}
			if (this.stoppingMode && this.stoppingDir  == this.dirDown && !pressS) {
				this.velY += stoppingDec;
			}
			if (this.stoppingMode && this.stoppingDir  == this.dirLeft && !pressA) {
				this.velX -= stoppingDec;
			}
			if (this.stoppingMode && this.stoppingDir  == this.dirRight && !pressD){
				this.velX += stoppingDec;
			} 
			
			if (pressD) { 
				if(this.stoppingMode && this.stoppingDir  == this.dirLeft ){
					/* console.debug("BRAKE=================",velX); */
					this.velX += (this.runIncrement+1);
					 
				}else{
					this.velX = 1 + this.run;
					this.run += this.runIncrement;
					this.entityDirection = stopStoppingModeIf(this.dirRight);
				}
			}
			if (pressA) { 
				if(this.stoppingMode && this.stoppingDir  == this.dirRight){
					this.velX -= (this.runIncrement+1);
				}else{
					
					this.velX = -1 - this.run;
					this.run += this.runIncrement;
					this.entityDirection = stopStoppingModeIf(this.dirLeft);
				}
				
			}
			if (pressS) { 
				if(this.stoppingMode && this.stoppingDir == this.dirUp){
					this.velY += (this.runIncrement+1);
					
				}else{
					this.velY = 1 + this.run;
					this.run += this.runIncrement;
					this.entityDirection = stopStoppingModeIf(this.dirDown);
				}
			}
			if (pressW ) { 
				if(this.stoppingMode && this.stoppingDir == this.dirDown){
					this.velY -= (this.runIncrement+1);
				}else{
					this.velY = -1 - this.run;
					this.run += this.runIncrement;
					this.entityDirection = stopStoppingModeIf(this.dirUp);
				}
			}
			if (key == "o") { fireMissile(); }
			/* else{ stoppingMode = false; } */
		}
		
		/**
			this method returns the direction :D
		*/
		function stopStoppingModeIf(dir){
			if(this.stoppingDir  == dir){
				this.stoppingMode= false;
			}	   			
			return dir;
		}

		function initAnimation() {
			this.isAnimate = !this.isAnimate;
			window.requestAnimationFrame(animate);
		}

		function animate() {
			clearCanvas();
			update();
			render();
			if (this.isAnimate) {
				window.requestAnimationFrame(animate);
			}
		}
		
		function getLayoutRole(role){
			switch (role) {
				case this.roleRight:
					return "RIGHT"; 
				case this.roleLeft:
					return "LEFT";	 
				case this.roleUp:
					return "UP"; 
				case this.roleDown:
					return "DOWN"; 
				case this.roleFinish:
					return "FINISH"; 
				default:
					break;
			}
			return "Not Circuit Role";
		}
		
		function getLatestStoppingDirH(){
			if(this.entityDirectionHistory.length > 0){
				for (var i = this.entityDirectionHistory.length-1; i >=0 ; i--) {
					var dir = this.entityDirectionHistory[i];
					if(dir == this.dirRight || dir == this.dirLeft){ 	return dir; 	}
				} 
			}
			return null;
		}
		
		function getLatestStoppingDirV(){
			if(this.entityDirectionHistory.length > 0){
				for (var i = this.entityDirectionHistory.length-1; i >=0 ; i--) {
					var dir = this.entityDirectionHistory[i];
					if(dir == this.dirUp || dir == this.dirDown){ 	return dir; }
				 } 
			}
			return null;
		}

		function renderEntity(currentEntity) {
			var isPlayer = (currentEntity.id == this.entity.id);
			if (isPlayer && this.entity != null) {
				//	missiles = this.entity.missiles;
				this.entity.stageId = currentEntity.stageId;
				this.entity.lap = currentEntity.lap;
				this.entity.stagesPassed = currentEntity.stagesPassed;
				currentEntity = this.entity;
			}
			
			if (currentEntity.missiles != null)
				for (let i = 0; i < currentEntity.missiles.length; i++) {
					let missile = currentEntity.missiles[i];

					let velocity = getVelocity(missile.physical.direction, 10);

					currentEntity.missiles[i].physical.x += velocity.x;
					currentEntity.missiles[i].physical.y += velocity.y;
 
					let missileIntersects = false;
					if (!isPlayer) {
						if (intersect(this.entity, missile).status == true) {
							firing = true;
							this.entity.life--;

						}
					}
					if (isPlayer) {
						if (this.entity.life <= 0) {
							alert("GAME OVER....");
							leave();
						}
						//check if missile intersects player
						for (let x = 0; x < this.entities.length; x++) {
							if (this.entities[x].id != this.entity.id) {
								if (intersect(missile, this.entities[x]).status == true) {
									this.firing = true;
									//		console.log("===============intersects",this.entity.id,entities[i].id );
									missileIntersects = true;
								}
							}
						}
						//check if missile intersects layout
						for (let x = 0; x < this.layouts.length; x++) {
							if (this.layouts[x].physical.role == 102  && intersect(missile, this.layouts[x]).status == true) {
								firing = true;
								//		console.log("===============intersects",this.entity.id,entities[i].id );
								missileIntersects = true;
							}
						}
					}

					//this works
					if (missileIntersects
							|| currentEntity.missiles[i].physical.x<0 || currentEntity.missiles[i].physical.x>WIN_W
							|| currentEntity.missiles[i].physical.y<0 || currentEntity.missiles[i].physical.y>WIN_H) {
						currentEntity.missiles.splice(i, 1);
					}
					let missilephysical = missile.physical;
					ctx.save();
					ctx.fillStyle = missilephysical.color;
					ctx.fillRect(missilephysical.x, missilephysical.y,
							missilephysical.w, missilephysical.h);
					ctx.restore();
				}

			if (isPlayer) {
				 
				let currentphysical = currentEntity.physical;
				let outOfBounds = isOutOfBounds(currentphysical, this.WIN_W, this.WIN_H, this.velX, this.velY);
				let layoutItemIntersects = {};
				let intersectLayout = false;
				let intersection = {};
				let intersectionReverse = {};
				
				let playerIntersects = {};
				let intersectPlayer  = false;
				let intersectionPlayer = {};
				let intersectionPlayerReverse = {};
				
				/*************CHECK INTERSECT PLAYER****************/
				for (var i = 0; i < this.entities.length; i++) {
					var theEntity = this.entities[i];
					var isPlayer2 = (theEntity.id == this.entity.id);
					if(!isPlayer2){ 
						if (  !intersectPlayer && intersect(currentEntity, theEntity).status == true) {
							intersectionPlayer = intersect(currentEntity, theEntity);
							intersectionPlayerReverse = intersectReverse(currentEntity, theEntity);
							intersectPlayer = true;							 
							layoutItemIntersects = theEntity;
						}
					}
				} 
				
				if (intersectPlayer
						&& (intersectionPlayer.direction == currentphysical.direction || intersectionPlayerReverse.direction == currentphysical.direction)) {
				 
					this.velX = 0;
					this.velY = 0;
					this.run = 0;
				}
				
				/*************CHECK INTERSECT LAYOUT****************/
				for (let i = 0; i < this.layouts.length; i++) {
					let layoutItem = this.layouts[i];
					if ( !intersectLayout && intersect(currentEntity, layoutItem).status == true) {
						intersection = intersect(currentEntity, layoutItem);
						intersectionReverse = intersectReverse(currentEntity, layoutItem);
						
						var layoutRole = layoutItem.physical.role;
						this.currentLayoutId = layoutItem.id;
						if(layoutRole != 102){
							printCircuitInfo(layoutRole+":"+getLayoutRole(layoutRole)+", name: "+layoutItem.name);
						}else{
							intersectLayout = true;
						}	
						
						layoutItemIntersects = layoutItem;
					}
				}

				if (intersectLayout
						&& (intersection.direction == currentphysical.direction || intersectionReverse.direction == currentphysical.direction)) {
					printInfo("intersect layout :" + intersectionInfo
							+ JSON.stringify(layoutItemIntersects));
					this.velX = 0;
					this.velY = 0;
					this.run = 0;
				}
				if (intersectLayout) { printInfo("WILL intersect layout :" + intersectionInfo + JSON.stringify(layoutItemIntersects)); } 
				else { printInfo("NO INTERSECTION"); }
				 				 
				if(this.stoppingMode){
					var latestDirectionV = getLatestStoppingDirV();
					var latestDirectionH = getLatestStoppingDirH();
					var theDirX = currentphysical.direction;
					var theDirY = currentphysical.direction;
					if(theDirY != this.dirUp && theDirY!= this.dirDown && latestDirectionV != null){
						if(latestDirectionV == this.dirUp || latestDirectionV == this.dirDown){
							theDirY = latestDirectionV;
						} 
					}
					if(theDirX != this.dirRight && theDirX!= this.dirLeft && latestDirectionH != null){
						if(latestDirectionH == this.dirRight || latestDirectionH == this.dirLeft){
							theDirX = latestDirectionH;
						}
					}
					
					this.velX = decreaseVelX(this.velX,theDirX );
					 
					this.velY = decreaseVelY(this.velY, theDirY);
					if(this.velX == 0 && this.velY == 0){
						console.debug("STOPPING MODE :FALSE");
						this.stoppingMode = false;
					}
				}
				 
				
				let velXToDo = this.velX;
				let velYToDo = this.velY;
				if (currentphysical.lastUpdate < this.entity.physical.lastUpdate) {
					currentEntity.physical.x = this.entity.physical.x;
					currentEntity.physical.y = this.entity.physical.y;
					/* velXToDo = 0;
					velYToDo = 0;
					run = 0; */
				}

				if (!outOfBounds) {
					currentEntity.physical.x += velXToDo;
					currentEntity.physical.y += velYToDo;
				}
				this.entity.stageId = currentEntity.stageId;
				this.entity.physical.direction = entityDirection;
				currentEntity.physical.direction = this.entity.physical.direction;
				currentEntity.life = this.entity.life;
				//currentEntity.missiles = this.entity.missiles;
				this.entity = currentEntity;
				printEntityInfo(this.entity);
				updateEntityInfo();
			}
			if (this.velX != 0 || this.velY != 0 || currentEntity.missiles.length > 0 || this.firing) {
				//console.log("=================",currentEntity.physical);
				if (this.firing)
					this.firing = false;
				entity.stageId = currentEntity.stageId;
				entity.layoutId = this.currentLayoutId;
				updateMovement(this.entity);
			}

			let physical = currentEntity.physical;
			ctx.save();
			ctx.fillStyle = physical.color;
			ctx.font = "15px Arial";

			//TEXT on top of the player
			if (!currentEntity.physical.layout) {
				ctx .fillText(currentEntity.name + "." + physical.direction
								+ "." + currentEntity.active + ".(" + currentEntity.life + ")", physical.x,
								physical.y - 10);
			} else { ctx.fillText(currentEntity.id, physical.x, physical.y - 10); }
			//ctx.strokeRect(physical.x, physical.y, currentEntity.physical.w, currentEntity.physical.h);
			//ctx.fillRect(physical.x, physical.y, currentEntity.physical.w, currentEntity.physical.h);
			ctx.drawImage(getEntityImage(currentEntity.physical.role,
					currentEntity.physical.direction), physical.x, physical.y,
					currentEntity.physical.w, currentEntity.physical.h);
			this.fireTiming++;
			ctx.restore();

		}

		function fireMissile() {
			if (this.fireTiming < 20) {
				return;
			}
			this.firing = true;
			this.fireTiming = 0;
			var missile = createMissile(this.entity);
			console.debug("==>Fire Missile", missile);
			this.entity.missiles.push(missile);
			this.entity.layoutId = currentLayoutId;
			updateMovement(this.entity);
		}

		function getEntityImage(role, dir) {
			var fullAddress = window.location.protocol + '//'
					+ window.location.hostname
					+ (window.location.port ? ':' + window.location.port : '');
			let url = fullAddress + this.playerImagePath
					+ getDirImage(role, dir);
			for (var i = 0; i <this. entityImages.length; i++) {
				if (this.entityImages[i].src == url) {
					return this.entityImages[i];
				}
			}
			return new Image();

		}

		function loadImages() {
			let urls = new Array();
			for (let i = 0; i < this.roles.length; i++) {
				let role = this.roles[i];
				urls .push(playerImagePath + role + "_u.png");
				urls .push(playerImagePath + role + "_d.png");
				urls .push(playerImagePath + role + "_r.png");
				urls .push(playerImagePath + role + "_l.png");
			}
			for (let i = 0; i < this.staticImages.length; i++) {
				let staticImage = this.staticImages[i];
				urls.push(imgPath+ staticImage);
			}

			for (let i = 0; i < urls.length; i++) {
				var image = new Image(); 
				image.onload = function() {
					console.log("Image loaded: ", urls[i], image); ctx.drawImage(image, i * 50, 0, 50, 38);
				}
				image.src = urls[i];
				entityImages.push(image);
			}
		}

		function render() {
			//layout not rendered because it is the background image
			for (let i = 0; i < this.entities.length; i++) {
				let currentEntity =this. entities[i];
				
				if(currentEntity.id == this.entity.id){
					this.playerPosition = i;
				}
				
				renderEntity(currentEntity);

				if (currentEntity.physical.role == 101
						&& currentEntity.id != this.entity.id) {
					if (intersect(this.entity, currentEntity).status == true) {
						let lifeEntity = currentEntity;
						this.velX = 0;
						this.velY = 0;
						if (this.entity.life < baseHealth) {
							this.entity.life += lifeEntity.life;
							if (this.entity.life > baseHealth) {
								this.entity.life = baseHealth
							}
							
							updateMovement(this.entity);
						}
						console.log("-------ADD BONUS", this.entity, lifeEntity);
						leaveApp(lifeEntity.id);
						this.entities.splice(i,1);
					}
				}
			}
		}

		function draw() {
			if (canvas.getContext) { ctx.beginPath(); ctx.arc(70, 80, 10, 0, 2 * Math.PI, false); ctx.fill(); } 
			else { alert("Not Supported"); }
		}

		function clearCanvas() {
			ctx.clearRect(0, 0, canvas.width, canvas.height);
		}
		
		function setupControlBtn(){
			let controlButtons = document.getElementsByClassName("control-btn");
			for (let i = 0; i < controlButtons.length; i++) {
				let button = controlButtons[i];
				let moveRole = button.getAttribute("move-role");
				button.onmousedown = function(){ 	move(moveRole); };
				
				button.onclick = function(){ 	move(moveRole); };
				
				button.onmouseup = function(){ release(moveRole); };
				
				button.onmouseout = function(){ release(moveRole); };
				
			}
			
		}
		setupControlBtn();
		draw();
	</script>
</body>
</html>

