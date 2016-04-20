package io.github.proxyprint.kitchen.models.repositories;

import io.github.proxyprint.kitchen.models.printshops.Employee;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

/**
 * Created by daniel on 09-04-2016.
 */
@Transactional
public interface EmployeeDAO extends CrudRepository<Employee,Long> {

    @Transactional
    public Employee findByUsername(String username);
}

