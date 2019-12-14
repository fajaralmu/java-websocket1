export function postReq(url, param,   callback, object){
	var request = new XMLHttpRequest(); 
	request.open("POST", url, true);
	request.setRequestHeader("Content-type",
			"application/x-www-form-urlencoded");
	request.onreadystatechange = function() {
		if (this.readyState == this.DONE && this.status == 200) {
			console.log(this.responseText);
			if (this.responseText != null) {
				callback(this.responseText, object); 
			} 
		}
	}
	request.send(param);
}
