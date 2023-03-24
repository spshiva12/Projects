package com.ojas.timesheet.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ojas.timesheet.entity.DSR;
import com.ojas.timesheet.repo.DSRRepo;
import com.ojas.timesheet.repo.ProjectsRepo;

@Controller
public class DSRController {

	private static final Logger logger = LogManager.getLogger(DSRController.class);

	@Autowired
	private DSRRepo dSRRepo;
	
	@Autowired
	private ProjectsRepo projectsRepo;

	@GetMapping("/")
	public String showHomePage(Model model) {
		// return "index";
		List<String> empDetails = dSRRepo.getAllEmpName();
		List<String> proDetails = projectsRepo.getAllProjectsName();
		
		System.out.println(empDetails);
		model.addAttribute("empDetails", empDetails);
		model.addAttribute("proDetails", proDetails);
		return "filterData";
	}

	@GetMapping("/dsr")
	public String empDetails(Model model) {

		List<String> empDetails = dSRRepo.getAllEmpName();
		List<String> proDetails = projectsRepo.getAllProjectsName();
		System.out.println(empDetails);
		model.addAttribute("empDetails", empDetails);
		model.addAttribute("proDetails", proDetails);
		return "filterData";
	}

	@PostMapping(path = "addDSR", consumes = "application/json", produces = "application/json")
	public Object DSR(@RequestBody String ajaxRequest) {
		JsonObject jobj = new Gson().fromJson(ajaxRequest, JsonObject.class);
		long millis = System.currentTimeMillis();
		java.sql.Date date = new java.sql.Date(millis);

		String empName = jobj.get("empName").getAsString();
		String projectName = jobj.get("projectName").getAsString();
		String dsrDate = jobj.get("dsrDate").getAsString();
		String hrs = jobj.get("hrs").getAsString();
		String taskDescription = jobj.get("taskDescription").getAsString();
		logger.info("ajax req is  ---" + ajaxRequest);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date sDate = null;
		try {
			sDate = sdf.parse(dsrDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		DSR dsr = new DSR();
		dsr.setEmpName(empName);
		dsr.setProject(projectName);
		dsr.setHrsWorked(hrs);
		dsr.setdSRReport(taskDescription);
		dsr.setdSRDate(sDate);
		dsr.setSubmitDate(date);
		dsr.setVisibility(true);

		// Check if entry exists
		DSR empr = dSRRepo.checkRecordExists(dsr.getEmpName(), dsr.getProject(), dsr.getdSRDate());
		System.out.println("Chek1");
		System.out.println(empr);
		///////////

		List<DSR> emp = dSRRepo.getName(empName);
		if (empr == null) {
			com.ojas.timesheet.entity.DSR save = dSRRepo.save(dsr);
			JsonArray jArray = new JsonArray();
			JsonObject innerObject;
			innerObject = new JsonObject();
			innerObject.addProperty("msg", "DSR Record Created Successfully");
			jArray.add(innerObject);
			String jsonString = jArray.toString();
			System.out.println(jsonString);
			return new ResponseEntity<Object>(jsonString, HttpStatus.OK);
		} else {
			String prev_cwh = empr.getHrsWorked();
			String curr_cwh = dsr.getHrsWorked();
			int hrss1 = Integer.parseInt(prev_cwh.split(":")[0]);
			int mins1 = Integer.parseInt(prev_cwh.split(":")[1]);
			
			int hrss2 = Integer.parseInt(curr_cwh.split(":")[0]);
			int mins2 = Integer.parseInt(curr_cwh.split(":")[1]);
			
			hrss1 = hrss1+hrss2;
			mins1 = mins1 + mins2;
			if(mins1>59) {
				int diff = Math.abs(mins1-mins2);
				hrss1++;
				curr_cwh = hrss1+":"+diff;
			}else {				
				curr_cwh = hrss1+":"+mins1;
			}
			dsr.setHrsWorked(curr_cwh);
			String curDes = empr.getdSRReport()+"<br/>---------<br/>"+dsr.getdSRReport();
			dsr.setdSRReport(curDes);
			
			dSRRepo.updateRecord(dsr.getEmpName(), dsr.getProject(), dsr.getdSRDate(), dsr.getdSRReport(),
					dsr.getHrsWorked());
			JsonArray jArray = new JsonArray();
			JsonObject innerObject;
			innerObject = new JsonObject();
			innerObject.addProperty("msg", "DSR Record Updated Successfully");
			jArray.add(innerObject);
			String jsonString = jArray.toString();
			System.out.println(jsonString);
			return new ResponseEntity<Object>(jsonString, HttpStatus.OK);
		}

	}

	@GetMapping("/fetchdsr")
	public Object featchTechDetails() {
		List<DSR> tech = dSRRepo.findAll();
		Gson gson = new GsonBuilder().create();
		String jsonString = gson.toJson(tech);
		System.out.println(jsonString);
		return new ResponseEntity<Object>(jsonString, HttpStatus.OK);
	}

	@GetMapping("/getName/{empName}")
	@ResponseBody
	public List<DSR> findByName(@PathVariable("empName") String empName) {
		List<DSR> emp = dSRRepo.getName(empName);
		return emp;
	}

}
