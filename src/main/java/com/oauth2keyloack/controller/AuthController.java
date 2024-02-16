package com.oauth2keyloack.controller;

import com.oauth2keyloack.keycloak.KeycloakUtils;
import com.oauth2keyloack.model.User;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final KeycloakUtils keycloakUtils;


    public AuthController(KeycloakUtils keycloakUtils) {
        this.keycloakUtils = keycloakUtils;
    }

    @PostMapping("/add")
    public ResponseEntity add(@RequestBody User user) {

        Response createdResponse = keycloakUtils.createKeycloakUser(user);

        if (createdResponse.getStatus() == 409) {
            return new ResponseEntity("user or email already exists " + user.getEmail(), HttpStatus.CONFLICT);
        }

        String id = CreatedResponseUtil.getCreatedId(createdResponse);
        System.out.println(id);

        List<String> defaultRoles = new ArrayList<>();

        defaultRoles.add("user");
        defaultRoles.add("admin");
        keycloakUtils.addRoles(id, defaultRoles);

        return ResponseEntity.status(createdResponse.getStatus()).build();
    }

    @PostMapping("/deletebyid")
    public ResponseEntity deleteUserById(@RequestBody String userId) {

        keycloakUtils.deleteKeycloakUser(userId);

        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/id")
    public ResponseEntity<UserRepresentation> findById(@RequestBody String userId){
        return ResponseEntity.ok(keycloakUtils.findUserById(userId));
    }

    @PostMapping("/search")
    public ResponseEntity<List<UserRepresentation>> search(@RequestBody String text){
        return ResponseEntity.ok(keycloakUtils.searchKeycloakUsers(text));
    }

    @PutMapping("/update")
    public ResponseEntity update(@RequestBody User user){

        if(user.getId() == null){
            return new ResponseEntity<>("missed param: id", HttpStatus.NOT_ACCEPTABLE);
        }

        keycloakUtils.updateKeycloakUser(user);
        return new ResponseEntity(HttpStatus.OK);
    }
}
