package com.oauth2keyloack.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/user")
public class AdminController {

   /* @GetMapping("/login")
    public String login() {
        return "Login work";
    }*/

    @GetMapping("/work")
    public String work() {
        return "Work!";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable(name = "id") String id, @AuthenticationPrincipal Jwt jwt) {
        System.out.println("jwt" + jwt.getClaim("email"));
        System.out.println("id delete " + id);
        return "delete " + id;
    }

    @GetMapping("/add")
    public String add() {
        return "add";
    }

    @GetMapping("/view")
    public String view() {
        return "view work";
    }
}
