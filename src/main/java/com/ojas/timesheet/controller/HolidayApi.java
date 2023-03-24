package com.ojas.timesheet.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Controller
public class HolidayApi {

	@Value("${app.root}")
	private String root;

	@Value("${app.storage}")
	private String storage;

	
	@Value("${app.datasetExtension}")
	private String datasetExtension;

	@Value("${app.dateapi}")
	private String dateapi;

	@Value("${app.dateApiCSVUrl}")
	private String dateApiCSVUrl;

	@GetMapping("/holidays/{p}")
	public Object holidayList(@PathVariable String p) {

		Year thisYear = Year.now();
		String y = thisYear.toString();
		//y = "2023";

		List<Date> wDays = null;

		String sDate1 = "1/1/" + y;
		String sDate2 = "31/12/" + y;
		Date date1 = null;
		Date date2 = null;
		try {
			date1 = new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);
			date2 = new SimpleDateFormat("dd/MM/yyyy").parse(sDate2);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		///////////////////////////////
		ArrayList<Date> hdays = new ArrayList<Date>();
		try {
			URL url = new URL(dateApiCSVUrl + y + datasetExtension);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Accept", "application/json");
			con.setDoOutput(true);
			try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
				String responseLine = null;
				int c = 0;
				while ((responseLine = br.readLine()) != null) {
					String[] fields = responseLine.split("\t");
					for (int i = 0; i < fields.length && c > 0; ++i) {
						// System.out.println(i+"---"+fields[i]);
						if (i == 2) {
							Date d;
							try {
								d = new SimpleDateFormat("dd-MMM-yyyy").parse(fields[i]);
								hdays.add(d);
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					c++;
				}

			} catch (Exception e) {
				System.out.println("Error-1:"+e.toString());
				Gson gson = new GsonBuilder().create();
				String jsonString = gson.toJson("{'status':'Current Year Data Not Found'}");
				System.out.println(jsonString);
				return jsonString;
			}
		} catch (Exception e) {
			System.out.println("Error-2:"+e.toString());
			Gson gson = new GsonBuilder().create();
			String jsonString = gson.toJson("{'status':'Current Year Data Not Found'}");
			System.out.println(jsonString);
			return jsonString;
		}

		//////////////////////////////

		wDays = getDaysBetweenDates(date1, date2, hdays);

		List<String> refined = new ArrayList<String>();
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		for (int i = 0; i < wDays.size(); i++) {
			String dayWithZeroTime = null;
			try {
				dayWithZeroTime = (formatter.format(wDays.get(i)));
				refined.add(dayWithZeroTime);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Gson gson = new GsonBuilder().create();
		// String jsonString = gson.toJson(refined);
		// System.out.println(jsonString);
		return new ResponseEntity<Object>(refined, HttpStatus.OK);

	}

	public List<Date> getDaysBetweenDates(Date startdate, Date enddate, ArrayList<Date> holidays) {
		List<Date> dates = new ArrayList<Date>();

		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startdate);

		while (calendar.getTime().before(enddate)) {
			Date result = calendar.getTime();
			Calendar c = Calendar.getInstance();
			c.setTime(result);
			int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
			if (dayOfWeek == 1) {
			} else if (dayOfWeek == 7) {
			} else {
				boolean add = true;
				for (int k = 0; k < holidays.size(); k++) {
					if (holidays.get(k).compareTo(result) == 0) {
						add = false;
						break;
					}
				}
				if (add) {
					dates.add(result);
				}
			}
			calendar.add(Calendar.DATE, 1);
		}
		return dates;
	}

}
