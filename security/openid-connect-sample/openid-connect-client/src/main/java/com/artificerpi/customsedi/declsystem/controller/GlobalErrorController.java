package com.artificerpi.customsedi.declsystem.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class GlobalErrorController implements ErrorController {
	
	@Override
	public String getErrorPath() {
		return "/error";
	}
	
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
    	Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
         
            if(statusCode == HttpStatus.NOT_FOUND.value()) {
                return "redirect:/error-404";
            }
            else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "redirect:/error-500";
            }
        }
        
    	return "redirect:/error-default";
    }
    
    @RequestMapping("/error-404")
    public @ResponseBody String handleError404() {
    	return ":(, sorry, we couldn't find the page you were looking for.";
    }

    @RequestMapping("/error-500")
    public @ResponseBody String handleError500() {
    	return ":<, sorry, something wrong is with the server. You may contact us to report this issue.";
    }
    
    @RequestMapping("/error-default")
    public @ResponseBody String handleDefaultError() {
    	return "Oops, something is wrong.";
    }
}
