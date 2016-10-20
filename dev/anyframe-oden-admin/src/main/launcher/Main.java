import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Main {

	private static Map<String, String> ports;

	public static void main(String[] args) throws Exception {
		runWinstone(args);
	}

	private static void runWinstone(String[] args) throws Exception {
		final File war = getHome();

		// Oden Admin 에서 필요한 oden.port , oden.db.port 등을 얻어온다.
		getAdminPorts(args);

		// setup options
		// List<String> arguments = new ArrayList<String>(Arrays.asList(args));
		List<String> arguments = new ArrayList<String>();

		arguments.add(args[2]);
		arguments.add("--warfile=" + war.getAbsolutePath());
		arguments.add("--commonLibFolder=.oden");
		arguments.add("--useJasper");
		arguments.add("--debug=1");
		arguments.add("--directoryListings=false");

		// extract libs
		URL url = null;
		String[] jars = { "winstone-lite-0.9.10.jar", "servlet-api-2.5.jar",
				"ant-1.6.5.jar", "core-3.1.1.jar",
				"jsp-api-2.1-glassfish-2.1.v20091210.jar",
				"jsp-2.1-glassfish-2.1.v20091210.jar"};

		File tmp = new File(war.getParent(), ".oden");
		removeDir(tmp);
		for (String s : jars) {
			File jar = new File(tmp, s);
			if (url == null) {
				url = jar.toURL();
				jar.getParentFile().mkdirs();
			}
			extractZip(war, s, jar);
		}
		// chage ports if default port is not
		chageProp(war, tmp, "context.properties");

		// run winstone
		ClassLoader loader = new URLClassLoader(new URL[] { url });
		Class launcherCl = loader.loadClass("winstone.Launcher");
		Method main = launcherCl.getMethod("main",
				new Class[] { String[].class });
		getMessagePre();
		main.invoke(null, new Object[] { arguments.toArray(new String[arguments
				.size()]) });
		getMessagePost(args);

	}

	private static void getAdminPorts(String[] args) throws Exception {
		ports = new HashMap<String, String>();

		for (String arg : args) {
			if (!arg.contains("--"))
				ports.put(arg.split("=")[0], arg.split("=")[1]);
		}
	}

	private static void chageProp(File src, File home, String file)
			throws Exception {
		//default 인 경우 리턴
//		if (ports.get("oden.port").equals("9860")
//				&& ports.get("oden.db.port").equals("9001"))
//			return;
//		
		String PROPERTY_FILE_PATH = "WEB-INF/classes";
		String PROPERTY_FILE = "context.properties";
		
		File property = new File(home, PROPERTY_FILE_PATH);
		if (!property.exists())
			property.mkdirs();
		
		File dest = new File(home, file);
		extractZip(src, PROPERTY_FILE_PATH + "/" + PROPERTY_FILE, dest);
		
		Scanner input = new Scanner(dest);
		
		File property_file = new File(property, PROPERTY_FILE);
		PrintWriter output = new PrintWriter(property_file);
		
		while (input.hasNext()) {
			String s1 = input.nextLine();

			if (s1.contains("oden.port"))
				s1 = "oden.port" + "=" + ports.get("oden.port");
			if (s1.contains("oden.db.port"))
				s1 = "oden.db.port" + "=" + ports.get("oden.db.port");
			if (s1.contains("url") && !ports.get("oden.db.port").equals("9001"))
				s1 = "url" + "=" + "jdbc:hsqldb:hsql://localhost:"
						+ ports.get("oden.db.port") + "/odendb";
			output.println(s1);
		}
		input.close();
		output.close();
		
		// New context.property 압축 수행(@ root/property_file.zip)
		File f = new File("property_file.zip");
		compress(new File(home.getPath()), f);
		
		// @ root/property_file.zip 에서 해당 zipentry 추출
		ZipInputStream propertyZip = new ZipInputStream(new BufferedInputStream(
				new FileInputStream(f)));
		
		// 신규 context.properties 를 함께 재 packaging
		File result = null;
		ZipInputStream inZip = null;
		ZipOutputStream outZip = null;

		try {
			result = File.createTempFile("anyframe-oden-admin", ".war");
			
			inZip = new ZipInputStream(new BufferedInputStream(
					new FileInputStream(src)));
			outZip = new ZipOutputStream(new FileOutputStream(result));
			
			for (ZipEntry in; (in = inZip.getNextEntry()) != null;) {
				ZipEntry out = new ZipEntry(in);
				if (! in.getName().equals("WEB-INF/classes/context.properties")) {
					byte[] contentAsBytes = new byte[1024 * 64];
					out.setSize(contentAsBytes.length);
					out = in;
					
					outZip.putNextEntry(out);
					copy(inZip, outZip);
				} 
			}
			
			for (ZipEntry in; (in = propertyZip.getNextEntry()) != null;) {
				ZipEntry out = new ZipEntry(in);
				if (in.getName().equals("WEB-INF/classes/context.properties")) {
					byte[] contentAsBytes = new byte[1024 * 64];
					out.setSize(contentAsBytes.length);
					out = in;
					
					outZip.putNextEntry(out);
					copy(propertyZip, outZip);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			inZip.close();
			outZip.close();
			propertyZip.close();
		}
		InputStream resultStream = new FileInputStream(result);
		OutputStream outSrcStream = new FileOutputStream(src);
		
		copy(resultStream , outSrcStream);
		resultStream.close();
		outSrcStream.close();
		
		removeDir(result);
		removeDir(f);
	}

	private static void removeDir(File dir) {
		try {
			if (dir != null && dir.isFile()) {
					dir.delete();
				return;
			}
	
			if (dir != null && dir.exists()) {
				File[] files = dir.listFiles();
				
				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) {
						removeDir(files[i]);
					} else {
						files[i].delete();
					}
				}
				dir.delete();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private static long copy(InputStream in, OutputStream out)
			throws IOException {
		byte[] buf = new byte[1024 * 64];
		long total = 0;
		int size = 0;
		while ((size = in.read(buf)) != -1) {
			out.write(buf, 0, size);
			total += size;
		}
		
		return total;
	}

	private static void getMessagePre() throws Exception {
		String oden = "[Oden Web Admin" + " ";
		String webapp = "[webapp" + " ";
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

		System.out.println(oden + dateFormat.format(new Date())
				+ "] - Beginning extraction from war file");
		System.out.println(webapp + dateFormat.format(new Date())
				+ "] - Initializing Spring root WebApplicationContext");
		System.out.println(webapp + dateFormat.format(new Date())
				+ "] - Initializing Spring FrameworkServlet 'action'");
		System.out
				.println(oden
						+ dateFormat.format(new Date())
						+ "] - Listener winstone.ajp13.Ajp13Listener not found / disabled - ignoring");
	}

	private static void getMessagePost(String[] args) throws Exception {
		List<String> argss = new ArrayList<String>(Arrays.asList(args));
		String portNo = "";
		String oden = "[Oden Web Admin" + " ";
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

		System.out
				.println(oden
						+ dateFormat.format(new Date())
						+ "] - Listener winstone.ssl.HttpsListener not found / disabled - ignoring");
		for (String arg : argss) {
			try {
				String port[] = arg.split("=");
				portNo = port[1];
			} catch (Exception e) {
				portNo = "9880";
			}

		}
		System.out
				.println(oden
						+ dateFormat.format(new Date())
						+ "] - Winstone Servlet Engine v0.9.10 running: controlPort=disabled");

		if (args.length == 0)
			portNo = "9880";
		System.out.println(oden + dateFormat.format(new Date())
				+ "] - HTTP Listener started: port=" + portNo);
	}

	private static File getHome() throws MalformedURLException {
		URL url = new URL(Main.class.getProtectionDomain().getCodeSource()
				.getLocation().toString());
		return new File(url.getPath());
	}

	private static void extractZip(File src, String entryName, File dest)
			throws IOException {
		if (!src.exists() || src.isDirectory())
			throw new IOException("Invalid src: " + src);

		ZipFile zip = new ZipFile(src);
		ZipEntry entry = zip.getEntry(entryName);

		if (entry == null)
			throw new IOException("No such entry: " + entryName);

		InputStream in = null;
		OutputStream out = null;
		try {
			in = new BufferedInputStream(zip.getInputStream(entry));
			out = new BufferedOutputStream(new FileOutputStream(dest));
			byte[] buf = new byte[1024 * 64];
			int size = 0;
			while ((size = in.read(buf)) != -1)
				out.write(buf, 0, size);
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
			}
			try {
				if (out != null)
					out.close();
			} catch (IOException e) {
			}
		}
	}

	private static long compress(File dir, File jar) throws IOException {
		if (!dir.exists() || !dir.isDirectory())
			throw new IOException("Couldn't find: " + dir);
		if (jar.exists()) {
			if (jar.isDirectory())
				throw new IOException("same directory is existed."
						+ jar.getPath());
			jar.delete();
		}

		ZipOutputStream jout = null;
		try {
			jout = new ZipOutputStream(new BufferedOutputStream(
					new FileOutputStream(jar)));
			compressDir(dir, dir, jout);
			return jar.length();
		} finally {
			if (jout != null)
				jout.close();
		}
	}

	private static long compressDir(final File root, File dir,
			ZipOutputStream out) throws IOException {
		long total = 0;

		File[] files = dir.listFiles();
		
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					total += compressDir(root, file, out);
					continue;
				}

				InputStream in = null;
				try {
					in = new BufferedInputStream(new FileInputStream(file));
				
					ZipEntry entry = new ZipEntry(getRelativePath(root
							.getPath(), file.getPath()));
					out.putNextEntry(entry);
					
					total += copy(in, out);
					entry.setTime(file.lastModified());
				} finally {
					try {
						out.closeEntry();
					} catch (IOException e) {
					}
					try {
						if (in != null)
							in.close();
					} catch (IOException e) {
					}

				}
			}

		}
		return total;
	}
	
	public static String getRelativePath(final String root, String file) {
		String root_ = normalize(root);
		root_ = root_.startsWith("/") ? root_.substring(1) : root_;
		String file_ = normalize(file);
		file_ = file_.startsWith("/") ? file_.substring(1) : file_;
		
		if(file_.startsWith(root_)){
			String relative = file_.substring(root_.length());
			if(relative.startsWith("/"))
					relative = relative.substring("/".length());
			return relative;
		}
		return null;
	}
	
	public static String normalize(String path){
		if(path == null)
			return null;
		
		path = path.replaceAll("\\\\", "/");
		if(!path.equals("/") && path.endsWith("/"))
			path = path.substring(0, path.length()-1);
		return path;
	}
}