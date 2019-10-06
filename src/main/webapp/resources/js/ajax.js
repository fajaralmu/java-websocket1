function postReq(url, param, process, callback){
	var request = new XMLHttpRequest();
	var param_to_send = "process="+process;
	if(param!=""){
		param_to_send=param+"&"+param_to_send;
	}
	
	//if (!confirm("Lanjutkan Operasi " + param_to_send + "  ?"))
	//	return;
	
	infoLoading();
	request.open("POST", url, true);
	request.setRequestHeader("Content-type",
			"application/x-www-form-urlencoded");
	request.onreadystatechange = function() {
		if (this.readyState == this.DONE && this.status == 200) {
			console.log(this.responseText);
			if (this.responseText != null) {
				callback(this.responseText);
				infoDone();
			}
			
		}
	}
	request.send(param_to_send);
}
