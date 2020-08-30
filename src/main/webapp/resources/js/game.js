import * as global from './globals.js'
import * as playerModule from './playerModule.js'
import * as ajax from './ajaxModule.js'
import * as websocketModule from './websocketModule.js'
import * as movementModule from './movementModule.js'


export class Game {
    serverName;
    layouts;
    contextPath;
    currentLayoutId = 0;
    playerImagePath;
    urlJoinPath;
    imgPath;
    WIN_W;
    WIN_H;
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
    
    updateConnectionInfo = function(connected){}

    constructor(callback) {
//    	var callback = null;
        console.debug("NEW GAME");
        if(callback){
        	callback();
        }
    }

    /***WEB SERVICE***/ 
    updateMovement = async function(entity) {
        await this.sendUpdate(entity);
    }
    
    resetPosition = async function(){
    	console.debug("resetPosition");
    	await websocketModule.resetPosition(this, this.entity);
    }

    sendUpdate = function(entity) {
        this.updateCount++;
        if (entity == null) {
            console.error("Entity is NULL");
            return;
        }
        if (this.updateCount < this.baseCount) { 
            return;
        }
        this.updateCount = 0; 
        return websocketModule.doSendUpdate(this, entity);
    }

    doConnect = function(stompClient) { 
        this.stompClient = stompClient;  
        websocketModule.doConnect(this);  
    }

    disconnect = function() {
        if (stompClient != null) {
            stompClient.disconnect();
        }
        setConnected(false);
        console.log("Disconnected");
        this.updateConnectionInfo(false);
    }
    
    connectCallback = function(){
    	this.updateConnectionInfo(true);
    }

    leaveApp = function(entityId) {
        websocketModule.doLeave(this, entityId);
    }

    init = function(canvas, ctx) {
        this.canvas = canvas;
        this.ctx = ctx;
    }

    print = function() {
        console.log("Hello from game");
    }

    getLatestDirection = function() {
        if (this.entityDirectionHistory.length > 0) {
            return this.entityDirectionHistory[this.entityDirectionHistory.length - 1];
        }
        return null;
    }


    releaseAll = function() {
        release('w');
        release('a');
        release('s');
        release('d');
    }

    /**
     * handles key release
     * @param {String} key 
     */
    release = function(key) {
        this.run = 0;
        if(this.entity==null || this.entity.physical == null){
        	return;
        }
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

        if (willStop) {
            this.stoppingMode = true;
            this.entityDirectionHistory.push(this.stoppingDir);
        } 
    }

   update = function() { }

    /**
     * handle entity movement
     * @param {String} key 
     */
    move = function(key) {
    	if( this.entity == null ||  this.entity.physical == null){
     		return;
    	}
        this.entity.physical.lastUpdated = new Date();

        const pressW = key == 'w';
        const pressS = key == 's';
        const pressA = key == 'a';
        const pressD = key == 'd';
        const pressR = key == 'r';
        const stoppingDec = playerModule.speedDec;

        if (this.stoppingMode && this.stoppingDir == this.dirUp && !pressW) {
            this.velY -= stoppingDec;
        }
        if (this.stoppingMode && this.stoppingDir == this.dirDown && !pressS) {
            this.velY += stoppingDec;
        }
        if (this.stoppingMode && this.stoppingDir == this.dirLeft && !pressA) {
            this.velX -= stoppingDec;
        }
        if (this.stoppingMode && this.stoppingDir == this.dirRight && !pressD) {
            this.velX += stoppingDec;
        }

        if (pressD) {
            if (this.stoppingMode && this.stoppingDir == this.dirLeft) {
                /* console.debug("BRAKE=================",velX); */
                this.velX += (this.runIncrement + 1); 
            } else {
                this.velX = 1 + this.run;
                this.run += this.runIncrement;
                this.entityDirection = this.stopStoppingModeIf(this.dirRight);
            }
        }
        if (pressA) {
            if (this.stoppingMode && this.stoppingDir == this.dirRight) {
                this.velX -= (this.runIncrement + 1);
            } else { 
                this.velX = -1 - this.run;
                this.run += this.runIncrement;
                this.entityDirection = this.stopStoppingModeIf(this.dirLeft);
            }

        }
        if (pressS) {
            if (this.stoppingMode && this.stoppingDir == this.dirUp) {
                this.velY += (this.runIncrement + 1); 
            } else {
                this.velY = 1 + this.run;
                this.run += this.runIncrement;
                this.entityDirection = this.stopStoppingModeIf(this.dirDown);
            }
        }
        if (pressW) {
            if (this.stoppingMode && this.stoppingDir == this.dirDown) {
                this.velY -= (this.runIncrement + 1);
            } else {
                this.velY = -1 - this.run;
                this.run += this.runIncrement;
                this.entityDirection = this.stopStoppingModeIf(this.dirUp);
            }
        }
        if (key == "o") { this.fireMissile(); }
        if(pressR){
        	this.resetPosition();
        }
        /* else{ stoppingMode = false; } */
    }

    /**
     * check entity direction if == current stopping direction
     * @param {String} dir 
     */
    stopStoppingModeIf = function(dir) {
        if (this.stoppingDir == dir) {
            this.stoppingMode = false;
        }
        return dir;
    }

    initAnimation  = function(obj) {
        this.isAnimate = !this.isAnimate;
        console.log("Init Anim", this);
        this.window.requestAnimationFrame(function () {
            obj.animate(obj)
        });
    }
    animate  = function(obj) {

        obj.clearCanvas();
        obj.update();
        obj.render();
        if (obj.isAnimate) {
            obj.window.requestAnimationFrame(function () {
                obj.animate(obj)
            });
        }
    }
 
    clearCanvas = function() {
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
    } 
    
    getLatestStoppingDirH = function() {
        if (this.entityDirectionHistory.length > 0) {
            for (let i = this.entityDirectionHistory.length - 1; i >= 0; i--) {
                const dir = this.entityDirectionHistory[i];
                if (dir == this.dirRight || dir == this.dirLeft) { return dir; }
            }
        }
        return null;
    }

    getLatestStoppingDirV = function() {
        if (this.entityDirectionHistory.length > 0) {
            for (let i = this.entityDirectionHistory.length - 1; i >= 0; i--) {
                const dir = this.entityDirectionHistory[i];
                if (dir == this.dirUp || dir == this.dirDown) { return dir; }
            }
        }
        return null;
    }

     
    /**
     * render entity
     * @param {*} currentEntity => entity object calculated from server
     */
    renderEntity = function(currentEntity) {
        const isPlayer = (currentEntity.id == this.entity.id);
        
        //////////Update Values From Server////////////
        let isForceUpdate = false;
        if (isPlayer && this.entity != null) {
            //	missiles = this.entity.missiles;
            this.entity.stageId = currentEntity.stageId;
            this.entity.lap = currentEntity.lap;
            this.entity.forceUpdate = currentEntity.forceUpdate; 
            this.entity.stagesPassed = currentEntity.stagesPassed;
            isForceUpdate = currentEntity.forceUpdate;
            
            if(isForceUpdate){
//            	document.title =  currentEntity.physical.x +", "+ currentEntity.physical.y;
            	currentEntity.physical.lastUpdate = new Date();
//            	console.debug("currentEntity Position: ",);
	        	this.entity.physical.x = currentEntity.physical.x ;
	            this.entity.physical.y = currentEntity.physical.y ;
            }else{
//            	document.title = isForceUpdate;
            }
            
            currentEntity = this.entity;   
        }

        /**
         * ===============================================
         * ======== HANDLE IF ENTITY HAS MISSILES ========
         * ===============================================
         */

        if (currentEntity.missiles != null){
            for (let i = 0; i < currentEntity.missiles.length; i++) {
                const missile = currentEntity.missiles[i];

                let velocity = playerModule.getVelocity(missile.physical.direction, 10);

                currentEntity.missiles[i].physical.x += velocity.x;
                currentEntity.missiles[i].physical.y += velocity.y;

                let missileIntersects = false;
                /**
                 * OTHER player missile
                 */
                if (!isPlayer) {
                    const missileIntersection  = playerModule.intersect(this.entity, missile);
                    if (missileIntersection.status == true) {
                        console.debug("ATTACKED BY MISSILE: ",missileIntersection);
                        this.firing = true;
                        this.entity.life--; 
                        missileIntersects = true;
                    }
                }
                /**
                 * MAIN player missile
                 */
                if (isPlayer) {
                    
                    const missileIntersectsPlayer = movementModule.missileIntersectsPlayer(missile,this.entity,this.entities);
                    const missileIntersectsLayout = movementModule.missileIntersectsLayout(missile, this.layouts, this.roleGeneralLayout);

                    missileIntersects = missileIntersectsPlayer||missileIntersectsLayout;
                    if(missileIntersects){
                        this.firing = true;
                    } 
                }

                //this works
                if (missileIntersects
                    || currentEntity.missiles[i].physical.x < 0 || currentEntity.missiles[i].physical.x > this.WIN_W
                    || currentEntity.missiles[i].physical.y < 0 || currentEntity.missiles[i].physical.y > this.WIN_H) {
                    currentEntity.missiles.splice(i, 1);
                }
                /**
                 * ====================
                 * == render missile ==
                 * ====================
                 */

                let missilephysical = missile.physical;
                ctx.save();
                ctx.fillStyle = missilephysical.color;
                ctx.fillRect(missilephysical.x, missilephysical.y, missilephysical.w, missilephysical.h);
                ctx.restore();
            }
        }

        /**
            ========================================================
            ============= HANDLE ENTITY IF MAIN PLAYER =============
            ========================================================
        */
        
        if (isPlayer) {
            //check life
            if (this.entity.life <= 0) {
                alert("GAME OVER....");
                leave();
            }

            const currentphysical = currentEntity.physical;
            const outOfBounds = playerModule.isOutOfBounds(currentphysical, this.WIN_W, this.WIN_H, this.velX, this.velY);
            const intersectPlayer = movementModule.intersectPlayer(currentEntity, this.entities);
            const intersectLayout = movementModule.intersectLayout(currentEntity, this, this.layouts);

            if (intersectLayout || intersectPlayer){
                this.velX = 0;
                this.velY = 0;
                this.run = 0;
            }
            
            if (this.stoppingMode) { 
                let theDirX = movementModule.getCurrentDirectionX(this, currentphysical);
                let theDirY = movementModule.getCurrentDirectionY(this, currentphysical);
                this.velX = playerModule.decreaseVelX(this.velX, theDirX); 
                this.velY = playerModule.decreaseVelY(this.velY, theDirY);
                if (this.velX == 0 && this.velY == 0) {
                    console.debug("STOPPING MODE :FALSE");
                    this.stoppingMode = false;
                }
            }

            let velXToDo = this.velX;
            let velYToDo = this.velY;
          
            if (currentphysical.lastUpdate < this.entity.physical.lastUpdate  ) {
                currentEntity.physical.x = this.entity.physical.x;
                currentEntity.physical.y = this.entity.physical.y;
            }

            if (!outOfBounds) {
                currentEntity.physical.x += velXToDo;
                currentEntity.physical.y += velYToDo;
            }
            this.entity.stageId = currentEntity.stageId;
            this.entity.physical.direction = this.entityDirection;
            currentEntity.physical.direction = this.entity.physical.direction;
            currentEntity.life = this.entity.life; 

            this.entity = currentEntity;
            printEntityInfo(this.entity, this.entities, this.playerPosition, this);
            updateEntityInfo();
        }

        /*===========END HANDLE IF MAIN PLAYER===================*/

        if (this.velX != 0 || this.velY != 0 || currentEntity.missiles.length > 0 || this.firing) {
            //console.log("=================",currentEntity.physical);
            if (this.firing)
                this.firing = false;
            this.entity.stageId = currentEntity.stageId;
            this.entity.layoutId = this.currentLayoutId;
            this.updateMovement(this.entity);
        }
        
        this.fireTiming++;

        /**================================
         * ===== BEGIN RENDER CANVAS ======
         * ================================
         */

        const physical = currentEntity.physical;

        this.ctx.save();
        this.ctx.fillStyle = physical.color;
        this.ctx.font = "15px Arial";

        /**
         * TEXT ON TOP OF PLAYERS
         */
        if (! physical.layout) {
            const INFO = currentEntity.name + "." + physical.direction + "." + currentEntity.active + ".(" + currentEntity.life + ")";

            ctx.fillText(INFO, physical.x, physical.y - 10);
        } else { 
            ctx.fillText(currentEntity.id, physical.x, physical.y - 10); 
        }

        /**
         * RENDER PLAYER
         */

        //ctx.strokeRect(physical.x, physical.y, currentEntity.physical.w, currentEntity.physical.h);
        //ctx.fillRect(physical.x, physical.y, currentEntity.physical.w, currentEntity.physical.h);
        const image = this.getEntityImage( physical.role,  physical.direction);
        if(null == image){
        	console.error("Texture not found!");
        }
        ctx.drawImage(image, physical.x, physical.y,  physical.w,  physical.h);        
        ctx.restore();

    }

    /**
     * do fire missile
     */
    fireMissile = function() {
        if (this.fireTiming < 20) {
            return;
        }
        this.firing = true;
        this.fireTiming = 0;
        const missile = playerModule.createMissile(this.entity);
        console.debug("==>Fire Missile", missile);
        this.entity.missiles.push(missile);
        this.entity.layoutId = this.currentLayoutId;
        this.updateMovement(this.entity);
    }

    getEntityImage = function(role, dir) { 
        const url = this.fullAddress + this.playerImagePath  + playerModule.getDirImage(role, dir);
        for (let i = 0; i < this.entityImages.length; i++) {
            if (this.entityImages[i].src == url) {
                return this.entityImages[i];
            }
        }
        return new Image(); 
    }

    /**
     * load textures for entities
     */
    loadImages = function() {
        let urls = new Array();
        for (let i = 0; i < this.roles.length; i++) {
            let role = this.playerImagePath +  this.roles[i];
            urls.push(  role + "_u.png");
            urls.push(  role + "_d.png");
            urls.push(  role + "_r.png");
            urls.push(  role + "_l.png");
        }
        for (let i = 0; i < this.staticImages.length; i++) {
            let staticImage = this.staticImages[i];
            urls.push(imgPath + staticImage);
        }

        for (let i = 0; i < urls.length; i++) {
            let image = new Image();
            image.onload = function () {
                console.log("Image loaded: ", urls[i], image); ctx.drawImage(image, i * 50, 0, 50, 38);
            }
            image.src = urls[i];
            this.entityImages.push(image);
        }
    }

    /**
     * main render method 
     */
    render = function() {
        //layout not rendered because it is the background image
    	 for (let i = 0; i < this.entities.length; i++) {
             let currentEntity = this.entities[i];

             if (currentEntity.id == this.entity.id) {
                 this.playerPosition = i;
             }

             this.renderEntity(currentEntity);

             if (currentEntity.physical.role == game.roleBonusLife
                 && currentEntity.id != this.entity.id) {
                 if (playerModule.intersect(this.entity, currentEntity).status == true) {
                     let lifeEntity = currentEntity;
                     this.velX = 0;
                     this.velY = 0;
                     if (this.entity.life < this.baseHealth) {
                         this.entity.life += lifeEntity.life;
                         if (this.entity.life > this.baseHealth) {
                             this.entity.life = this.baseHealth
                         }

                         this.updateMovement(this.entity);
                     }
                     console.log("-------ADD BONUS", this.entity, lifeEntity);
                     this.leaveApp(lifeEntity.id);
                     this.entities.splice(i, 1);
                 }
             }
         }
    }

    draw = function() {
        if (canvas.getContext) { ctx.beginPath(); ctx.arc(70, 80, 10, 0, 2 * Math.PI, false); ctx.fill(); }
        else { alert("Not Supported"); }
    }
 
    join = function(name, serverName, successCallback, errorCallback) {
        this.entityDirectionHistory = new Array(); 
        this.entity.name = name;
        this.serverName = serverName;
        
        ajax.postReq( this.urlJoinPath,  "name=" + name +"&server="+serverName,  
            function (response, object) {
                let responseObject = JSON.parse(response);
                console.log("RESPONSE", responseObject);
                if (responseObject && responseObject.responseCode == "00") {
                    object.handleSuccessJoin(responseObject);
                    successCallback();
                } else {
                    alert("FAILED :" + responseObject.responseMessage);
                    errorCallback();
                }
            }, this);
    }

    start = function() {
        draw();
    }

    handleSuccessJoin = function(responseObject){ 
        this.entity = responseObject.entity;
        //	console.log("USER",entity);
        printEntityInfo(this.entity, this.entities, this.playerPosition, this);
        this.window.document.title = "PLAYER: " + this.entity.name;
        this.document.getElementById("name").disabled = true;
        this.initAnimation(this);
        this.loadImages();
        alert(responseObject.responseMessage+", Click 'Connect' To Continue..");
    }

}