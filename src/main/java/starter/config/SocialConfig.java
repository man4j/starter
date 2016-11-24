package starter.config;

import java.util.UUID;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfigurer;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.security.AuthenticationNameUserIdSource;

@Configuration
@EnableSocial
public class SocialConfig implements SocialConfigurer {
    @Autowired
    private DataSource dataSource;
    
    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer conf, Environment env) {
        //empty
    }

    @Override
    public UserIdSource getUserIdSource() {
        return new AuthenticationNameUserIdSource();
    }

    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator locator) {
        JdbcUsersConnectionRepository repository = new JdbcUsersConnectionRepository(dataSource, locator, Encryptors.noOpText());
        
        //создаем коннекшен, даже, если юзер в системе отсутствует
        //возвращаем временный идентификатор соединения
        repository.setConnectionSignUp(c -> UUID.randomUUID().toString());
        
        return repository;
    }
}
