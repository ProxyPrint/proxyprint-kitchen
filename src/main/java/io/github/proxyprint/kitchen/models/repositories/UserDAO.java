/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.proxyprint.kitchen.models.repositories;

import io.github.proxyprint.kitchen.models.User;
import javax.transaction.Transactional;
import org.springframework.data.repository.CrudRepository;

@Transactional
public interface UserDAO extends CrudRepository<User, Long> {

    @Transactional
    public User findByUsername(String username);
}
