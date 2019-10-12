var intersectionInfo = "";

function getPixel(img, x, y) {
   var canvas = document.createElement('canvas');
  var context = canvas.getContext('2d');
  context.drawImage(img, 0, 0);
  return context.getImageData(x, y, 1, 1).data;
}



function intersect(mainuser, user) {
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
	// console.log("MAIN",mainPos);
	// console.log("USER",userPos);
	let cond1 = false;
	let cond2 = false;
	let cond3 = false;
	let cond4 = false;

	if (userX >= mainX && mainX + mainW >= userX) {
		// console.log("1");
		if (userY >= mainY && mainY + mainH >= userY) {
			// console.log("2");
			cond1 = true;
			intersectionInfo = "COND1";
		}
	}
	if (mainX >= userX && userX + userW >= mainX) {
		// console.log("3");
		if (mainY >= userY && userY + userH >= mainY) {
			// console.log("-----4");
			cond2 = true;
			intersectionInfo = "COND2";
		}
	}
	if (mainX <= userX && mainX + mainW >= userX) {
		if (mainY >= userY && userY + userH >= mainY) {
			cond3 = true;
			intersectionInfo = "COND3";
		}
	}
	if (userX <= mainX && userX + userW >= mainX) {
		if (userY >= mainY && mainY + mainH >= userY) {
			cond4 = true;
			intersectionInfo = "COND4" +(userX*1 + userW*1)*1+ ":"+mainX;
		}
	}
	if (cond1 || cond2 || cond3 || cond4) {
		return true;
	}

	return false;
}

function getDirImage(role,dir) {
	return role+"_"+dir+".png"; 
}

function createMissile(user) {
	var missile = {
		'id' : Math.floor(Math.random() * 10000),
		'userId' : user.id,
		'entity' : {
			'x' : user.entity.x,
			'y' : user.entity.y,
			'color' : user.entity.color,
			'direction' : user.entity.direction,
			'w' : 10,
			'h' : 5
		}
	};
	return missile;
}

function isOutOfBounds(currentEntity, WIN_W, WIN_H, velX, velY) {
	if (currentEntity.x + currentEntity.w + velX > WIN_W) {
		return true;
	} else if (currentEntity.y + currentEntity.h + velY > WIN_H) {
		return true;
	} else if (currentEntity.x + velX < 0) {
		return true;
	} else if (currentEntity.y + velY < 0) {
		return true;
	}
	return false;
}

function getVelocity(dir, vel) {
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