package com.artificerpi.samples.oidcclient.oidc;

import org.mitre.openid.connect.client.OIDCAuthenticationFilter;
import org.mitre.openid.connect.client.service.impl.DynamicServerConfigurationService;
import org.mitre.openid.connect.client.service.impl.HybridIssuerService;
import org.mitre.openid.connect.client.service.impl.PlainAuthRequestUrlBuilder;
import org.mitre.openid.connect.client.service.impl.StaticAuthRequestOptionsService;
import org.mitre.openid.connect.client.service.impl.StaticClientConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

@EnableGlobalMethodSecurity(securedEnabled=true)
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	@Autowired
	private AuthenticationEntryPoint authenticationEntryPoint;
	@Autowired
	private AuthenticationProvider openIdConnectAuthenticationProvider;
	@Autowired
	private HybridIssuerService hybridIssuerService;
	@Autowired
	private DynamicServerConfigurationService dynamicServerConfigurationService;
	@Autowired
	private StaticClientConfigurationService staticClientConfigurationService;
	@Autowired
	private StaticAuthRequestOptionsService staticAuthRequestOptionsService;
	@Autowired
	private PlainAuthRequestUrlBuilder plainAuthRequestUrlBuilder;
	
	@Override
    protected void configure(HttpSecurity http) throws Exception {
	     // @formatter:off
		 http.requestMatchers()
		 		.antMatchers("/login", OpenIDConnectConfig.LOGIN_FORM_URL)
		 		.and()
		 	.authorizeRequests()
            	.anyRequest().authenticated()
             	.and()
			.addFilterBefore(openIdConnectAuthenticationFilter(), AbstractPreAuthenticatedProcessingFilter.class)
		 	.logout();
		 // @formatter:on
		 
	     http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
    }

	@Bean(name = BeanIds.AUTHENTICATION_MANAGER)
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
    protected void configure(
      AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(openIdConnectAuthenticationProvider);
    }
	
	/**
	 * The authentication filter
	 * @return
	 * @throws Exception
	 */
	@Bean
	public OIDCAuthenticationFilter openIdConnectAuthenticationFilter() throws Exception {
		OIDCAuthenticationFilter filter = new OIDCAuthenticationFilter();
		filter.setAuthenticationManager(authenticationManagerBean());
		filter.setIssuerService(hybridIssuerService);
		filter.setServerConfigurationService(dynamicServerConfigurationService);
		filter.setClientConfigurationService(staticClientConfigurationService);
		filter.setAuthRequestOptionsService(staticAuthRequestOptionsService);
		filter.setAuthRequestUrlBuilder(plainAuthRequestUrlBuilder);
		
		return filter;
	}
}
