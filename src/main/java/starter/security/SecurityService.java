package starter.security;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

import starter.model.AbstractProfile;
import starter.service.ProfileService;

public class SecurityService {
    @Autowired
    private ProfileService profileService;

    @Autowired
    private UserEnvironmentService userEnvironmentService;
    
    @Autowired
    private TokenBasedRememberMeServices tokenBasedRememberMeServices;

    public AbstractProfile getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Object principal = auth.getPrincipal();

        if (principal instanceof UserDetails) {
            return profileService.getById(((UserDetails) principal).getUsername());
        }

        return null;
    }

    public void auth(AbstractProfile profile, HttpServletRequest request, HttpServletResponse response, boolean rememberMe) {
        Authentication auth = getAuthentication(profile);
        
        SecurityContextHolder.getContext().setAuthentication(auth);
        
        userEnvironmentService.restoreUserEnvironment(profile);
        
        if (rememberMe) {
            tokenBasedRememberMeServices.onLoginSuccess(request, response, auth);
        }
    }
    
    private Authentication getAuthentication(AbstractProfile profile) {
        List<GrantedAuthority> authorities = profile.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(new User(profile.getId(), profile.getPassword(), profile.isConfirmed(), true, true, true, authorities), profile.getPassword(), authorities);
    }

    public boolean isCurrentUser(String login) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return auth.getName().equals(login);
    }

    public String getCurrentPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return auth.getName();
    }

    public List<String> getRoles() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
    }

    public String generatePassword() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10);
    }
}