


function infoLoading() {
	document.getElementById("info").innerHTML = ""
			+ ""
			+ "<img width='60px' src=\"/websocket1/res/img/loading-disk.gif\"/><br/>"
			+ "Mohon menunggu...";
}

function infoDone() {
	document.getElementById("info").innerHTML = "";
}

function _byId(id){
	if(id==null || id == ""){
		console.warn("ID IS EMPTY");
	}
	return document.getElementById(id);
}


function sleep(ms) {
	  return new Promise(resolve => setTimeout(resolve, ms));
	}

	 