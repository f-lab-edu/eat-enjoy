package com.restaurant.eatenjoy.util;

import java.security.MessageDigest;

import org.springframework.stereotype.Component;

@Component
public class SHA256Encrypt implements EncryptUtil {

	@Override
	public String encrypt(String msg) {

		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			byte[] hash = messageDigest.digest(msg.getBytes("UTF-8"));

			StringBuffer hexString = new StringBuffer();

			for (int i = 0; i < hash.length; i++) {
				String hexValue = Integer.toHexString(0xff & hash[i]);

				if (hexValue.length() == 1) {
					hexString.append("0");
				}

				hexString.append(hexValue);
			}

			return hexString.toString();
		} catch (Exception e) {
			throw new RuntimeException("null algorithm name " + e);
		}
	}
}
