package anyframe.oden.eclipse.core.license;

public class EncodingMain {

	public static void main(String[] args) {

		String org = "SAMSUNG SDS";
		String name = "ANYFRAME";

		EncodingLicence l = new EncodingLicence();
		String license = l.getLicense(org, name);

		EncodingID ei = new EncodingID(license);
		String id = ei.getID();

		System.out.println("================================");
		System.out.println("Anyframe Oden License");
		System.out.println("================================");
		System.out.println();
		System.out.print("Organiztion : ");
		System.out.println(org);
		System.out.print("Name : ");
		System.out.println(name);
		System.out.print("ID : ");
		System.out.println(id);
		System.out.print("License : ");
		System.out.println(license);
		System.out.println();
		System.out.println("================================");
	}

}
