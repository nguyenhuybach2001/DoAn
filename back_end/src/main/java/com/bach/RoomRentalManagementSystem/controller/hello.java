package com.bach.RoomRentalManagementSystem.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class hello {

    @GetMapping("/")
    public String Hello() {
        return "DM bách";
    }
    
    @GetMapping("/admin")
    public String hiadmin() {
        return "admin nè";
    }
    
    @GetMapping("/staff")
    public String hisafff() {
        return "staff nè";
    }
    
    @GetMapping("/user")
    public String hicustomer() {
        return "khách nè";
    }
}
