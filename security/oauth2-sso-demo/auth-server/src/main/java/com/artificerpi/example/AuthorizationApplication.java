package com.artificerpi.example;

import org.apache.catalina.filters.RequestDumperFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.web.bind.annotation.RestController;

@EnableAuthorizationServer
@SpringBootApplication
@RestController
public class AuthorizationApplication {
	@Profile("!cloud")
	@Bean
	RequestDumperFilter requestDumperFilter() {
		return new RequestDumperFilter();
	}
	
	@Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("AUTHSESSIONID"); 
        serializer.setCookiePath("/"); 
        serializer.setDomainNamePattern("^.+?\\.(\\w+\\.[a-z]+)$"); 
        return serializer;
    }

	public static void main(String[] args) {
		SpringApplication.run(AuthorizationApplication.class, args);
	}

}
