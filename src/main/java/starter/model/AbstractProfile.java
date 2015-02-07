package starter.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

abstract public class AbstractProfile {
    private Set<String> roles = new HashSet<>();

    private String id;
    
    private String email;
    
    private String password;

    private String confirmUuid = UUID.randomUUID().toString();

    private boolean confirmed;

    public Set<String> getRoles() {
        return roles;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmUuid() {
        return confirmUuid;
    }

    public void setConfirmUuid(String confirmUuid) {
        this.confirmUuid = confirmUuid;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }
}
