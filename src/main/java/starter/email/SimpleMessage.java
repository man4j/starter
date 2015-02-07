package starter.email;

import java.io.Serializable;

public class SimpleMessage implements Serializable {
    private String fromEmail;
    
    private String fromPersonal;

    private String to;

    private String body;

    private String subject;

    public SimpleMessage(String fromEmail, String fromPersonal, String to, String body, String subject) {
        this.fromEmail = fromEmail;
        this.fromPersonal = fromPersonal;
        this.to = to;
        this.body = body;
        this.subject = subject;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String getFromPersonal() {
        return fromPersonal;
    }

    public void setFromPersonal(String fromPersonal) {
        this.fromPersonal = fromPersonal;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
