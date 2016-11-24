package starter.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import starter.util.HibernateString2SetConverter;

@MappedSuperclass
abstract public class AbstractProfile {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    
    @Convert(converter = HibernateString2SetConverter.class)
    private Set<String> roles = new HashSet<>();
    
    private String email;
    
    private String password;

    @Column(name = "confirm_uuid")
    private String confirmUuid;

    private boolean confirmed;

    public Set<String> getRoles() {
        return roles;
    }

    public int getId() {
        return id;
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
