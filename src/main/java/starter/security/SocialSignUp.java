package starter.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;

import starter.model.AbstractProfile;

public class SocialSignUp implements ConnectionSignUp {
    @Autowired
    private ProfileService profileService;

    @Autowired
    private SecurityService securityService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public String execute(Connection<?> connection) {
        String userId = connection.getKey().toString();
        
        AbstractProfile profile = profileService.getById(userId);

        if (profile == null) {
            profileService.createProfile(userId, "", passwordEncoder.encode(securityService.generatePassword()), true);
        }

        return userId;
    }
}
