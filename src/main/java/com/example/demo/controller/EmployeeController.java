package com.example.demo.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Employee;
import com.example.demo.service.EmployeeService;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

	private EmployeeService employeeService;
	
	public EmployeeController(EmployeeService theEmployeeService) {
		employeeService = theEmployeeService;
	}
	



	@PostMapping("/update")
	public void updateEmployee(@RequestBody Employee theEmployee) {
		
		// update the employee
		 employeeService.save(theEmployee);
		
		 
	}	
	
	
	@PostMapping("/save")
	public Employee saveEmployee(@RequestBody Employee theEmployee) {
		
		// save the employee
		 return employeeService.save(theEmployee);
		
		// use a redirect to prevent duplicate submissions
		 
	}
	
	
	@PostMapping("/delete")
	public String delete(@RequestParam("employeeId") int theId) {
		
		// delete the employee
		employeeService.deleteById(theId);
		
		// redirect to /employees/list
		return "redirect:/employees/list";
		
	}
}


















