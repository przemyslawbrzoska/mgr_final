package mgr.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RequestDTO {

    public String timestamp;
    public String receiver;
    public String ID;
    public String NS;
    public String count;
}
