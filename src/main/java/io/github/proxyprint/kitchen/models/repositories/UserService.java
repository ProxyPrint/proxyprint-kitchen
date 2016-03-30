/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.github.proxyprint.kitchen.models.repositories;

import io.github.proxyprint.kitchen.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 * @author josesousa
 */
@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserDAO userDAO;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userDAO.findByUsername(username);
        if (u == null) {
            throw new UsernameNotFoundException("Username not found: " + username);
        }
        return new org.springframework.security.core.userdetails.User(username, u.getPassword(), u.getRoles());
    }

}
