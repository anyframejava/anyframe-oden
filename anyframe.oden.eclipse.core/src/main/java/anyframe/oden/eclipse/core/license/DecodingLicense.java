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

import java.util.StringTokenizer;

/**
 * This class check Anyframe Oden License Key is available.
 * 
 * @author LEE Sujeong
 * @version 1.0.0
 */
public class DecodingLicense {
	private String organization;
	private String name;
	private String license;

	private boolean boolLicense;

	private static String first;
	private static String second;
	private static String third;
	private static String fourth;

	private static String total;

	public boolean checkLicenseAvailable(String organization, String name, String license) {
		this.organization = organization;
		this.name = name;
		total = "";
		
		return check(license);
	}

	private boolean check(String license) {
		this.license = license;

		
		int i=0;
		int n=0;
		while(i<4){
			if(license.substring(n).indexOf("-")!=-1){
				i++;
				n=license.substring(n).indexOf("-");
			}else{
				break;
			}
		}
		
		if(i==4){
			// ��ū���� parsing�ϱ�
			parsingLicense();
			// ù��° ��ū
			analyzeFirst();
			// �ι�° ��ū
			analyzeSecond();
			// ����° ��ū
			analyzeThird();
			// �׹�° ��ū
			analyzeFourth();
			// üũ����Ʈ Ȯ���ϱ�
			confirmCheckPoint();
		}else{
			boolLicense = false;
		}
		return boolLicense;
	}

	private void parsingLicense() {
		StringTokenizer token = new StringTokenizer(license, "-");
		first = token.nextToken();
		second = token.nextToken();
		third = token.nextToken();
		fourth = token.nextToken();
	}

	private void analyzeFirst() { // RRNNN

		int random1 = changeCharNum(first.charAt(0));
		int random2 = changeCharNum(first.charAt(1));

		String binaryNum1 = fillBinaryString(Integer
				.toBinaryString(changeCharNum(first.charAt(2))));
		String binaryNum2 = fillBinaryString(Integer
				.toBinaryString(changeCharNum(first.charAt(3))));
		String binaryNum3 = fillBinaryString(Integer
				.toBinaryString(changeCharNum(first.charAt(4))));

		String strNum1 = intToString(shiftBinary(binaryNum1, random1, random2));
		String strNum2 = intToString(shiftBinary(binaryNum2, random1, random2));
		String strNum3 = intToString(shiftBinary(binaryNum3, random1, random2));

		total = total + strNum1 + strNum2 + strNum3;
	}

	private void analyzeSecond() { // RNRNN

		int random1 = changeCharNum(second.charAt(0));
		int random2 = changeCharNum(second.charAt(2));

		String binaryNum1 = fillBinaryString(Integer
				.toBinaryString(changeCharNum(second.charAt(1))));
		String binaryNum2 = fillBinaryString(Integer
				.toBinaryString(changeCharNum(second.charAt(3))));
		String binaryNum3 = fillBinaryString(Integer
				.toBinaryString(changeCharNum(second.charAt(4))));

		String strNum1 = intToString(shiftBinary(binaryNum1, random1, random2));
		String strNum2 = intToString(shiftBinary(binaryNum2, random1, random2));
		String strNum3 = intToString(shiftBinary(binaryNum3, random1, random2));

		total = total + strNum1 + strNum2 + strNum3;
	}

	private void analyzeThird() { // NNRNN

		int random1 = changeCharNum(third.charAt(2));

		String binaryNum1 = fillBinaryString(Integer
				.toBinaryString(changeCharNum(third.charAt(0))));
		String binaryNum2 = fillBinaryString(Integer
				.toBinaryString(changeCharNum(third.charAt(1))));
		String binaryNum3 = fillBinaryString(Integer
				.toBinaryString(changeCharNum(third.charAt(3))));
		String binaryNum4 = fillBinaryString(Integer
				.toBinaryString(changeCharNum(third.charAt(4))));

		String strNum1 = intToString(shiftBinary(binaryNum1, random1, 0));
		String strNum2 = intToString(shiftBinary(binaryNum2, random1, 0));
		String strNum3 = intToString(shiftBinary(binaryNum3, random1, 0));
		String strNum4 = intToString(shiftBinary(binaryNum4, random1, 0));

		total = total + strNum1 + strNum2 + strNum3 + strNum4;
	}

	private void analyzeFourth() { // RNNTR

		int random1 = changeCharNum(fourth.charAt(0));
		int random2 = changeCharNum(fourth.charAt(4));

		String binaryNum1 = fillBinaryString(Integer
				.toBinaryString(changeCharNum(fourth.charAt(1))));
		String binaryNum2 = fillBinaryString(Integer
				.toBinaryString(changeCharNum(fourth.charAt(2))));

		String strNum1 = intToString(shiftBinary(binaryNum1, random1, random2));
		String strNum2 = intToString(shiftBinary(binaryNum2, random1, random2));

		total = total + strNum1 + strNum2;
	}

	private String intToString(int n) {
		String result = "";
		result = (char) (n + 64) + "";
		return result;
	}

	private int changeCharNum(int n) {
		int result = 0;
		if (n >= 48 && n <= 57) {// ����
			result = n - 48;
		} else if (n >= 65 && n <= 90) {// �빮��
			result = n - 64;
		} else if (n >= 97) {// �ҹ���
			result = n - 70;
		} else {

		}
		return result;
	}

	private static String fillBinaryString(String binaryString) {
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

	private static int shiftBinary(String binary, int n, int m) {

		char[] result = binary.toCharArray();
		char temp;
		String strResult = "";

		for (int i = 0; i < n + m; i++) {
			temp = result[4];
			result[4] = result[3];
			result[3] = result[2];
			result[2] = result[1];
			result[1] = result[0];
			result[0] = temp;

		}
		for (int i = 0; i < 5; i++) {
			strResult += result[i];
		}
		// binary to decimal
		return Integer.parseInt(strResult, 2);
	}

	private void confirmCheckPoint() {
		boolean a = checkMessage();
		boolean b = useCheckingNumber();

		boolLicense = a && b;
	}

	private boolean checkMessage() {// �޽��� ����ֳ�
		String message = getMessage();
		
		if (total.equals(message)) {
			return true;
		} else {
			return false;
		}
	}

	private String getMessage() {
		String upperOrg = organization.toUpperCase();
		String upperName = name.toUpperCase();

		String temp = upperOrg + upperName;
		String info = "";
		
		String randomString = "";

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
		
		return randomString;
	}

	private boolean useCheckingNumber() { // �׹�° ��ū �׹�° ����
		char c = license.charAt(license.length() - 2);
		int n = c;
		int checkNum = 0;

		if (n >= 48 && n <= 57) {// ����
			checkNum = n - 48;
		} else if (n >= 65 && n <= 90) {// �빮��
			checkNum = n - 64;
		} else if (n >= 97) {// �ҹ���
			checkNum = n - 70;
		} else {
		}

		String binary = fillBinaryString(Integer.toBinaryString(checkNum));

		String num1 = binary.charAt(0) + "";
		String num2 = binary.charAt(1) + "";
		String num3 = binary.charAt(2) + "";
		String num4 = binary.charAt(3) + "";
		String num5 = binary.charAt(4) + "";

		//Num5 check
		boolean checkNum5;

		int temp = Integer.parseInt(num1) + Integer.parseInt(num2)
				+ Integer.parseInt(num3) + Integer.parseInt(num4);

		if (temp % 2 == 0) { // even
			if (num5.equals("0")) {
				checkNum5 = true;
			} else {
				checkNum5 = false;
			}
		} else { // odd
			if (num5.equals("1")) {
				checkNum5 = true;
			} else {
				checkNum5 = false;
			}
		}

		boolean n1 = checkNum1(num1);
		boolean n2 = checkNum2(num2);
		boolean n3 = checkNum3(num3);
		boolean n4 = checkNum4(num4);

		return n1 && n2 && n3 && n4 && checkNum5;
	}

	private boolean checkNum1(String num1) {
		int sum = 0;
		for (int i = 0; i < license.length(); i++) {
			int n = license.charAt(i) - 48;
			if (n < 10 && n >= 0) {
				sum += n;
			}
		}

		if (license.charAt(license.length() - 2) < 58) { // integer
			sum -= (license.charAt(license.length() - 2) - 48);
		}

		if (sum % 2 == 0) { // even
			if (num1.equals("0")) {
				return true;
			} else {
				return false;
			}
		} else { // odd
			if (num1.equals("1")) {
				return true;
			} else {
				return false;
			}
		}
	}

	private boolean checkNum2(String num2) {
		int sum = 0;
		for (int i = 0; i < license.length(); i++) {
			int n = license.charAt(i) - 48;
			if (n >= 10) {// not integer
				if ((license.charAt(i)) > 90) { // small letter
					sum += (license.charAt(i) - 96);
				} else { // big letter
					sum += (license.charAt(i) - 64);
				}
			}
		}
		int n = license.length();
		if ((license.charAt(n - 2)) > 90) { // small letter
			sum -= (license.charAt(n - 2) - 96);
		} else if ((license.charAt(n - 2) <= 90)
				&& (license.charAt(n - 2) >= 65)) { // big letter
			sum -= (license.charAt(n - 2) - 64);
		} else {
		}
		if (sum % 2 == 0) { // even
			if (num2.equals("0")) {
				return true;
			} else {
				return false;
			}
		} else { // odd
			if (num2.equals("1")) {
				return true;
			} else {
				return false;
			}
		}
	}

	private boolean checkNum3(String num3) {
		boolean result = false;
		for (int i = 0; i < license.length(); i++) {
			int n = license.charAt(i) - 48;
			if (n >= 10) {// not integer
				if ((license.charAt(i)) > 90) { // small letter
					if (num3.equals("1")) {
						result = true;
						break;
					} else {
						result = false;
					}
				} else { // big letter
					if (num3.equals("0")) {
						result = true;
					} else {
						result = false;
					}
				}
			}
		}
		return result;
	}

	private boolean checkNum4(String num4) {

		int sum = 0;
		for (int i = 0; i < license.length(); i++) {
			int n = license.charAt(i) - 48;
			if (n < 10 && n >= 0) {
				if ((license.charAt(i)) > 90) { // small letter
					sum += (license.charAt(i) - 96);
				} else { // big letter
					sum += (license.charAt(i) - 64);
				}
			} else {// not integer
				if ((license.charAt(i)) > 90) { // small letter
					sum += (license.charAt(i) - 96);
				} else { // big letter
					sum += (license.charAt(i) - 64);
				}
			}
		}
		int n = license.length();

		if ((license.charAt(n - 2)) > 90) { // small letter
			sum -= (license.charAt(n - 2) - 96);
		} else if ((license.charAt(n - 2) <= 90)
				&& (license.charAt(n - 2) >= 65)) { // big letter
			sum -= (license.charAt(n - 2) - 64);
		} else {
			sum -= (license.charAt(n - 2) - 48);
		}

		if (sum % 2 == 0) { // even
			if (num4.equals("0")) {
				return true;
			} else {
				return false;
			}
		} else { // odd
			if (num4.equals("1")) {
				return true;
			} else {
				return false;
			}
		}
	}

}
