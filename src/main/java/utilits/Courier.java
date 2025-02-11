package utilits;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Courier {
    private String login;
    private String password;
    private String firstName;

    public Courier(String login, String password, boolean firstName) {
        this.login = login;
        this.password = password;
    }

    public Courier(String login, boolean password, String firstName) {
        this.login = login;
        this.firstName = firstName;
    }

    public Courier(String login, boolean password) {
        this.login = login;
    }

    public Courier(boolean login, String password) {
        this.password = password;
    }

    public Courier() {
    }
}
