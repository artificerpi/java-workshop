package com.artificerpi.example;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.apache.catalina.filters.RequestDumperFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;

@SpringBootApplication
public class UiApplication {

	@Profile("!cloud")
	@Bean
	RequestDumperFilter requestDumperFilter() {
		return new RequestDumperFilter();
	}

  @Bean
  OAuth2RestTemplate oauth2RestTemplate(OAuth2ProtectedResourceDetails details,
      OAuth2ClientContext oauth2ClientContext) {
    OAuth2RestTemplate template = new OAuth2RestTemplate(details, oauth2ClientContext);

    return template;
  }
	
	@Bean
	public ServletContextInitializer servletContextInitializer() {
	    return new ServletContextInitializer() {

          public void onStartup(ServletContext servletContext) throws ServletException {
            servletContext.getSessionCookieConfig().setName("UISESSIONID");
            
          }
	    };

	}
	
	public static void main(String[] args) {
		SpringApplication.run(UiApplication.class, args);
	}

}
