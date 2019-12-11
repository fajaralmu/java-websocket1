import * as global from './globals.js'
import * as playerModule from './playerModule.js'
import * as ajax from './ajaxModule.js'


export class Game{

    layouts;
    /*declared in websocket-util.js*/ contextPath ;
    currentLayoutId = 0;
    playerImagePath ;
    urlJoinPath;
    imgPath;
    WIN_W;
    WIN_H ;
    rolePlayer;
    roleBonusLife;
    roleBonusArmor;
    //CIRCUIT
    roleRight;
    roleLeft;
    roleUp;
    roleDown;
    roleFinish;		
    
    roles;
    staticImages;
    baseHealth;

    dirUp = "u";
    dirLeft = "l";
    dirRight = "r";
    dirDown = "d";
    
    playerPosition = 0;
    entityDirection = "r";		
    isAnimate = false;
    velX = 0;
        velY = 0;
    x = 10;
        y = 10;
    entities = new Array();
    entity = {};
    
    firing = false;
    entityDirectionHistory = new Array();
    
    fireTiming = 0;
    entityImages = new Array();
    allMissiles = new Array();
    run = 0;
    runIncrement = 0.3;
    stoppingDir = "0";
    stoppingSide = "0";
    stoppingMode = false;  
    canvas;
    ctx;
    window;
    document;

    fullAddress;

    stompClient = null;
    baseCount = 3;
    updateCount = this.baseCount;

    constructor(){
        console.log("NEW GAME");
    }

    /***WEB SERVICE***/
    

    async updateMovement(entity){
        await this.sendUpdate(entity);
    }

    sendUpdate(entity) {
        this.updateCount++;
        if(entity == null){
            return null;
        }
        if(this.updateCount<this.baseCount){
            return;
        }
        this.updateCount = 0;
        const obj = this;
        
        return new Promise((resolve, reject) => {
        //	console.log("===============Update Entity, ",entity);
            obj.stompClient.send("/app/move", {}, JSON.stringify({
                'entity' : {
                    'id' : entity.id * 1,
                    'life' : entity.life,
                    'active' : true,
                    'layoutId' : entity.layoutId,
                    'lap' : entity.lap,
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

    doConnect(stompClient ) {
        const obj = this;
        this.stompClient = stompClient;
        this.stompClient.connect({}, function(frame) {
        //    obj.setConnected(true);
            console.log('Connected -> ' + frame);
            console.log('stomp client',obj.stompClient);
            obj.document.getElementById("ws-info").innerHTML = stompClient.ws._transport.ws.url;
            obj.stompClient.subscribe('/wsResp/players', function(response) {
                var respObject = JSON.parse(response.body);
                obj.entities = respObject.entities;
    //		 	console.log("************REALTIME: ",entities);
    //		 	document.getElementById("realtime-info").innerHTML = response.body;
            });
            obj.updateMovement(obj.entity);
        });
        
    }

    disconnect() {
        if (stompClient != null) {
            stompClient.disconnect();
        }
        setConnected(false);
        console.log("Disconnected");
    }

    leaveApp(entityId){
        stompClient.send("/app/leave", {}, JSON.stringify({
            'entity' : {
                'id':entityId*1
            }
        }));
    }

    init(canvas, ctx){
        this.canvas = canvas;
        this.ctx = ctx;
    }

    print(){
        console.log("Hello from game");
    }

     getLatestDirection(){
        if(this.entityDirectionHistory.length >0){
            return this.entityDirectionHistory[this.entityDirectionHistory.length-1];
        }
        return null;
    }	

     
     releaseAll(){
        release('w');
        release('a');
        release('s');
        release('d');
    }

     release(key) {
        this.run = 0;
        this.entity.physical.lastUpdated = new Date();
         /**
        * stopping object wil be handled in the loooooop
        */
        
         let willStop = true;
         
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
            willStop = false;
            break;
        }
         
         if(willStop){
             this.stoppingMode = true; 
             this.entityDirectionHistory.push(this.stoppingDir);
         }
         
         
    }

     update (){ }
    
     move(key) {
        this.entity.physical.lastUpdated = new Date(); 
        
        const pressW = key == 'w';
        const pressS = key == 's';
        const pressA = key == 'a';
        const pressD = key == 'd';
        const stoppingDec = playerModule.speedDec;
        
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
                this.entityDirection = this.stopStoppingModeIf(this.dirRight);
            }
        }
        if (pressA) { 
            if(this.stoppingMode && this.stoppingDir  == this.dirRight){
                this.velX -= (this.runIncrement+1);
            }else{
                
                this.velX = -1 - this.run;
                this.run += this.runIncrement;
                this.entityDirection = this.stopStoppingModeIf(this.dirLeft);
            }
            
        }
        if (pressS) { 
            if(this.stoppingMode && this.stoppingDir == this.dirUp){
                this.velY += (this.runIncrement+1);
                
            }else{
                this.velY = 1 + this.run;
                this.run += this.runIncrement;
                this.entityDirection = this.stopStoppingModeIf(this.dirDown);
            }
        }
        if (pressW ) { 
            if(this.stoppingMode && this.stoppingDir == this.dirDown){
                this.velY -= (this.runIncrement+1);
            }else{
                this.velY = -1 - this.run;
                this.run += this.runIncrement;
                this.entityDirection = this.stopStoppingModeIf(this.dirUp);
            }
        }
        if (key == "o") { fireMissile(); }
        /* else{ stoppingMode = false; } */
    }
    
    /**
        this method returns the direction :D
    */
     stopStoppingModeIf (dir){
        if(this.stoppingDir  == dir){
            this.stoppingMode= false;
        }	   			
        return dir;
    }

     initAnimation(obj) {
        this.isAnimate = !this.isAnimate;
        console.log("Init Anim",this);
        this.window.requestAnimationFrame(function(){ 
            obj.animate(obj)
        });
    }

    clearCanvas() {
        this.ctx.clearRect(0, 0, canvas.width, canvas.height);
    }

     animate(obj) {
          
        obj.clearCanvas();
       obj.update();
       obj.render(obj);
        if (obj.isAnimate) {
        	obj.window.requestAnimationFrame(function(){ 
        		obj.animate(obj)
            });
        }
    }
    
     getLayoutRole(role){
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
    
     getLatestStoppingDirH(){
        if(this.entityDirectionHistory.length > 0){
            for (let i = this.entityDirectionHistory.length-1; i >=0 ; i--) {
            	const dir = this.entityDirectionHistory[i];
                if(dir == this.dirRight || dir == this.dirLeft){ 	return dir; 	}
            } 
        }
        return null;
    }
    
     getLatestStoppingDirV(){
        if(this.entityDirectionHistory.length > 0){
            for (let i = this.entityDirectionHistory.length-1; i >=0 ; i--) {
            	const dir = this.entityDirectionHistory[i];
                if(dir == this.dirUp || dir == this.dirDown){ 	return dir; }
             } 
        }
        return null;
    }

     renderEntity(currentEntity) {
        const isPlayer = (currentEntity.id == this.entity.id);
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

                let velocity = playerModule.getVelocity(missile.physical.direction, 10);

                currentEntity.missiles[i].physical.x += velocity.x;
                currentEntity.missiles[i].physical.y += velocity.y;

                let missileIntersects = false;
                if (!isPlayer) {
                    if (playerModule.intersect(this.entity, missile).status == true) {
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
                            if (playerModule.intersect(missile, this.entities[x]).status == true) {
                                this.firing = true;
                                //		console.log("===============intersects",this.entity.id,entities[i].id );
                                missileIntersects = true;
                            }
                        }
                    }
                    //check if missile intersects layout
                    for (let x = 0; x < this.layouts.length; x++) {
                        if (this.layouts[x].physical.role == 102  && playerModule.intersect(missile, this.layouts[x]).status == true) {
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
            let outOfBounds = playerModule.isOutOfBounds(currentphysical, this.WIN_W, this.WIN_H, this.velX, this.velY);
            let layoutItemIntersects = {};
            let intersectLayout = false;
            let intersection = {};
            let intersectionReverse = {};
            
            let playerIntersects = {};
            let intersectPlayer  = false;
            let intersectionPlayer = {};
            let intersectionPlayerReverse = {};
            
            /*************CHECK INTERSECT PLAYER****************/
            for (let i = 0; i < this.entities.length; i++) {
                const theEntity = this.entities[i];
                const isPlayer2 = (theEntity.id == this.entity.id);
                if(!isPlayer2){ 
                    if (  !intersectPlayer && playerModule.intersect(currentEntity, theEntity).status == true) {
                        intersectionPlayer = playerModule.intersect(currentEntity, theEntity);
                        intersectionPlayerReverse = playerModule.intersectReverse(currentEntity, theEntity);
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
                if ( !intersectLayout && playerModule.intersect(currentEntity, layoutItem).status == true) {
                    intersection = playerModule.intersect(currentEntity, layoutItem);
                    intersectionReverse = playerModule.intersectReverse(currentEntity, layoutItem);
                    
                    const layoutRole = layoutItem.physical.role;
                    this.currentLayoutId = layoutItem.id;
                    if(layoutRole != 102){
                       printCircuitInfo(layoutRole+":"+this.getLayoutRole(layoutRole)+", name: "+layoutItem.name);
                    }else{
                        intersectLayout = true;
                    }	
                    
                    layoutItemIntersects = layoutItem;
                }
            }

            if (intersectLayout
                    && (intersection.direction == currentphysical.direction || intersectionReverse.direction == currentphysical.direction)) {
                printInfo("intersect layout :" + playerModule.intersectionInfo   + JSON.stringify(layoutItemIntersects));
                this.velX = 0;
                this.velY = 0;
                this.run = 0;
            }
            if (intersectLayout) { printInfo("WILL intersect layout :" + playerModule.intersectionInfo + JSON.stringify(layoutItemIntersects)); } 
            else { printInfo("NO INTERSECTION"); }
                              
            if(this.stoppingMode){
                let latestDirectionV =this.getLatestStoppingDirV();
                let latestDirectionH =this.getLatestStoppingDirH();
                let theDirX = currentphysical.direction;
                let theDirY = currentphysical.direction;
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
                
                this.velX = playerModule.decreaseVelX(this.velX,theDirX );
                 
                this.velY = playerModule.decreaseVelY(this.velY, theDirY);
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
            this.entity.physical.direction = this.entityDirection;
            currentEntity.physical.direction = this.entity.physical.direction;
            currentEntity.life = this.entity.life;
            //currentEntity.missiles = this.entity.missiles;
            this.entity = currentEntity;
            printEntityInfo(this.entity, this.entities, this.playerPosition, this);
            updateEntityInfo();
        }
        if (this.velX != 0 || this.velY != 0 || currentEntity.missiles.length > 0 || this.firing) {
            //console.log("=================",currentEntity.physical);
            if (this.firing)
                this.firing = false;
            this.entity.stageId = currentEntity.stageId;
            this.entity.layoutId = this.currentLayoutId;
            this.updateMovement(this.entity);
        }

        let physical = currentEntity.physical;
        this.ctx.save();
        this.ctx.fillStyle = physical.color;
        this.ctx.font = "15px Arial";

        //TEXT on top of the player
        if (!currentEntity.physical.layout) {
            ctx .fillText(currentEntity.name + "." + physical.direction
                            + "." + currentEntity.active + ".(" + currentEntity.life + ")", physical.x,
                            physical.y - 10);
        } else { ctx.fillText(currentEntity.id, physical.x, physical.y - 10); }
        //ctx.strokeRect(physical.x, physical.y, currentEntity.physical.w, currentEntity.physical.h);
        //ctx.fillRect(physical.x, physical.y, currentEntity.physical.w, currentEntity.physical.h);
        const image = this.getEntityImage(currentEntity.physical.role,
            currentEntity.physical.direction);
        ctx.drawImage(image, physical.x, physical.y,
                currentEntity.physical.w, currentEntity.physical.h);
        this.fireTiming++;
        ctx.restore();

    }

     fireMissile() {
        if (this.fireTiming < 20) {
            return;
        }
        this.firing = true;
        this.fireTiming = 0;
        missile = playerModule.createMissile(this.entity);
        console.debug("==>Fire Missile", missile);
        this.entity.missiles.push(missile);
        this.entity.layoutId = currentLayoutId;
        this.updateMovement(this.entity);
    }

     getEntityImage(role, dir) {
       
        let url = this.fullAddress + this.playerImagePath
                + playerModule.getDirImage(role, dir);
        for (let i = 0; i <this. entityImages.length; i++) {
            if (this.entityImages[i].src == url) {
                return this.entityImages[i];
            }
        }
        return new Image();

    }

     loadImages () {
        let urls = new Array();
        for (let i = 0; i < this.roles.length; i++) {
            let role = this.roles[i];
            urls .push(this.playerImagePath + role + "_u.png");
            urls .push(this.playerImagePath + role + "_d.png");
            urls .push(this.playerImagePath + role + "_r.png");
            urls .push(this.playerImagePath + role + "_l.png");
        }
        for (let i = 0; i < this.staticImages.length; i++) {
            let staticImage = this.staticImages[i];
            urls.push(imgPath+ staticImage);
        }

        for (let i = 0; i < urls.length; i++) {
            let image = new Image(); 
            image.onload = function() {
                console.log("Image loaded: ", urls[i], image); ctx.drawImage(image, i * 50, 0, 50, 38);
            }
            image.src = urls[i];
            this.entityImages.push(image);
        }
    }

     render(obj) {
        //layout not rendered because it is the background image
        for (let i = 0; i < obj.entities.length; i++) {
            let currentEntity =obj. entities[i];
            
            if(currentEntity.id == obj.entity.id){
                obj.playerPosition = i;
            }
            
            obj.renderEntity(currentEntity);

            if (currentEntity.physical.role == 101
                    && currentEntity.id != obj.entity.id) {
                if (playerModule.intersect(obj.entity, currentEntity).status == true) {
                    let lifeEntity = currentEntity;
                    obj.velX = 0;
                    obj.velY = 0;
                    if (obj.entity.life < obj.baseHealth) {
                        obj.entity.life += lifeEntity.life;
                        if (obj.entity.life > obj.baseHealth) {
                            obj.entity.life = obj.baseHealth
                        }
                        
                        obj.updateMovement(obj.entity);
                    }
                    console.log("-------ADD BONUS", obj.entity, lifeEntity);
                    obj.leaveApp(lifeEntity.id);
                    obj.entities.splice(i,1);
                }
            }
        }
    }

     draw() {
        if (canvas.getContext) { ctx.beginPath(); ctx.arc(70, 80, 10, 0, 2 * Math.PI, false); ctx.fill(); } 
        else { alert("Not Supported"); }
    }

   
    

    join(name) {
        this.entityDirectionHistory = new Array();
        
        this.entity.name = name;
        ajax.postReq(
                this.urlJoinPath,
                "name=" + name,
                "join",
                function(response,object) {
                    let responseObject = JSON.parse(response);
                    console.log("RESPONSE", responseObject);
                    if (responseObject.responseCode == "00") {
                        object. entity = responseObject.entity;
                        //	console.log("USER",entity);
    					printEntityInfo(object.entity, object.entities, object.playerPosition, object);
    					object.window.document.title = "PLAYER: " + object.entity.name;
    					object.document.getElementById("name").disabled = true;
                        object.initAnimation(object);
                        object.loadImages();
    
                    } else {
                        alert("FAILED :" + responseObject.responseMessage);
                    }
                }, this);
    }
     
    start(){
        draw();
    }
    
}