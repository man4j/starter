package starter.config;

import java.util.HashMap;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;

import starter.profile.ApplicationProfile;

@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@ComponentScan({"starter.dao"})
public abstract class DbConfig {
    @Autowired
    private ApplicationProfile profile;

    @Bean(destroyMethod = "close")
    public DataSource dataSource() {
        HikariDataSource ds = new HikariDataSource();
        
        ds.setJdbcUrl(profile.getDbUrl() + "/" + profile.getDbName());
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUsername(profile.getDbUser());
        ds.setMaximumPoolSize(50);
        ds.setPassword(profile.getDbPassword());

        ds.addDataSourceProperty("useSSL", "false");
        ds.addDataSourceProperty("characterEncoding", "UTF-8");
        ds.addDataSourceProperty("cachePrepStmts", "true");
        ds.addDataSourceProperty("prepStmtCacheSize", "250");
        ds.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        
        return ds;
    }
    
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
       LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
       
       em.setDataSource(dataSource());
       HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
       jpaVendorAdapter.setShowSql(profile.isShowSql());
       
       em.setJpaPropertyMap(new HashMap<String, Object>() {{put("hibernate.id.new_generator_mappings", true);
                                                            put("hibernate.dialect", "org.hibernate.dialect.MySQL57InnoDBDialect");
                                                            put("hibernate.format_sql", true);
                                                            put("hibernate.jdbc.fetch_size", 100);
                                                            put("hibernate.jdbc.batch_size", 1000);
                                                            put("hibernate.order_inserts", true);
                                                            put("hibernate.order_updates", true);
                                                          }});
       em.setJpaVendorAdapter(jpaVendorAdapter);
       em.setPackagesToScan(packagesToScan().toArray(new String[] {}));
       
       return em;
    }
    
    @Bean
    public PlatformTransactionManager txManager() {
        return new JpaTransactionManager(entityManagerFactory().getObject());
    }
    
    public abstract Set<String> packagesToScan();
}
