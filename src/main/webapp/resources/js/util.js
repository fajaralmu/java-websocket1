
function listDataStok(ArrayData, table, halamanawal, jumlah, stokRinci) {
	console.log("halaman awal:" + halamanawal);
	console.log("jml per halaman:" + jumlah);

	var keyNames = Object.keys(ArrayData[0]);
	var rowhtml = "";
	var cb_sort = document.getElementById("cb_sort");
	cb_sort.innerHTML = "";
	for (var i = 0; i < keyNames.length; i++) {
		/*------------------CEK KADALUARSA--------------------*/
		if (keyNames[i] == "statuskadaluarsa")
			continue;
		rowhtml += "<th>" + capitalize1st(keyNames[i]) + "</th>";
		var option = document.createElement("option");
		option.setAttribute("value", keyNames[i]);
		option.innerHTML = keyNames[i];
		cb_sort.append(option);
	}
	table.innerHTML = "<tr class=\"th\" >" + "<th>No</th>" + rowhtml + "</tr>";
	var hampirED = 0, ED = 0;
	for (var i = halamanawal; i < halamanawal + jumlah; i++) {
		if (ArrayData[i] == null)
			continue;
		var row = document.createElement("tr");
		row.setAttribute("class", "rowedit");
		
		if(stokRinci){
			row.setAttribute("name", "kodestok");
			row.setAttribute("id", ArrayData[i]["id"]);
		}else{
			row.setAttribute("name", "kodeobat");
			row.setAttribute("id", ArrayData[i]["kodeobat"]);
		}
		
		row.innerHTML = "<td>" + (i + 1) + "</td>";
		if (i % 2 == 0)
			row.setAttribute("style", "background-color:rgba(222,222,191,0.4)");
		for (var r = 0; r < Object.keys(ArrayData[i]).length; r++) {

			var value = ArrayData[i][keyNames[r]];

			/*------------------CEK KADALUARSA--------------------*/

			if (keyNames[r] == "statuskadaluarsa") {
				if (value == 0) {
					row.setAttribute("style",
							"background-color:rgba(255,200,70,0.6); color:red");
					hampirED += ArrayData[i]["jumlah"];
				} else if (value == -1) {
					row
							.setAttribute("style",
									"background-color:rgba(255,20,20,0.6); color:white");
					ED += ArrayData[i]["jumlah"];
				}
				continue;
			} else {
				if (keyNames[r] == "stokaman")
					if ((ArrayData[i]["jumlah"]) - value <= 0)
						row
								.setAttribute("style",
										"background-color:rgba(255,200,70,0.6); color:red");

				var elem = document.createElement("td");
				elem.innerHTML = value;
				row.appendChild(elem);
			}
		}
		table.appendChild(row);

	}
	if (hampirED > 0 || ED > 0)
		alert("PERHATIAN!\nObat KADALUARSA: " + ED + "Mendekati Kadaluarsa: "
				+ hampirED);
}

function mulaiDengan(kalimat, test) {
	if (kalimat.length < test.length)
		return false;
	for (var i = 0; i < test.length; i++) {
		if (kalimat && i <= kalimat.length
				&& test[i].toLowerCase() != kalimat[i].toLowerCase())
			return false;
	}
	return true;
}

function cariTransaksiDgnRincianObat(t, key, val, sesuai) {
	for (var a = 0; a < t['aliranobat'].length; a++) {
		var ao = t['aliranobat'][a];
		var prop = ao[key];
		if (typeof (prop) != "string") {
			prop = ao[key].toString();
			val = val.toString();
		}
		if (sesuai == 1) {
			if (mulaiDengan(prop.toLowerCase(), val.toLowerCase())) {
				console.log(prop + "=" + val + " in " + t["kodetransaksi"]);
				return true;
				//break;
			}

		} else if (sesuai == 2) {
			if (prop.toLowerCase() == val.toLowerCase()) {
				console.log(prop + "=" + val + " in " + t["kodetransaksi"]);
				return true;
				//break;
			}
		} else {
			if (prop.toLowerCase().includes(val.toLowerCase())) {
				console.log(prop + "=" + val + " in " + t["kodetransaksi"]);
				return true;
//				break;
			}
		}
	}
	return false;
}

function prosesedit(url, param, proses, postprocess, callback) {
	var request = new XMLHttpRequest();
	if (!confirm("Lanjutkan Operasi " + param + "  ?"))
		return;
	infoLoading();
	request.open("POST", url, true);
	request.setRequestHeader("Content-type",
			"application/x-www-form-urlencoded");
	request.onreadystatechange = function() {
		if (this.readyState == this.DONE && this.status == 200) {
			console.log(this.responseText);
			if (this.responseText != null) {
				if (this.responseText == "true") {
					if (proses == "edit")
						alert("data berhasil diperbarui!");
					else
						alert("data berhasil ditambah!");
					var element = document.getElementById('formedit');
					if (typeof (element) != 'undefined' && element != null) {
						tutup("formedit");
					}

					callback(true);
				} else if (this.responseText == "false") {
					alert("operasi gagal!" + this.responseText);
				}
			} else if (this.status != 200) {
				alert("server error\n" + "Mohon muat ulang! " + this.status);
			}
			infoDone();
		}
	};
	if (proses == "edit")
		request.send("process=" + postprocess + "&" + param);
	else if (proses == "tambah")
		request.send("process=" + postprocess + "&" + param);
}

function listEntitas(url, proses, callback, pakaifilter, jumlah_tampil) {
	infoLoading();
	var arr = new Array();
	var request = new XMLHttpRequest();
	request.open("POST", url, true);
	request.setRequestHeader("Content-type",
			"application/x-www-form-urlencoded");
	request.onreadystatechange = function() {
		if (this.readyState == this.DONE && this.status == 200) {
			var respon = this.responseText;
			if (respon != null) {
				var arr = JSON.parse(respon);
				if (typeof (arr) == "object") {
					// console.log(arr);
					callback(arr, pakaifilter, 0, jumlah_tampil);
				} else {
					alert("proses error\n" + "Mohon muat ulang! " + this.status);
					console.log("tipe: " + typeof (arr));
					console.log(arr);
				}
			} else {
				alert("tidak ada data");
			}
		} else if (this.status != 200) {
			alert("server error\n" + "Mohon muat ulang! " + this.status);
		}
		infoDone();
	};
	request.send("process=" + proses);

}

function proseseditGET(url, callback) {
	var request = new XMLHttpRequest();
	infoLoading();
	request.open("GET", url, true);
	request.setRequestHeader("Content-type",
			"application/x-www-form-urlencoded");
	request.onreadystatechange = function() {
		if (this.readyState == this.DONE && this.status == 200) {
			var respon = this.responseText;
			var json = JSON.parse(respon);
			if (respon != null) {
				if (respon == "true") {
					alert("operasi berhasil");
				} else if (respon == "false") {
					alert("operasi gagal!" + respon);
				} else if (typeof (json) == "object") {
					var mm = json;
					console.log("status: " + mm["status"]);
					if (mm["status"] == "true") {
						alert("operasi berhasil");
						callback(mm["kode"]);
					} else {
						var out = "\n[";
						loop: for (var i = 0; i < mm.length; i++) {
							var obj = mm[i];
							for (let n in obj) {
								out += n + " : " + obj[n] + "\n";
								continue loop;
							}
						}
						out += "]";
						alert("Gagal menghapus obat, Anda harus menghapus entitas\n yang berelasi lainnya : "
								+ out);
					}
				}
			} else if (this.status != 200) {
				alert("server error\n" + "Mohon muat ulang! " + this.status);
			}
			infoDone();
		}
	};
	request.send();
}

function proseshapus(url, param, postprocess, callback) {
	var c = confirm("Yakin akan menghapus data? " + param);
	if (c == false) {
		return;
	}
	infoLoading();
	var request = new XMLHttpRequest();
	request.open("POST", url, true);
	request.setRequestHeader("Content-type",
			"application/x-www-form-urlencoded");
	request.onreadystatechange = function() {
		if (this.readyState == this.DONE && this.status == 200) {
			if (this.responseText != null) {
				console.log("Response text: ");
				console.log(this.responseText);
				if (this.responseText == "true") {
					alert("data terhapus");
					var element = document.getElementById('formopsi');
					if (typeof (element) != 'undefined' && element != null) {
						tutup("formedit");
					}
					callback();
				} else if (this.responseText == "false") {
					alert("gagal menghapus data: " + this.responseText);
				} else {
					var mm = JSON.parse(this.responseText);
					console.log(mm);
					var out = "\n[";
					loop: for (var i = 0; i < mm.length; i++) {
						var obj = mm[i];
						for (n in obj) {
							out += n + " : " + obj[n] + "\n";
							continue loop;
						}
					}
					out += "]";
					alert("Gagal menghapus data, Anda harus menghapus entitas\n yang berelasi lainnya : "
							+ out);
				}
			} else if (this.status != 200) {
				alert("server error\n" + "Mohon muat ulang! " + this.status);
			}
			infoDone();
		}
	};
	request.send("process=" + postprocess + "&" + param);
}

function tutup(id) {
	document.getElementById(id).style.display = "none";
}

function halamanOpsi(kode) {
	document.getElementById("formopsi").style.display = "none";
	var btn_edit = document.getElementById("opsiedit");
	var btn_hps = document.getElementById("opsihapus");
	btn_edit.setAttribute("onclick", "halamanEdit('" + kode + "')");
	btn_hps.setAttribute("onclick", "hapus('" + kode + "')");
	document.getElementById("formopsi").style.display = "block";

}

function maxInt(array) {
	var max = 0;
	for (var i = 0; i < array.length; i++) {
		if (array[i] > max)
			max = array[i];
	}
	return max;

}


function infoLoading() {
	document.getElementById("info").innerHTML = ""
			+ ""
			+ "<img width='60px' src=\"/puskesmas/res/img/loading-disk.gif\"/><br/>"
			+ "Mohon menunggu...";
}

function infoDone() {
	document.getElementById("info").innerHTML = "";
}
/***GRAFIK***/
function gambarGrafik(judul, label, list) {
	var judul_grafik = document.getElementById("judul_grafik");
	judul_grafik.innerHTML = judul;
	var grafik = document.getElementById("grafik");
	var canvas = document.querySelector("canvas");
	canvas.setAttribute("id", "kanvas");
	var cx = canvas.getContext("2d");

	var w = 600, h = 300;
	cx.fillStyle = "white";
	cx.fillRect(0, 0, w, h);
	cx.fillStyle = "green";
	cx.beginPath();
	var x = 0, y = h - 100;
	var max = maxInt(list);
	var gap = w / (list.length + 1);
	var tinggiGrafik = h - 100;
	x += gap;
	for (var i = 0; i < list.length; i++) {
		y = (h - 50) - (list[i] / max * tinggiGrafik);
		if (i == 0)
			cx.moveTo(x, y);
		cx.lineTo(x, y);
		cx.fillText(list[i], x, y);
		cx.fillText(label[i], x, h - 35);
		x += gap;
	}
	cx.stroke();
	grafik.style.display = "block";

	var imgURL = canvas.toDataURL();
	var div = document.getElementById("gambar_grafik");
	div.innerHTML = '<img style="border: solid 2px orange" src="' + imgURL
			+ '">';
	document.getElementById("btn_cetak").setAttribute("onclick",
			"cetakPDF('gambar_grafik','" + judul + "')");
}

function adaItemDiList(param, val, list) {
	for (var i = 0; i < list.length; i++) {
		var item = list[i];
		if (item[param] == val) {
			return true;
			break;
		}
	}
	return false;
}

function dapatkanObjek(param, val, list) {
	var item = null;
	for (var i = 0; i < list.length; i++) {
		item = list[i];
		if (item[param] == val) {
			break;
		}
	}
	return item;
}

/****CETAK****/
function cetakBukti(kode, url, callback_url) {
	var param = "kodetransaksi=" + kode;
	prosesCetak(url, param, "buktitransaksi", callback_url, true);
}

function cetakLabel(kode, url, callback_url) {
	var param = "kodetransaksi=" + kode;
	prosesCetak(url, param, "labelstok", callback_url, true);
}

function cetakPDF(id, juduldefault) {
	var judul = prompt("Masukkan judul", juduldefault);
	if (judul == null)
		judul = juduldefault;
	if (!confirm("Mencetak laporan dengan judul " + judul + " ?"))
		return;

	var konten = document.getElementById(id).innerHTML;
	var printWindow = window.open('', '');
	printWindow.document.write('<html><head><title>' + judul + '</title>');
	printWindow.document
			.write('<style>'
					+ 'table{table-layout: fixed; width: 90%; margin:auto; font-size:12px}'
					+ '.th td,th{text-align:center;border-bottom: solid 1px; border-top: solid 1px}'
					+ 'td{text-align: center;	}' + '</style>');
	printWindow.document.write('</head><body style="float:center">');
	printWindow.document
			.write('<div id="kop" ">'
					+ '<img style="float:left; margin-left:10%" width="7%" src="/puskesmas/res/img/kbmlogo.png"/>'
					+ '<div style="margin-right:15%;text-align:center;"><span style="font-size:12px; padding:0">PEMERINTAH KABUPATEN KEBUMEN</span><br/>'
					+ '<b style="font-size:14px; padding:0">DINAS KESEHATAN</b><br/>'
					+ '<b style="font-size:14px; padding:0">UPTD UNIT PUSKESMAS SEHAT</b><br/>'
					+ '<i style="font-size:12px; padding:0">Alamat : Desa Village Kec.Municipality.Kab.Kebumen.Telp.(0123)334567Kebumen</i><br/>'
					+ '<span style="font-size:12px; padding:0; margin:auto">E-mail : sehat.puskesmas@yahoo.com</span></div>'
					+ '<hr style="border-top: 4px double; border-color:black;background-color:black;  padding:0">'
					+ '</div>');
	printWindow.document.write('<div style="margin:auto; text-align:center">');
	printWindow.document.write('<p align="center"><b>' + judul + '</b></p>');
	printWindow.document.write(konten);
	printWindow.document
			.write('<hr><p align="center">Puskesmas Sehat 2018</p>');
	printWindow.document.write('</div>');
	printWindow.document.write('</body></html>');
	printWindow.document.close();
	printWindow.setTimeout(function() {
		printWindow.print();
		printWindow.close();
	}, 200);

};

function ExportToExcel(mytblId) {
	var htmltable = document.getElementById(mytblId);
	var html = htmltable.outerHTML;
	window.open('data:application/vnd.ms-excel,' + encodeURIComponent(html));
}

var oke = false;
var st = 0;

function prosesCetak(url, param, process, docURL, openFile) {
	infoLoading();
	var request = new XMLHttpRequest();
	request.open("POST", url, true);
	request.setRequestHeader("Content-type",
			"application/x-www-form-urlencoded");
	request.onreadystatechange = function() {
		if (this.readyState == this.DONE && this.status == 200) {
			if (this.responseText != null) {
				console.log(this.responseText);
				if (this.responseText == "false") {
					alert("operasi gagal");
				} else {
					loadUrl(docURL + "/" + this.responseText);
				}
			} else {
				alert("tidak ada data");
			}
		} 
		infoDone();
	};
	request.send("process=" + process + "&" + param);
}


function loadUrl(newLocation) {
	window.open(newLocation, "pkm sehat");
	return false;
}

function maxObj(list, val) {
	var max = list[0];
	// console.log("max fnc: "+max[val]);
	for (var i = 0; i < list.length; i++) {
		if (typeof (list[0][val]) == "number") {
			if (list[i][val] > max[val])
				max = list[i];
		} else {
			if (list[i][val].toLowerCase() > max[val].toLowerCase())
				max = list[i];
		}
	}
	return max;
}

function minObj(list, val) {
	var min = list[0];
	// console.log("max fnc: "+max[val]);
	for (var i = 0; i < list.length; i++) {
		if (typeof (list[0][val]) == "number") {
			if (list[i][val] < min[val])
				min = list[i];
		} else {
			if (list[i][val].toLowerCase() < min[val].toLowerCase())
				min = list[i];
		}
	}
	return min;
}

function urutkanObj(list_obj, val) {
	console.log("urutan asc berdasarkan: " + val);
	var list_urut = new Array();
	var map_obj = new Array();
	var map_obj_urut = new Array();
	var map_obj_urut_final = new Array();
	console.log(list_obj.length);

	var min = maxObj(list_obj, val);
	console.log(val + " is typeof " + typeof (min[val]));

	var key = 0, key_map_urut = 0;
	for (var i = 0; i < list_obj.length; i++) {
		map_obj[i] = list_obj[i];
		// console.log(map_obj[i][val]);
	}

	console.log("MIN: " + min[val]);
	console.log("L :" + map_obj.length);
	for (var i = 0; i < map_obj.length; i++) {
		loop: for (var j = 0; j < map_obj.length; j++) {
			if (j in map_obj_urut) {
				continue;
			}
			if (typeof (map_obj[j][val]) == "number") {
				if (map_obj[j][val] <= min[val]) {
					min = map_obj[j];
					key = j;
				} else
					continue loop;
			} else {
				if (map_obj[j][val].toLowerCase() <= min[val].toLowerCase()) {
					min = map_obj[j];
					key = j;
				} else
					continue loop;
			}
		}
		map_obj_urut_final[key_map_urut] = min;
		map_obj_urut[key] = min;
		key_map_urut++;
		min = maxObj(list_obj, val);
	}
	for (var i = 0; i < map_obj_urut_final.length; i++) {
		list_urut[i] = map_obj_urut_final[i];
	}

	/*
	 * for (var i = 0; i < list_urut.length; i++) console.log(list_urut[i]);
	 */
	// console.log("LIST_URUT: ");
	// console.log(list_urut);
	return list_urut;
}

function urutkanObjDESC(list_obj, val) {
	console.log("urutan desc berdasarkan: " + val);
	var list_urut = new Array();
	var map_obj = new Array();
	var map_obj_urut = new Array();
	var map_obj_urut_final = new Array();
	console.log(list_obj.length);

	var max = minObj(list_obj, val);

	console.log(val + " is typeof " + typeof (max[val]));

	var key = 0, key_map_urut = 0;
	for (var i = 0; i < list_obj.length; i++) {
		map_obj[i] = list_obj[i];
		// console.log(map_obj[i][val]);
	}

	console.log("MAX: " + max[val]);
	console.log("L :" + map_obj.length);
	for (var i = 0; i < map_obj.length; i++) {
		loop: for (var j = 0; j < map_obj.length; j++) {
			if (j in map_obj_urut) {
				continue;
			}
			if (typeof (map_obj[j][val]) == "number") {
				if (map_obj[j][val] >= max[val]) {
					max = map_obj[j];
					key = j;
				} else
					continue loop;
			} else {
				if (map_obj[j][val].toLowerCase() >= max[val].toLowerCase()) {
					max = map_obj[j];
					key = j;
				} else
					continue loop;
			}
		}
		map_obj_urut_final[key_map_urut] = max;
		map_obj_urut[key] = max;
		key_map_urut++;
		max = minObj(list_obj, val);
	}
	for (var i = 0; i < map_obj_urut_final.length; i++) {
		list_urut[i] = map_obj_urut_final[i];
	}

	/*
	 * for (var i = 0; i < list_urut.length; i++) console.log(list_urut[i]);
	 */
	// console.log("LIST_URUT: ");
	// console.log(list_urut);
	return list_urut;
}

function cariEntitas(input_id, filter_id, list_entitas, callback, startwith,
		jumlah_tampil) {
	var filter = document.getElementById(input_id).value;
	var kriteria = document.getElementById(filter_id).value;
	var list_filter = new Array();
	// console.log(filter + " " + listpfilter.length);
	for (var i = 0; i < list_entitas.length; i++) {
		var ip = list_entitas[i];
		var filterstring = ip[kriteria];
		if (startwith) {
			if (mulaiDengan(filterstring, filter))
				list_filter.push(ip);
		} else {
			if (filterstring.toLowerCase().includes(filter.toLowerCase()))
				list_filter.push(ip);
		}
	}
	callback(list_filter, true, 0, jumlah_tampil);
	return list_filter;
}



function dapatkanentitasV2(obj, toShow) {
	if (toShow.length > 0) {
		for (var j = 0; j < toShow.length; j++) {
			console.log(toShow[j]);
			if (document.getElementById(toShow[j]).nodeName == "INPUT") {
				if (document.getElementById(toShow[j]).getAttribute("type") == "checkbox") {
					document.getElementById(toShow[j]).checked = obj[toShow[j]] == 1 ? true
							: false;
				} else
					document.getElementById(toShow[j]).value = obj[toShow[j]];
			} else
				document.getElementById(toShow[j]).innerHTML = obj[toShow[j]];
		}
	}

}

function isiTabelTransaksi(list_entitas, idtabel, names, opsi) {
	var tabel = document.getElementById(idtabel);
	tabel.innerHTML = "";
	if (list_entitas.length == 0) {
		return;
	}

	var keyNames = names.length == 0 ? Object.keys(list_entitas[0]) : names;
	for (var i = 0; i < list_entitas.length; i++) {
		var item = list_entitas[i];
		var row = document.createElement("tr");
		var no = document.createElement("td");
		no.innerHTML = i + 1;
		row.appendChild(no);
		for (var j = 0; j < keyNames.length; j++) {
			var td = document.createElement("td");
			td.innerHTML = item[keyNames[j]];
			row.appendChild(td);
		}
		if (opsi) {
			var opsi = document.createElement("td");
			if (typeof (keyNames[0]) == "number")
				opsi.innerHTML = "<button class=\"tombol\" name=\"edit\" onclick=\"halamanEdit("
						+ item[keyNames[0]]
						+ ")\">edit</button> "
						+ "<button class=\"tombol\" name=\"hapus\" onclick=\"hapus("
						+ item[keyNames[0]] + ")\">hapus</button>";
			else
				opsi.innerHTML = "<button class=\"tombol\" name=\"edit\" onclick=\"halamanEdit('"
						+ item[keyNames[0]]
						+ "')\">edit</button> "
						+ "<button class=\"tombol\" name=\"hapus\" onclick=\"hapus('"
						+ item[keyNames[0]] + "')\">hapus</button>";
			row.appendChild(opsi);
		}
		tabel.appendChild(row);
	}

}

function setListEntitas(list, pakaifilter, idtabel, idheader, names, id_cb,
		start, jumlah_tampil) {
	var tabel = document.getElementById(idtabel);
	var tabelheder = document.getElementById(idheader);
	var mulai = start * jumlah_tampil;
	var total = mulai * 1 + jumlah_tampil * 1;
	if (!pakaifilter) {
		mulai = 0;
		total = list.length;
	}
	// console.log("mulai: "+mulai+", Sampai: "+total);
	tabel.innerHTML = "";
	if (list.length == 0) {
		return;
	}
	let main_list = new Array();
	if (!pakaifilter)
		main_list = new Array();
	for (var i = mulai; i < total; i++) {
		if (list[i] == null)
			break;
		var io = list[i];
		if (!pakaifilter)
			main_list.push(io);
		var row = document.createElement("tr");
		if (i % 2 == 0)
			row.setAttribute("style", "background-color:rgba(222,222,191,0.4)");
		var no = document.createElement("td");
		no.style.width = "7%";
		no.innerHTML = i + 1;
		row.appendChild(no);
		if (names.length > 0) {
			for (var j = 0; j < names.length; j++) {
				var e = document.createElement("td");
				e.innerHTML = io[names[j]];
				row.appendChild(e);
			}
		}

		row.setAttribute("onclick", "halamanOpsi('" + io[names[0]] + "')");
		row.setAttribute("class", "rowedit");
		tabel.appendChild(row);
	}

	if (names.length > 0) {
		tabelheder.innerHTML = "";
		var rowheader = document.createElement("tr");
		rowheader.setAttribute("class", "th");
		rowheader.innerHTML = "<td style='width:7%'>No</td>";
		for (var h = 0; h < names.length; h++) {
			var e_th = document.createElement("td");
			e_th.innerHTML = capitalize1st(names[h]);
			rowheader.appendChild(e_th);
		}
		tabelheder.appendChild(rowheader);
	}

	if (id_cb != null && !pakaifilter) {
		var cb_sort = document.getElementById(id_cb);
		cb_sort.innerHTML = "";
		for (var o = 0; o < names.length; o++) {
			var option = document.createElement("option");
			option.setAttribute("value", names[o]);
			option.innerHTML = names[o];
			cb_sort.append(option);
		}
	}
}

function inputEntitasV2(wrapper, list, key, /* index_terpilih, */onClickItem) {
	var pilihan = document.createElement("div");
	pilihan.setAttribute("class", "pilihan");
	wrapper.appendChild(pilihan);
	var keyNames = Object.keys(list[0]);

	for (var i = 0; i < list.length; i++) {
		let txt = document.createElement("div");
		let obj = list[i];
		txt.innerHTML = obj[key];
		txt.setAttribute("class", "pilih");
		/*
		 * if (i == index_terpilih) { txt.style.backgroundColor = "rgb(200, 200,
		 * 200)"; txt.style.color = "white"; }
		 */
		txt.setAttribute("id", obj[keyNames[0]]);
		txt.onclick = function() {
			onClickItem(obj);
			wrapper.innerHTML = "";
			//index_terpilih = -1;
		};
		pilihan.appendChild(txt);
	}

}


/****MISC***/

function addLine(element) {
	var br = document.createElement("br");
	element.appendChild(br);

}

function gantitahun(select_year_dari, select_year_ke) {
	var thn_dari = document.getElementById(select_year_dari);
	var thn_ke = document.getElementById(select_year_ke);
	if (thn_dari.value > thn_ke.value)
		thn_ke.value = thn_dari.value;

}

function resetFields(fields) {
	for (var i = 0; i < fields.length; i++) {
		var fieldName = fields[i];
		if (document.getElementById(fieldName) == null)
			continue;
		var element = document.getElementById(fieldName);

		if (element.nodeName == "INPUT") {
			if (element.getAttribute("type") == "number")
				element.value = 0;
			else if (element.getAttribute("type") == "checkbox") {
				element.checked = false;
			} else
				element.value = "";
		} else
			element.innerHTML = "";
	}

}

function tidakFokus(input, listpilihan) {
	if (document.getElementById(input).value == "")
		document.getElementById(listpilihan).innerHTML = "";
}

function cbAppend(min, max, cb) {
	for (var i = min; i <= max; i++) {
		var o = document.createElement("option");
		o.setAttribute("value", i);
		o.innerHTML = i;
		cb.append(o);
	}

}
function capitalize1st(string) {
	return string.charAt(0).toUpperCase() + string.slice(1);
}