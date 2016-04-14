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

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

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
    @JsonIgnore
    @Column(nullable = false, name = "accepted")
    private boolean accepted = false;
    @JsonIgnore
    @Column(nullable = false, name = "pshop_date_request")
    private GregorianCalendar pShopDateRequest;
    @JsonIgnore
    @Column(nullable = true, name = "pshop_date_request_accepted")
    private GregorianCalendar pShopDateRequestAccepted;
    @Transient
    private String pShopDateRequestString;
    @Transient
    private String pShopDateRequestAcceptedString;

    public RegisterRequest() {
        // Return a Calendar based on default Locale and TimeZone
        this.pShopDateRequest = (GregorianCalendar) GregorianCalendar.getInstance();
        this.pShopDateRequestAccepted = null;
        this.pShopDateRequestString = GregorianCalendarToString(this.pShopDateRequest);
        this.pShopDateRequestAcceptedString = "";
    }

    public RegisterRequest(String managerName, String managerEmail, String managerPassword, String pShopAddress, String pShopLatitude, String pShopLongitude, String pShopNIF, String pShopName, boolean accepted) {
        this.managerName = managerName;
        this.managerEmail = managerEmail;
        this.managerPassword = managerPassword;
        this.pShopAddress = pShopAddress;
        this.pShopLatitude = pShopLatitude;
        this.pShopLongitude = pShopLongitude;
        this.pShopNIF = pShopNIF;
        this.pShopName = pShopName;
        this.accepted = accepted;
        this.pShopDateRequest = (GregorianCalendar) GregorianCalendar.getInstance();
        this.pShopDateRequestAccepted = null;
        this.pShopDateRequestString = GregorianCalendarToString(this.pShopDateRequest);
        this.pShopDateRequestAcceptedString = "";
    }

    public long getId() { return id; }

    public String getManagerName() { return managerName; }

    public void setManagerName(String managerName) { this.managerName = managerName; }

    public String getManagerEmail() { return managerEmail; }

    public void setManagerEmail(String managerEmail) { this.managerEmail = managerEmail; }

    public String getManagerPassword() { return managerPassword; }

    public void setManagerPassword(String managerPassword) { this.managerPassword = managerPassword; }

    public String getpShopAddress() { return pShopAddress; }

    public void setpShopAddress(String pShopAddress) { this.pShopAddress = pShopAddress; }

    public String getpShopLatitude() { return pShopLatitude; }

    public void setpShopLatitude(String pShopLatitude) { this.pShopLatitude = pShopLatitude; }

    public String getpShopLongitude() { return pShopLongitude; }

    public void setpShopLongitude(String pShopLongitude) { this.pShopLongitude = pShopLongitude; }

    public String getpShopNIF() { return pShopNIF; }

    public void setpShopNIF(String pShopNIF) { this.pShopNIF = pShopNIF; }

    public String getpShopName() { return pShopName; }

    public void setpShopName(String pShopName) { this.pShopName = pShopName; }

    public boolean isAccepted() { return accepted; }

    public void setAccepted(boolean accepted) { this.accepted = accepted; }

    public GregorianCalendar getpShopDateRequest() { return pShopDateRequest; }

    public void setpShopDateRequest(GregorianCalendar pShopDateRequest) { this.pShopDateRequest = pShopDateRequest; }

    public GregorianCalendar getpShopDateRequestAccepted() { return pShopDateRequestAccepted; }

    public void setpShopDateRequestAccepted(GregorianCalendar pShopDateRequestAccepted) { this.pShopDateRequestAccepted = pShopDateRequestAccepted; }

    @Override
    public String toString() {
        return "RegisterRequest{" + "id=" + id + ", managerName=" + managerName + ", managerEmail=" + managerEmail + ", managerPassword=" + managerPassword + ", pShopAddress=" + pShopAddress + ", pShopLatitude=" + pShopLatitude + ", pShopLongitude=" + pShopLongitude + ", pShopNIF=" + pShopNIF + ", pShopName=" + pShopName + '}';
    }

    /**
     *
     * @param c, GregorianCalendar instance
     * @return Well formated string for display
     */
    private String GregorianCalendarToString(GregorianCalendar c) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        sdf.setCalendar(c);
        String dateFormatted = sdf.format(c.getTime());
        return dateFormatted;
    }
}
