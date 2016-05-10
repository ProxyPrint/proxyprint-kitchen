package io.github.proxyprint.kitchen.models.repositories;

import io.github.proxyprint.kitchen.models.printshops.Employee;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by daniel on 09-04-2016.
 */
@Transactional
public interface EmployeeDAO extends CrudRepository<Employee,Long> {

    @Transactional
    public Employee findByUsername(String username);
    @Transactional
    public List<Employee> findByPrintShop(PrintShop printShop);
}

