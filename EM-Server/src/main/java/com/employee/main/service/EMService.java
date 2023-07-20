package com.employee.main.service;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.employee.main.model.EMModel;
import com.employee.main.model.Response;
import com.employee.main.repository.EMRepository;
import com.opencsv.CSVWriter;
import com.opencsv.ICSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

@Service
public class EMService {
	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private EMRepository data;
	@Autowired
	private JavaMailSender mailSender;

	Response res = new Response();

	public Response sendCredentials(String toEmail, String empId) {

		try {
			String fromMail = "kavinkannan892@gmail.com";
			String sendSubject = "OTP for verification";
			String sendMessage = "Greetings, \n\n Please use the following Credentials to login your account \n\nUser name: "
					+ empId
					+ "\nPassword: your created password. \n\nDo not share this credentials with anyone.\n\nThanks & Regards,\nKavin S,\nFull Stack Developer.";
			SimpleMailMessage message = new SimpleMailMessage();
			message.setFrom(fromMail);
			message.setSubject(sendSubject);
			message.setText(sendMessage);
			message.setTo(toEmail);

			mailSender.send(message);
			res.setResponseMsg("SUCCESS");
			res.setResponseCode(200);
			res.setData("OTP sent successfully");

		} catch (Exception e) {
			e.printStackTrace();
			res.setResponseMsg("ERROR");
			res.setResponseCode(500);
			res.setData("Invalid Email");
		}

		return res;
	}

	public Response createUser(EMModel value) {
		String uuid = UUID.randomUUID().toString();
		String empId = uuid.substring(0, 6);
		value.setEmpId(empId);
		Date date = new Date(Calendar.getInstance().getTime().getTime());
		value.setCreatedDate(date);
		try {
			// Checking for existing username
			String email = value.getEmail();
			EMModel user = data.findFirstByEmail(email);
			if (user == null && Pattern.matches("^[a-zA-Z0-9._%+-]+@gmail\\.com$", value.getEmail())) {
				if (String.valueOf(value.getPhNo()).length() == 10 && value.getPhNo() >= 6000000000L
						&& value.getPhNo() <= 9999999999L
						&& Pattern.matches("[6789][0-9]{9}", String.valueOf(value.getPhNo()))) {
//						Sending otp to user
					try {
						Response sen = sendCredentials(value.getEmail(), value.getEmpId());
						if (sen.getResponseMsg().equals("SUCCESS")) {
							data.save(value);
							res.setData("User Created Succesfully");
							res.setResponseCode(200);
							res.setResponseMsg("SUCCESS");
						} else {
							res.setData("unable to send otp");
							res.setResponseCode(404);
							res.setResponseMsg("ERROR");
						}

					} catch (Exception e) {
						e.printStackTrace();
						res.setData("unable to send otp");
						res.setResponseCode(404);
						res.setResponseMsg("ERROR");
					}
				} else {
					System.out.println("Phone number is not valid");
					res.setData("Phone number is not valid");
					res.setResponseCode(404);
					res.setResponseMsg("ERROR");
				}
			} else {
				System.out.println("Email is not valid");
				res.setData("Email is not valid");
				res.setResponseCode(404);
				res.setResponseMsg("ERROR");

			}

		} catch (Exception e) {
			e.printStackTrace();
			res.setData("Server Error");
			res.setResponseCode(500);
			res.setResponseMsg("ERROR");
		}

		return res;
	}

	public Response loginUser(EMModel value) {
		try {
			EMModel user = data.findFirstByEmpIdAndPswd(value.getEmpId(), value.getPswd());
			if (user == null) {
				res.setData("User Not Found");
				res.setResponseCode(404);
				res.setResponseMsg("ERROR");

			} else {
				res.setData("User Logged in Succesfully");
				res.setResponseCode(200);
				res.setResponseMsg("SUCCESS");
			}
		} catch (Exception e) {
			e.printStackTrace();
			res.setData("Server Error");
			res.setResponseCode(500);
			res.setResponseMsg("ERROR");
		}
		return res;
	}
	public Response loginAdmin(EMModel value) {
		try {
			EMModel user = data.findFirstByEmpIdAndPswdAndIsAdmin(value.getEmpId(), value.getPswd(),1);
			if (user == null) {
				res.setData("Admin Not Found");
				res.setResponseCode(404);
				res.setResponseMsg("ERROR");

			} else {
				res.setData("Admin Logged in Succesfully");
				res.setResponseCode(200);
				res.setResponseMsg("SUCCESS");
			}
		} catch (Exception e) {
			e.printStackTrace();
			res.setData("Server Error");
			res.setResponseCode(500);
			res.setResponseMsg("ERROR");
		}
		return res;
	}

	public Response loginAdmin(HttpServletResponse response) {

		EMModel value = new EMModel();

		try {

			EMModel user = data.findFirstByEmpIdAndPswdAndIsAdmin(value.getEmpId(), value.getPswd(), 1);
			// set file name and content type
			String filename = "Employee-data.csv";

			response.setContentType("text/csv");
			response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
			// create a csv writer
			StatefulBeanToCsv<EMModel> writer = new StatefulBeanToCsvBuilder<EMModel>(
					(ICSVWriter) response.getOutputStream()).withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
					.withSeparator(CSVWriter.DEFAULT_SEPARATOR).withOrderedResults(false).build();
			// write all employees data to csv file
			writer.write(user);

			res.setData("Admin Logged in Succesfully");
			res.setResponseCode(200);
			res.setjData(writer);
			res.setResponseMsg("SUCCESS");

		} catch (Exception e) {
			e.printStackTrace();
			res.setData("Server Error");
			res.setResponseCode(500);
			res.setResponseMsg("ERROR");
		}
		return res;
	}

	@SuppressWarnings("unchecked")
	public Response getAll() {

		try {
		
		List<EMModel> value = data.findAll();

		JSONArray jsonArray = new JSONArray();
		for (EMModel object : value) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("empId", object.getEmpId());
			jsonObject.put("firstName", object.getFirstName());
			jsonObject.put("lastName", object.getLastName());
			jsonObject.put("email", object.getEmail());
			jsonObject.put("phNo", object.getPhNo());
			jsonObject.put("pswd", object.getPswd());
			jsonObject.put("jobTitle", object.getJobTitle());
			jsonObject.put("gender", object.getGender());
			jsonObject.put("address", object.getAddress());
			jsonObject.put("createdDate", object.getCreatedDate());
			jsonObject.put("updatedDate", object.getUpdatedDate());
			jsonObject.put("isAdmin", object.getIsAdmin());
			jsonObject.put("salary", object.getSalary());
			jsonObject.put("dept", object.getDept());
			jsonObject.put("emergencyContName", object.getEmergencyContName());
			jsonObject.put("emergencyContNum", object.getEmergencyContNum());
//		 last_name, email, pswd, dob, ph_no, dept, job_title, gender, age, address, created_date, updated_date, is_admin, salary, emergency_contact_name, emergency_contact_number, user_name
			
		
			jsonArray.add(jsonObject);
		}
		res.setData("Admin Logged in Succesfully");
		res.setResponseCode(200);
		res.setjData(jsonArray);
		res.setResponseMsg("SUCCESS");

	} catch (Exception e) {
		e.printStackTrace();
		res.setData("Server Error");
		res.setResponseCode(500);
		res.setResponseMsg("ERROR");
		res.setjData(null);
	}
		return res;
	}

	public Response getOne(String empId) {
		try {
			 EMModel valueEmp = data.findFirstByEmpId(empId);

	
		        if (valueEmp != null) {
		            res.setData("Employee found");
		            res.setResponseCode(200);
		            res.setjData(valueEmp);
		            res.setResponseMsg("SUCCESS");
		        } else {
		            res.setData("Employee not found");
		            res.setResponseCode(404);
		            res.setjData(null);
		            res.setResponseMsg("NOT FOUND");
		        }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
	public Response updateUser(EMModel value) {
		try {
			Optional<EMModel> existingUser = data.findById(value.getEmpId());
			
			if(existingUser.isPresent()) {
				EMModel userData = existingUser.get();
				userData.setEmail(value.getEmail());
				userData.setPhNo(value.getPhNo());
				userData.setFirstName(value.getFirstName());
				userData.setLastName(value.getLastName());
				userData.setGender(value.getGender());
				userData.setAge(value.getAge());
				userData.setEmergencyContName(value.getEmergencyContName());
				userData.setEmergencyContNum(value.getEmergencyContNum());
//				userData.setDob(value.getDob());
				userData.setSalary(value.getSalary());
				userData.setJobTitle(value.getJobTitle());
				userData.setDept(value.getDept());
				userData.setUpdatedDate(value.getUpdatedDate());
				
				data.save(userData);
				res.setData("Updated User Successfully");
				res.setResponseCode(200);
				res.setResponseMsg("SUCCESS");
				
			}else {
				res.setData("No Such User Found");
				res.setResponseCode(200);
				res.setResponseMsg("SUCCESS");	
			}

		} catch (Exception e) {
			e.printStackTrace();
			res.setData("Invalid Input");
			res.setResponseCode(500);
			res.setResponseMsg("ERROR");
		}
		
		return res;
	}

}
