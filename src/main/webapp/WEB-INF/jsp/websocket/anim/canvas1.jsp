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
	<div style="width:1000px; border:solid 1px blue;">
		<div id="life-bar" class="life-bar"></div>
	</div>
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
		var stompClient = null;
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
		var firing = false;

		function connect(){
			doConnect();
		}
		
		function join(){
			var name = document.getElementById("name").value;
			user.name = name;
			postReq("/websocket1/chat-simple/join","name="+name,"join",function(response) {
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

		function updateMovement(){
			stompClient.send("/app/move", {}, JSON.stringify({
				'user' :{
				'id':user.id*1,
				'life':user.life,
				'active':true,
				'entity':	{
					'x' : user.entity.x,
					'y' : user.entity.y,
					'direction':user.entity.direction,
					'color':user.entity.color
				},
				'missiles':user.missiles
				} 
			}));
		}

		function setConnected(connected) {
			document.getElementById('connect-info').innerHTML = connected;
		}

		function doConnect() {
			var socket = new SockJS('/websocket1/chat');
			stompClient = Stomp.over(socket);
			stompClient.connect({}, function(frame) {
				setConnected(true);
				console.log('Connected -> ' + frame);
				stompClient.subscribe('/wsResp/messages', function(response) {
					var respObject = JSON.parse(response.body);
				//	console.log("subscribed", respObject);
				document.getElementById("msg-info").innerHTML = JSON.stringify(respObject);
					users = respObject.users;
				});
				updateMovement();
			});
		}

		function disconnect() {
			if (stompClient != null) {
				stompClient.disconnect();
			}
			setConnected(false);
			console.log("Disconnected");
		}
		
		function leave(){
			window.document.title = "0FF-PLAYER: "+user.name;
		stompClient.send("/app/leave", {}, JSON.stringify({
				'user' : {
					'id':user.id*1
				}
			}));
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
			if(key == "a" || key == "d"){
				velX = 0;
			}else if(key == "w" || key == "s"){
				velY =0;
			}
			
		}
		
		function getDirImage(dir){
			let imgName = "up.png";
			switch (dir){
			case dirUp:
				imgName = "up.png";
				break;
			case dirDown:
				imgName= "down.png";
				break;
			case dirLeft:
				imgName = "left.png";
				break;
			case dirRight:
				imgName = "right.png";
				break;
			default:
				break;
			
			}
			return imgName;
		}
		
		function setEntityDirection(dir){
			if(user!=null && user.entity!= null){
				user.entity.direction = dir;
			}
		}
		var run = 0;
		var runIncrement=0.5;
		function move(key) {
			if (key=="d"){
				velX = 1+run;
				run+=runIncrement;
				setEntityDirection(dirRight);
			}
			if(key== "a"){
				velX = -1-run;
				run+=runIncrement;
				setEntityDirection(dirLeft);
			}
			if(key=="s"){
				velY = 1+run;
				run+=runIncrement;
				setEntityDirection(dirDown);
			}
			if(key== "w"){
				velY = -1-run;
				run+=runIncrement;
				setEntityDirection(dirUp);
			}
			if(key=="o"){
				fireMissile();
				
			}
		}

		function initAnimation() {
			//connectBtn.disabled = false;
			isAnimate = !isAnimate;
			window.requestAnimationFrame(animate);
		}

		function animate() {
			clearCanvas();
			//var x = Math.floor(Math.random()*400);
			//var y = Math.floor(Math.random()*500);
			render();
			if (isAnimate) {
				window.requestAnimationFrame(animate);
			}
		}
		
		function getVelocity(dir, vel){
			var velocity = {};
			velocity.x = 0;
			velocity.y= 0;
			switch (dir){
			case dirUp:
				velocity.y = -vel;
				break;
			case dirDown:
				velocity.y = vel;
				break;
			case dirLeft:
				velocity.x = -vel;
				break;
			case dirRight:
				velocity.x = vel;
				break;
			default:
				break;
			}
			return velocity;
		}
		
		function renderUser(currentUser){
			var isPlayer =(currentUser.id == this.user.id);
			for(let i=0;i<currentUser.missiles.length;i++){
				let missile = currentUser.missiles[i];
				
				let velocity = getVelocity(missile.entity.direction, 5);
			//	console.log("Missile Vel",velocity, isPlayer);
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
				
				currentUser.entity.x += velX;
				currentUser.entity.y += velY;
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
			var userEntity = this.user.entity;
			var missile = {
					'id':Math.floor(Math.random() * 10000),
					'userId': this.user.id,
					'entity':{
						'x':userEntity.x,
						'y':userEntity.y,
						'color':userEntity.color,
						'direction':userEntity.direction,
						'w':10,
						'h':5
					}
			}
			console.log("000000000000000000000000000000Fire Missile",missile);
			this.user.missiles.push(missile);
			updateMovement();
		}
		
		function getUserImage(dir){
			var fullAddress = window.location.protocol+'//'+window.location.hostname+(window.location.port ? ':'+window.location.port: '');
		//	console.log("URI",fullAddress);
			let url = fullAddress+"<c:url value="/res/img/player/"/>"+getDirImage(dir);
			 for(var i=0;i<userImages.length;i++){
				// console.log("```````````````````````````````",userImages[i].src,url);
				 if(userImages[i].src == url){
				//	 console.log("======================IMAGE ",userImages[i].src);
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
		
		
		function drawImage() {
			var img = new Image();
			img.onload = function() {
				for (var i = 0; i < 4; i++) {
					for (var j = 0; j < 3; j++) {
						ctx.drawImage(img, j * 50, i * 38, 50, 38);
					}
				}
			};
			img.src = 'asset/image1.png';
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
		
		function intersect(mainuser,user){
			var mainPos = mainuser.entity;
			var userPos = user.entity;
			var mainX = mainPos.x;
			var mainY = mainPos.y;
			var mainW = mainPos.w;
			var mainH = mainPos.h;
			var userX = userPos.x;
			var userY = userPos.y;
			var userW = userPos.w;
			var userH = userPos.h;
		//	console.log("MAIN",mainPos);
			//console.log("USER",userPos);
			let cond1 = false;
			let cond2 = false;
			let cond3 = false;
			let cond4 = false;
			
			if(userX >= mainX && mainX + mainW >=userX){
				//console.log("1");
				if(userY >= mainY && mainY + mainH >=userY){
					//console.log("2");
					cond1 = true;
				}
			}
			if(mainX >= userX && userX + userW >=mainX){
				//console.log("3");
				if(mainY >=userY && userY + userH >=mainY){
					//console.log("-----4");
					cond2 = true;
				}
			}
			if(mainX <= userX && mainX + mainW >= userX){
				if(mainY >= userY && userY + userH >= mainY){
					cond3 = true;
				}
			}
			if(userX <= mainX && userX+userW >= mainX){
				if(userY >= mainY && mainY+mainH >= userY){
					cond4 = true;
				}
			}
			if(cond1 || cond2 || cond3 || cond4){
				return true;
			}
				
			return false;
		}
		
		draw();
		
		
	</script>
</body>
</html>