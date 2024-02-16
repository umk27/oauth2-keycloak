package com.oauth2keyloack.keycloak;

import com.oauth2keyloack.model.User;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class KeycloakUtils {

    @Value("${keycloak.auth-server-url}")
    private String serverURL;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.resource}")
    private String clientID;

    @Value("${keycloak.credentials.secret}")
    private String clientSecret;


    private static Keycloak keycloak;
    private static RealmResource realmResource;
    private static UsersResource usersResource;


    @PostConstruct
    public Keycloak getInstance() {

        if (keycloak == null) {
            keycloak = KeycloakBuilder.builder()
                    .realm(realm)
                    .serverUrl(serverURL)
                    .clientId(clientID)
                    .clientSecret(clientSecret)
                    .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                    .build();

            realmResource = keycloak.realm(realm);
            usersResource = realmResource.users();

        }

        return keycloak;
    }

    public Response createKeycloakUser(User user) {

        RealmResource realmResource = getInstance().realm(realm);

        UsersResource usersResource = realmResource.users();

        CredentialRepresentation credentialRepresentation = createPasswordCredentials(user.getPassword());

        UserRepresentation kcUser = new UserRepresentation();

        kcUser.setUsername(user.getUsername());
        kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
        kcUser.setEmail(user.getEmail());
        kcUser.setEnabled(true);
        kcUser.setEmailVerified(false);


        Response response = usersResource.create(kcUser);

        return response;
    }


    private CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }


    public void addRoles(String userId, List<String> roles) {

        List<RoleRepresentation> kcRoles = new ArrayList<>();

        for (String role : roles) {
            RoleRepresentation roleRep = realmResource.roles().get(role).toRepresentation();
            kcRoles.add(roleRep);
        }

        UserResource uniqueUserResource = usersResource.get(userId);

        uniqueUserResource.roles().realmLevel().add(kcRoles);
    }

    public void deleteKeycloakUser(String userId) {

        UserResource uniqueUserResource = usersResource.get(userId);
        uniqueUserResource.remove();
    }

    public UserRepresentation findUserById(String userId) {

        return usersResource.get(userId).toRepresentation();
    }

    public List<UserRepresentation> searchKeycloakUsers(String text) {

        return usersResource.searchByAttributes(text);
    }

    public void updateKeycloakUser(User user){

        CredentialRepresentation credentialRepresentation = createPasswordCredentials(user.getPassword());

        UserRepresentation kcUser = new UserRepresentation();
        kcUser.setUsername(user.getUsername());
        kcUser.setCredentials(Collections.singletonList(credentialRepresentation));
        kcUser.setEmail(user.getEmail());

        UserResource uniqueUserResource = usersResource.get(user.getId());
        uniqueUserResource.update(kcUser);
    }
}
