export var intersectionInfo = "";
export const speedDec = 0.2;
import * as global from './globals.js'
 
/**
 * calculate if main entity intersect given entity
 * @param {object} mainentity 
 * @param {object} entity 
 */
export function intersect(mainentity, entity) {
	var intersection = {};
	intersection.status = false;
	var mainPos = mainentity.physical;
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

/**
 * calculate if main entity intersects given entity in reverse direction
 * @param {object} mainentity 
 * @param {object} entity 
 */
export function intersectReverse(mainentity, entity) {
	var intersection = {};
	intersection.status = false;
	var mainPos = mainentity.physical;
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
 
export function getDirImage(role, dir) {
	return role + "_" + dir + ".png";
}

/**
 * create missile when firing
 * @param {Object} entity 
 */
export function createMissile(entity) {
	var missile = {
		'id' : Math.floor(Math.random() * 10000),
		'entityId' : entity.id,
		'physical' : {
			'x' : entity.physical.x,
			'y' : entity.physical.y,
			'color' : entity.physical.color,
			'direction' : entity.physical.direction,
			'w' : 10,
			'h' : 5
		}
	};
	return missile;
}

/**
 * check if current position is out of bounds
 * @param {object} currentphysical 
 * @param {Number} WIN_W 
 * @param {Number} WIN_H 
 * @param {Number} velX 
 * @param {Number} velY 
 */
export function isOutOfBounds(currentphysical, WIN_W, WIN_H, velX, velY) {
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
 
/**
 * 
 * @param {Number} velX 
 * @param {String} dir 
 */
export function decreaseVelX(velX, dir) {
	if (dir == global.dirLeft){
		 
		return velX + speedDec  > 0? 0 : velX + speedDec;
	}
	if (dir == global.dirRight)
		return velX - speedDec  < 0? 0 : velX - speedDec;;
	return velX;
}

/**
 * 
 * @param {Number} velY 
 * @param {String} dir 
 */
export function decreaseVelY(velY, dir) {
	if (dir == global.dirUp)
		return velY + speedDec  > 0? 0 : velY + speedDec ;
	if (dir == global.dirDown)
		return velY - speedDec  < 0? 0 : velY - speedDec;
	return velY;
}

/**
 * get velocity object
 * @param {String} dir 
 * @param {Number} vel 
 */
export function getVelocity(dir, vel) {
	var velocity = {};
	velocity.x = 0;
	velocity.y = 0;
	if (dir == global.dirUp)
		velocity.y = -vel;
	if (dir == global.dirDown)
		velocity.y = vel;
	if (dir == global.dirLeft)
		velocity.x = -vel;
	if (dir == global.dirRight)
		velocity.x = vel;

	return velocity;
}


