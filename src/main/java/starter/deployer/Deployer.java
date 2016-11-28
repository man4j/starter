package starter.deployer;

import java.util.Collections;
import java.util.Set;

import javax.servlet.DispatcherType;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;

import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.SpringServletContainerInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

import io.undertow.Undertow;
import io.undertow.Undertow.Builder;
import io.undertow.server.HttpHandler;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletContainerInitializerInfo;

public class Deployer {
    protected DeploymentInfo deploymentInfo = Servlets.deployment();
    
    protected Builder serverBuilder = Undertow.builder();
    
    private Undertow server;
    
    protected Set<Class<?>> configureSpringInitializers() {
        return Collections.emptySet();
    }
    
    protected void configureContextParams() {
        deploymentInfo.addInitParameter("contextClass", "org.springframework.web.context.support.AnnotationConfigWebApplicationContext")                      
                      .addInitParameter("defaultHtmlEscape", "true");//enable html escape in spring.ftl (see spring.bind)        
    }
    
    protected void configureFilters() {
        deploymentInfo.addFilter(Servlets.filter("charset", CharacterEncodingFilter.class).addInitParam("encoding", "UTF-8").addInitParam("forceEncoding", "true"))
                      .addFilter(Servlets.filter("springSecurityFilterChain", DelegatingFilterProxy.class))
                      .addFilterServletNameMapping("charset", "dispatcher", DispatcherType.REQUEST)
                      .addFilterServletNameMapping("springSecurityFilterChain", "dispatcher", DispatcherType.REQUEST);
    }
    
    protected void configureListeners() {
        deploymentInfo.addListener(Servlets.listener(RequestContextListener.class))
                      .addListener(Servlets.listener(ContextLoaderListener.class))
                      .addListener(Servlets.listener(HttpSessionEventPublisher.class));
    }
    
    protected void configureServlets() {
        deploymentInfo.addServlet(Servlets.servlet("dispatcher", DispatcherServlet.class).addInitParam("contextClass", "org.springframework.web.context.support.AnnotationConfigWebApplicationContext")
                      .addInitParam("contextConfigLocation", "starter.config.DispatcherConfig")
                      .setMultipartConfig(new MultipartConfigElement(null, 10485760, 10485760, 1048576))
                      .addMapping("/")
                      .setLoadOnStartup(1));
    }
    
    protected void configureContextPath() {
        deploymentInfo.setContextPath("/");
    }
    
    protected void configureDeploymentName() {
        deploymentInfo.setDeploymentName("test.war");
    }
    
    protected void configureServer() {
        serverBuilder.addHttpListener(8080, "0.0.0.0");
    }
    
    public void deploy() {
        Set<Class<?>> springInitializerHandleTypes = configureSpringInitializers();
        
        deploymentInfo.addServletContainerInitalizer(new ServletContainerInitializerInfo(SpringServletContainerInitializer.class, springInitializerHandleTypes));
        
        configureContextParams();
        configureFilters();
        configureListeners();
        configureServlets();
        configureContextPath();
        configureDeploymentName();    
        configureServer();
        
        deploymentInfo.setClassLoader(this.getClass().getClassLoader());
        
        DeploymentManager manager = Servlets.defaultContainer().addDeployment(deploymentInfo);
        
        manager.deploy();

        HttpHandler handler;
        
        try {
            handler = manager.start();
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }

        server = serverBuilder.setHandler(handler).build(); 

        server.start();
    }
    
    public void undeploy() {
        server.stop();
    }
}

