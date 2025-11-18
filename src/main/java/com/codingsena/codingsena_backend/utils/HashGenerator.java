package com.codingsena.codingsena_backend.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashGenerator {
	
	public static String generateHash(String input) throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		byte[] hash = messageDigest.digest(input.getBytes());
		StringBuilder hexString = new StringBuilder();
		for (byte b : hash) {
			hexString.append(String.format("%02x", b));
		}
		return hexString.toString();
	}
	
}
