 package com.example.demo.jstl;

 import java.io.IOException;

import javax.servlet.jsp.tagext.SimpleTagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

 public class HelloTag extends SimpleTagSupport {
	 @Override
     public void doTag() throws JspException, IOException {
       JspWriter out = getJspContext().getOut();
       out.println("Hello Custom Tag!");
     }
  }