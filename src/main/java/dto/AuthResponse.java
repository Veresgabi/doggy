package dto;

import lombok.Data;
import model.User;

@Data
public class AuthResponse extends AbstractResponse {

    User user;
    public AuthResponse(String message, User user) {
        super(message);
        this.user = user;
    }

    public AuthResponse() { }
}
