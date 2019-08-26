package com.reactivespringwebflux.controller;

import static com.reactivespringwebflux.constants.EmployeeConstants.EMPLOYEE_END_POINT_V1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.reactivespringwebflux.document.Employee;
import com.reactivespringwebflux.repository.EmployeeReactiveRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class EmployeeController {

	@Autowired
	EmployeeReactiveRepository employeeReactiveRepository;

	@GetMapping(EMPLOYEE_END_POINT_V1)
	public Flux<Employee> getAllEmployees() {
		return employeeReactiveRepository.findAll();
	}

	@GetMapping(EMPLOYEE_END_POINT_V1 + "/{id}")
	public Mono<ResponseEntity<Employee>> getOneEmployee(@PathVariable String id) {
		return employeeReactiveRepository.findById(id).map((employee) -> new ResponseEntity<>(employee, HttpStatus.OK))
			.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	@PostMapping(EMPLOYEE_END_POINT_V1)
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Employee> createEmployee(@RequestBody Employee employee) {
		return employeeReactiveRepository.save(employee);
	}

	@DeleteMapping(EMPLOYEE_END_POINT_V1 + "/{id}")
	public Mono<Void> deleteEmployee(@PathVariable String id) {
		return employeeReactiveRepository.deleteById(id);
	}

	@GetMapping(EMPLOYEE_END_POINT_V1 + "/runtimeException")
	public Flux<Employee> runtimeException() {
		return employeeReactiveRepository.findAll()
			.concatWith(Mono.error(new RuntimeException("RuntimeException Occurred.")));
	}

	// id and item to be updated in the req = path variable and request body - completed
	// using the id get the item from database - completed
	// updated the item retrieved with the value from the request body - completed
	// save the item - completed
	// return the saved item - completed
	@PutMapping(EMPLOYEE_END_POINT_V1 + "/{id}")
	public Mono<ResponseEntity<Employee>> updateEmployee(@PathVariable String id, @RequestBody Employee employee) {
		return employeeReactiveRepository.findById(id).flatMap(currentEmployee -> {
			currentEmployee.setEmployeeName(employee.getEmployeeName());
			currentEmployee.setSalary(employee.getSalary());
			return employeeReactiveRepository.save(currentEmployee);
		}).map(updatedItem -> new ResponseEntity<>(updatedItem, HttpStatus.OK))
			.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
}
