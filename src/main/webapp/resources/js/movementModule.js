import * as playerModule from './playerModule.js'

export function getLayoutRole(role, obj) {
	switch (role) {
		case obj.roleRight:
			return "RIGHT";
		case obj.roleLeft:
			return "LEFT";
		case obj.roleUp:
			return "UP";
		case obj.roleDown:
			return "DOWN";
		case obj.roleFinish:
			return "FINISH";
		default:
			break;
	}
	return "Not Circuit Role";
}

export function intersectLayout(currentEntity, obj, layouts) {
	/*************CHECK INTERSECT LAYOUT****************/
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
			if (layoutRole != 102) {
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

export function intersectPlayer(mainEntity, entities) {
	let isIntersectEntity = false;
	let intersectionObject = {};
	let intersectionObjectReverse = {};

	/*************CHECK INTERSECT PLAYER****************/
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