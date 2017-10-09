package com.example.demo;

import java.util.ArrayList;
import java.util.List;

public class FakeDB {
	private static List<String> messages = new ArrayList<String>();

	public static void addMessage(String msg) {
		messages.add(msg);
	}

	public static List<String> getMessages() {
		return messages;
	}
}
