package anyframe.oden.eclipse.core.license;

import java.util.Random;
import java.util.StringTokenizer;

public class EncodingID {

	private final int binaryLength = 5;
	private String id;
	private String license;

	private String randomProcessingString;

	private String strRandom;
	private String strTemp;

	public EncodingID(String license) {
		super();
		this.license = license;
	}

	public String getRandomProcessingString() {
		return randomProcessingString;
	}

	public void setRandomProcessingString(String randomProcessingString) {
		this.randomProcessingString = randomProcessingString;
	}

	public String getID() {
		startGettingID();
		return id;
	}

	private void startGettingID() {
		String result = "";

		StringTokenizer token = new StringTokenizer(license, "-");
		String licenseToken1 = token.nextToken();
		String licenseToken2 = token.nextToken();
		String licenseToken3 = token.nextToken();
		String licenseToken4 = token.nextToken();

		String licenseNum1 = getChar(licenseToken1);
		String licenseNum2 = getChar(licenseToken2);
		String licenseNum3 = getChar(licenseToken3);
		String licenseNum4 = getChar(licenseToken4);

		// System.out.println(licenseNum1);
		// System.out.println(licenseNum2);
		// System.out.println(licenseNum3);
		// System.out.println(licenseNum4);

		result = licenseNum1 + licenseNum2 + licenseNum3 + licenseNum4;

		randomProcessingString = result;

		String binaryResult1 = fillBinaryString(Integer
				.toBinaryString(randomProcessingString.charAt(0) - 64));
		String binaryResult2 = fillBinaryString(Integer
				.toBinaryString(randomProcessingString.charAt(1) - 64));
		String binaryResult3 = fillBinaryString(Integer
				.toBinaryString(randomProcessingString.charAt(2) - 64));
		String binaryResult4 = fillBinaryString(Integer
				.toBinaryString(randomProcessingString.charAt(3) - 64));

		int random = new Random().nextInt(26) + 1; // 1 to 26
//		int random = 20; // 1 to 26

//		System.out.println("random : " + random);

		int resultNum1 = shiftBinary(binaryResult1, random);
		int resultNum2 = shiftBinary(binaryResult2, random);
		int resultNum3 = shiftBinary(binaryResult3, random);
		int resultNum4 = shiftBinary(binaryResult4, random);

		String num1 = intToString(resultNum1);
		String num2 = intToString(resultNum2);
		String num3 = intToString(resultNum3);
		String num4 = intToString(resultNum4);

		strRandom = intToString(random);

		strTemp = num1 + num2 + num3 + num4 + strRandom;

//		String check = setCheckingNumber();

//		id = strTemp + check;
		
		id = strTemp;
	}

	private String getChar(String token) {
		String c = "";
		token = token.toUpperCase();

		for (int i = 0; i < token.length(); i++) {
			if (token.charAt(i) >= 65 && token.charAt(i) <= 90) {// �����ϰ�츸
				c = token.charAt(i) + "";
			} else {
			}
		}

		if (c == null || c.equals("")) {
			c = "S";
		}
		return c;
	}

	private String fillBinaryString(String binaryString) {
		String result = "";

		int n = binaryString.length();
		String fillZero = "";

		if (binaryString.length() < binaryLength) {
			for (int i = 0; i < binaryLength - n; i++) {
				fillZero += "0";
			}
			result = fillZero + binaryString;
		} else {
			result = binaryString;
		}

		return result;
	}

	private int shiftBinary(String binary, int n) {
//		System.out.println("binary : " + binary);
		char[] result = binary.toCharArray();
		char temp;
		String strResult = "";

		for (int i = 0; i < n; i++) {
//			System.out.print(i + " : ");
			temp = result[0];
			for (int a = 0; a < binary.length() - 1; a++) {
				result[a] = result[a + 1];
			}
			result[binary.length() - 1] = temp;

//			for (int a = 0; a < result.length; a++) {
//				System.out.print(result[a]);
//			}
//			System.out.println();
		}

		for (int a = 0; a < binary.length(); a++) {
			strResult += result[a];
		}
		return Integer.parseInt(strResult, 2);
	}

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

	private String setCheckingNumber() { // XXXXX(binary)

		String num1 = checkNum1();
		String num2 = checkNum2();
		String num3 = checkNum3();
		String num4 = checkNum4();
		String num5 = checkNum5(num1, num2, num3, num4);

		String checkNum = num1 + num2 + num3 + num4 + num5;

		String result = "";
		int temp = Integer.parseInt(checkNum, 2) + 1;

		if (temp < 10) { // integer
			result = selectNumOrStr(temp);
		} else if (temp >= 10 && temp < 27) { // big letter
			result = (char) (temp + 64) + "";
		} else { // small letter
			result = (char) (temp + 70) + "";
		}
		return result;
	}

	private String checkNum1() {
		String result = "";
		String tempLicense = strTemp;

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
		String tempLicense = strTemp;

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
		String tempLicense = strTemp;

		for (int i = 0; i < tempLicense.length(); i++) {
			int n = tempLicense.charAt(i) - 48;
			if (n >= 10) {// not integer
				if ((tempLicense.charAt(i)) > 90) { // small letter
					result = 1 + "";
					break;
				} else { // big letter
					result = 0 + "";
				}
			} else {
				result = 0 + "";
			}
		}
		return result;
	}

	private String checkNum4() {
		String result = "";
		int total = 0;
		String tempLicense = strTemp;

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
