package dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public abstract class AbstractResponse {

    private String message;

    public AbstractResponse() { }
}
