package com.artificerpi.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class OAuth2Controller {
  
  @RequestMapping("/oauth/error")
  public @ResponseBody String error() {
    return "Access-Denied";
  }
  
}
