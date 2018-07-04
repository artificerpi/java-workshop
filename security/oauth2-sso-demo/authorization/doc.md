# 文档

## HttpSecurity
```java
http.formLogin()
            // set login url to /login
			.loginPage("/login").permitAll()
            .and()
            // allow rememberMe login
		    .rememberMe().key("uniqueAndSecret")
            .and()
            // ant matcher to apply HttpSecurity, TODO
			.requestMatchers().antMatchers("/**", "/login", "/oauth/authorize", "/oauth/confirm_access")
            .and()
            // filter all request for authentication, this should be put in the end
			.authorizeRequests().anyRequest().authenticated();
```


## 参考
* https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/config/annotation/web/builders/HttpSecurity.html