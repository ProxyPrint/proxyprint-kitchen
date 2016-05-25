package io.github.proxyprint.kitchen.models.repositories;

import io.github.proxyprint.kitchen.models.printshops.Manager;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

/**
 * Created by daniel on 04-18-2016.
 */
@Transactional
public interface ManagerDAO extends CrudRepository<Manager,Long> {

    @Transactional
    public Manager findByUsername(String username);
    @Transactional
    public Manager findByPrintShop(PrintShop printShop);
}
