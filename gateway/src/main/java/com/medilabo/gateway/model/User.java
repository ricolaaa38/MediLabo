package com.medilabo.gateway.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * User is a model class that represents a user in the system. It contains fields for the user's ID, email, password, and role.
 * The class is annotated with @Document to indicate that it should be stored in a MongoDB collection named "users".
 */
@Document(collection = "users")
@Data
public class User {

    @Id
    private String id;

    @NotBlank
    @Email
    @Indexed(unique = true)
    private String email;

    private String password;

    @NotNull
    private Role role;

    public User(String id, String email, String password, Role role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
