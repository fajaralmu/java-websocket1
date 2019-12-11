import * as ff from 'globals.js' ;

export class Entity{

    id;
    name;
    joinedDate;
    physical;
    stagesPassed;
    life;
    active;
    layoutId;
    stageId;
    position;
    lap;
    missiles;
    
     intersect( entity) {
        var intersection = {};
        intersection.status = false;
        var mainPos = this.physical;
        var entityPos = entity.physical;
        var mainX = mainPos.x;
        var mainY = mainPos.y;
        var mainW = mainPos.w;
        var mainH = mainPos.h;
        var entityX = entityPos.x;
        var entityY = entityPos.y;
        var entityW = entityPos.w;
        var entityH = entityPos.h;
        // console.log("MAIN",mainPos);
        // console.log("entity",entityPos);
        let cond1 = false;
        let cond2 = false;
        let cond3 = false;
        let cond4 = false;
    
        if (entityX > mainX && mainX + mainW > entityX) {
            // console.log("1");
            if (entityY > mainY && mainY + mainH > entityY) {
                // console.log("2");
                cond1 = true;
                intersectionInfo = "COND1" + mainentity.physical.direction;
                intersection.direction = "r";
            }
        }
        if (mainX > entityX && entityX + entityW > mainX) {
            // console.log("3");
            if (mainY > entityY && entityY + entityH > mainY) {
                // console.log("-----4");
                cond2 = true;
                intersectionInfo = "COND2" + mainentity.physical.direction;
                intersection.direction = "l";
            }
        }
        if (mainX < entityX && mainX + mainW > entityX) {
            if (mainY > entityY && entityY + entityH > mainY) {
                cond3 = true;
                intersectionInfo = "COND3" + mainentity.physical.direction;
                intersection.direction = "u";
            }
        }
        if (entityX < mainX && entityX + entityW > mainX) {
            if (entityY > mainY && mainY + mainH > entityY) {
                cond4 = true;
                intersectionInfo = "COND4" + mainentity.physical.direction;
                intersection.direction = "d";
            }
        }
        if (cond1 || cond2 || cond3 || cond4) {
            intersection.status = true;
        }
    
        return intersection;
    }
    
     intersectReverse( entity) {
        var intersection = {};
        intersection.status = false;
        var mainPos = this.physical;
        var entityPos = entity.physical;
        var mainX = mainPos.x;
        var mainY = mainPos.y;
        var mainW = mainPos.w;
        var mainH = mainPos.h;
        var entityX = entityPos.x;
        var entityY = entityPos.y;
        var entityW = entityPos.w;
        var entityH = entityPos.h;
        // console.log("MAIN",mainPos);
        // console.log("entity",entityPos);
        let cond1 = false;
        let cond2 = false;
        let cond3 = false;
        let cond4 = false;
    
        if (entityX > mainX && mainX + mainW > entityX) {
            // console.log("1");
            if (entityY > mainY && mainY + mainH > entityY) {
                // console.log("2");
                cond1 = true;
                intersectionInfo = "COND1" + mainentity.physical.direction;
                intersection.direction = "d";
            }
        }
        if (mainX > entityX && entityX + entityW > mainX) {
            // console.log("3");
            if (mainY > entityY && entityY + entityH > mainY) {
                // console.log("-----4");
                cond2 = true;
                intersectionInfo = "COND2" + mainentity.physical.direction;
                intersection.direction = "u";
            }
        }
        if (mainX < entityX && mainX + mainW > entityX) {
            if (mainY > entityY && entityY + entityH > mainY) {
                cond3 = true;
                intersectionInfo = "COND3" + mainentity.physical.direction;
                intersection.direction = "r";
            }
        }
        if (entityX < mainX && entityX + entityW > mainX) {
            if (entityY > mainY && mainY + mainH > entityY) {
                cond4 = true;
                intersectionInfo = "COND4" + mainentity.physical.direction;
                intersection.direction = "l";
            }
        }
        if (cond1 || cond2 || cond3 || cond4) {
            intersection.status = true;
        }
    
        return intersection;
    }
    
     getDirImage(role, dir) {
        return role + "_" + dir + ".png";
    }
    
     createMissile( ) {
        var missile = {
            'id' : Math.floor(Math.random() * 10000),
            'entityId' : this.id,
            'physical' : {
                'x' : this.physical.x,
                'y' : this.physical.y,
                'color' : this.physical.color,
                'direction' : this.physical.direction,
                'w' : 10,
                'h' : 5
            }
        };
        return missile;
    }
    
     isOutOfBounds(currentphysical, WIN_W, WIN_H, velX, velY) {
        if (currentphysical.x + currentphysical.w + velX > WIN_W) {
            return true;
        } else if (currentphysical.y + currentphysical.h + velY > WIN_H) {
            return true;
        } else if (currentphysical.x + velX < 0) {
            return true;
        } else if (currentphysical.y + velY < 0) {
            return true;
        }
        return false;
    }
    
     isMoving(velX, velY, dir, stoppingSide) {
        var movingProps = {
            'x' : false,
            'y' : false
        }
        if (dir == dirUp) {
            if (velY <  0 ) {
                movingProps.y = true;
            }
        }
        if (dir == dirDown) {
            if (velY >  0) {
                movingProps.y = true;
            }
        }
        if (dir == dirLeft) {
            if (velX <  0) {
                movingProps.x = true;
            }
        }
        if (dir == dirRight){
            if(velX >  0)
            {
                movingProps.x = true
            }
        } 
        return movingProps;
    
    }
    
     decreaseVelX(velX, dir) {
        if (dir == dirLeft){ 
            return velX + speedDec  > 0? 0 : velX + speedDec;
        }
        if (dir == dirRight)
            return velX - speedDec  < 0? 0 : velX - speedDec;;
        return velX;
    }
    
     decreaseVelY(velY, dir) {
        if (dir == dirUp)
            return velY + speedDec  > 0? 0 : velY + speedDec ;
        if (dir == dirDown)
            return velY - speedDec  < 0? 0 : velY - speedDec;
        return velY;
    }
    
     getVelocity(dir, vel) {
        var velocity = {};
        velocity.x = 0;
        velocity.y = 0;
        if (dir == dirUp)
            velocity.y = -vel;
        if (dir == dirDown)
            velocity.y = vel;
        if (dir == dirLeft)
            velocity.x = -vel;
        if (dir == dirRight)
            velocity.x = vel;
    
        return velocity;
    }

   
}