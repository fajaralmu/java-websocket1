const timeOuts = {};

function incrementHeightAsync(imageElement, maxHeight, incrementBy,
		finalHandler) {
	const id = new Date().getMilliseconds() + "_" + incrementBy + "_"
			+ maxHeight;
	timeOuts[id] = setTimeout(function() {
		incrementImageHeightv2(imageElement, id, maxHeight, incrementBy,
				finalHandler);
	}, 1);
}

function incrementImageHeight(icon, id, maxHeight, incrementBy, finalHandler) {
	if (!incrementBy || incrementBy == 0) {
		incrementBy = 1;
	}
	if (!maxHeight) {
		maxHeight = 300;
	}
	var test = icon.height >= maxHeight;

	if (incrementBy < 0) {
		test = icon.height <= maxHeight;
	}

	icon.height = icon.height + incrementBy;

	if (test) {
		clearTimeout(timeOuts[id]);
		if (finalHandler) {
			finalHandler();
		}
	} else {
		setTimeout(
				function() {
					incrementImageHeight(icon, id, maxHeight, incrementBy,
							finalHandler);
				}, 1);
	}
}

function incrementImageHeightv2(icon, id, maxHeight, incrementBy, finalHandler) {
	animateObjAttribute(icon, "height", id, function(e, height) {
		const newVal = e.height + incrementBy;
		var test = incrementBy < 0 ? e.height <= maxHeight : e.height >= maxHeight;
		if (test) {
			return null;
		}
		return newVal;
	}, finalHandler);
}

function animateObjAttribute(element, attribute, id, modifier, finalHandler) {

	const attributeVal = element.getAttribute(attribute);
	var newValue = modifier(element, attributeVal);

	if (newValue == null) {
		clearTimeout(timeOuts[id]);
		if (finalHandler) {
			finalHandler();
		}
	} else {
		element.setAttribute(attribute, newValue);

		setTimeout(
				function() {
					animateObjAttribute(element, attribute, id, modifier,
							finalHandler);
				}, 1);
	}
}