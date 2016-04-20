package io.github.proxyprint.kitchen.models.repositories;

import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;

/**
 * Created by daniel on 04-18-2016.
 */
@Transactional
public interface PrintShopDAO extends CrudRepository<PrintShop,Long> {

    @Transactional
    public PrintShop findByName(String name);
}
