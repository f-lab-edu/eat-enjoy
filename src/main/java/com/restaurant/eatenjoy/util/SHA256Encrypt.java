package com.restaurant.eatenjoy.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Component;

@Component
public class SHA256Encrypt implements EncryptUtil {

	@Override
	public String encrypt(String msg) {

		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			byte[] hash = messageDigest.digest(msg.getBytes("UTF-8"));

			StringBuilder hexString = new StringBuilder();

			for (int i = 0; i < hash.length; i++) {
				String hexValue = Integer.toHexString(0xff & hash[i]);

				if (hexValue.length() == 1) {
					hexString.append("0");
				}

				hexString.append(hexValue);
			}

			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("null algorithm name ", e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("지원하지 않는 문자셋 입니다 ", e);
		}
	}
}
