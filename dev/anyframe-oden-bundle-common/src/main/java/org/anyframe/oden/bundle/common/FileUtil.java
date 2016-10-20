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
package org.anyframe.oden.bundle.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Collections of utility methods which are helping to manipulate file.
 * 
 * @author Junghwan Hong
 */
public class FileUtil {

	/**
	 * method to create directory
	 * 
	 * @param f
	 * @throws IOException
	 */
	public static void mkdirs(File f) throws IOException {
		File parent = f.getParentFile();
		if (parent != null && !parent.exists()) {
			if (!parent.mkdirs()) {
				throw new IOException("Fail to create dir: " + parent.getPath());
			}
		}
		// return f.createNewFile();
	}

	/**
	 * 
	 * @param dir
	 * @param jar
	 * @return size of the compressed file
	 * @throws IOException
	 */
	public static long compress(File dir, File jar) throws IOException {
		if (!dir.exists() || !dir.isDirectory()) {
			throw new IOException("Couldn't find: " + dir);
		}
		if (jar.exists()) {
			if (jar.isDirectory()) {
				throw new IOException("same directory is existed."
						+ jar.getPath());
			}
			jar.delete();
		}

		ZipOutputStream jout = null;
		try {
			jout = new ZipOutputStream(new BufferedOutputStream(
					new FileOutputStream(jar)));
			compressDir(dir, dir, jout);
			return jar.length();
		} finally {
			if (jout != null) {
				jout.close();
			}
		}
	}

	/**
	 * @param root
	 * @param dir
	 * @param out
	 * @return size of the orginal dir
	 * @throws IOException
	 */
	@SuppressWarnings("PMD")
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
					ZipEntry entry = new ZipEntry(getRelativePath(
							root.getPath(), file.getPath()));
					out.putNextEntry(entry);

					total += copy(in, out);
					entry.setTime(file.lastModified());
				} finally {
					try {
						out.closeEntry();
					} catch (IOException e) {
					}
					try {
						if (in != null) {
							in.close();
						}
					} catch (IOException e) {
					}

				}
			}

		}
		return total;
	}

	/**
	 * @throws IOException
	 * @throws ZipException
	 */
	public static List<String> updateJar(File ref, File target)
			throws IOException {
		if (!ref.exists() || ref.isDirectory()) {
			throw new IOException("Couldn't find: " + ref);
		}
		if (target.exists() && target.isDirectory()) {
			throw new IOException("Directory is existed: " + target);
		}

		List<String> updatedfiles = null;

		File tmpdir = temporaryDir();
		try {
			// extract target to tmp
			extractZip(target, tmpdir);

			// extract ref to tmp (if exist, check date);
			updatedfiles = extractLatest(ref, tmpdir);

			// compress tmp to jar
			compress(tmpdir, target);
			target.setLastModified(ref.lastModified());
		} finally {
			removeDir(tmpdir);
		}
		return updatedfiles;
	}

	/**
	 * method to get the temp directory which is vary from OS.
	 * 
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("PMD")
	public static File temporaryDir() throws IOException {
		Random random = new Random();
		for (int maxAttempts = 100; maxAttempts > 0; --maxAttempts) {
			String path = random.nextLong() + "";
			File tmpdir = new File(System.getProperty("java.io.tmpdir"), path);
			if (!tmpdir.exists()) {
				tmpdir.mkdirs();
				return tmpdir;
			}
		}
		throw new java.io.IOException("Can't create temporary directory.");
	}

	/**
	 * @param file
	 * @return
	 */
//	private static String pathName(File file) {
//		String sfile = file.getName();
//		int idot = sfile.lastIndexOf('.');
//		if (idot == -1) {
//			return sfile;
//		}
//
//		String ext = sfile.length() > idot + 1 ? sfile.substring(idot + 1) : "";
//		return sfile.substring(0, idot) + "_" + ext;
//	}

	/**
	 * @param jar
	 * @param dir
	 * @throws IOException
	 * @throws ZipException
	 */
	@SuppressWarnings("PMD")
	private static List<String> extractLatest(File jar, File dir)
			throws ZipException, IOException {
		List<String> updatedfiles = new ArrayList<String>();
		InputStream in = null;
		OutputStream out = null;

		ZipFile zip = new ZipFile(jar);
		Enumeration<? extends ZipEntry> e = zip.entries();
		while (e.hasMoreElements()) {
			File old = null;
			long time = -1;
			try {
				ZipEntry entry = (ZipEntry) e.nextElement();
				if (entry.isDirectory()) {
					old = new File(dir, entry.getName());
					old.mkdirs();
					// time = entry.getTime();
				} else {
					old = new File(dir, entry.getName());
					if (old.exists()) {
						if (old.lastModified() >= entry.getTime()) {
							continue;
						}
					}

					in = new BufferedInputStream(zip.getInputStream(entry));
					out = new BufferedOutputStream(new FileOutputStream(old));

					copy(in, out);
					time = entry.getTime();
					updatedfiles.add(entry.getName());
				}
			} finally {
				try {
					if (out != null) {
						out.close();
					}
				} catch (IOException x) {
				}
				try {
					if (in != null) {
						in.close();
					}
				} catch (IOException x) {
				}
				try {
					if (zip != null) {
						zip.close();
					}
				} catch (IOException x) {
				}
				if (old != null && time > -1) {
					old.setLastModified(time);
				}
			}
		}
		return updatedfiles;
	}

	/**
	 * @param src
	 * @param dest
	 *            dir
	 * @throws IOException
	 * @throws ZipException
	 * @throws ZipException
	 * @throws IOException
	 */
	@SuppressWarnings("PMD")
	public static Map<FileInfo, Boolean> extractZip(File src, File dest)
			throws IOException {
		if (!src.exists() || src.isDirectory()) {
			throw new IOException("Couldn't find: " + src);
		}

		Map<FileInfo, Boolean> extractedfiles = new HashMap<FileInfo, Boolean>();

		ZipFile zip = new ZipFile(src);
		Enumeration<? extends ZipEntry> e = zip.entries();
		while (e.hasMoreElements()) {
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

					copy(in, out);
					success = true;
				} catch (Exception e2) {
				} finally {
					// do u wanna read this?
					try {
						if (out != null) {
							out.close();
						}
					} catch (IOException x) {
					}
					try {
						if (in != null) {
							in.close();
						}
					} catch (IOException x) {
					}
					try {
						if (zip != null) {
							zip.close();
						}
					} catch (IOException x) {
					}
					if (f != null) {
						f.setLastModified(time);
					}
				}
				extractedfiles.put(
						new FileInfo(entry.getName(), false, f.lastModified(),
								f.length()), success);
			}
		}
		return extractedfiles;
	}

	/**
	 * method to get the relative path from the root.
	 * 
	 * @param root
	 * @param file
	 * @return
	 */
	@SuppressWarnings("PMD")
	public static String getRelativePath(final String root, String file) {
		String root_ = normalize(root);
		root_ = root_.startsWith("/") ? root_.substring(1) : root_;
		String file_ = normalize(file);
		file_ = file_.startsWith("/") ? file_.substring(1) : file_;

		if (file_.startsWith(root_)) {
			String relative = file_.substring(root_.length());
			if (relative.startsWith("/")) {
				relative = relative.substring("/".length());
			}
			return relative;
		}
		return null;
	}

	/**
	 * change path to normized path. it's separator is '/'.
	 * 
	 * @param path
	 * @return path with separated '/'. it is not end with '/'.
	 */
	@SuppressWarnings("PMD")
	public static String normalize(String path) {
		if (path == null) {
			return null;
		}

		path = path.replaceAll("\\\\", "/");
		if (!"/".equals(path) && path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		return path;
	}

	/**
	 * path is combined and normalized.
	 * 
	 * @param parent
	 * @param child
	 * @param sep
	 * @return
	 */
	@SuppressWarnings("PMD")
	public static String combinePath(String parent, String child) {
		if (parent == null && child == null) {
			return null;
	 	} else if (parent == null || parent.length() == 0) {
			return normalize(child);
		} else if (child == null || child.length() == 0) {
			return normalize(parent);
		}
		child = normalize(child);
		return normalize(parent) + (child.startsWith("/") ? "" : "/") + child;
	}

	/**
	 * get the parent path of the 'path';
	 * 
	 * @param path
	 * @return
	 */
	@SuppressWarnings("PMD")
	public static String parentPath(String path) {
		path = normalize(path);
		int i = path.lastIndexOf('/');
		return i == -1 ? "" : path.substring(0, i);
	}

	/**
	 * extract a file name from 'path'
	 * 
	 * @param path
	 * @return
	 */
	@SuppressWarnings("PMD")
	public static String fileName(String path) {
		path = normalize(path);
		// after normalized, can not be end with '/'.
		return path.substring(path.lastIndexOf('/') + 1);
	}

	/**
	 * get the file name whose extensions will be ignored.
	 * 
	 * @param file
	 * @return
	 */
	public static String nameOnly(String file) {
		int dot = file.indexOf(".");
		if (dot == -1) {
			return file;
		}
		return file.substring(0, dot);
	}

	/**
	 * remove directory which are located on the path.
	 * 
	 * @param path
	 */
	public static void removeDir(File path) {
		if (path != null && path.isFile()) {
			path.delete();
			return;
		}

		if (path != null && path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					removeDir(files[i]);
				} else {
					files[i].delete();
				}
			}
			path.delete();
		}
	}

	/**
	 * copy the src file to dest. orignal file's date is preserved.
	 * 
	 * @param src
	 * @param dest
	 * @return
	 * @throws IOException
	 */
	public static long copy0(File src, File dest) throws IOException {
		long size = 0;
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new BufferedInputStream(new FileInputStream(src));
			out = new BufferedOutputStream(new FileOutputStream(dest));
			size = copy(in, out);
		} finally {
			if (out != null) {
				out.close();
			}
			if (in != null) {
				in.close();
			}
		}

		if (!dest.setLastModified(src.lastModified())) {
			throw new IOException("Fail to set date: " + dest);
		}
		return size;
	}

	/**
	 * copy the in to out
	 * 
	 * @param in
	 * @param out
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("PMD")
	public static long copy(InputStream in, OutputStream out)
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

	public static long copy(File src, File dest) throws IOException {
		FileChannel in = null;
		FileChannel out = null;
		try {
			in = new FileInputStream(src).getChannel();
			out = new FileOutputStream(dest).getChannel();
			return in.transferTo(0, in.size(), out);
		} finally {
			if (out != null) {
				out.close();
			}
			if (in != null) {
				in.close();
			}
		}
	}

	public static long touchcopy(File src, File dest) throws IOException {
		FileChannel in = null;
		FileChannel out = null;
		long date = src.lastModified();
		try {
			in = new FileInputStream(src).getChannel();
			out = new FileOutputStream(dest).getChannel();
			return in.transferTo(0, in.size(), out);
		} finally {
			if (out != null) {
				out.close();
			}
			if (in != null) {
				in.close();
			}
			dest.setLastModified(date);
		}
	}

	/**
	 * split s with sep and trim it's result
	 * 
	 * @param s
	 * @param sep
	 *            separator
	 * @return
	 */
	public static String[] splitBy(String s, String sep) {
		StringTokenizer tokenizer = new StringTokenizer(s, sep);

		String[] tokens = new String[tokenizer.countTokens()];
		for (int i = 0; tokenizer.hasMoreTokens(); i++) {
			tokens[i] = tokenizer.nextToken().trim();
		}
		return tokens;
	}

	/**
	 * match if path is included in the includes and is not included in the
	 * excludes.
	 * 
	 * @param path
	 * @param includes
	 * @param excludes
	 * @return
	 */
	public static boolean matched(String path, List<String> includes,
			List<String> excludes) {
		return matched(path, includes) && !matched(path, excludes);
	}

	/**
	 * match if path is matched one of the wildcard.
	 * 
	 * @param path
	 * @param wildcards
	 * @return
	 */
	public static boolean matched(String path, List<String> wildcards) {
		String _file = path.replaceAll("\\\\", "/");
		for (String wildcard : wildcards) {
			if (_file.matches(toRegex(wildcard))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return common parent. null if there's no common parent
	 */
	public static String commonParent(List<String> wcs) {
		if (wcs.isEmpty()) {
			return null;
		}

		String common = null;
		for (String wc : wcs) {
			if (common == null) {
				common = normalParent(wc);
			} else {
				common = commonParent(common, wc);
			}
		}
		return common;
	}

	/**
	 * @return wc's parent not having wildcard character.
	 */
	@SuppressWarnings("PMD")
	private static String normalParent(String wc) {
		wc = normalize(wc);

		StringBuffer buf = new StringBuffer();
		StringBuffer tok = new StringBuffer();
		for (int i = 0; i < wc.length(); i++) {
			char c = wc.charAt(i);
			if (c == '?' || c == '*') { // wc
				tok = new StringBuffer(); // make tok empty
				break;
			} else { // not wc
				tok.append(c);
				if (c == '/') {
					buf.append(tok);
					tok = new StringBuffer();
				}
			}
		}
		return buf.toString();
	}

	/**
	 * method to extract a same parent directory
	 */
	@SuppressWarnings("PMD")
	private static String commonParent(String wc0, String wc1) {
		wc0 = normalize(wc0);
		wc1 = normalize(wc1);

		StringBuffer buf = new StringBuffer();
		StringBuffer tok = new StringBuffer();
		for (int i = 0; i < wc0.length(); i++) {
			if (wc1.length() <= i) {
				break;
			}

			char c = wc0.charAt(i);
			if (c != wc1.charAt(i) || c == '?' || c == '*') { // not same or wc
				tok = new StringBuffer(); // make tok empty
				break;
			} else { // same and not wc
				tok.append(c);
				if (c == '/') {
					buf.append(tok);
					tok = new StringBuffer();
				}
			}
		}
		return buf.append(tok).toString();
	}

	/**
	 * convert wild card to regular expression
	 */
	public static String toRegex(String wildcard) {
		// **/ >> (.*/)?
		// ** >> (.*/)?
		String converted = "^"
				+ wildcard.replaceAll("(^|/)\\*\\*/", "@@")
						.replaceAll("(^|/)\\*\\*", "@@")
						.replaceAll("\\.", "\\\\.").replaceAll("\\*", "[^/]*")
						.replaceAll("\\?", ".");

		// add to last ((^|/).*)? to consider files in folder
		if (converted.contains("@@")) {
			converted = converted.replaceAll("@@", "(.*/)?") + "((^|/).*)?$";
		}
		return converted;
	}

	/**
	 * get the file name which are not duplicated others in the same directory.
	 * 
	 * @param path
	 * @param prefix
	 * @param postfix
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("PMD")
	public static File uniqueFile(File path, String prefix, String postfix)
			throws IOException {
		final int maxAttempts = 300;
		for (int i = 0; i < maxAttempts; i++) {
			File uniquef = new File(path, prefix + "_" + Integer.valueOf(i)
					+ postfix);
			if (!uniquef.exists()) {
				return uniquef;
			}
		}
		throw new IOException("Couldn't get a unique file name with prefix: "
				+ prefix + " & postfix: " + postfix);
	}

	public static boolean isAbsolutePath(String path) {
		if (path == null) {
			return false;
		}
		return path.startsWith("/") || /* UNIX */
		path.matches("^[a-zA-Z]:[/\\\\].*"); /* Windows */
	}

	/**
	 * start with . or .. is not allowed.
	 * 
	 * @param path
	 *            absolute path like /aaa/bbb/../../ccc/ddd
	 * @return
	 */
	@SuppressWarnings("PMD")
	public static String resolveDotNatationPath(String path) {
		path = path.replaceAll("\\\\", "/");

		if (!isAbsolutePath(path)) {
			return null;
		}

		// remove /.
		int prevlen = 0;
		if (path.contains(".")) {
			while (prevlen != path.length()) {
				prevlen = path.length();
				path = path.replaceFirst("/\\./?$", "");
				path = path.replaceFirst("/\\./", "/");
			}
		}

		// replace /..
		prevlen = 0;
		if (path.contains("..")) {
			while (prevlen != path.length()) {
				prevlen = path.length();
				path = path.replaceFirst("/[^/]+/\\.\\.", "");
			}
		}

		if (path.contains("/.")) {
			return null;
		}
		return path;
	}
	
	@SuppressWarnings("PMD")
	public static void listAllFiles(Collection<FileInfo> ret, String root,
			File dir) {
		if (!dir.exists()) {
			return;
		}
		File[] fs = dir.listFiles();
		for (File f : fs) {
			if (f.isDirectory()) {
				listAllFiles(ret, root, f);
			} else {
				ret.add(new FileInfo(FileUtil.getRelativePath(root,
						f.getAbsolutePath()), false, f.lastModified(), f
						.length()));
			}
		}
	}

	/**
	 * replace string
	 * 
	 * @param source
	 * @param pattern
	 * @param replace
	 * @return
	 */
	public static String replace(String source, String pattern, String replace) {
		int sIndex = 0;
		int eIndex = 0;
		String sourceTemp = null;
		StringBuffer result = new StringBuffer();
		sourceTemp = source.toUpperCase();
//		while ((eIndex = sourceTemp.indexOf(pattern.toUpperCase(), sIndex)) >= 0) {
//			result.append(source.substring(sIndex, eIndex));
//			result.append(replace);
//			sIndex = eIndex + pattern.length();
//		}
		while (true) {
			eIndex = sourceTemp.indexOf(pattern.toUpperCase(), sIndex);
			if (eIndex < 0) {
				break;
			}
			result.append(source.substring(sIndex, eIndex));
			result.append(replace);
			sIndex = eIndex + pattern.length();
		}
		result.append(source.substring(sIndex));
		return result.toString();
	}
}
