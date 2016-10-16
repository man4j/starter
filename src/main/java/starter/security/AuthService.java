package starter.security;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.security.SocialUser;

import starter.model.AbstractProfile;
import starter.service.ProfileService;

public class AuthService implements UserDetailsService {
    @Autowired
    private ProfileService profileService;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        AbstractProfile profile = profileService.getById(id);

        if (profile == null) {
            throw new UsernameNotFoundException("Account " + id + " could not be found");
        }

        return new SocialUser(id, profile.getPassword(), profile.isConfirmed(), true, true, true, profile.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
    }
}
