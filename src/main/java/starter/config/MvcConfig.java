package starter.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import starter.profile.ApplicationProfile;
import starter.util.AutoEscapeFileTemplateLoader;
import starter.util.FileUtils;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateModelException;

@Configuration
@EnableWebMvc
@EnableAsync
@ComponentScan({"starter.email"})
public class MvcConfig extends WebMvcConfigurerAdapter implements AsyncConfigurer {
    private static volatile long lastModified;

    @Autowired
    private ApplicationProfile applicationProfile;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/" + getLastModified() + "/**").addResourceLocations("classpath:/public/").setCachePeriod(31556926);
    }

    private long getLastModified() {
        if (lastModified == 0) {
            lastModified = FileUtils.getLastModified("/public");
        }
        
        return lastModified;
    }
    
    @Override
    public Validator getValidator() {
        LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
        
        validatorFactoryBean.setValidationMessageSource(messageSource());
        
        return validatorFactoryBean;
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver resolver = new SessionLocaleResolver();

        resolver.setDefaultLocale(new Locale("ru"));

        return resolver;
    }
    
    @Bean
    public FreeMarkerConfigurer freeMarkerConfigurer() throws TemplateModelException {
        FreeMarkerConfigurer fmc = new FreeMarkerConfigurer();
        
        freemarker.template.Configuration config = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_21);
        
        BeansWrapper beansWrapper = new BeansWrapperBuilder(freemarker.template.Configuration.VERSION_2_3_21).build();
        
        SimpleHash simpleHash = new SimpleHash(beansWrapper);
        
        simpleHash.put("version", getLastModified() + "");
        simpleHash.put("enum", beansWrapper.getEnumModels());
        simpleHash.put("static", beansWrapper.getStaticModels());
        
        MultiTemplateLoader tl = new MultiTemplateLoader(new TemplateLoader[] {new AutoEscapeFileTemplateLoader("classpath:/templates"), 
                new AutoEscapeFileTemplateLoader("classpath:/org/springframework/web/servlet/view/freemarker")});
        
        config.setAutoFlush(false);
        config.setDefaultEncoding(StandardCharsets.UTF_8.name());
        config.setOutputEncoding(StandardCharsets.UTF_8.name());
        config.setObjectWrapper(beansWrapper);
        config.setTemplateUpdateDelay(applicationProfile.isTemplateCacheEnabled() ? 999999 : 0);
        config.setTemplateLoader(tl);
        config.setAllSharedVariables(simpleHash);
                
        fmc.setConfiguration(config);
        
        return fmc;
    }
    
    @Bean
    public ViewResolver viewResolver() {
        FreeMarkerViewResolver viewResolver = new FreeMarkerViewResolver();
        
        viewResolver.setSuffix(".ftl");
        viewResolver.setContentType("text/html;charset=UTF-8");
        viewResolver.setRequestContextAttribute("rc");
        viewResolver.setCache(applicationProfile.isTemplateCacheEnabled());        
        
        return viewResolver;
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();

        messageSource.setBasenames("classpath:messages/messages", "classpath:messages/validation");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(applicationProfile.getMessagesCacheInterval());// # -1 : never reload, 0 always reload

        return messageSource;
    }
        
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        
        converter.setObjectMapper(mapper());
        
        converters.add(converter);
    }
    
    @Bean
    public ObjectMapper mapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        JSR310Module jsr310Module = new JSR310Module();
        
        jsr310Module.addSerializer(LocalDate.class, new JsonSerializer<LocalDate>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            
            @Override
            public void serialize(LocalDate value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
                jgen.writeString(value.format(formatter));                
            }
        });
        
        jsr310Module.addDeserializer(LocalDate.class, new JsonDeserializer<LocalDate>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            
            @Override
            public LocalDate deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                return LocalDate.parse(jp.getText(), formatter);
            }
        });
        
        mapper.registerModule(jsr310Module);
        
        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        mapper.setVisibility(PropertyAccessor.GETTER, Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.IS_GETTER, Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.SETTER, Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.CREATOR, Visibility.NONE);
        mapper.setSerializationInclusion(Include.NON_EMPTY);
        
        return mapper;
    }
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/closeWindow").setViewName("close_window");
        registry.addViewController("/accessDenied").setViewName("access_denied");
    }

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        executor.setCorePoolSize(1);        
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(Integer.MAX_VALUE);
        executor.setThreadNamePrefix("MySpringAsyncExecutor-");
        executor.initialize();
        
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;//for default
    }
    
    @Bean
    public StandardServletMultipartResolver multipartResolver(){
        return new StandardServletMultipartResolver();
    }
}
