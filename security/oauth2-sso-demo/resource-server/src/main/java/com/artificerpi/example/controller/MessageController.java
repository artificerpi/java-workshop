package com.artificerpi.example.controller;

import java.security.Principal;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.artificerpi.example.model.Message;

@RestController
public class MessageController {
	final List<Message> messages = Collections.synchronizedList(new LinkedList<>());

	@RequestMapping(path = "api/messages", method = RequestMethod.GET)
	List<Message> getMessages(Principal principal) {
		return messages;
	}

	@RequestMapping(path = "api/messages", method = RequestMethod.POST)
	Message postMessage(Principal principal, @RequestBody Message message) {
		message.setUsername(principal.getName());
		message.setCreatedAt(new java.util.Date());
		messages.add(0, message);
		return message;
	}
}
