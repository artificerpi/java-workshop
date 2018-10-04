package main

import (
	"encoding/base64"
	"fmt"
	"io/ioutil"
	"log"
	"math/rand"
	"net/http"
	"net/url"
	"os"
	"time"

	"golang.org/x/oauth2"
)

const htmlIndex = `<html><body>
<a href="/login">Log in with Single sign-on</a>
</body></html>
`

var (
	oauthConfig oauth2.Config

	oauthStateString string
	checkTokenURL    string
)

func init() {
	oauthConfig = oauth2.Config{
		RedirectURL:  "http://localhost:3000/callback",
		ClientID:     os.Getenv("CLIENT_ID"),
		ClientSecret: os.Getenv("CLIENT_SECRET"),
		Scopes:       []string{"read", "write"},
		Endpoint:     oauth2.Endpoint{AuthURL: os.Getenv("AUTH_URL"), TokenURL: os.Getenv("TOKEN_URL")},
	}
	checkTokenURL = os.Getenv("CHECK_TOKEN_ENDPOINT_URL")
}

func main() {
	http.HandleFunc("/", handleMain)
	http.HandleFunc("/login", handleLogin)
	http.HandleFunc("/callback", handleCallback)

	fmt.Println("serving: http://localhost:3000")

	log.Fatal(http.ListenAndServe(":3000", nil))
}

func handleMain(w http.ResponseWriter, r *http.Request) {
	fmt.Fprintf(w, htmlIndex)
}

func handleLogin(w http.ResponseWriter, r *http.Request) {
	oauthStateString = encodeState(genRandState(), "http://localhost:3000")
	url := oauthConfig.AuthCodeURL(oauthStateString)

	log.Println("oauth2 login url is", url)

	http.Redirect(w, r, url, http.StatusTemporaryRedirect)
}

func handleCallback(w http.ResponseWriter, r *http.Request) {
	state := r.FormValue("state")
	if state != oauthStateString {
		fmt.Printf("invalid oauth state, expected '%s', got '%s'\n", oauthStateString, state)
		http.Redirect(w, r, "/", http.StatusTemporaryRedirect)
		return
	}

	code := r.FormValue("code")
	token, err := oauthConfig.Exchange(oauth2.NoContext, code)
	if err != nil {
		log.Println("Code exchange failed with", err)
		http.Redirect(w, r, "/", http.StatusTemporaryRedirect)
		return
	}

	log.Println("Got authorization code", code, ", and access token", token.AccessToken)

	if nil != checkAccessToken(token.AccessToken) {
		log.Println(err)
	}

	_, returnURL := decodeState(state)
	log.Println("Redirect to original request URL", returnURL)
	http.Redirect(w, r, returnURL, http.StatusTemporaryRedirect)

}

func checkAccessToken(accessToken string) error {
	client := &http.Client{
		Timeout: time.Second * 5,
	}

	req, err := http.NewRequest("GET", checkTokenURL+"?token="+accessToken, nil)
	req.SetBasicAuth(oauthConfig.ClientID, oauthConfig.ClientSecret)
	req.Header.Set("Accept", "application/json")
	if err != nil {
		return err
	}

	resp, err := client.Do(req)
	if err != nil {
		return err
	}

	defer resp.Body.Close()

	data, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		return err
	}

	log.Println("access token", accessToken, "is valid", string(data))

	return nil
}

func genRandState() string {
	return string(rand.Int())
}

func encodeState(state, callbackURL string) string {
	s := url.Values{}

	s.Add("security_token", state)
	s.Add("callback", callbackURL)

	queryStr := s.Encode()
	return base64.URLEncoding.EncodeToString([]byte(queryStr))
}

func decodeState(encodedState string) (state, callbackURL string) {
	queryStrData, err := base64.URLEncoding.DecodeString(encodedState)
	if err != nil {
		return
	}

	s, err := url.ParseQuery(string(queryStrData))
	if err != nil {
		return
	}

	state = s.Get("security_token")
	callbackURL = s.Get("callback")
	return
}
