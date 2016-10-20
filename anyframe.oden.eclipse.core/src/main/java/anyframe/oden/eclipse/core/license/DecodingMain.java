package anyframe.oden.eclipse.core.license;



public class DecodingMain {
	public static void main(String[] args){
		String organiztion = "SAMSUNG SDS";
		String name = "ANYFRAME";
		String id = "";
		String license = "";
		
		DecodingLicense dl = new DecodingLicense();
		DecodingID di = new DecodingID();
		boolean b = dl.checkLicenseAvailable(organiztion, name, license);
		boolean bId = di.checkIdAvailable(id, license);
		
		System.out.println("================================");
		System.out.println("Confirm License");
		System.out.println("================================");
		System.out.println();
		System.out.print("Organiztion : ");
		System.out.println(organiztion);
		System.out.print("Name : ");
		System.out.println(name);
		System.out.print("ID : ");
		System.out.println(id);
		System.out.print("License : ");
		System.out.println(license);
		System.out.println();
		System.out.print("Available : ");
		System.out.println(b && bId);
		System.out.println("================================");
	}
}
