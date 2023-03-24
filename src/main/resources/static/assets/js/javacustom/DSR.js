Table = null;

$(document).ready(function() {
	Table = $('#getDSR').DataTable({
		"dom": '<"pull-left"f><"pull-right"l>tip',
		columns: [
			{ "data": "empName" },
			{ "data": "project" },
			{ "data": "dSRDate" },
			{ "data": "hrsWorked" },
			{ "data": "dSRReport" },
		],
	});

	populateTable();


	$("#filterDsr").submit(function(e) {
		e.preventDefault();
		var empName = $("#empName").val();
		if (containsNumbers(empName)) {
			$('#result').html('<font color="red">Employee Name not valid. Contains Numerics(E6)</font>');
			return;
		}
		var projectName = $("#projectName").val();
		if (projectName === 'Select Project') {
			$('#result').html('<font color="red">Please Select A Project(E7)</font>');
			return;
		}
		var dsrDate = $("#dsrDate").val();
		var hrs = $("#hrs").val();
		var flg = checkHoursMinutesFormat(hrs);
		console.log('--------------FLAG----------------------'+flg);
		if (!flg) {
			return;
		}
		hrs = $("#hrs").val();
		var taskDescription = $("#taskDescription").val();
		//alert("Hello");

		var json = {
			"empName": empName,
			"projectName": projectName,
			"dsrDate": dsrDate,
			"hrs": hrs,
			"taskDescription": taskDescription,

		};

		console.log(json);

		$.ajax({
			url: "addDSR",
			type: "post",
			contentType: "application/json",
			data: JSON.stringify(json),
			dataType: 'json',
		}).done(function(result) {
			console.log("------------------result-------------------------");
			console.log(result);
			$('#result').html(result[0].msg);
			$("#filterDsr").trigger("reset");
			//location.reload();
			//Table.clear().draw();
			//Table.rows.add(result).draw();
			populateTable()

		}).fail(function(jqXHR, textStatus, errorThrown) {
			$('#result').html("Error");
			// needs to implement if it fails
			console.log(jqXHR);
			console.log(textStatus);
			console.log(errorThrown);
		})
	});
});

function formatDatePlain(date) {
	return (
		[
			padTo2Digits(date.getMonth() + 1),
			padTo2Digits(date.getDate()),
			date.getFullYear(),
		].join('/')
	);
}

function populateTable() {
	$.ajax({
		url: "fetchdsr",
		type: "get",
		contentType: "application/json",
		dataType: 'json',
	}).done(function(result) {
		console.log("------------------result----all---------------------");
		console.log(result.length);
		for (var i = 0; i < result.length; i++) {
			var int_sch_date = result[i].dSRDate;
			//console.log(int_sch_date);
			var withoutOffset = moment(int_sch_date).local().format("MM/DD/YYYY");
			const d = new Date(withoutOffset);
			//var pdate = "-Scheduled On : " + formatDateAndTime(d);
			result[i].dSRDate = formatDatePlain(d);
		}
		Table.clear().draw();
		Table.rows.add(result).draw();

	}).fail(function(jqXHR, textStatus, errorThrown) {
		// needs to implement if it fails
		console.log(jqXHR);
		console.log(textStatus);
		console.log(errorThrown);
	});
}

function containsNumbers(str) {
	return /\d/.test(str);
}