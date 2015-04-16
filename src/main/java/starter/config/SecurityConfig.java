package starter.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.social.security.SpringSocialConfigurer;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true, proxyTargetClass = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsService authService;
    
    @Autowired
    private SimpleUrlAuthenticationSuccessHandler rememberMeAuthenticationSuccessHandler;
    
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        configureMatchers(http);
        configureRememberMe(http);
        configureSocial(http);
        configureLogout(http);
        
        http.csrf().disable()                                
                   .exceptionHandling().authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/auth/signin"))
                                       .accessDeniedPage("/accessDenied");
    }
    
    protected void configureLogout(HttpSecurity http) throws Exception {
        http.logout();
    }

    protected void configureSocial(HttpSecurity http) throws Exception {
        http.apply(new SpringSocialConfigurer()).postLoginUrl("/closeWindow").alwaysUsePostLoginUrl(true);
    }
    
    protected void configureRememberMe(HttpSecurity http) throws Exception {
        http.rememberMe().rememberMeServices(rememberMeServices())
                         .userDetailsService(authService)
                         .authenticationSuccessHandler(rememberMeAuthenticationSuccessHandler)
                         .key("secret key for encrypt cookie");               
    }
    
    protected void configureMatchers(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/closeWindow").permitAll()
                                .antMatchers("/accessDenied").permitAll()
                                .antMatchers("/logout").permitAll()
                                .antMatchers("/auth/**").anonymous()                                
                                .anyRequest().authenticated();
    }
    
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(authService);
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new StandardPasswordEncoder("salt");        
    }
    
    /**
     * Явное создание бина, чтобы можно было пользоваться из контроллеров и сервисов 
     */
    @Bean
    public TokenBasedRememberMeServices rememberMeServices() {
        return new TokenBasedRememberMeServices("secret key for encrypt cookie", authService);
    }
    
    /**
     * https://jira.spring.io/browse/SEC-2477
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
         return super.authenticationManagerBean();
    }
}
