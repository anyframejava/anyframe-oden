package anyframe.oden.bundle.common;

import java.util.Arrays;

import anyframe.oden.bundle.common.FileUtil;

public class FileUtilTest {
	
	public static void main(String[] args) {
		try {
			
			toRegexTest();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private static void toRegexTest() {
		System.out.println(FileUtil.toRegex("/Users/asdf/**"));
		System.out.println(FileUtil.toRegex("**/*"));
		System.out.println(FileUtil.toRegex("*"));
		System.out.println(FileUtil.matched(
				"C:\\eclipse\\plugins\\webapp\\asdf.xml", 
				Arrays.asList(new String[]{"**"}) ) );
		
		System.out.println(FileUtil.matched(
				"/Users/asdf/aa/asfds.xml", 
				Arrays.asList(new String[]{"/Users", "/Users/**/aa/*.xml"}) ) );
		
		System.out.println(FileUtil.matched(
				"eclipse\\plugins\\webapp\\", 
				Arrays.asList(new String[]{"**/app/*", "**/plugins/**"}) ) );
		
		System.out.println(FileUtil.matched(
				"C:\\eclipse\\plugins\\webap", 
				Arrays.asList(new String[]{"**/*"}) ) );
	}
	
}
