/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.anyframe.oden.admin.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hsqldb.Server;
import org.hsqldb.util.SqlFile;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class HsqlDB implements InitializingBean {
	private String dbName = "odendb";
	private Log logger = LogFactory.getLog(this.getClass());
	private String url;
	private static Server hsqlServer;
	private int port;
	
	protected static ApplicationContext context;
	
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		// HSQL DB Active 확인
		setup();
		
		try {
			isServerActive();
		} catch (Exception e) {
			// Active 서버 없음
			startServer();
		}
		
	}
	
	/**
	 * initializing
	 */
	private void setup() {
		context = new ClassPathXmlApplicationContext("classpath:spring/context-property.xml");
		Map key = (Map) context.getBean("contextProperties");
		
		port = Integer.valueOf((String) key.get("oden.db.port"));
		url = (String) key.get("url");
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
			
			logger.info("HSQL DB Set port" + " " + port);
			hsqlServer.setPort(port);
			
			hsqlServer.start();
			
			if (hsqlServer.getStateDescriptor().equals("ONLINE")) {
				logger.info("Invoking HSQL DB Started" + " " + "port:" + hsqlServer.getPort());
				logger.info("hsqlServer Address:" +" " + hsqlServer.getAddress());
			}
			
			Connection con = null;

			try {
				if (!isFileScript()) {
					Class.forName("org.hsqldb.jdbcDriver");
					System.out.println("DB url:" + " " + url);
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
		return new Socket("localhost", port).isBound();
	}
}