package com.example.demo.jstl;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class HelloTag extends SimpleTagSupport {
  private String message;

  public void setMessage(String msg) {
    this.message = msg;
  }

  public void doTag() throws JspException, IOException {
    if (message != null) {
      JspWriter out = getJspContext().getOut();
      out.println(message);
    } else {
      getJspContext().getOut().println("Hello!");
    }
  }
}