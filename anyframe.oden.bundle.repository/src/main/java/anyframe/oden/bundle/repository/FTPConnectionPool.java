package anyframe.oden.bundle.repository;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import anyframe.oden.bundle.common.OdenException;
import anyframe.oden.bundle.common.OdenIncompleteArgumentException;

/**
 * This pool manipulate ftp connection instances.
 * 
 * @author joon1k
 *
 */
public class FTPConnectionPool {
	private Map<List<String>, FTPClient> pool = 
			new HashMap<List<String>, FTPClient>();
	
	private static FTPConnectionPool instance;
	
	protected FTPConnectionPool(){
	}
	
	public static FTPConnectionPool instance(){
		if(instance == null)
			instance = new FTPConnectionPool();	
		return instance;
	}
	
	public FTPClient connection(String[] repoargs) throws OdenException{		
		if(repoargs.length < 2)
			throw new OdenIncompleteArgumentException(repoargs);

		List<String> key = Arrays.asList(repoargs);
		FTPClient conn = pool.get(key);
		if(conn == null || !conn.isConnected()){
			String uri = repoargs[0];
			String id = repoargs.length > 2 ? repoargs[2] : null;
			String pwd = repoargs.length > 3 ? repoargs[3] : null;
			
			conn = initFTP(uri, id, pwd);
			pool.put(key, conn);
		}
		
		String repoRoot = repoargs[1];
		try {
			if(repoRoot != null)
				conn.changeWorkingDirectory(repoRoot);
		} catch (IOException e) {
			// because of socket timeout, this occurs. so make connection again
			try { conn.disconnect(); } catch (IOException e1) { }
			conn = connection(repoargs);		
		}
		conn.setDataTimeout(30000);
		return conn;
	}
	
	private FTPClient initFTP(String uri, String id, String pwd) throws OdenException{
		URI _uri = null;
		try {
			_uri = new URI(stripProtocol(uri));
		} catch (URISyntaxException e) {
			throw new OdenException("Illegal URI syntax: " + uri);
		}
		int port = _uri.getPort();
		
		FTPClient ftp = new FTPClient();
		try {
			ftp.setDefaultTimeout(10000);	// wait to connect
			ftp.connect(_uri.getHost(), port == -1 ? 21 : port);
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
	
	private String stripProtocol(String uri) {
		StringBuffer buf = new StringBuffer(uri);
		
		// remove protocol
		if(uri.startsWith("ftp://")){
			buf.delete(0, "ftp://".length());
		}
		return buf.toString();
	}
	
}
