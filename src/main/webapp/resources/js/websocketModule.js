import { Game } from "./game.js";

/**
 * connect to server
 * @param {Game} obj 
 */
export function  doConnect(obj) { 
	const serverName = obj.serverName;
    obj.stompClient.connect({}, function (frame) {
        obj.connectCallback();
        console.log('Connected -> ' + frame);
        console.log('stomp client', obj.stompClient);
        try{
        	document.getElementById("ws-info").innerHTML = obj.stompClient.ws._transport.ws.url;
        }catch (e) {
			 
		}
        //////////SUBSCRIBE//////////
        obj.stompClient.subscribe('/wsResp/players/'+serverName, function (response) {
            
            var respObject = JSON.parse(response.body);
            
            obj.document.getElementById("ws-info").innerHTML = "<b>["+respObject.serverName+"]</b> url: "+ obj.stompClient.ws._transport.ws.url;
            obj.entities = respObject.entities; 
        });
        obj.updateMovement(obj.entity);
    }); 
}

/**
 * leave game and remove from server
 * @param {Game} obj 
 * @param {Number} entityId 
 */
export function doLeave(obj, entityId){
    obj.stompClient.send("/app/leave", {}, JSON.stringify({
        'entity': {
            'id': entityId * 1
        },
        'serverName':obj.serverName
    }));
}

/**
 * player move and update to server
 * @param {Game} obj 
 * @param {Object} entity 
 */
export function doSendUpdate(obj, entity){
    return new Promise((resolve, reject) => {
        //	console.log("===============Update Entity, ",entity);
        obj.stompClient.send("/app/move", {}, JSON.stringify({
            'serverName':obj.serverName,
            'entity': {
                'id': entity.id * 1,
                'life': entity.life,
                'active': true,
                'layoutId': entity.layoutId,
                'lap': entity.lap,
                'stagesPassed': entity.stagesPassed,
                'forceUpdate':false,
                'physical': {
                    'x': entity.physical.x,
                    'y': entity.physical.y,
                    'direction': entity.physical.direction,
                    'color': entity.physical.color,
                    'lastUpdated': new Date()
                },
                'missiles': entity.missiles
            }
        }));

    });
}

/**
 * reset to starting position
 * @param {Game} obj 
 * @param {Object} entity  
 */
export function resetPosition(obj, entity){
    return new Promise((resolve, reject) => {
        //	console.log("===============Update Entity, ",entity);
        obj.stompClient.send("/app/resetposition", {}, JSON.stringify({
            'serverName':obj.serverName,
            'entity': {
                'id': entity.id * 1
            }
        }));

    });
}