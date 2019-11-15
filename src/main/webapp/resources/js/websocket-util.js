var stompClient = null;
var baseCount = 10;
var updateCount = baseCount;
var contextPath = "";

async function updateMovement(entity){
	await sendUpdate(entity);
}

function sendUpdate(entity) {
	updateCount++;
	if(entity == null){
		return null;
	}
	if(updateCount<baseCount){
		return;
	}
	updateCount = 0;
	
	return new Promise((resolve, reject) => {
	//	console.log("===============Update Entity, ",entity);
		stompClient.send("/app/move", {}, JSON.stringify({
			'entity' : {
				'id' : entity.id * 1,
				'life' : entity.life,
				'active' : true,
				'layoutId' : entity.layoutId,
				'stagesPassed': entity.stagesPassed,
				'physical' : {
					'x' : entity.physical.x,
					'y' : entity.physical.y,
					'direction' : entity.physical.direction,
					'color' : entity.physical.color,
					'lastUpdated': new Date()
				},
				'missiles' : entity.missiles
			}
		}));
	
	});
}

function doConnect(entity) {
	var socket = new SockJS(contextPath+'/game-app');
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function(frame) {
		setConnected(true);
		console.log('Connected -> ' + frame);
		console.log('stomp client',stompClient);
		document.getElementById("ws-info").innerHTML = stompClient.ws._transport.ws.url;
		stompClient.subscribe('/wsResp/players', function(response) {
			var respObject = JSON.parse(response.body);
		 	entities = respObject.entities;
//		 	console.log("************REALTIME: ",entities);
//		 	document.getElementById("realtime-info").innerHTML = response.body;
		});
		updateMovement(entity);
	});
	
}

function disconnect() {
	if (stompClient != null) {
		stompClient.disconnect();
	}
	setConnected(false);
	console.log("Disconnected");
}

function leaveApp(entityId){
	stompClient.send("/app/leave", {}, JSON.stringify({
		'entity' : {
			'id':entityId*1
		}
	}));
}