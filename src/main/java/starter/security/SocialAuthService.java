package starter.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUserDetails;
import org.springframework.social.security.SocialUserDetailsService;

public class SocialAuthService implements SocialUserDetailsService {
    @Autowired
    private UserDetailsService authService;
    
    @Override
    public SocialUserDetails loadUserByUserId(String userId) throws UsernameNotFoundException {
        return (SocialUserDetails) authService.loadUserByUsername(userId);
    }
}
