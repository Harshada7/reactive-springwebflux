package com.reactivespringwebflux.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.reactivespringwebflux.document.Employee;

import reactor.core.publisher.Mono;

public interface EmployeeReactiveRepository extends ReactiveMongoRepository<Employee, String> {

	Mono<Employee> findByEmployeeName(String employeeName);
}
