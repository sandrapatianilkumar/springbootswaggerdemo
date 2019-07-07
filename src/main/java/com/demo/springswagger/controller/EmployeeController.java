package com.demo.springswagger.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.demo.springswagger.exception.ResourceNotFoundException;
import com.demo.springswagger.model.Employee;
import com.demo.springswagger.repository.EmployeeRepository;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "Employee Management System", description = "Employee crud operations for employee management system")
@RestController
@RequestMapping("/api/v1")
public class EmployeeController {
	@Autowired
	EmployeeRepository employeeRepository;

	@ApiOperation(value = "Create an employee", response = Employee.class)
	@RequestMapping(path = "/employees", method = RequestMethod.POST, produces = "application/json")
	public Employee createEmployee(
			@ApiParam(value = "Employee object adds in database", required = true) @Valid @RequestBody Employee employee) {
		return employeeRepository.save(employee);
	}

	@ApiOperation(value = "View a list of employees", response = List.class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully retrived list of employees"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })
	@RequestMapping(path = "/employees", method = RequestMethod.GET, produces = "application/json")
	public List<Employee> getAllEmployees() {
		return employeeRepository.findAll();
	}

	@ApiOperation(value = "Get employee by an Id", response = Employee.class)
	@RequestMapping(path = "/employee/{id}", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<Employee> getEmployeeById(
			@ApiParam(value = "Employee id from which employee object will retrive", required = true) @PathVariable(value = "id") Long employeeId)
			throws ResourceNotFoundException {
		Employee employee = employeeRepository.findById(employeeId)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id:: " + employeeId));
		return ResponseEntity.ok().body(employee);
	}

	@ApiOperation(value = "update an employee", response = Employee.class)
	@RequestMapping(path = "/employee/{id}", method = RequestMethod.PUT, produces = "application/json")
	public ResponseEntity<Employee> updateEmployee(
			@ApiParam(value = "Employee Id to update employee object", required = true) @PathVariable(value = "id") Long employeeId,
			@ApiParam(value = "Update employee object", required = true) @Valid @RequestBody Employee employee)
			throws ResourceNotFoundException {
		employeeRepository.findById(employeeId)
				.orElseThrow(() -> new ResourceNotFoundException("Resource not found with this id:: " + employeeId));
		employee.setId(employeeId);
		Employee emp = employeeRepository.save(employee);
		return ResponseEntity.ok().body(emp);
	}

	@ApiOperation(value = "Delete an employee")
	@RequestMapping(path = "/employee/{id}", method = RequestMethod.DELETE, produces = "application/json")
	public Map<String, Boolean> deleteEmployee(
			@ApiParam(value = "Employee Id from which employee object will delete from database table", required = true) @PathVariable(value = "id") Long employeeId)
			throws ResourceNotFoundException {
		Employee emp = employeeRepository.findById(employeeId)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id :: " + employeeId));
		employeeRepository.delete(emp);
		Map<String, Boolean> response = new HashMap<>();
		response.put("Deleted", Boolean.TRUE);
		return response;
	}

}
