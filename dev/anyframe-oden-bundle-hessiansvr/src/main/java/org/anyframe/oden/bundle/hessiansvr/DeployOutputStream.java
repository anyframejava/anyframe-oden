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
package org.anyframe.oden.bundle.hessiansvr;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.anyframe.oden.bundle.common.FileUtil;
import org.anyframe.oden.bundle.common.OdenException;
import org.anyframe.oden.bundle.deploy.DoneFileInfo;

/**
 * This class make files with BufferedOutputStream class.
 * 
 * @author Junghwan Hong
 */
public class DeployOutputStream {
	private final static String TMP_PREFIX = "oden_";

	private String parentPath;
	private String filePath;
	private OutputStream out;
	private File tmpfile;
	private long date;
	boolean isUpdated = false;
	private Map<String, String> ownerShip = new HashMap<String, String>();

	public Map<String, String> getOwnerShip() {
		return ownerShip;
	}

	public DeployOutputStream(String parent, String path, long date)
			throws IOException {
		this(parent, path, date, true);
	}

	public DeployOutputStream(String parent, String path, long date,
			boolean useTmp, Map<String, String> ownerShip) throws IOException {
		this(parent, path, date, useTmp);

		this.ownerShip = ownerShip;
	}

	boolean useTmp = true;

	public DeployOutputStream(String parent, String path, long date,
			boolean useTmp) throws IOException {
		this.useTmp = useTmp;
		this.parentPath = parent;
		this.filePath = path;

		try {
			if (useTmp) {
				tmpfile = File.createTempFile(TMP_PREFIX,
						String.valueOf(System.currentTimeMillis()));
			} else {
				tmpfile = new File(parentPath, filePath);
				if (tmpfile.exists()) {
					isUpdated = true;
				} else {
					FileUtil.mkdirs(tmpfile);
				}
			}
			this.date = date;

			this.out = new BufferedOutputStream(new FileOutputStream(tmpfile));
		} catch (IOException e) {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e2) {
			}
			if (tmpfile != null && useTmp) {
				tmpfile.delete();
			}
			throw e;
		}
	}

	/**
	 * write bytes to this file
	 * 
	 * @param buf
	 * @param size
	 * @return
	 */
	public boolean write(byte[] buf, int size) {
		try {
			if (out != null) {
				out.write(buf, 0, size);
				return true;
			}
		} catch (IOException e) {
		}
		return false;
	}

	public boolean write(byte[] buf) {
		return write(buf, buf.length);
	}

	/**
	 * close this stream and copy temp file to original one. Before copying,
	 * backup original one to the bakdir.
	 * 
	 * @param updatefiles
	 * @param bakdir
	 * @return
	 * @throws IOException
	 */
	public DoneFileInfo close(List<String> updatefiles, String bakdir,
			int backupcnt) throws Exception {
		try {
			boolean isNew = false;
			
			if (this.out != null) {
				this.out.close();
			}
			// tmpfile can have 0 size cause File.createTempFile method.
			if (tmpfile == null || !tmpfile.exists()) {
				throw new IOException("Fail to transfer file: " + filePath);
			}
			if (date > -1) {
				tmpfile.setLastModified(date);
			}

			if (!useTmp) {
				return new DoneFileInfo(filePath, false,
						tmpfile.lastModified(), tmpfile.length(), isUpdated,
						true);
			}

			File destfile = new File(parentPath, filePath);

			// backup
			if (destfile.exists()) {
				isUpdated = DeployerUtils.undoBackup(parentPath, filePath,
						bakdir, backupcnt);
			} else {
				FileUtil.mkdirs(destfile);
				isNew = true;
			}

			// copy
			if (updatefiles != null) { // jar update. this is not used
				updatefiles.addAll(FileUtil.updateJar(tmpfile, destfile));
			} else {
				FileUtil.copy(tmpfile, destfile);
			}
			// set chown
			Process p;
			boolean isWin = System.getProperty("os.name").startsWith("Windows");

			if (!ownerShip.isEmpty()) {
				if (isWin) {
					p = Runtime.getRuntime().exec(
							new String[] {
									"cmd",
									"/c",
									"subinacl /onlyfile " + parentPath
											+ File.separator + filePath + " "
											+ "/setowner="
											+ ownerShip.get("owner") });

				} else {
					// TODO sysout test chown command~~~~
					String command;
					
					if(isNew) {
						command = "chown -R " + ownerShip.get("owner") + ":"
						+ ownerShip.get("group") + " " + parentPath; 
					} else {
						command = "chown " + ownerShip.get("owner") + ":"
						+ ownerShip.get("group") + " " + parentPath
						+ File.separator + filePath;
					}
					
					System.out.println("command: " + command);
//					p = Runtime.getRuntime().exec(
//							"chown " + ownerShip.get("owner") + ":"
//									+ ownerShip.get("group") + " " + parentPath
//									+ File.separator + filePath);
					p = Runtime.getRuntime().exec(command);
				}
				
				int exitValue = p.waitFor();
				if (exitValue != 0) {
					throw new OdenException(
							"Fail to get diretory's imformation:" + parentPath
									+ ownerShip.get("owner") + ":"
									+ ownerShip.get("group"));
				}
			}

			return new DoneFileInfo(filePath, false, destfile.lastModified(),
					destfile.length(), isUpdated, true);
		} finally {
			if (tmpfile != null && useTmp) {
				tmpfile.delete();
			}
		}
	}

}
