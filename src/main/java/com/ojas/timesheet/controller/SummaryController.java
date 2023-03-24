package com.ojas.timesheet.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ojas.timesheet.entity.DSR;
import com.ojas.timesheet.entity.Employee;
import com.ojas.timesheet.repo.DSRRepo;
import com.ojas.timesheet.repo.ProjectsRepo;

@Controller
public class SummaryController {

	private static final Logger logger = LogManager.getLogger(SummaryController.class);

	@Autowired
	private DSRRepo dSRRepo;
	@Autowired
	private ProjectsRepo projectsRepo;

	@Value("${app.dateapi}")
	private String dateapi;

	@Value("${app.workHours}")
	private int workHours;

	@GetMapping("/summary")
	public String dSRSummary(Model model) {
		List<String> proDetails = projectsRepo.getAllProjectsName();
		model.addAttribute("proDetails", proDetails);
		return "summary";
	}

	@PostMapping(path = "/fetchSummary", consumes = "application/json", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public @ResponseBody String fetchSummary(@RequestBody String ajaxRequest, HttpServletResponse response) {
		JsonObject jobj = new Gson().fromJson(ajaxRequest, JsonObject.class);
		long millis = System.currentTimeMillis();
		java.sql.Date date = new java.sql.Date(millis);
		String projectName = jobj.get("projectName").getAsString();
		String year = jobj.get("year").getAsString();
		String month = jobj.get("month").getAsString();
		System.out.println(projectName);
		System.out.println(year);
		System.out.println(month);
		if (month.length() == 1) {
			month = "0" + month;
		}

		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

		// Calling CalenderApi
		StringBuilder resp = new StringBuilder();
		List<Date> refined = new ArrayList<Date>();
		List<String> refinedDateString = new ArrayList<String>();
		try {
			URL url = new URL(dateapi);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Accept", "application/json");
			con.setDoOutput(true);
			try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
				String responseLine = null;
				while ((responseLine = br.readLine()) != null) {
					resp.append(responseLine.trim());
				}
				// System.out.println(resp.toString());
				String dates = resp.toString();
				dates = dates.replace("[", "");
				dates = dates.replace("]", "");
				String dateValues[] = dates.split(",");
				System.out.println(dateValues.length);

				for (int i = 0; i < dateValues.length; i++) {
					Date dayWithZeroTime = null;
					try {
						if (dateValues[i].contains("/" + month + "/")) {
							refined.add(formatter.parse(dateValues[i].replace("\"", "")));
							refinedDateString.add((dateValues[i].replace("\"", "")));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				System.out.println("Found " + refined.size() + " days");

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// End of CalenderApi

		if (resp != null) {
			if (resp.toString().trim().length() > 0) {
				List<String> empNames = dSRRepo.fetchEmpNameByProject(projectName, year, month);
				JsonArray jArray = new JsonArray();
				int eligibleHours = 0;
				int bill = 0;

				for (int i = 0; i < empNames.size(); i++) {
					String ename = empNames.get(i);
					System.out.println(ename);
					JsonObject innerObject;
					List<com.ojas.timesheet.entity.DSR> tech = dSRRepo.fetchEmpProjectSummaryDetails(projectName, ename,
							workHours, refinedDateString);

					for (int k = 0; k < tech.size(); k++) {

						innerObject = new JsonObject();
						innerObject.addProperty("index", (i + 1));
						int eH = (refined.size() * workHours);
						int hours = Integer.parseInt(tech.get(k).getHrsWorked());
						innerObject.addProperty("ename", tech.get(k).getEmpName());
						innerObject.addProperty("eligiblity", eH);
						innerObject.addProperty("bill", hours);

						eligibleHours += eH;
						bill += hours;

						jArray.add(innerObject);
						// System.out.println(tech.get(k).getEmpName()+","+(refined.size()*workHours)+","+(hours));

					}
				}

				JsonObject innerObject = new JsonObject();
				innerObject.addProperty("index", -1);
				innerObject.addProperty("ename", "");
				innerObject.addProperty("eligiblity", eligibleHours);
				innerObject.addProperty("bill", bill);
				jArray.add(innerObject);

				Gson gson = new GsonBuilder().create();
				// String jsonString = gson.("Error");
				System.out.println(jArray.toString());
				return jArray.toString();
			} else {
				Gson gson = new GsonBuilder().create();
				String jsonString = gson.toJson("{'status':'No response for calenderApi'}");
				System.out.println(jsonString);
				return jsonString;
			}
		} else {
			Gson gson = new GsonBuilder().create();
			String jsonString = gson.toJson("{'status':'No response for calenderApi'}");
			System.out.println(jsonString);
			return jsonString;
		}

	}

	@PostMapping(path = "/fetchSummaryOpt", consumes = "application/json", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public @ResponseBody String fetchSummaryOpt(@RequestBody String ajaxRequest, HttpServletResponse response) {
		JsonObject jobj = new Gson().fromJson(ajaxRequest, JsonObject.class);
		long millis = System.currentTimeMillis();
		java.sql.Date date = new java.sql.Date(millis);
		String projectName = jobj.get("projectName").getAsString();
		String year = jobj.get("year").getAsString();
		String month = jobj.get("month").getAsString();
		System.out.println(projectName);
		System.out.println(year);
		System.out.println(month);
		if (month.length() == 1) {
			month = "0" + month;
		}

		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		// Calling CalenderApi
		StringBuilder resp = new StringBuilder();
		List<Date> refined = new ArrayList<Date>();
		List<String> refinedDateString = new ArrayList<String>();
		try {
			URL url = new URL(dateapi);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Accept", "application/json");
			con.setDoOutput(true);
			try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
				String responseLine = null;
				while ((responseLine = br.readLine()) != null) {
					resp.append(responseLine.trim());
				}
				// System.out.println(resp.toString());
				String dates = resp.toString();
				dates = dates.replace("[", "");
				dates = dates.replace("]", "");
				String dateValues[] = dates.split(",");
				System.out.println(dateValues.length);

				for (int i = 0; i < dateValues.length; i++) {
					Date dayWithZeroTime = null;
					try {
						if (dateValues[i].contains("/" + month + "/")) {
							refined.add(formatter.parse(dateValues[i].replace("\"", "")));
							refinedDateString.add((dateValues[i].replace("\"", "")));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				System.out.println("Found " + refined.size() + " days");

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// End of CalenderApi
		if (resp != null) {
			if (resp.toString().trim().length() > 0) {
				List<String> empNames = dSRRepo.fetchEmpNameByProject(projectName, year, month);
				JsonArray jArray = new JsonArray();
				int eligibleHours = 0;
				int bill = 0;

				for (int i = 0; i < empNames.size(); i++) {
					String ename = empNames.get(i);
					System.out.println(ename);
					JsonObject innerObject;
					List<com.ojas.timesheet.entity.DSR> tech = dSRRepo.fetchEmpProjectSummaryDetailsOpt(projectName,
							ename, year, month, workHours);

					for (int k = 0; k < tech.size(); k++) {

						innerObject = new JsonObject();
						innerObject.addProperty("index", (i + 1));
						int eH = (refined.size() * workHours);
						int hours = Integer.parseInt(tech.get(k).getHrsWorked());
						innerObject.addProperty("ename", tech.get(k).getEmpName());
						innerObject.addProperty("eligiblity", eH);
						innerObject.addProperty("bill", hours);

						eligibleHours += eH;
						bill += hours;

						jArray.add(innerObject);
						// System.out.println(tech.get(k).getEmpName()+","+(refined.size()*workHours)+","+(hours));

					}
				}

				JsonObject innerObject = new JsonObject();
				innerObject.addProperty("index", -1);
				innerObject.addProperty("ename", "");
				innerObject.addProperty("eligiblity", eligibleHours);
				innerObject.addProperty("bill", bill);
				jArray.add(innerObject);

				Gson gson = new GsonBuilder().create();
				// String jsonString = gson.("Error");
				System.out.println(jArray.toString());
				return jArray.toString();
			} else {
				Gson gson = new GsonBuilder().create();
				String jsonString = gson.toJson("{'status':'No response for calenderApi'}");
				System.out.println(jsonString);
				return jsonString;
			}
		} else {
			Gson gson = new GsonBuilder().create();
			String jsonString = gson.toJson("{'status':'No response for calenderApi'}");
			System.out.println(jsonString);
			return jsonString;
		}

	}

	@PostMapping(path = "/fetchsummaryBC", consumes = "application/json", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<StreamingResponseBody> fetchSummaryBC(@RequestBody String ajaxRequest,
			HttpServletResponse response) {
		JsonObject jobj = new Gson().fromJson(ajaxRequest, JsonObject.class);
		long millis = System.currentTimeMillis();
		java.sql.Date date = new java.sql.Date(millis);
		String projectName = jobj.get("projectName").getAsString();
		String year = jobj.get("year").getAsString();
		String month = jobj.get("month").getAsString();
		System.out.println(projectName);
		System.out.println(year);
		System.out.println(month);

		// Blank workbook
		XSSFWorkbook workbook = new XSSFWorkbook();

		List<String> empNames = dSRRepo.fetchEmpNameByProject(projectName, year, month);
		for (int i = 0; i < empNames.size(); i++) {
			String ename = empNames.get(i);
			// Creating a blank Excel sheet
			XSSFSheet sheet = workbook.createSheet(ename);

			int rowCount = 0;
			// Creating a new row in the sheet
			Row row0 = sheet.createRow(rowCount);
			rowCount++;
			// Creating a new row in the sheet
			Row row = null;
			System.out.println(ename);
			List<com.ojas.timesheet.entity.DSR> tech = dSRRepo.fetchTechDetails(projectName, ename, year, month);
			for (int k = 0; k < tech.size(); k++) {
				// Creating a new row in the sheet
				row = sheet.createRow(rowCount);

				Date d = tech.get(k).getdSRDate();

				System.out.println(tech.get(k).getEmpName() + "===========" + d.getDay());
				int cellnum = 0;
				Cell cell = row.createCell(cellnum++);

				if (d.getDay() == 0) {
					CellStyle css = workbook.createCellStyle();
					css.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());

					cell.setCellValue("Sunday");
					cell.setCellStyle(css);

				} else if (d.getDay() == 6) {
					cell.setCellValue("Saturday");
				} else {
					cell.setCellValue("WorkingDay");
				}
				cell = row.createCell(cellnum++);
				cell.setCellValue(tech.get(k).getEmpName());
				cell = row.createCell(cellnum++);

				CellStyle css = workbook.createCellStyle();
				CreationHelper ch = workbook.getCreationHelper();
				css.setDataFormat(ch.createDataFormat().getFormat("yyyy-mm-dd"));
				cell.setCellValue(tech.get(k).getdSRDate());
				cell.setCellStyle(css);

				cell = row.createCell(cellnum++);
				cell.setCellValue(tech.get(k).getdSRReport());
				cell = row.createCell(cellnum++);
				cell.setCellValue(tech.get(k).getHrsWorked());

				rowCount++;

			}
		}
		// gen excel report
		// Try block to check for exceptions
		try {
			File f = new File("d:/temp.xlsx");
			// Writing the workbook
			FileOutputStream out = new FileOutputStream(f);
			workbook.write(out);

			// Closing file output connections
			out.close();

			// Console message for successful execution of
			// program
			System.out.println("xlsx written successfully on disk.");

			InputStream in = new FileInputStream(f.getAbsolutePath());
			StreamingResponseBody body = outputStream -> FileCopyUtils.copy(in, outputStream);
			return ResponseEntity.ok().header("Content-Disposition", "attachment;filename=" + f.getName()).body(body);

		} // Catch block to handle exceptions
		catch (Exception e) {

			// Display exceptions along with line number
			// using printStackTrace() method
			e.printStackTrace();
			Gson gson = new GsonBuilder().create();
			String jsonString = gson.toJson("Error");
			System.out.println(jsonString);
			return null;
		}
		/////////////////

	}
}
