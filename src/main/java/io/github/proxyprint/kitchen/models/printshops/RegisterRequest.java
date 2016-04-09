/*
 * Copyright 2016 Jorge Caldas, José Cortez
 * José Francisco, Marcelo Gonçalves
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.proxyprint.kitchen.models.printshops;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author josesousa
 */
@Entity
@Table(name = "register_requests")
public class RegisterRequest implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false, name = "manager_name")
    private String managerName;
    @Column(nullable = false, name = "manager_email")
    private String managerEmail;
    @Column(nullable = false, name = "manager_password")
    private String managerPassword;
    @Column(nullable = false, name = "pshop_address")
    private String pShopAddress;
    @Column(nullable = false, name = "pshop_latitude")
    private String pShopLatitude;
    @Column(nullable = false, name = "pshop_longitude")
    private String pShopLongitude;
    @Column(nullable = false, name = "pshop_nif")
    private String pShopNIF;
    @Column(nullable = false, name = "pshop_name")
    private String pShopName;

    public RegisterRequest() {
    }

    public RegisterRequest(String managerName, String managerEmail, String managerPassword, String pShopAddress, String pShopLatitude, String pShopLongitude, String pShopNIF, String pShopName) {
        this.managerName = managerName;
        this.managerEmail = managerEmail;
        this.managerPassword = managerPassword;
        this.pShopAddress = pShopAddress;
        this.pShopLatitude = pShopLatitude;
        this.pShopLongitude = pShopLongitude;
        this.pShopNIF = pShopNIF;
        this.pShopName = pShopName;
    }

    public long getId() {
        return id;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getManagerEmail() {
        return managerEmail;
    }

    public void setManagerEmail(String managerEmail) {
        this.managerEmail = managerEmail;
    }

    public String getManagerPassword() {
        return managerPassword;
    }

    public void setManagerPassword(String managerPassword) {
        this.managerPassword = managerPassword;
    }

    public String getpShopAddress() {
        return pShopAddress;
    }

    public void setpShopAddress(String pShopAddress) {
        this.pShopAddress = pShopAddress;
    }

    public String getpShopLatitude() {
        return pShopLatitude;
    }

    public void setpShopLatitude(String pShopLatitude) {
        this.pShopLatitude = pShopLatitude;
    }

    public String getpShopLongitude() {
        return pShopLongitude;
    }

    public void setpShopLongitude(String pShopLongitude) {
        this.pShopLongitude = pShopLongitude;
    }

    public String getpShopNIF() {
        return pShopNIF;
    }

    public void setpShopNIF(String pShopNIF) {
        this.pShopNIF = pShopNIF;
    }

    public String getpShopName() {
        return pShopName;
    }

    public void setpShopName(String pShopName) {
        this.pShopName = pShopName;
    }

    @Override
    public String toString() {
        return "RegisterRequest{" + "id=" + id + ", managerName=" + managerName + ", managerEmail=" + managerEmail + ", managerPassword=" + managerPassword + ", pShopAddress=" + pShopAddress + ", pShopLatitude=" + pShopLatitude + ", pShopLongitude=" + pShopLongitude + ", pShopNIF=" + pShopNIF + ", pShopName=" + pShopName + '}';
    }

}
