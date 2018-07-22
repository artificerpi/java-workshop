package com.artificerpi.samples.oidcclient.oidc;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.mitre.jose.keystore.JWKSetKeyStore;
import org.mitre.jwt.signer.service.impl.DefaultJWTSigningAndValidationService;
import org.mitre.jwt.signer.service.impl.JWKSetCacheService;
import org.mitre.oauth2.model.ClientDetailsEntity.AuthMethod;
import org.mitre.oauth2.model.RegisteredClient;
import org.mitre.openid.connect.client.NamedAdminAuthoritiesMapper;
import org.mitre.openid.connect.client.OIDCAuthenticationProvider;
import org.mitre.openid.connect.client.SubjectIssuerGrantedAuthority;
import org.mitre.openid.connect.client.keypublisher.ClientKeyPublisher;
import org.mitre.openid.connect.client.service.impl.DynamicRegistrationClientConfigurationService;
import org.mitre.openid.connect.client.service.impl.DynamicServerConfigurationService;
import org.mitre.openid.connect.client.service.impl.EncryptedAuthRequestUrlBuilder;
import org.mitre.openid.connect.client.service.impl.HybridClientConfigurationService;
import org.mitre.openid.connect.client.service.impl.HybridIssuerService;
import org.mitre.openid.connect.client.service.impl.HybridServerConfigurationService;
import org.mitre.openid.connect.client.service.impl.PlainAuthRequestUrlBuilder;
import org.mitre.openid.connect.client.service.impl.SignedAuthRequestUrlBuilder;
import org.mitre.openid.connect.client.service.impl.StaticAuthRequestOptionsService;
import org.mitre.openid.connect.client.service.impl.StaticClientConfigurationService;
import org.mitre.openid.connect.client.service.impl.StaticServerConfigurationService;
import org.mitre.openid.connect.client.service.impl.StaticSingleIssuerService;
import org.mitre.openid.connect.client.service.impl.ThirdPartyIssuerService;
import org.mitre.openid.connect.client.service.impl.WebfingerIssuerService;
import org.mitre.openid.connect.config.ServerConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import com.google.common.collect.Sets;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWSAlgorithm;

@Configuration
public class OpenIDConnectConfig {
	public static final String LOGIN_FORM_URL = "/openid_connect_login";
	private static final String NAMED_ADMINS_SUBJECT = "90342.ASDFJWFA";
	private static final String ISSUER = "http://localhost:8080/openid-connect-server-webapp/";
	private static final String AUTHORIZATION_ENDPOINT_URI = "http://localhost:8080/openid-connect-server-webapp/authorize";
	private static final String TOKEN_ENDPOINT_URI = "http://localhost:8080/openid-connect-server-webapp/token";
	private static final String USER_INFO_URI = "http://localhost:8080/openid-connect-server-webapp/userinfo";
	private static final String JWKS_URI = "http://localhost:8080/openid-connect-server-webapp/jwk";
	
	@Bean
	public AuthenticationEntryPoint authenticationEntryPoint() {
		return new LoginUrlAuthenticationEntryPoint(LOGIN_FORM_URL);
	}
	
	@Bean
	public OIDCAuthenticationProvider openIdConnectAuthenticationProvider() {
		OIDCAuthenticationProvider authenticationProvider = new OIDCAuthenticationProvider();
		NamedAdminAuthoritiesMapper authoritiesMapper = new NamedAdminAuthoritiesMapper();
		authoritiesMapper.setAdmins(namedAdmins());
		authenticationProvider.setAuthoritiesMapper(authoritiesMapper);
		
		return authenticationProvider;
	}
	
	@Bean(name = "namedAdmins")
	public Set<SubjectIssuerGrantedAuthority> namedAdmins(){
		SubjectIssuerGrantedAuthority admin = new SubjectIssuerGrantedAuthority(NAMED_ADMINS_SUBJECT, ISSUER);
		Set<SubjectIssuerGrantedAuthority> admins = Sets.newHashSet(admin);
		
		return admins;
	}
	
	// Issuer Services: Determine which identity provider issuer is used.
	
	/**
	 * Static issuer service, returns the same issuer for every request.
	 * @return
	 */
	@Bean
	public StaticSingleIssuerService staticIssuerService() {
		StaticSingleIssuerService issuerService = new StaticSingleIssuerService();
		issuerService.setIssuer(ISSUER);
		
		return issuerService;
	}
	
	/**
	 * WebFinger issuer service, does OpenID Connect Discovery on user-entered text (received from the
		loginPageUrl page) to find the issuer. The login page needs to return the user-entered text
		as the "identifier" parameter as a query parameter.
	 * @return
	 */
	@Bean
	public WebfingerIssuerService webfingerIssuerService() {
		WebfingerIssuerService webfingerIssuerService = new WebfingerIssuerService();
		webfingerIssuerService.setLoginPageUrl("login");
		return webfingerIssuerService;
	}
	
	/**
	 * Third-party (account chooser) issuer service. Looks for the "iss" parameter on the request
		and returns that as the issuer. If there is no "iss" value, redirects to the configured
		account chooser URI. This URI should direct back to the login filter URL with an
		"iss" value as a query parameter. 
	 * @return
	 */
	@Bean
	public ThirdPartyIssuerService thirdPartyIssuerService() {
		ThirdPartyIssuerService thirdPartyIssuerService = new ThirdPartyIssuerService();
		thirdPartyIssuerService.setAccountChooserUrl("http://localhost/account-chooser/");
		return thirdPartyIssuerService;
	}
	
	/**
	 * Hybrid issuer service. If an issuer is passed in directly with the "iss" parameter, it will use that. If not, it will
		look for an "identifier" parameter to do Webfinger discovery on that. Failing that, it will redirect to the login
		page URL.
	 * @return
	 */
	@Bean
	public HybridIssuerService hybridIssuerService() {
		HybridIssuerService hybridIssuerService = new HybridIssuerService();
		hybridIssuerService.setLoginPageUrl("login");
		hybridIssuerService.setForceHttps(false);
		
		return hybridIssuerService;
	}
	
	
	// Server configuration: determines the parameters and URLs of the server to talk to.
	
	/**
	 * Static server configuration, contains a map of server configuration objects keyed by the issuer URL.
	 * @return
	 */
	@Bean
	public StaticServerConfigurationService staticServerConfigurationService() {
		StaticServerConfigurationService service = new StaticServerConfigurationService();
		service.setServers(servers());
		return service;
	}
	
	/**
	 * Dynamic server configuration, fetches the server's information using OIDC Discovery.
	 * @return
	 */
	@Bean
	public DynamicServerConfigurationService dynamicServerConfigurationService() {
		return new DynamicServerConfigurationService();
	}
	
	/**
	 * Hybrid server configuration. Tries to look up a statically configured server in the map, does
		dynamic OIDC Discovery if the static lookup fails.
	 * @return
	 */
	@Bean
	public HybridServerConfigurationService hybridServerConfigurationService() {
		HybridServerConfigurationService service = new HybridServerConfigurationService();
		service.setServers(servers());
		return service;
	}
	
	
	// Client Configuration: Determine which client identifier and credentials are used.
	/**
	 * Dynamic Client Configuration, uses dynamic client registration. This version stores the registered
	    clients in an in-memory map. To override, add a bean to the registeredClientService property.
	 * @return
	 */
	@Bean
	public DynamicRegistrationClientConfigurationService dynamicRegistrationClientConfigurationService() {
		DynamicRegistrationClientConfigurationService service = new DynamicRegistrationClientConfigurationService();
		
		RegisteredClient template = template();
		template.setRequestObjectSigningAlg(JWSAlgorithm.RS256);
		template.setJwksUri("http://localhost:9090/simple-web-app/jwk");
		
		service.setTemplate(template());
		
//		Registered Client Service. Uncomment this to save dynamically registered clients out to a
//		file on disk (indicated by the filename property) or replace this with another implementation
//		of RegisteredClientService. This defaults to an in-memory implementation of RegisteredClientService
//		which will forget and re-register all clients on restart.
//		
//		String filename = "/tmp/simple-web-app-clients.json";
//		service.setRegisteredClientService(new JsonFileRegisteredClientService(filename));
		
		return service;
	}
	
	/**
	 * Static Client Configuration. Configures a client statically by storing configuration on a per-issuer basis.
	 * @return
	 */
	@Bean
	public StaticClientConfigurationService staticClientConfigurationService() {
		StaticClientConfigurationService service = new StaticClientConfigurationService();
		service.setClients(clients());
		
		return service;
	}
	
	/**
	 * Hybrid Client Configuration. Tries to configure a client statically first, but if a client isn't found in the map,
		it will dynamically configure one. 
	 * @return
	 */
	@Bean
	public HybridClientConfigurationService hybridClientConfigurationService() {
		HybridClientConfigurationService service = new HybridClientConfigurationService();
		service.setClients(clients());
		service.setTemplate(template());
		
//		Registered Client Service. Uncomment this to save dynamically registered clients out to a
//		file on disk (indicated by the filename property) or replace this with another implementation
//		of RegisteredClientService. This defaults to an in-memory implementation of RegisteredClientService
//		which will forget and re-register all clients on restart.
//		
//		String filename = "/tmp/simple-web-app-clients.json";
//		service.setRegisteredClientService(new JsonFileRegisteredClientService(filename));
		
		return service;
	}
	
	/**
	 * Auth request options service: returns the optional components of the request
	 * @return
	 */
	@Bean
	public StaticAuthRequestOptionsService staticAuthRequestOptionsService() {
		StaticAuthRequestOptionsService service = new StaticAuthRequestOptionsService();
		
		Map<String, String> options = Collections.emptyMap();
		
//		// Entries in this map are sent as key-value parameters to the auth request
//		ImmutableMap<String, String> options = ImmutableMap.<String, String>builder()
//				.put("display", "page")
//				.put("max_age", "30")
//				.put("prompt", "none")
//				.build();
	
		service.setOptions(options);
		
		return service;
	}
	
	// Authorization URL Builders: create the URL to redirect the user to for authorization.
	/**
	 * Plain authorization request builder, puts all options as query parameters on the GET request
	 * @return
	 */
	@Bean
	public PlainAuthRequestUrlBuilder plainAuthRequestUrlBuilder() {
		return new PlainAuthRequestUrlBuilder();
	}
	
	/**
	 * Signed authorization request builder, puts all options as elements in a JWS-signed request object 
	 * @return
	 * @throws InvalidKeySpecException 
	 * @throws NoSuchAlgorithmException 
	 */
	@Bean
	public SignedAuthRequestUrlBuilder signedAuthRequestUrlBuilder() throws NoSuchAlgorithmException, InvalidKeySpecException {
		SignedAuthRequestUrlBuilder signedAuthRequestUrlBuilder = new SignedAuthRequestUrlBuilder();
		signedAuthRequestUrlBuilder.setSigningAndValidationService(defaultSignerService());
		
		return signedAuthRequestUrlBuilder;
	}
	
	/**
	 * Encrypted authorization request builder, puts all the options as elements in a JWE-encrypted request object
	 * @return
	 */
	@Bean
	public EncryptedAuthRequestUrlBuilder encryptedAuthRequestUrlBuilder() {
		EncryptedAuthRequestUrlBuilder builder = new EncryptedAuthRequestUrlBuilder();
		builder.setEncrypterService(validatorCache());
		builder.setAlg(JWEAlgorithm.RSA1_5);
		builder.setEnc(EncryptionMethod.A128GCM);
		
		return builder;
	}
	
	

	//  Utility beans for the above classes
	
	/**
	 * This service fetches and caches JWK sets from URLs.
	 * @return
	 */
	@Bean
	public JWKSetCacheService validatorCache() {
		return new JWKSetCacheService();
	}
	
	/**
	 * 	This service sets up a bunch of signers and validators based on our own keys.
		TODO Replace this keystore's contents for a production deployment.
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	@Bean
	public DefaultJWTSigningAndValidationService defaultSignerService() throws NoSuchAlgorithmException, InvalidKeySpecException {
		DefaultJWTSigningAndValidationService defaultSignerService = new DefaultJWTSigningAndValidationService(defaultKeyStore());
		defaultSignerService.setDefaultSignerKeyId("rsa1");
		defaultSignerService.setDefaultSigningAlgorithmName("RS256");
		
		return defaultSignerService;
	}
	
	@Bean
	public JWKSetKeyStore defaultKeyStore() {
		JWKSetKeyStore defaultKeyStore = new JWKSetKeyStore();
		defaultKeyStore.setLocation(new ClassPathResource("keystore.jwks"));
		
		return defaultKeyStore;
	}
	
	/**
	 * This service publishes the client's public key on a the endpoint "jwk" off the root of this client.
	 * @return
	 * @throws InvalidKeySpecException 
	 * @throws NoSuchAlgorithmException 
	 */
	@Bean
	public ClientKeyPublisher clientKeyPublisher() throws NoSuchAlgorithmException, InvalidKeySpecException {
		ClientKeyPublisher clientKeyPublisher = new ClientKeyPublisher();
		clientKeyPublisher.setJwkPublishUrl("jwk");
		clientKeyPublisher.setSigningAndValidationService(defaultSignerService());
		
		return clientKeyPublisher;
	}

	
	private Map<String, ServerConfiguration> servers() {
		String key = "http://localhost:8080/openid-connect-server-webapp/";
		
		ServerConfiguration serverConfig = new ServerConfiguration();
		serverConfig.setIssuer(ISSUER);
		serverConfig.setAuthorizationEndpointUri(AUTHORIZATION_ENDPOINT_URI);
		serverConfig.setTokenEndpointUri(TOKEN_ENDPOINT_URI);
		serverConfig.setUserInfoUri(USER_INFO_URI);
		serverConfig.setJwksUri(JWKS_URI);
		
		return Collections.singletonMap(key, serverConfig);
	}
	
	
	private Map<String, RegisteredClient> clients() {
		String key = "http://localhost:8080/openid-connect-server-webapp/";
		Set<String> scope = Sets.newHashSet("openid", "email", "address", "profile", "phone");
		Set<String> redirectUris = Sets.newHashSet("http://localhost:9090/simple-web-app/openid_connect_login");
		
		RegisteredClient client = new RegisteredClient();
		client.setClientId("client");
		client.setClientSecret("secret");
		client.setScope(scope);
		client.setTokenEndpointAuthMethod(AuthMethod.SECRET_BASIC);
		client.setRedirectUris(redirectUris);
		
		Map<String, RegisteredClient> clients = Collections.singletonMap(key, client);
				
		return clients;
	}
	
	private RegisteredClient template() {
		Set<String> scope = Sets.newHashSet("openid", "email", "address", "profile", "phone");
		Set<String> redirectUris = Sets.newHashSet("http://localhost:9090/simple-web-app/openid_connect_login");
		
		RegisteredClient template = new RegisteredClient();
		template.setClientName("Simple Web App");
		template.setScope(scope);
		template.setTokenEndpointAuthMethod(AuthMethod.SECRET_BASIC);
		template.setRedirectUris(redirectUris);
		
		return template;
	}

}
