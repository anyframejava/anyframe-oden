/*
 * Copyright 2009 SAMSUNG SDS Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package anyframe.oden.bundle.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Collections of utility methods which are helping to manipulate file.
 * 
 * @author joon1k
 *
 */
public class FileUtil {
	public static boolean createNewFile(File f) throws IOException {
		File parent = f.getParentFile();
		if (!parent.exists()) {
			if(parent.mkdirs() == false)
				throw new IOException("Fail to create dir: " + parent.getPath());
		}
		return f.createNewFile();
	}
	
	/**
	 * dir을 jar로 묶음.
	 * jar가 이미 존재하면, 기존꺼 새걸로 바꿈.
	 * @param dir
	 * @param jar
	 * @return size of the compressed file
	 * @throws IOException 
	 */
	public static long compress(File dir, File jar) 
			throws IOException{
		if(jar.exists())
			jar.delete();
		
		ZipOutputStream jout = null;
		try{
			jout = new ZipOutputStream(
					new BufferedOutputStream(new FileOutputStream(jar) ));
			compressDir(dir, dir, jout);
			return jar.length();
		} finally {
			if(jout != null) jout.close();
		}
	}
	
	/**
	 * dir의 파일들을 out으로 압축. root는 dir과 동일하게 적으면 됨
	 * 파일들의 시간 그대로 유지.
	 * @param root
	 * @param dir
	 * @param out
	 * @return size of the orginal dir
	 * @throws IOException
	 */
	private static long compressDir(final File root, File dir, ZipOutputStream out) 
			throws IOException {
		long total = 0;
		
		File[] files = dir.listFiles();
		if(files != null){
			for(File file : files) {
				if(file.isDirectory()){
					total += compressDir(root, file, out);
					continue;
				}
				
				InputStream in = null;
				try {
					in = new BufferedInputStream(
							new FileInputStream(file));
					ZipEntry entry = new ZipEntry(getRelativePath(root.getPath(), file.getPath()));
					out.putNextEntry(entry);
					
					total += copy(in, out);
					entry.setTime(file.lastModified());
					
//					System.out.println("[DEBUG] file: " + String.valueOf(file.lastModified())
//							+ " entry: " + String.valueOf(entry.getTime()));
//					System.out.println("[DEBUG] compressed: " + file.getPath());
				} finally {
					try { out.closeEntry(); } catch (IOException e) {}
					try{ if(in != null) in.close(); } catch(IOException e) {}
					
				}
			}
						
		}
		return total;
	}
	
	/**
	 * 'target' jar 업데이트. 'ref'참조.<br/><br/>
	 * 'ref"에만 있으면 'target'에 추가.
	 * 둘다 있으면 날짜 비교하여 최신꺼가 'target'에 존재하도록.
	 * 'target'에만 있으면 냅둠.
	 * 
	 * @throws IOException 
	 * @throws ZipException 
	 */
	public static List<String> updateJar(File target, File ref) 
			throws ZipException, IOException {
		List<String> updatedfiles = null;
		
		File tmpdir = temporaryDir();
		
		try{
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
	
	public static File temporaryDir() throws IOException {
		Random random = new Random();
		for (int maxAttempts = 100; maxAttempts > 0; --maxAttempts) {
			String path = random.nextLong() + "";
			File tmpdir = new File(System.getProperty("java.io.tmpdir"), path);
			if(!tmpdir.exists()){
				tmpdir.mkdirs();
				return tmpdir;
			}
		}
		throw new java.io.IOException("Can't create temporary directory.");
	}
	
	private static String pathName(File file) {
		String sfile = file.getName();
		int idot = sfile.lastIndexOf('.');
		if(idot == -1)
			return sfile;
		
		String ext = sfile.length() > idot+1 ? sfile.substring(idot+1) : "";
		return sfile.substring(0, idot) + "_" + ext;
	}
	
	/**
	 * jar를 dir로 압축해제. dir에 동일파일 있으면 최신꺼만 있도록.
	 * 이걸 사용하기 전에 dir에 구 파일들 갖다 놓을 것.
	 * 
	 * @param jar
	 * @param dir
	 * @throws IOException 
	 * @throws ZipException 
	 */
	private static List<String> extractLatest(File jar, File dir) 
			throws ZipException, IOException {
		List<String> updatedfiles = new ArrayList<String>();
		InputStream in = null;
		OutputStream out = null;
		
		ZipFile zip = new ZipFile(jar);
		Enumeration<? extends ZipEntry> e = zip.entries();
		while(e.hasMoreElements()){
			File old = null;
			long time = -1;
			try {
				ZipEntry entry = (ZipEntry) e.nextElement();
				if(entry.isDirectory()) {
					old = new File(dir, entry.getName());
					old.mkdirs();
//					time = entry.getTime();
				} else {
					old = new File(dir, entry.getName());
					if(old.exists())
						if(old.lastModified() >= entry.getTime()){
							continue;
						}
					
					in = new BufferedInputStream(zip.getInputStream(entry)); 
					out = new BufferedOutputStream(new FileOutputStream(old));
					
					copy(in, out);
					time = entry.getTime();
					updatedfiles.add(entry.getName());
				}
			}catch(Exception ex) {
				ex.printStackTrace();
			}finally {
				try{ if(out != null) out.close(); }catch(IOException x){}
				try{ if(in != null) in.close(); }catch(IOException x){}
				if(old != null && time > -1) old.setLastModified(time);
			}
		}
		return updatedfiles;
	}
	
	/**
	 * 
	 * @param src
	 * @param dest dir
	 * @throws ZipException
	 * @throws IOException
	 */
	public static List<PairValue<String, Boolean>> extractZip(File src, File dest) 
			throws ZipException, IOException {
		List<PairValue<String, Boolean>> extractedfiles = new ArrayList<PairValue<String, Boolean>>();
		InputStream in = null;
		OutputStream out = null;
		
		ZipFile zip = new ZipFile(src);
		Enumeration<? extends ZipEntry> e = zip.entries();
		while(e.hasMoreElements()){
			File f = null;
			long time = 0;
			try {				
				ZipEntry entry = (ZipEntry) e.nextElement();
				time = entry.getTime();
				if(entry.isDirectory()) {
					f = new File(dest, entry.getName());
					f.mkdirs();
				} else {
					in = new BufferedInputStream(zip.getInputStream(entry)); 
					
					f = new File(dest, entry.getName());
					File fparent = f.getParentFile();
					if(!fparent.exists())
						fparent.mkdirs();
										
					out = new BufferedOutputStream(new FileOutputStream(f));
					
					copy(in, out);
					extractedfiles.add(new PairValue(f.getPath(), true));
				}
			}catch(Exception exc) {
				extractedfiles.add(new PairValue(f.getPath(), false));
			}finally {
				// do u wanna read this?
				try{ if(out != null) out.close(); }catch(IOException x){}
				try{ if(in != null) in.close(); }catch(IOException x){}
				if(f != null) f.setLastModified(time);
			}
		}
		return extractedfiles;
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
	
	/**
	 * change path to normized path. it's separator is '/'.  it is not end with '/'.
	 * 
	 * @param path
	 * @return
	 */
	public static String normalize(String path){
		if(path == null)
			return null;
		
		path = path.replaceAll("\\\\", "/");
		if(path.endsWith("/"))
			path = path.substring(0, path.length()-1);
		return path;
	}
	
	/**
	 * path is combined and normalized.
	 * @param parent
	 * @param child
	 * @param sep
	 * @return
	 */
	public static String combinePath(String parent, String child){
		String _parent = normalize(parent);
		String _child = normalize(child);
	
		return _parent + "/" + _child;
	}
	
	public static String parentPath(String path){
		path = normalize(path);
		return path.substring(0, path.lastIndexOf('/'));
	}
	
	public static String fileName(String path) {
		path = normalize(path);
		// after normalized, can not be end with '/'.
		return path.substring(path.lastIndexOf('/')+1);
	}
	
	public static void removeFile(File file) {
		if(file.exists())
			file.delete();
	}

	public static void removeDir(File path) {
		if(path != null && path.isFile()) {
			path.delete();
			return;
		}
			
		if(path != null && path.exists()) {
			File[] files = path.listFiles();
			for(int i=0; i<files.length; i++) {
				if(files[i].isDirectory()) {
					removeDir(files[i]);
				}else {
					files[i].delete();
				}
			}
	        path.delete();
        }
	}
	
	public static long copy(File src, File dest) throws IOException{
		long size = 0;
		InputStream in = null;
		OutputStream out = null;
		try{
			in = new BufferedInputStream(new FileInputStream(src));
			out = new BufferedOutputStream(new FileOutputStream(dest));
			size = copy(in, out);
		} finally {
			if(out != null) out.close();
			if(in != null) in.close();
		}
		return size;
	}
	
	public static long copy(InputStream in, OutputStream out) 
			throws IOException {
		byte[] buf = new byte[1024*8];
		long total = 0;
		int size = 0;
		while((size = in.read(buf)) != -1){
			out.write(buf, 0, size);
			total += size;
		}
		return total;
	}
	
	
	/**
	 * split s with sep and trim it's result
	 * @param s
	 * @param sep separator
	 * @return
	 */
	public static String[] splitBy(String s, String sep){
		StringTokenizer tokenizer = new StringTokenizer(s, sep);
		
		String[] tokens = new String[tokenizer.countTokens()];
		for(int i=0; tokenizer.hasMoreTokens(); i++){
			tokens[i] = tokenizer.nextToken().trim();
		}
		return tokens;
	}
	
	public static boolean matched(String path, List<String> includes, List<String> excludes) {		
		return matched(path, includes) && !matched(path, excludes);
	}
	
	public static boolean matched(String path, List<String> wildcards) {		
		String _file = path.replaceAll("\\\\", "/");
		for(String wildcard : wildcards){
			if( _file.matches(toRegex(wildcard)))
				return true;
		}
		return false;
	}
	
	/**
	 * @return common parent. null if there's no common parent
	 */
	public static String commonParent(List<String> wcs){
		if(wcs.size() == 0)
			return null;
		
		String common = null;
		for(String wc : wcs){
			if(common == null)
				common = normalParent(wc);
			else
				common = commonParent(common, wc);
		}
		return common;
	}
	
	/**
	 * @return wc's parent not having wildcard character.
	 */
	private static String normalParent(String wc){
		wc = normalize(wc);
		
		StringBuffer buf = new StringBuffer();
		StringBuffer tok = new StringBuffer();
		for(int i=0; i<wc.length(); i++){
			char c = wc.charAt(i);			
			if(c == '?' || c == '*'){		// wc
				tok = new StringBuffer();		// make tok empty
				break;
			}else {			// not wc
				tok.append(c);
				if(c == '/'){
					buf.append(tok);
					tok = new StringBuffer();
				}
			}
		}
		return buf.toString();
	}
	
	private static String commonParent(String wc0, String wc1){
		wc0 = normalize(wc0);
		wc1 = normalize(wc1);
		
		StringBuffer buf = new StringBuffer();
		StringBuffer tok = new StringBuffer();
		for(int i=0; i<wc0.length(); i++){
			if(wc1.length() <= i)
				break;
			
			char c = wc0.charAt(i);			
			if(c != wc1.charAt(i) || c == '?' || c == '*'){		// not same or wc
				tok = new StringBuffer();		// make tok empty
				break;
			}else {			// same and not wc
				tok.append(c);
				if(c == '/'){
					buf.append(tok);
					tok = new StringBuffer();
				}
			}
		}
		return buf.append(tok).toString();
	}
	
	// TODO 빈 와일드카드 문제.  *  문제 
	public static String toRegex(String wildcard) {		
		// **/ >> (.*/)?
		// ** >> (.*/)?
		String converted = "^" + wildcard.replaceAll("(^|/)\\*\\*/", "@@").replaceAll("(^|/)\\*\\*", "@@").replaceAll("\\.", "\\\\.").replaceAll("\\*", "[^/]*").replaceAll("\\?", ".");
		
		// add to last ((^|/).*)? to consider files in folder
		if(converted.contains("@@"))
			converted = converted.replaceAll("@@", "(.*/)?") + "((^|/).*)?$";
		return converted;
	}
	
	public static void writeToFile(File target, byte[] contents) throws IOException {
		OutputStream out = null;
		try {
			FileUtil.createNewFile(target);
			out = new BufferedOutputStream(new FileOutputStream(target));
			out.write(contents);
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if(out != null) out.close();
			} catch (IOException e) {
			}
		}
	}

	public static void main(String[] args) throws ZipException, IOException {
		final String JAR ="/Users/joon1k/dev_rscs/anyframe-sample/build/anyframe-sample-services.jar";
		final String REF="/Users/joon1k/dev_rscs/anyframe-sample/src/webapps/WEB-INF/lib/anyframe-sample-services.jar";
		
		File jar = new File(JAR);
		if(!jar.exists())
			jar.createNewFile();
		
//		compress(new File(DIR), JAR);
		
		updateJar(new File(JAR), new File(REF) );
	}
	
	public static File uniqueFile(File path, String prefix, String postfix)
			throws IOException{
		final int maxAttempts = 300;
		for(int i=0; i<maxAttempts; i++){
			File uniquef = new File(path, prefix + "_" + Integer.valueOf(i) + postfix);
			if(!uniquef.exists())
				return uniquef;
		}
		throw new IOException("Couldn't get a unique file name with prefix: " + 
				prefix + " & postfix: " + postfix);
	}
	
}
