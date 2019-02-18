package com.artificerpi.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@Order(-20)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
  
    private static final String LOGIN_URI = "/login";
    private static final String LOGOUT_URI = "/logout";
    
    
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http.formLogin()
			.loginPage(LOGIN_URI)
			.permitAll()
			.and()
		    .rememberMe()
		    .key("uniqueAndSecret")
			.and()
			.requestMatchers()
			.antMatchers("/", LOGIN_URI, "/oauth/authorize", "/oauth/confirm_access", "/oauth/error")
			.and()
			.authorizeRequests()
			.anyRequest()
			.authenticated();
		// @formatter:on

		// @formatter:off
		http.logout()
			.logoutSuccessUrl(LOGIN_URI)
			.logoutUrl(LOGOUT_URI)
			.deleteCookies("JSESSIONID")
			.permitAll();
		// @formatter:on
	}
}
