import * as playerModule from './playerModule.js'
import { Game } from './game.js';

/**
 * just for info printed out
 * @param {Number} role 
 * @param {Game} obj 
 */
export function getLayoutRole(role, obj) {
	switch (role) {
		case obj.roleRight:
			return "right";
		case obj.roleLeft:
			return "left";
		case obj.roleUp:
			return "up";
		case obj.roleDown:
			return "down";
		case obj.roleFinish:
			return "finish";
		default:
			break;
	}
	return "Not Circuit Role";
}

/**
 * check if entity intersects one of layouts
 * @param {object} currentEntity 
 * @param {Game} obj 
 * @param {Array} layouts 
 */
export function intersectLayout(currentEntity, obj, layouts) {
	 
	let layoutItemIntersects = {};
	let intersectLayout = false;
	let intersection = {};
	let intersectionReverse = {};
	for (let i = 0; i < layouts.length; i++) {
		const layoutItem = layouts[i];
		if (!intersectLayout && playerModule.intersect(currentEntity, layoutItem).status == true) {
			intersection = playerModule.intersect(currentEntity, layoutItem);
			intersectionReverse = playerModule.intersectReverse(currentEntity, layoutItem);

			const layoutRole = layoutItem.physical.role;
			obj.currentLayoutId = layoutItem.id;
			if (layoutRole !=  obj.roleGeneralLayout){// "ROLE_LAYOUT_1") {
				printCircuitInfo(layoutRole + ":" + getLayoutRole(layoutRole, obj) + ", name: " + layoutItem.name);
			} else {
				intersectLayout = true;
			}

			layoutItemIntersects = layoutItem;
		}
	}
	if (intersectLayout) { printInfo("WILL intersect layout :" + playerModule.intersectionInfo + JSON.stringify(layoutItemIntersects)); }
	else { printInfo("NO INTERSECTION"); }

	if (intersectLayout
		&& (intersection.direction == currentEntity.physical.direction || intersectionReverse.direction == currentEntity.physical.direction)) {
		printInfo("intersect layout :" + playerModule.intersectionInfo + JSON.stringify(layoutItemIntersects));
		return true;
	}
	return false;
}
/**
 * check if main entity intersects one of entities
 * @param {object} mainEntity 
 * @param {Array} entities 
 */
export function intersectPlayer(mainEntity, entities) {
	let isIntersectEntity = false;
	let intersectionObject = {};
	let intersectionObjectReverse = {};
 
	for (let i = 0; i < entities.length; i++) {
		const theEntity = entities[i];
		const isPlayer2 = (theEntity.id == mainEntity.id);
		if (!isPlayer2) {
			if (!isIntersectEntity && playerModule.intersect(mainEntity, theEntity).status == true) {
				intersectionObject = playerModule.intersect(mainEntity, theEntity);
				intersectionObjectReverse = playerModule.intersectReverse(mainEntity, theEntity);
				isIntersectEntity = true;
			}
		}
	}

	if (isIntersectEntity
		&& (intersectionObject.direction == mainEntity.physical.direction || 
			intersectionObjectReverse.direction == mainEntity.physical.direction)) {

		return true;
	}

	return false;
}

export function getCurrentDirectionY(obj, currentphysical){
	let latestDirectionV = obj.getLatestStoppingDirV();
	let theDirY = currentphysical.direction;
	if (theDirY != obj.dirUp && theDirY != obj.dirDown && latestDirectionV != null) {
		if (latestDirectionV == obj.dirUp || latestDirectionV == obj.dirDown) {
			theDirY = latestDirectionV;
		}
	} 
	return theDirY;
}

export function getCurrentDirectionX(obj, currentphysical){ 
	let latestDirectionH = obj.getLatestStoppingDirH();
	let theDirX = currentphysical.direction; 
	 
	if (theDirX != obj.dirRight && theDirX != obj.dirLeft && latestDirectionH != null) {
		if (latestDirectionH == obj.dirRight || latestDirectionH == obj.dirLeft) {
			theDirX = latestDirectionH;
		}
	}
	return theDirX;
}

/**
 * check if missile intersect one of players
 * @param {missile} missile 
 * @param {entity} entity 
 * @param {Array} entities 
 */
export function missileIntersectsPlayer(missile, entity, entities){
	let missileIntersects = false;
	for (let x = 0; x < entities.length; x++) {
		//off course if not main player
		if (entities[x].id != entity.id) {
			if (playerModule.intersect(missile, entities[x]).status == true) { 
				missileIntersects = true;
				console.debug("MISSILE INTERSECT PLAYER :",entities[x]);
			}
		}
	}
	return missileIntersects;
}

/**
 * create missile object when firing
 * @param {object} missile 
 * @param {Array} layouts 
 */
export function missileIntersectsLayout(missile,layouts, layoutRole){
	let missileIntersects = false;
	//check if missile intersects layout
	for (let x = 0; x <  layouts.length; x++) {
		if (layouts[x].physical.role == layoutRole && playerModule.intersect(missile, layouts[x]).status == true) {
			 
			missileIntersects = true;
		}
	}
	return missileIntersects;
}
