package com.employee.main.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.employee.main.model.EMModel;
import com.employee.main.model.Response;
import com.employee.main.service.EMService;

@CrossOrigin("*")
@RestController
@RequestMapping("/auth")
public class EMController {

	@Autowired
	EMService ems;

	@PostMapping("/signup")
	public ResponseEntity<Response> createUser(@RequestBody EMModel value) {

		return ResponseEntity.ok(ems.createUser(value));
	}

	@PostMapping("/userlogin")
	public ResponseEntity<Response> loginUser(@RequestBody EMModel value) {

		return ResponseEntity.ok(ems.loginUser(value));
	}
	@PostMapping("/adminlogin")
	public ResponseEntity<Response> loginAdmin(@RequestBody EMModel value) {

		return ResponseEntity.ok(ems.loginAdmin(value));
	}

	@GetMapping("/getall")
	public ResponseEntity<Response> getAll(){
		
		return ResponseEntity.ok(ems.getAll());
	}
	@GetMapping("/getone")
	public ResponseEntity<Response> getOne(@RequestParam String empId){
		
		return ResponseEntity.ok(ems.getOne(empId));
	}
	
	@GetMapping("/admindash")
	public ResponseEntity<Response> csvGenerator(HttpServletResponse response) {

		return ResponseEntity.ok(ems.loginAdmin(response));
	}
	@PutMapping("/update")
	public ResponseEntity<Response> updateUser(@RequestBody EMModel value) {

		return ResponseEntity.ok(ems.updateUser(value));
	}
}
