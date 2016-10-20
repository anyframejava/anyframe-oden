package anyframe.oden.eclipse.core.license;

import java.util.StringTokenizer;

public class DecodingID {

	private String id;
	private String license;
	
	public boolean checkIdAvailable(String id, String license) {
		this.id = id;
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
			return check();	
		}else{
			return false;
		}
	}

	private boolean check() {// NNNNRC

		StringTokenizer token = new StringTokenizer(license, "-");
		String licenseToken1 = token.nextToken();
		String licenseToken2 = token.nextToken();
		String licenseToken3 = token.nextToken();
		String licenseToken4 = token.nextToken();

		String licenseNum1 = getChar(licenseToken1);
		String licenseNum2 = getChar(licenseToken2);
		String licenseNum3 = getChar(licenseToken3);
		String licenseNum4 = getChar(licenseToken4);

		int random = id.charAt(4) - 64;

		String binaryNum1 = fillBinaryString(Integer
				.toBinaryString(changeCharNum(licenseNum1)));
		String binaryNum2 = fillBinaryString(Integer
				.toBinaryString(changeCharNum(licenseNum2)));
		String binaryNum3 = fillBinaryString(Integer
				.toBinaryString(changeCharNum(licenseNum3)));
		String binaryNum4 = fillBinaryString(Integer
				.toBinaryString(changeCharNum(licenseNum4)));

		String strNum1 = intToString(shiftBinary(binaryNum1, random));
		String strNum2 = intToString(shiftBinary(binaryNum2, random));
		String strNum3 = intToString(shiftBinary(binaryNum3, random));
		String strNum4 = intToString(shiftBinary(binaryNum4, random));

		String result = strNum1 + strNum2 + strNum3 + strNum4;

		boolean bID = checkSameString(result);

		return bID;
	}

	private boolean checkSameString(String result) {
		String temp = "";
		for (int i = 0; i < 4; i++) {
			if (id.charAt(i) >= 48 && id.charAt(i) <= 57) {// ����
				char n = (char) (id.charAt(i) + 16); // to Big letter
				temp += n + "";
			} else {
				temp += id.charAt(i) + "";
			}
		}
		if (temp.equals(result)) {
			return true;
		} else {
			return false;
		}
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

	private int changeCharNum(String strNum) {
		int n = strNum.charAt(0);
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

	private int shiftBinary(String binary, int n) {
		char[] result = binary.toCharArray();
		char temp;
		String strResult = "";

		for (int i = 0; i < n; i++) {
			temp = result[0];
			for (int a = 0; a < binary.length() - 1; a++) {
				result[a] = result[a + 1];
			}
			result[binary.length() - 1] = temp;
		}

		for (int a = 0; a < binary.length(); a++) {
			strResult += result[a];
		}
		return Integer.parseInt(strResult, 2);
	}

	private String intToString(int n) {
		String result = "";
		
		if (n > 0 && n <= 26) {
			result = (char) (n + 64) + "";
		} else if (n > 26) {
			result = (char) (n + 70) + "";
		} else if (n == 0) {
			result = "0";
		} else {
			result = "";
		}
		
		return result;
	}

}
