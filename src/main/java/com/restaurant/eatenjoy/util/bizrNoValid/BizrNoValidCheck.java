package com.restaurant.eatenjoy.util.bizrNoValid;

import java.util.stream.Stream;

import com.restaurant.eatenjoy.exception.BizrNoValidException;

public class BizrNoValidCheck {

	private static final int[] bizrNoValidKey = new int[] { 1, 3, 7, 1, 3, 7, 1, 3, 5 };

	public static boolean valid(String bizrNo) {

		bizrNo = bizrNo.replace("-", "");

		if (bizrNo.length() != 10) {
			throw new BizrNoValidException("사업자 등록 번호는 10자리 입니다");
		}

		long chkValue = 0;
		int sum = 0;
		long plusValue = 0;

		long bizrNoToLong = Long.parseLong(bizrNo);

		long [] bizrNoToLongArray = Stream.of(String.valueOf(bizrNoToLong).split("")).mapToLong(Long::parseLong).toArray();

		for (int i = 0; i < bizrNoValidKey.length; i++) {
			// 사업자 번호 9자리와 인증키 9자리를 각각 곱하여 모두 더한다
			sum += bizrNoToLongArray[i] * bizrNoValidKey[i];

			if (i == bizrNoValidKey.length -1) {
				// 사업자 번호 마지막 자리와 인증키의 마지막 자리 값을 곱하고 10으로 나눈다
				plusValue = bizrNoToLongArray[i] * bizrNoValidKey[i] / 10;
			}
		}

		// chkValue값이 사업자 번호 마지막 자리와 같아야지 유효한 사업자 번호
		chkValue = 10 - ((sum + plusValue) % 10);

		String stringChkValue = Long.toString(chkValue);
		String endOfBizrNoString = bizrNo.substring(bizrNo.length() - 1);

		// chkValue값이 사업자 번호 마지막 자리와 같아야지 유효한 사업자 번호
		return stringChkValue.equals(endOfBizrNoString);
	}
}
