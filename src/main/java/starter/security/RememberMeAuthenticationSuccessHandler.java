package starter.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

public class RememberMeAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Autowired
    private UserEnvironmentService userEnvironmentService;

    @Autowired
    private SecurityService securityService;
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        super.onAuthenticationSuccess(request, response, authentication);

        userEnvironmentService.restoreUserEnvironment(securityService.getCurrentUser());
    }
    
    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        return request.getRequestURL().toString() + (request.getQueryString() == null ? "" : "?" + request.getQueryString());
    }
}
