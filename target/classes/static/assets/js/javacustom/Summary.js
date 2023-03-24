Table = null;



$(document).ready(function() {
	var fn = 'report';
	Table = $('#getDSRSummary').DataTable({
		//dom: 'lBfrtip',
		"dom": '<"pull-left"f><"pull-right"Bl>tip',
		ordering: false,
		buttons: [
			{
				extend: 'copy',
				filename: fn,
				messageTop: fn,
				title: null,
			},
			{
				extend: 'csv',
				filename: fn,
				messageTop: fn,
				title: null,
			},
			{
				extend: 'excel',
				filename: fn,
				messageTop: fn,
				title: null,
			},
			{
				extend: 'pdf',
				filename: fn,
				messageTop: fn,
				title: null,

			},
			{
				extend: 'print',
				filename: fn,
				messageTop: fn,
				title: '',
			}
		],

		columns: [
			{ "data": "index","width": "10%" },
			{ "data": "ename","width": "50%" },
			{ "data": "eligiblity","width": "20%" },
			{ "data": "bill","width": "20%" },
		],
	});

	var currentYear = new Date().getFullYear()-1;
	var currentMonth = new Date().getMonth();
	var cascadedDropDownMonthId = "#dropDownYearMonth";

	//Adding Last 10 Years to Year Drop Down
	var c = 0;
	for (var i = currentYear; i < currentYear + 4; i++) {
		console.log(c+"----"+i.toString());
		if (c == 1) {
			$("#dropDownYear1").append('<option value="' + i.toString() + '" selected>' + i.toString() + '</option>');
		} else {
			$("#dropDownYear1").append('<option value="' + i.toString() + '">' + i.toString() + '</option>');
		}
		c++;

	}

	$(cascadedDropDownMonthId).prop("disabled", false);
	//Get Current Year from Dropdown and Converting to Integer for performing math operations
	var currentSelectedYear = new Date().getFullYear();

	//As Index of Javascript Month is from 0 to 11 therefore totalMonths are 11 NOT 12
	var totalMonths = 11;
	if (currentSelectedYear == currentYear) {
		//totalMonths = currentMonth;
	}
	var monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
	//Emptying the Month Dropdown to clear our last values
	$(cascadedDropDownMonthId).empty();

	//$(cascadedDropDownMonthId).append('<option value="-1">-Month-</option>');

	//Appending Current Valid Months
	for (var month = 0; month <= totalMonths; month++) {
		$(cascadedDropDownMonthId).append('<option value="' + (month + 1) + '">' + monthNames[month] + '</option>');
	}

	//Disabling Month Dropdown in case of invalid Selections.
	//$(cascadedDropDownMonthId).prop("disabled", true);

	$("#dropDownYear1").change(function() {

		var currentSelectedValue = $(this).val();

		if (currentSelectedValue == "-1") {
			$(cascadedDropDownMonthId).prop("disabled", true);
		}
		else {
			$(cascadedDropDownMonthId).prop("disabled", false);
			//Get Current Year from Dropdown and Converting to Integer for performing math operations
			var currentSelectedYear = parseInt($(this).val());

			//As Index of Javascript Month is from 0 to 11 therefore totalMonths are 11 NOT 12
			var totalMonths = 11;
			if (currentSelectedYear == currentYear) {
				//totalMonths = currentMonth;
			}
			var monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
			//Emptying the Month Dropdown to clear our last values
			$(cascadedDropDownMonthId).empty();

			//$(cascadedDropDownMonthId).append('<option value="-1">-Month-</option>');

			//Appending Current Valid Months
			for (var month = 0; month <= totalMonths; month++) {
				$(cascadedDropDownMonthId).append('<option value="' + (month + 1) + '">' + monthNames[month] + '</option>');
			}
		}
	});

	$("#submitdate").click(function(e) {
		e.preventDefault();
		var projectName = $("#projectName").val();
		if(projectName==='Select Project'){
			$('#result').html('<font color="red">Please Select A Project</font>');
			return;
		}
		var year = $("#dropDownYear1").val();
		var month = $("#dropDownYearMonth").val();


		var json = {
			"projectName": projectName,
			"year": year,
			"month": month,
		};

		console.log(json);

		$.ajax({
			url: "fetchSummary",
			type: "post",
			contentType: "application/json",
			data: JSON.stringify(json),
			dataType: 'json',
		}).done(function(result) {
			console.log("------------------result----al---------------------");
			console.log(result);
			for (var i = 0; i < result.length; i++) {
				console.log(result[i].index);
				if (result[i].index == -1) {
					result[i].index = '<p id="change1" style="color:red"></p>';
					result[i].ename = '<p style="color:Blue">' + 'TotalHours' + '</p>';
				}
			}

			Table.clear().draw();
			Table.rows.add(result).draw();
			document.getElementById('change1').innerHTML = '';
		}).fail(function(jqXHR, textStatus, errorThrown) {
			// needs to implement if it fails
			console.log(jqXHR);
			console.log(textStatus);
			console.log(errorThrown);
		});

	});

	$("#submitdateOpt").click(function(e) {
		e.preventDefault();
		var projectName = $("#projectName").val();
		if(projectName==='Select Project'){
			$('#result').html('<font color="red">Please Select A Project</font>');
			return;
		}
		var year = $("#dropDownYear1").val();
		var month = $("#dropDownYearMonth").val();


		var json = {
			"projectName": projectName,
			"year": year,
			"month": month,
		};

		console.log(json);

		$.ajax({
			url: "fetchSummaryOpt",
			type: "post",
			contentType: "application/json",
			data: JSON.stringify(json),
			dataType: 'json',
		}).done(function(result) {
			console.log("------------------result----al---------------------");
			console.log(result);
			for (var i = 0; i < result.length; i++) {
				console.log(result[i].index);
				if (result[i].index == -1) {
					result[i].index = '<p id="change1" style="color:red"></p>';
					result[i].ename = '<p style="color:Blue">' + 'TotalHours' + '</p>';
				}
			}

			Table.clear().draw();
			Table.rows.add(result).draw();
			document.getElementById('change1').innerHTML = '';
		}).fail(function(jqXHR, textStatus, errorThrown) {
			// needs to implement if it fails
			console.log(jqXHR);
			console.log(textStatus);
			console.log(errorThrown);
		});

	});

});

