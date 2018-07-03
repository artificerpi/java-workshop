package com.artificerpi.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@Order(-20)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
		http.formLogin()
			.loginPage("/login")
			.permitAll()
			.and()
		    .rememberMe()
		    .key("uniqueAndSecret")
			.and()
			.requestMatchers()
			.antMatchers("/", "/login", "/oauth/authorize", "/oauth/confirm_access")
			.and()
			.authorizeRequests()
			.anyRequest()
			.authenticated();
		// @formatter:on

		// @formatter:off
		http.logout()
			.logoutSuccessUrl("/")
			.logoutUrl("/logout")
			.deleteCookies("JSESSIONID")
			.permitAll();
		// @formatter:on
	}
}
