package com.restaurant.eatenjoy.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256Util {

	private SHA256Util() { }

	public static String encrypt(String value) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] passBytes = value.getBytes();
			md.reset();
			byte[] digested = md.digest(passBytes);
			StringBuilder sb = new StringBuilder();
			for (byte b : digested) {
				sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
			}

			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("유효하지 않은 암호화 알고리즘 입니다.", e);
		}
	}

}
