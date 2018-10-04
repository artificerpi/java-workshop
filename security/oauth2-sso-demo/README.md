# OAuth2 SSO Demo with Spring Boot + Spring Security OAuth2

This demo app consists of following three components:

* [Authorization](auth-server) ... OAuth2 [Authorization Server](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-security-oauth2-authorization-server)
* [Resource](resource) ... OAuth2 [Resource Server](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-security-oauth2-resource-server). Provides REST API.
* [UI](ui) ... Web UI using [SSO](http://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-security-oauth2-single-sign-on) based on OAuth2


Note that it use `RequestDumperFilter` for logging requests.

### Authorization Code Flow

![image](https://qiita-image-store.s3.amazonaws.com/0/1852/19969057-c8d1-e2d7-fd56-82fe784e7a36.png)

#### SSO (single-sign-on)


## TODO

**Client** should not use spring-security-oauth2 as an oauth2 library, which is bulky and buggy.

Use google-oauth2-client instead, a simple effective oauth2 filter is good enough for this work.

### Resource Owner Password Credentials Flow

Get an Access Token

```bash
curl -XPOST -u demo:demo localhost:8081/uaa/oauth/token -d grant_type=password -d username=user123 -d password=pass1234
# response
{"access_token":"00bc1b1a-36be-4884-855b-c7854d7b7915","token_type":"bearer","refresh_token":"06c522b3-66fc-4de1-9a0e-cd1765f8a0a2","expires_in":43199,"scope":"read write"}
```

Post a Resource

``` console
$ curl -H 'Authorization: Bearer 00bc1b1a-36be-4884-855b-c7854d7b7915' \
       -H 'Content-Type: application/json' \
       -d '{"text" : "Hello World!"}' \
       localhost:7777/api/messages
{"text":"Hello World!","username":"user","createdAt":"2016-05-16T12:48:39.466"}
```

Get Resources

``` console
$ curl -H 'Authorization: Bearer 00bc1b1a-36be-4884-855b-c7854d7b7915' localhost:7777/api/messages
[{"text":"Hello World!","username":"user","createdAt":"2016-05-16T12:48:39.466"}]
```

## Run
hot-reload
``` bash
gradle build -t
gradle bootRun
```

## Variants

* [JWT version](https://github.com/making/oauth2-sso-demo/tree/jwt)
* [Zuul integration](https://github.com/making/oauth2-sso-demo/tree/zuul) using Ajax
* [Use GitHub API instead of Authorization Server](https://github.com/making/oauth2-sso-demo/tree/github)
* [Use Google+ API instead of Authorization Server](https://github.com/making/oauth2-sso-demo/tree/google)
