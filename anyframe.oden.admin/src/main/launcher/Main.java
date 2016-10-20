import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Main {

	public static void main(String[] args) throws Exception {
		runWinstone(args);
	}

	private static void runJetty(String[] args) throws Exception {
		final File war = getHome();

		List<URL> urls = new ArrayList<URL>();
		String[] jars = new String[] { "jetty-6.1.25.jar",
				"jetty-util-6.1.25.jar", "servlet-api-2.5.jar" };
		for (String s : jars) {
			File jar = new File(war.getParent(), ".oden/" + s);
			urls.add(jar.toURL());
			jar.getParentFile().mkdirs();
			extractZip(war, s, jar);
		}

		ClassLoader loader = new URLClassLoader(urls.toArray(new URL[urls
				.size()]));
		Class webCtxCl = loader
				.loadClass("org.mortbay.jetty.webapp.WebAppContext");
		Object webCtx = webCtxCl.getDeclaredConstructor(String.class,
				String.class).newInstance(war.toURL().toExternalForm(), "/");

		Class svrCl = loader.loadClass("org.mortbay.jetty.Server");
		Object svr = svrCl.getDeclaredConstructor(int.class).newInstance(
				new Integer(9880));

		Class handlerCl = loader.loadClass("org.mortbay.jetty.Handler");
		Method setHandler = svrCl.getMethod("setHandler", handlerCl);
		setHandler.invoke(svr, webCtx);

		Method start = svrCl.getMethod("start");
		start.invoke(svr);
	}

	private static void runWinstone(String[] args) throws Exception {
		final File war = getHome();
		// setup options
		List<String> arguments = new ArrayList<String>(Arrays.asList(args));
		arguments.add("--warfile=" + war.getAbsolutePath());
		arguments.add("--commonLibFolder=.oden");
		arguments.add("--useJasper");
		arguments.add("--debug=3");

		// extract libs
		URL url = null;
		String[] jars = { "winstone-lite-0.9.10.jar", "servlet-api-2.5.jar",
				"ant-1.6.5.jar", "core-3.1.1.jar",
				"jsp-api-2.1-glassfish-2.1.v20091210.jar",
				"jsp-2.1-glassfish-2.1.v20091210.jar" };

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

	private static void removeDir(File dir) {
		if (dir == null || !dir.exists())
			return;
		for (File f : dir.listFiles())
			f.delete();
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

	public static void extractZip(File src, String entryName, File dest)
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

}
