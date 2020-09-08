package com.example.demo.config;

import java.util.HashMap;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.example.demo.model.User;

@Component
public class Processor implements ItemProcessor<User, User> {
	@Override
	public User process(User item) throws Exception {
		return item;
	}

	
}
