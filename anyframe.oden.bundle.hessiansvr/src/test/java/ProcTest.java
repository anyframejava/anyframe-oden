import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import anyframe.oden.bundle.common.FileInfo;
import anyframe.oden.bundle.common.FileUtil;


public class ProcTest {
	public static void main(String[] args) throws Exception{
		test1();
	}
	
	public static void test0() throws Exception{
//		String[] cmds = {".", "aa.sh"};
//		Process process = Runtime.getRuntime().exec(cmds, null, 
//				new File("/Applications/tomcat-6.0.2/bin"));
		String[] cmds = {"/Applications/tomcat-6.0.2/bin/catalina.sh", "run"};
		Process process = Runtime.getRuntime().exec(cmds, null, null);
		BufferedReader in = new BufferedReader(
				new InputStreamReader(process.getInputStream()) );

		StringBuffer buf = new StringBuffer();
		String s;
		while( (s = in.readLine()) != null){
			buf.append(s+"\n");
		}
		System.out.println(buf.toString());	
	}
	
	public static void test1() throws Exception{
		final String DIR = "/Users/joon1k/works/oden/anyframe.oden.admin/dist";
		
		long t = System.currentTimeMillis();
		Set<FileInfo> ret = new HashSet<FileInfo>();
		listAllFilesFileInfo(DIR, DIR, Collections.EMPTY_LIST, ret);
		System.out.println("elapsed: " + (System.currentTimeMillis()-t));
		System.out.println("# files: " + ret.size());
	}
	
	private static void listAllFilesFileInfo(String dir, String root, 
			List<String> excludes, Collection<FileInfo> ret){
		File[] files = new File(dir).listFiles();
		if(files == null) return;
		
		for(File file : files) {
			String path = file.getAbsolutePath();
			if(file.isFile()){
				String pathAfterSub = FileUtil.getRelativePath(root, path);
				if(FileUtil.matched(pathAfterSub, excludes))
					continue;
				ret.add(new FileInfo(pathAfterSub, false, 
						file.lastModified(), file.length()));
			} else {	// directory
				listAllFilesFileInfo(path, root, excludes, ret);
			}
		}

	}
	
	private static void test2(){
		String[] paths={"./", ".", ".hud", "", "../", "..", "asaf/./sdf", "ewe/../wef"};
		for(String path : paths){
			// [^
			// [^/].$, ./, 
			String x = path.replaceAll("^\\.(/|$)","");
			System.out.println(path + " >> " + x);
		}
	}
	
	private static void test3() throws IOException{
		RandomAccessFile raf = new RandomAccessFile("aa.txt", "rw");
		raf.writeChars("aaaaaaaaaa");
		raf.seek(3);
		raf.writeChars("bb");
		raf.close();
	}
	
	private static void test4() {
		String eclipseHome = System.getProperty("eclipse.home.location");
		System.out.println(eclipseHome);
		
		eclipseHome = eclipseHome.substring("file:/".length());
		File[] fs = new File(eclipseHome, "plugins").listFiles();
		for(File f : fs){
			System.out.println(f.getAbsolutePath());
		}
	}
	
	private static void test5() throws InterruptedException, IOException {
		Process proc = Runtime.getRuntime().exec(
				new String[]{
						"sh",
						"-c",
						"/Applications/tomcat-6.0.2/bin/startup.sh"});
		
		BufferedReader in = null;
		try{
			in = new BufferedReader(
					new InputStreamReader(proc.getInputStream())); 
			StreamGobbler gobbler = new StreamGobbler(
					in, 
					new PrintWriter(System.out));
			gobbler.start();
			
			proc.waitFor();
			gobbler.join();
		}finally{
			if(in != null) in.close();
		}
	}
	
}

class StreamGobbler extends Thread {
	BufferedReader in;
	PrintWriter out;
	
	public StreamGobbler(BufferedReader in, PrintWriter out){
		this.in = in;
		this.out = out;
	}
	
	@Override
	public void run(){
		try{
			String s = null;
			// TODO: stop
			while( (s = in.readLine()) != null )
				out.println(s);
		}catch(IOException e){
			e.printStackTrace(out);
		}
	}
}

