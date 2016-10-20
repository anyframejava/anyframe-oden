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
package org.anyframe.oden.bundle.auth;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Properties;

import org.anyframe.oden.bundle.http.SecurityHandler;
import org.apache.commons.codec.binary.Base64;

/**
 * Main class to generate oden accounts
 * 
 * @author Junghwan Hong
 */
public class Account {
	// final static String CONFIG_FILE = "conf/oden.ini";

	public static void main(String... args) {
		if (args.length != 2) {
			System.out.println("Usage: <id> <pwd>");
			System.exit(-1);
		}

		File accFile = null;
		try {
			accFile = new File(odenHome(), SecurityHandler.ACCOUNT_FILE);

			String encoded = encode(args[0], args[1]);
			writeToFile(accFile, args[0], encoded);
		} catch (Exception e) {
			try {
				System.out.println(Account.class.getProtectionDomain()
						.getCodeSource().getLocation().toURI()
						+ "");
			} catch (Exception ee) {
			}
			System.out.println("Fail to register account. " + e.getMessage());
			System.exit(-1);
		}
		System.out.println(args[0] + " is registered: " + accFile);
	}

	private static String encode(String id, String pwd)
			throws UnsupportedEncodingException {
		return new String(Base64.encodeBase64((id + ":" + pwd).getBytes()),
				"ASCII");
	}

	private static void writeToFile(File f, String id, String encoded)
			throws IOException {
		Properties prop = new Properties();

		if (f.exists()) {
			InputStream in = null;
			try {
				in = new BufferedInputStream(new FileInputStream(f));
				prop.load(in);
			} finally {
				if (in != null) {
					in.close();
				}
			}
		}

		prop.put(id, encoded);

		OutputStream out = null;
		try {
			out = new BufferedOutputStream(new FileOutputStream(f));
			prop.store(out, null);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	public static File odenHome() {
		try {
			URL url = new URL(Account.class.getProtectionDomain()
					.getCodeSource().getLocation().toString());
			return new File(url.getPath()).getParentFile().getParentFile();
		} catch (Exception e) {
			return new File("..");
		}
	}
}
