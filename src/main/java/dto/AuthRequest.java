package dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class AuthRequest {

    @NotBlank(message = "Username cannot be empty")
    private String username;

    @NotBlank(message = "Email address cannot be empty")
    @Email(message = "Please provide valid email address")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    private String password;
}
