package com.reactivespringwebflux.initialize;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.reactivespringwebflux.document.Employee;
import com.reactivespringwebflux.repository.EmployeeReactiveRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Component
@Profile("!test")
@Slf4j
public class EmployeeDataInitializer implements CommandLineRunner {

	@Autowired
	EmployeeReactiveRepository employeeReactiveRepository;

	// @Autowired
	// MongoOperations mongoOperations;
	@Override
	public void run(String... args) throws Exception {
		initalDataSetUp();
	}

	public List<Employee> data() {
		return Arrays.asList(new Employee(null, "John", 399.99), new Employee(null, "Alex", 329.99),
			new Employee(null, "Jack", 649.99), new Employee("123", "Will", 499.99));
	}

	private void initalDataSetUp() {
		employeeReactiveRepository.deleteAll().thenMany(Flux.fromIterable(data()))
			.flatMap(employeeReactiveRepository::save).thenMany(employeeReactiveRepository.findAll())
			.subscribe((employee -> {
				System.out.println("Item inserted from CommandLineRunner : " + employee);
			}));
	}
}
