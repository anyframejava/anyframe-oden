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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.anyframe.oden.bundle.common.FileInfo;
import org.anyframe.oden.bundle.common.FileUtil;
import org.anyframe.oden.bundle.deploy.DoneFileInfo;
import org.anyframe.oden.bundle.deploy.StoppableJob;

/**
 * Job to extract files.
 * 
 * @author Junghwan Hong
 * @see anyframe.oden.bundle.deploy.StoppableJob
 */
public class ExtractJob extends StoppableJob {
	String srcdir;
	String zipname;
	String destdir;

	public ExtractJob(String id, String srcdir, String zipname, String destdir) {
		super(id);
		this.srcdir = srcdir;
		this.zipname = zipname;
		this.destdir = destdir;
	}

	@Override
	protected Object execute() throws IOException {
		File s = new File(srcdir);
		File d = new File(destdir);
		if (!s.isAbsolute()) {
			throw new IOException("Absolute path is allowed only: " + s);
		}
		if (!d.isAbsolute()) {
			throw new IOException("Absolute path is allowed only: " + d);
		}
		return extractSnapshot(new File(s, zipname), d);
	}

	@SuppressWarnings("PMD")
	private List<DoneFileInfo> extractSnapshot(File src, File dest)
			throws IOException {
		List<DoneFileInfo> result = new ArrayList<DoneFileInfo>();
		Map<FileInfo, Boolean> m = _extract(src, dest);
		for (FileInfo info : m.keySet()) {
			result.add(new DoneFileInfo(info.getPath(), info.isDir(), info
					.lastModified(), info.size(), false, true));
		}
		return result;
	}

	@SuppressWarnings("PMD")
	private Map<FileInfo, Boolean> _extract(File src, File dest)
			throws IOException {
		if (!src.exists() || src.isDirectory())
			throw new IOException("Couldn't find: " + src);

		Map<FileInfo, Boolean> extractedfiles = new HashMap<FileInfo, Boolean>();

		ZipFile zip = new ZipFile(src);
		Enumeration<? extends ZipEntry> e = zip.entries();
		while (e.hasMoreElements()) {
			if (this.stop)
				break;

			ZipEntry entry = (ZipEntry) e.nextElement();
			File f = new File(dest, entry.getName());
			if (entry.isDirectory()) {
				f.mkdirs();
			} else {
				InputStream in = null;
				OutputStream out = null;

				boolean success = false;
				long time = entry.getTime();
				try {
					in = new BufferedInputStream(zip.getInputStream(entry));

					f = new File(dest, entry.getName());
					File fparent = f.getParentFile();
					fparent.mkdirs();

					out = new BufferedOutputStream(new FileOutputStream(f));

					FileUtil.copy(in, out);
					success = true;
				} catch (IOException e2) {
				}

				// do u wanna read this?
				try {
					if (out != null)
						out.close();
				} catch (IOException x) {
				}
				try {
					if (in != null)
						in.close();
				} catch (IOException x) {
				}
				if (f != null)
					f.setLastModified(time);
				extractedfiles.put(
						new FileInfo(entry.getName(), false, f.lastModified(),
								f.length()), success);
			}
		}
		try {
			zip.close();
		} catch (IOException e2) {
		}
		return extractedfiles;
	}
}
