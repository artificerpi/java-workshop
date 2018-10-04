#!/bin/bash

export AUTH_SERVER="http://localhost:8081/uaa"

export CLIENT_ID=go-oauth2-client
export CLIENT_SECRET=go-oauth2-client-secret
export AUTH_URL=${AUTH_SERVER}/oauth/authorize
export TOKEN_URL=${AUTH_SERVER}/oauth/token
export CHECK_TOKEN_ENDPOINT_URL=${AUTH_SERVER}/oauth/check_token
