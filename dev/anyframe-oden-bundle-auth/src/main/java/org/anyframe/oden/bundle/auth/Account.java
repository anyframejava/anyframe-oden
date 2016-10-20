/*
 * Copyright 2009, 2010 SAMSUNG SDS Co., Ltd. All rights reserved.
 *
 * No part of this "source code" may be reproduced, stored in a retrieval
 * system, or transmitted, in any form or by any means, mechanical,
 * electronic, photocopying, recording, or otherwise, without prior written
 * permission of SAMSUNG SDS Co., Ltd., with the following exceptions:
 * Any person is hereby authorized to store "source code" on a single
 * computer for personal use only and to print copies of "source code"
 * for personal use provided that the "source code" contains SAMSUNG SDS's
 * copyright notice.
 *
 * No licenses, express or implied, are granted with respect to any of
 * the technology described in this "source code". SAMSUNG SDS retains all
 * intellectual property rights associated with the technology described
 * in this "source code".
 *
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
 * @author joon1k
 *
 */
public class Account {
//	final static String CONFIG_FILE = "conf/oden.ini";
	
	public static void main(String... args){
		if(args.length != 2){
			System.out.println("Usage: <id> <pwd>");
			System.exit(-1);
		}
		
		File accFile = null;
		try {
			accFile = new File(odenHome(), SecurityHandler.ACCOUNT_FILE);

			String encoded = encode(args[0], args[1]);
			writeToFile(accFile, args[0], encoded);
		} catch (Exception e) {
			try{System.out.println(Account.class.getProtectionDomain().getCodeSource().getLocation().toURI()+"");}catch(Exception ee){}
			System.out.println("Fail to register account. " + e.getMessage());
			System.exit(-1);
		}
		System.out.println(args[0] + " is registered: " + accFile);
	}

	private static String encode(String id, String pwd) throws UnsupportedEncodingException {
		return new String(Base64.encodeBase64((id + ":" + pwd).getBytes()), "ASCII");
	}
	
	private static void writeToFile(File f, String id, String encoded) throws IOException {
		Properties prop = new Properties();
		
		if(f.exists()){
			InputStream in = null;
			try{
				in = new BufferedInputStream(new FileInputStream(f));
				prop.load(in);
			}finally{
				if(in != null) in.close();
			}
		}
		
		prop.put(id, encoded);
		
		OutputStream out = null;
		try{
			out = new BufferedOutputStream(new FileOutputStream(f));
			prop.store(out, null);
		}finally{
			if(out != null) out.close();
		}	
	}

	public static File odenHome() {
		try{
			URL url = new URL(Account.class.getProtectionDomain().getCodeSource().getLocation().toString());
			return new File(url.getPath()).getParentFile().getParentFile();
		}catch(Exception e){
			return new File("..");
		}
	}
}
