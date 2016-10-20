/*
 * Copyright 2009 SAMSUNG SDS Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package anyframe.oden.eclipse.core.license;

import java.util.Random;

/**
 * This class make Anyframe Oden License Key
 * 
 * @author LEE Sujeong
 * @version 1.0.0
 */

public class EncodingLicence {

	//	
	// [type] : RRNNN-RNRNN-NNRNN-RNNTR
	// - R : random number
	// - N : shift R times binary number of A(1) to Z(26) if R is more than two,
	// than sum of all Rs.
	// - T : checking number. 5bit binary number.(see the setCheckingNumber()
	// method in this class.)
	//
	//	  
	// [information]
	// 1) Ns are ANYFAME ODEN in order.(total 12 characters)
	// 2) most of characters are capital, but some characters are small letter.
	// - When shifting binary number of 'ANYFRAME ODEN', max number over Z(26)'s
	// binary number. So use some of small letter.
	//	 
	//	  
	// [checking point]
	// 1) license knows difference between big letter and small letter.
	// 2) license includes message("ANYFRAMEODEN")
	//	  
	//	 

	private String[] first = new String[5];
	private String[] second = new String[5];
	private String[] third = new String[5];
	private String[] fourth = new String[5];

	private String organization;
	private String name;
	private String id;
	private String license = "";
	private String randomString = "";
	
	public String getLicense(String organization, String name) {
		this.organization = organization;
		this.name = name;

		setChar();
		
		return license;
	}

	private void setChar() {
		String upperOrg = organization.toUpperCase();
		String upperName = name.toUpperCase();

		String temp = upperOrg + upperName;
		String info = "";

		for (int i = temp.length() - 1; i >= 0; i--) {
			char t = temp.charAt(i);
			if (t >= 65 && t <= 90) {// ���, Ư�����ڻ��� �� revert�� input
				info += t;
			}
		}

		if (info.length() > 12) {

			// 0, 2, 4, 6 ... ������ ¦����°�����θ� �켱����
			// ���ڶ�� 1, 3, 5, 7... ������ ä���
			// ������ 12�� subString
			for (int i = 0; i < info.length(); i += 2) {
				randomString += info.charAt(i);
			}
			if (randomString.length() > 12) {
				randomString = randomString.substring(0, 12);
				// 12�� substring
			} else if (randomString.length() < 12) {
				for (int i = 1; i < info.length(); i += 2) {
					randomString += info.charAt(i);
				}
				if (randomString.length() > 12) {
					randomString = randomString.substring(0, 12);
					// 12�� substring
				}
			} else {
			}
		} else if (info.length() == 12) {
			randomString = info;
		} else {// fill in order alphabet
			randomString = info;
			String temporary = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
			for(int i=0; i<12 - info.length(); i++){
				randomString += temporary.charAt(i);
			}
		}
		
		 startEncoding();
	}

	private void startEncoding() {
		// ù��° ��ū
		settingFirst();
		// �ι�° ��ū
		settingSecond();
		// ����° ��ū
		settingThird();
		// �׹�° ��ū
		settingFourth();
		// ���Ͽ�
		String result = resultLicense();

		license = result;
		// System.out.println("=========================");
		// System.out.println("     Anyframe Oden");
		// System.out.println("      License Key");
		// System.out.println();
		// System.out.println(license);
		// System.out.println("=========================");
	}

	private void settingFirst() {
		String binaryA = fillBinaryString(Integer.toBinaryString(randomString
				.charAt(0)-64));
		String binaryN = fillBinaryString(Integer.toBinaryString(randomString
				.charAt(1)-64));
		String binaryY = fillBinaryString(Integer.toBinaryString(randomString
				.charAt(2)-64));
		
		int random1 = getRandom();
		int random2 = getRandom();

		int intA = shiftBinary(binaryA, random1, random2);
		int intN = shiftBinary(binaryN, random1, random2);
		int intY = shiftBinary(binaryY, random1, random2);

		first[0] = intToString(random1);
		first[1] = intToString(random2);
		first[2] = intToString(intA);
		first[3] = intToString(intN);
		first[4] = intToString(intY);

	}

	private void settingSecond() {
		String binaryF = fillBinaryString(Integer.toBinaryString(randomString
				.charAt(3)-64));
		String binaryR = fillBinaryString(Integer.toBinaryString(randomString
				.charAt(4)-64));
		String binaryA = fillBinaryString(Integer.toBinaryString(randomString
				.charAt(5)-64));

		int random1 = getRandom();
		int random2 = getRandom();

		int intF = shiftBinary(binaryF, random1, random2);
		int intR = shiftBinary(binaryR, random1, random2);
		int intA = shiftBinary(binaryA, random1, random2);

		second[0] = intToString(random1);
		second[1] = intToString(intF);
		second[2] = intToString(random2);
		second[3] = intToString(intR);
		second[4] = intToString(intA);

	}

	private void settingThird() {
		String binaryM = fillBinaryString(Integer.toBinaryString(randomString
				.charAt(6)-64));
		String binaryE = fillBinaryString(Integer.toBinaryString(randomString
				.charAt(7)-64));
		String binaryO = fillBinaryString(Integer.toBinaryString(randomString
				.charAt(8)-64));
		String binaryD = fillBinaryString(Integer.toBinaryString(randomString
				.charAt(9)-64));

		int random1 = getRandom();

		int intM = shiftBinary(binaryM, random1, 0);
		int intE = shiftBinary(binaryE, random1, 0);
		int intO = shiftBinary(binaryO, random1, 0);
		int intD = shiftBinary(binaryD, random1, 0);

		third[0] = intToString(intM);
		third[1] = intToString(intE);
		third[2] = intToString(random1);
		third[3] = intToString(intO);
		third[4] = intToString(intD);

	}

	private void settingFourth() {
		String binaryE = fillBinaryString(Integer.toBinaryString(randomString
				.charAt(10)-64));
		String binaryN = fillBinaryString(Integer.toBinaryString(randomString
				.charAt(11)-64));

		int random1 = getRandom();
		int random2 = getRandom();

		int intE = shiftBinary(binaryE, random1, random2);
		int intN = shiftBinary(binaryN, random1, random2);

		fourth[0] = intToString(random1);
		fourth[1] = intToString(intE);
		fourth[2] = intToString(intN);
		fourth[3] = "0"; // temporary
		fourth[4] = intToString(random2);

		setCheckingNumber();
	}

	private String fillBinaryString(String binaryString) {
		String result = "";
		if (binaryString.length() == 5) {
			result = binaryString;
		} else if (binaryString.length() == 4) {
			result = "0" + binaryString;
		} else if (binaryString.length() == 3) {
			result = "00" + binaryString;
		} else if (binaryString.length() == 2) {
			result = "000" + binaryString;
		} else if (binaryString.length() == 1) {
			result = "0000" + binaryString;
		} else {
		}
		return result;
	}

	private String resultLicense() {
		String firstToken = arrayToString(first);
		String secondToken = arrayToString(second);
		String thirdToken = arrayToString(third);
		String fourthToken = arrayToString(fourth);

		return firstToken + "-" + secondToken + "-" + thirdToken + "-"
				+ fourthToken;
	}

	private String arrayToString(String[] strArray) {
		String result = "";
		for (int i = 0; i < 5; i++) {
			result += strArray[i];
		}
		return result;
	}

	private int getRandom() {
		int result = 0;

		Random random = new Random();
		result = random.nextInt(27); // 0~26

		return result;
	}

	private int shiftBinary(String binary, int n, int m) {

		char[] result = binary.toCharArray();
		char temp;
		String strResult = "";

		for (int i = 0; i < n + m; i++) {
			temp = result[0];
			result[0] = result[1];
			result[1] = result[2];
			result[2] = result[3];
			result[3] = result[4];
			result[4] = temp;

		}
		for (int i = 0; i < 5; i++) {
			strResult += result[i];
		}
		// binary to decimal
		return Integer.parseInt(strResult, 2);
	}

	// int to string
	private String intToString(int n) {

		String strNum = "";

		if (n > 9 && n <= 26) {
			strNum = (char) (n + 64) + "";
		} else if (n > 26) {
			strNum = (char) (n + 70) + "";
		} else if (n == 0) {
			strNum = "0";
		} else { // 0<n<10
			strNum = selectNumOrStr(n);
		}
		return strNum;
	}

	private String selectNumOrStr(int n) {
		String result = "";
		Random random = new Random();
		if (random.nextInt(2) == 0) { // num
			result = (char) (n + 64) + "";
		} else { // str
			result = n + "";
		}
		return result;
	}

	private void setCheckingNumber() { // XXXXX (binary)
		//		
		// Make 5bit binary number and convert decimal by this class's convert
		// rule.
		//		  
		// 1st bit : sum of license's numbers is even(0) or odd(1)
		// 2nd bit : sum of license's characters is even(0) or odd(1) (not
		// ascii)
		// 3rd bit : small letters are exist(1) or non-exist(0)
		// 4th bit : sum of license's all numbers and characters is even(0) or
		// odd(1) (not ascii)
		// 5th bit : sum of 4 bits is even(0) or odd(1)

		String num1 = checkNum1();
		String num2 = checkNum2();
		String num3 = checkNum3();
		String num4 = checkNum4();
		String num5 = checkNum5(num1, num2, num3, num4);

		String resultBinary = num1 + num2 + num3 + num4 + num5;
		int temp = Integer.parseInt(resultBinary, 2);
		if (temp < 10) { // integer
			fourth[3] = selectNumOrStr(temp);
		} else if (temp >= 10 && temp < 27) { // big letter
			fourth[3] = (char) (temp + 64) + "";
		} else { // small letter
			fourth[3] = (char) (temp + 70) + "";
		}
	}

	private String checkNum1() {
		String result = "";
		String tempLicense = resultLicense();

		int total = 0;

		for (int i = 0; i < tempLicense.length(); i++) {
			int n = tempLicense.charAt(i) - 48;
			if (n < 10 && n >= 0) {
				total += n;
			}
		}

		if (total % 2 == 0) { // even
			result = 0 + "";
		} else { // odd
			result = 1 + "";
		}

		return result;
	}

	private String checkNum2() {
		String result = "";
		String tempLicense = resultLicense();

		int total = 0;

		for (int i = 0; i < tempLicense.length(); i++) {
			int n = tempLicense.charAt(i) - 48;
			if (n >= 10) {// not integer
				if ((tempLicense.charAt(i)) > 90) { // small letter
					total += (tempLicense.charAt(i) - 96);
				} else { // big letter
					total += (tempLicense.charAt(i) - 64);
				}
			}
		}
		if (total % 2 == 0) { // even
			result = 0 + "";
		} else { // odd
			result = 1 + "";
		}
		return result;
	}

	private String checkNum3() {
		String result = "";
		String tempLicense = resultLicense();

		for (int i = 0; i < tempLicense.length(); i++) {
			int n = tempLicense.charAt(i) - 48;
			if (n >= 10) {// not integer
				if ((tempLicense.charAt(i)) > 90) { // small letter
					result = 1 + "";
					break;
				} else { // big letter
					result = 0 + "";
				}
			}
		}
		return result;
	}

	private String checkNum4() {
		String result = "";
		int total = 0;
		String tempLicense = resultLicense();

		for (int i = 0; i < tempLicense.length(); i++) {
			int n = tempLicense.charAt(i) - 48;
			if (n < 10 && n >= 0) {
				if ((tempLicense.charAt(i)) > 90) { // small letter
					total += (tempLicense.charAt(i) - 96);
				} else { // big letter
					total += (tempLicense.charAt(i) - 64);
				}
			} else {// not integer
				if ((tempLicense.charAt(i)) > 90) { // small letter
					total += (tempLicense.charAt(i) - 96);
				} else { // big letter
					total += (tempLicense.charAt(i) - 64);
				}
			}
		}
		if (total % 2 == 0) { // even
			result = 0 + "";
		} else { // odd
			result = 1 + "";
		}
		return result;
	}

	private String checkNum5(String num1, String num2, String num3, String num4) {
		String result = "";
		int n = Integer.parseInt(num1) + Integer.parseInt(num2)
				+ Integer.parseInt(num3) + Integer.parseInt(num4);
		if (n % 2 == 0) { // even
			result = 0 + "";
		} else { // odd
			result = 1 + "";
		}
		return result;
	}
}
