package com.reactivespringwebflux.handler;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.reactivespringwebflux.document.Employee;
import com.reactivespringwebflux.repository.EmployeeReactiveRepository;

import reactor.core.publisher.Mono;

@Component
public class EmployeesHandler {

	@Autowired
	EmployeeReactiveRepository employeeReactiveRepository;

	static Mono<ServerResponse> notFound = ServerResponse.notFound().build();

	public Mono<ServerResponse> getAllEmployees(ServerRequest serverRequest) {
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(employeeReactiveRepository.findAll(),
			Employee.class);
	}

	public Mono<ServerResponse> getOneEmployee(ServerRequest serverRequest) {
		String id = serverRequest.pathVariable("id");
		Mono<Employee> employeeMono = employeeReactiveRepository.findById(id);
		return employeeMono
			.flatMap(employee -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(fromObject(employee)))
			.switchIfEmpty(notFound);
	}

	public Mono<ServerResponse> createEmployee(ServerRequest serverRequest) {
		Mono<Employee> itemTobeInserted = serverRequest.bodyToMono(Employee.class);
		return itemTobeInserted.flatMap(employee -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
			.body(employeeReactiveRepository.save(employee), Employee.class));
	}

	public Mono<ServerResponse> deleteEmployee(ServerRequest serverRequest) {
		String id = serverRequest.pathVariable("id");
		Mono<Void> deleteEmployee = employeeReactiveRepository.deleteById(id);
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(deleteEmployee, Void.class);
	}

	public Mono<ServerResponse> updateEmployee(ServerRequest serverRequest) {
		String id = serverRequest.pathVariable("id");
		Mono<Employee> updatedEmployee = serverRequest.bodyToMono(Employee.class).flatMap((employee) -> {
			Mono<Employee> employeeMono = employeeReactiveRepository.findById(id).flatMap(currentEmployee -> {
				currentEmployee.setEmployeeName(employee.getEmployeeName());
				currentEmployee.setSalary(employee.getSalary());
				return employeeReactiveRepository.save(currentEmployee);
			});
			return employeeMono;
		});
		return updatedEmployee
			.flatMap(employee -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(fromObject(employee)))
			.switchIfEmpty(notFound);
	}
}
