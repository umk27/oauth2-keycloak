package com.oauth2keyloack.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class User {

    private String id;

    private String username;

    private String email;

    private String password;


}
