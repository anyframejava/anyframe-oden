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
package anyframe.oden.bundle.repository;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import anyframe.oden.bundle.common.OdenException;

/**
 * This pool manipulate ftp connection instances.
 * 
 * @author joon1k
 *
 */
class FTPConnectionPool {
	// map Thread id & FTP connection.
	private Map<String, FTPClient> pool = 
			new HashMap<String, FTPClient>();

	public FTPConnectionPool(){
	}
		
	public FTPClient connection(String addr, String id, String pwd) throws OdenException{	
		String key = key(Thread.currentThread().getId(), addr, id, pwd);
		FTPClient conn = pool.get(key);
		if(conn == null || !conn.isConnected()){
			if(conn != null)
				remove(key);
			conn = initFTP(addr, id, pwd);
			pool.put(key, conn);
		}
		return conn;
	}
	
	public static String key(long thread, String addr, String id, String pwd){
		return String.valueOf(thread) + ", " + addr + ", " + id + ", " + pwd;
	}
	
	private FTPClient initFTP(String addr, String id, String pwd) throws OdenException{
		URI _uri = null;
		try {
			_uri = new URI(addr);
		} catch (URISyntaxException e) {
			throw new OdenException("Illegal URI syntax: " + addr);
		}
		int port = _uri.getPort();
		
		FTPClient ftp = new FTPClient();
		try {
			ftp.setDefaultTimeout(10000);	// wait to connect
			ftp.setDataTimeout(600000);
			ftp.connect(_uri.getHost(), port == -1 ? 21 : port);
//System.out.println("connected to ftp: " + Thread.currentThread().getId()
//		+ " by " + new Throwable().getStackTrace()[4].getMethodName());
			if(!FTPReply.isPositiveCompletion( ftp.getReplyCode()))
				throw new IOException();
		} catch (IOException e) {
			throw new OdenException("FTP connection failed: "
					+  ftp.getReplyString());
		}
		
		try {
			if(!ftp.login(id, pwd))
				throw new IOException();
		} catch (IOException e) {
			throw new OdenException("Could not login to FTP server. id: " +
					id + ", pwd: " + pwd);
		}
		
		try {
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			if (!FTPReply.isPositiveCompletion( ftp.getReplyCode()))
				throw new IOException();
		} catch (IOException e) {
			throw new OdenException("Could not set transfer type: "
	                +  ftp.getReplyString());
		}
        return ftp;
	        
	}
	
	public void remove(String key){
		FTPClient conn = pool.get(key);
		if(conn != null){
			try{
				conn.logout();
				conn.disconnect();
//System.out.println("disconnected to ftp: " + Thread.currentThread().getId());				
				pool.remove(key);
			}catch(IOException e){}
		}
	}
	
}
