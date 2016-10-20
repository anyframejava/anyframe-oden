package anyframe.oden.admin.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hsqldb.Server;
import org.hsqldb.util.SqlFile;
import org.springframework.beans.factory.InitializingBean;

public class HsqlDB implements InitializingBean {
	private String dbName = "odendb";
	private Log logger = LogFactory.getLog(this.getClass());
	private String url = "jdbc:hsqldb:hsql://localhost/" + dbName;
	private static Server hsqlServer;

	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		// HSQL DB Active 확인
		try {
			isServerActive();
		} catch (Exception e) {
			// Active 서버 없음
			startServer();
		}
	}

	public void shutdown() {
		if (hsqlServer != null)
			hsqlServer.shutdown();
	}

	private void startServer() {
		if (!isDirectory())
			new File(dbName).mkdirs();

		hsqlServer = new Server();

		try {
			hsqlServer.setLogWriter(null);
			hsqlServer.setSilent(true);

			hsqlServer.setDatabaseName(0, dbName);
			hsqlServer.setDatabasePath(0, "file:" + dbName);

			hsqlServer.start();

			if (hsqlServer.getStateDescriptor().equals("ONLINE"))
				logger.info("Invoking HSQL DB Started");

			Connection con = null;

			try {
				if (!isFileScript()) {
					Class.forName("org.hsqldb.jdbcDriver");
					con = DriverManager.getConnection(url, "sa", "");
					getInitData(con);
				}

			} finally {
				if (con != null) {
					con.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private Connection getInitData(Connection con) throws IOException {

		InputStream resourceAsStream = getClass().getResourceAsStream(
				"/sql/initialdb.sql");
		if (resourceAsStream != null) {
			System.setIn(resourceAsStream);
			try {
				SqlFile file = new SqlFile(null, true, null);
				file.execute(con, true);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				resourceAsStream.close();
			}
		}
		return con;
	}

	private boolean isFileScript() {
		String[] exts = { ".log", ".properties", ".script" };

		for (String ext : exts) {
			File dbFile = new File(dbName + ext);
			if (!dbFile.exists())
				return false;
		}
		return true;
	}

	private boolean isDirectory() {
		return new File(dbName).exists();
	}

	private boolean isServerActive() throws Exception {
		return new Socket("localhost", 9001).isBound();
	}
}