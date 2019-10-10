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
<script src="<c:url value="/res/js/player.js"></c:url >"></script>
<script src="<c:url value="/res/js/websocket-util.js"></c:url >"></script>
<title>Canvas Animation</title>
<style type="text/css">
canvas {
	border: 1px solid black;
}
.btn-ok{
	background-color: green;
	font-size:2em;
	color:white
}
.btn-danger{
	background-color: red;
	font-size:2em;
	color:white
}

.life-bar{
	border:solid 1px black;
	background-color: green;
	height:20px;
	width:1000px;
	
}
</style>
</head>
<body onload="disconnect()">
	<p id="info" align="center"></p>
	<p id="user-info"></p>
	<h3 id="ws-info"></h3>
	<table>
	<tr>
	<td>Health:</td>
	<td><div style="width:1000px; padding:5px; border:solid 1px blue;">
		<div id="life-bar" class="life-bar"></div>
	</div></td>
	</tr></table>
	<hr>
	<canvas id="tutorial" width="1200" height="600"> </canvas>
	<hr />
	<label>Input Name: </label>
	<input id="name" type="text" />
	<button class="btn-ok" id="join" onclick="join()">Join</button>	 
	<button class="btn-ok" id="connect" onclick="connect()">Connect</button>
	<button class="btn-danger" id="leave" onclick="leave()">Leave</button>
	<hr />
	<p>
		Connected: <span id="connect-info" />
	</p>
	
	<p id="msg-info"></p>
	<script type="text/javascript">
		var WIN_W= 1200;
		var WIN_H = 600;
		var connectBtn = document.getElementById('connect');
		var canvas = document.getElementById('tutorial');
		var ctx = canvas.getContext('2d');
		var textInput = document.getElementById("draw-text");
		var initBtn = document.getElementById("animate");
		var isAnimate = false;
		var velX = 0, velY = 0;
		var x = 10, y = 10;
		var users = new Array();
		var user = {};
		var dirUp = "u";
		var dirLeft = "l";
		var dirRight = "r";
		var dirDown = "d";
		var userDirection = "r";
		var firing = false;

		function connect(){
			doConnect();
		}
		
		function join(){
			var name = document.getElementById("name").value;
			user.name = name;
			postReq("/websocket1/game-app-simple/join","name="+name,"join",function(response) {
				var responseObject = JSON.parse(response);
				console.log("RESPONSE", responseObject);
				if(responseObject.responseCode == "00"){
					user = responseObject.user;
				//	console.log("USER",user);
					document.getElementById("user-info").innerHTML = JSON.stringify(user);
					window.document.title = "PLAYER: "+user.name;
					document.getElementById("name").disabled = true;
					initAnimation();
					loadImages();
					
				}else{
					alert("FAILED :"+responseObject.responseMessage);
				}
			});
		}

		function setConnected(connected) {
			document.getElementById('connect-info').innerHTML = connected;
		}

		function leave(){
			window.document.title = "0FF-PLAYER: "+user.name;
			leaveApp(user.id);
	}
	</script>
	<script type="text/javascript">
	
		var fireCount=0;
		var userImages = new Array();
		var allMissiles = new Array();

		function updateUserInfo(){
			document.getElementById("life-bar").style.width=this.user.life+"px";
		}
		
		window.onkeydown = function (e){
			move(e.key);
		}
		
		window.onkeyup = function(e){
			run = 0;
			release(e.key);
		}
		
		function release(key){
			if(key == "a" || key == "d")
				velX = 0;
			 if(key == "w" || key == "s") 
				velY =0;
			
		}
		
	 	
		var run = 0;
		var runIncrement=0.5;
		function move(key) {
			if (key=="d"){
				velX = 1+run;
				run+=runIncrement;
				userDirection=(dirRight);
			}
			if(key== "a"){
				velX = -1-run;
				run+=runIncrement;
				userDirection=(dirLeft);
			}
			if(key=="s"){
				velY = 1+run;
				run+=runIncrement;
				userDirection=(dirDown);
			}
			if(key== "w"){
				velY = -1-run;
				run+=runIncrement;
				userDirection=(dirUp);
			}
			if(key=="o"){
				fireMissile();
				
			}
		}

		function initAnimation() {
			isAnimate = !isAnimate;
			window.requestAnimationFrame(animate);
		}

		function animate() {
			clearCanvas();
			render();
			if (isAnimate) {
				window.requestAnimationFrame(animate);
			}
		}
				
		function renderUser(currentUser){
			var isPlayer =(currentUser.id == this.user.id);
			for(let i=0;i<currentUser.missiles.length;i++){
				let missile = currentUser.missiles[i];
				
				let velocity = getVelocity(missile.entity.direction, 5);
				
				currentUser.missiles[i].entity.x += velocity.x;
				currentUser.missiles[i].entity.y += velocity.y;
				
				/* for(let j=0;j<users.length;j++){
					var u = users[i];
					if(u.id != missile.userId){
						if(intersect(u, missile)){
							users[i].life--;
						}
					}
				}  */
				if(!isPlayer){
					if(intersect(this.user, missile)){
						currentUser.missiles.splice(i,1);
						firing = true;
						this.user.life--;
						continue;
					}
				}
				if(currentUser.missiles[i].entity.x<0 || currentUser.missiles[i].entity.x>WIN_W
					||
					currentUser.missiles[i].entity.y<0 || currentUser.missiles[i].entity.y>WIN_H){
						currentUser.missiles.splice(i,1);
				}
				let missileEntity = missile.entity;
				ctx.save();
				ctx.fillStyle = missileEntity.color;
				ctx.fillRect(missileEntity.x, missileEntity.y, missileEntity.w, missileEntity.h);
				ctx.restore();
			}
			
			if(isPlayer){
				let currentEntity =currentUser.entity;
				let outOfBounds = isOutOfBounds(currentEntity, WIN_W, WIN_H, velX, velY);
				
				if(!outOfBounds){
					currentUser.entity.x += velX;
					currentUser.entity.y += velY;
				}
				this.user.entity.direction=userDirection;
				currentUser.entity.direction = this.user.entity.direction;
				currentUser.life = this.user.life;
				//currentUser.missiles = this.user.missiles;
				this.user = currentUser;
				document.getElementById("user-info").innerHTML = JSON.stringify(this.user);
				updateUserInfo();
			}
			if(velX != 0 || velY!=0 || currentUser.missiles.length>0 || firing){
				//console.log("=================",currentUser.entity);
				if(firing)firing =false;
				updateMovement();
			}
			
			let position = currentUser.entity;
			ctx.save();
			ctx.fillStyle = position.color;
			ctx.font = "30px Arial";
			ctx.fillText(currentUser.name+"."+position.direction+"."+currentUser.active+".("+currentUser.life+")", position.x, position.y - 10);
			//ctx.strokeRect(position.x, position.y, currentUser.entity.w, currentUser.entity.h);
			//ctx.fillRect(position.x, position.y, currentUser.entity.w, currentUser.entity.h);
			ctx.drawImage(getUserImage(currentUser.entity.direction), position.x, position.y, currentUser.entity.w, currentUser.entity.h);
			fireCount++;
			ctx.restore();
			
		}
		
		function fireMissile(){
			if(fireCount<20)
			{
				return;
			} 
			 firing =true;
			fireCount = 0;
			var missile = createMissile(this.user);
			console.log("000000000000000000000000000000Fire Missile",missile);
			this.user.missiles.push(missile);
			updateMovement();
		}
		
		function getUserImage(dir){
			var fullAddress = window.location.protocol+'//'+window.location.hostname+(window.location.port ? ':'+window.location.port: '');
			let url = fullAddress+"<c:url value="/res/img/player/"/>"+getDirImage(dir);
			 for(var i=0;i<userImages.length;i++){
				 if(userImages[i].src == url){
					 return userImages[i];
				 }
			 }
			 return new Image();
			 
		}
		
		function loadImages(){
			let urls = new Array();
			urls.push("<c:url value="/res/img/player/"/>up.png");
			urls.push("<c:url value="/res/img/player/"/>down.png");
					urls.push("<c:url value="/res/img/player/"/>right.png");
							urls.push("<c:url value="/res/img/player/"/>left.png");
			for(let i=0;i<urls.length;i++){
				var image = new Image();
				
				image.onload = function(){
					console.log("Image loaded: ",urls[i]);
					ctx.drawImage(image, i * 50, 0, 50, 38);
				}
				image.src = urls[i];
				userImages.push(image);
			}
		}
		
		function render(){
			for(let i=0;i<users.length;i++){
				renderUser(users[i]);
				if(users[i].id != this.user.id){
					if(intersect(this.user,users[i])){
				//		console.log("===============intersects",this.user.id,users[i].id );
					}
				}
			}
		}
		
		
		function draw() {
			if (canvas.getContext) {
				ctx.beginPath();
				ctx.arc(70, 80, 10, 0, 2 * Math.PI, false);
				ctx.fill();
				
			} else {
				alert("Not Supported");
			}
		}

		function clearCanvas() {
			ctx.clearRect(0, 0, canvas.width, canvas.height);
		}
		draw();
		
		
	</script>
</body>
</html>