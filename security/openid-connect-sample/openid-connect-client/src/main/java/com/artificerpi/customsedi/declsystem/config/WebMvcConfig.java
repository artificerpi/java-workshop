package com.artificerpi.customsedi.declsystem.config;

import org.mitre.openid.connect.web.UserInfoInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer{

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// Inject the UserInfo into the current context 
		registry.addInterceptor(new UserInfoInterceptor());
	}
	
}
