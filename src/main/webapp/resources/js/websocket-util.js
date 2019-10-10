var stompClient = null;
		

function updateMovement() {
	stompClient.send("/app/move", {}, JSON.stringify({
		'user' : {
			'id' : user.id * 1,
			'life' : user.life,
			'active' : true,
			'entity' : {
				'x' : user.entity.x,
				'y' : user.entity.y,
				'direction' : user.entity.direction,
				'color' : user.entity.color
			},
			'missiles' : user.missiles
		}
	}));
}

function doConnect() {
	var socket = new SockJS('/websocket1/game-app');
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function(frame) {
		setConnected(true);
		console.log('Connected -> ' + frame);
		console.log('stomp client',stompClient);
		document.getElementById("ws-info").innerHTML = stompClient.ws._transport.ws.url;
		stompClient.subscribe('/wsResp/players', function(response) {
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

function leaveApp(userId){
	stompClient.send("/app/leave", {}, JSON.stringify({
		'user' : {
			'id':userId*1
		}
	}));
}