package com.artificerpi.samples.oidcclient.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.BeanIds;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

// Root Context: defines shared resources visible to all other web components 

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

	@Qualifier(BeanIds.AUTHENTICATION_MANAGER)
	@Autowired
    private AuthenticationManager authenticationManager;
	
	@Override
    protected AuthenticationManager authenticationManager() {
        return authenticationManager;
    }
	
	@Override
	protected MethodSecurityExpressionHandler createExpressionHandler() {
		DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
		return expressionHandler;
	}
}
