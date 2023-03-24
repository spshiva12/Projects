function padTo2Digits(num) {
	return num.toString().padStart(2, '0');
}


function formatDate(date) {
	return (
		[
			padTo2Digits(date.getMonth() + 1),
			padTo2Digits(date.getDate()),
			date.getFullYear(),
		].join('/') +
		' ' +
		[
			padTo2Digits(date.getHours()),
			padTo2Digits(date.getMinutes()),
			padTo2Digits(date.getSeconds()),
		].join(':')
	);
}

function formatDateAndTime(date) {
	var hours = date.getHours();
	var minutes = date.getMinutes();
	var ampm = hours >= 12 ? 'pm' : 'am';
	hours = hours % 12;
	hours = hours ? hours : 12; // the hour '0' should be '12'
	minutes = minutes < 10 ? '0' + minutes : minutes;
	var strTime = ", " + hours + ':' + minutes + ' ' + ampm;
	return [
		padTo2Digits(date.getMonth() + 1),
		padTo2Digits(date.getDate()),
		date.getFullYear(),
	].join('/') + strTime;
}

function checkHoursMinutesFormat(hrs) {
	console.log("Hours Before Processing : " + hrs.length);
	if (!hrs.includes(":")) {
		if (hrs.length == 0) {
			hrs = "0:00";
		}
		else if (hrs.length == 1) {
			hrs = "0" + hrs;
			hrs = hrs + ":00";
		}
		else if (hrs.length == 2) {
			hrs = hrs + ":00";
		}
		else if (hrs.length == 3) {
			if (hrs.includes(":") && hrs.endsWith(":")) {
				hrs = hrs + "00";
			}
		}
	} else {
		if (hrs.length == 0) {
			hrs = "0:00";
		}
		else if (hrs.length == 1) {
			hrs = "0" + hrs;
			hrs = hrs + ":00";
		}
		else if (hrs.length == 2) {
			hrs = hrs + ":00";
		}
		else if (hrs.length == 3) {
			if (hrs.includes(":") && hrs.endsWith(":")) {
				hrs = hrs + "00";
			}
		}
		else if (hrs.length == 4) {
			if (hrs.includes(":")) {
				var n = hrs.split(":");
				if (n[0].length == 1) {
					hrs = "0" + hrs;
				}
			}
		}
	}
	console.log("Hours After Processing : " + hrs.length);
	$("#hrs").val(hrs);
	if (!hrs.includes(":")) {
		$('#result').html('<font color="red">Worked Hours not in Specified Format(E1)</font>');
		return false;
	}
	var n = hrs.split(":");
	if (n.length != 2) {
		$('#result').html('<font color="red">Worked Hours not in Specified Format(E2)</font>');
		return false;
	} else {
		console.log("Checking--1");
		console.log(parseInt(n[0]));
		console.log(parseInt(n[1]));
		console.log(typeof n[0]);
		console.log(typeof n[1]);
		console.log("End Checking--1");

		if (!(n[0].length == 2 && typeof parseInt(n[0]) == 'number')) {
			$('#result').html('<font color="red">Worked Hours not in Specified Format(E3)</font>');
			return false;
		}
		if (!(n[1].length == 2 && typeof parseInt(n[1]) == 'number')) {
			$('#result').html('<font color="red">Worked Hours not in Specified Format(E4)</font>');
			return false;
		}
		if ((parseInt(n[1]) > 59)) {
			$('#result').html('<font color="red">Worked Hours not in Specified Format. Minutes Exceeding(E5)</font>');
			return false;
		}
		if (isNaN((n[0]))) {
			$('#result').html('<font color="red">Worked Hours not in Specified Format(E8)</font>');
			return false;
		}
		if (isNaN((n[1]))) {
			$('#result').html('<font color="red">Worked Hours not in Specified Format(E9)</font>');
			return false;
		}
	}
	return true;
}