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

import org.jasypt.salt.ZeroSaltGenerator;
import org.jasypt.encryption.pbe.PooledPBEByteEncryptor;

/**
 * Cipher Util for using jasypt.
 * 
 * @author Junghwan Hong
 */
public class CipherUtil {

	private static final PooledPBEByteEncryptor encryptor = new PooledPBEByteEncryptor();

	private static final String ONLINE_PASSWORD_KEY = "anyframe_oden";

	static {
		encryptor.setPassword(ONLINE_PASSWORD_KEY);
		encryptor.setSaltGenerator(new ZeroSaltGenerator());
		encryptor.setPoolSize(8);
	}

	/**
	 * Do encryption
	 * @param message
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] message) throws Exception {
		if (message == null) {
			return null;
		}
		byte[] rtnByte = null;
		try {
			rtnByte = encryptor.encrypt(message);
		} catch(Exception e) {
			throw new OdenException("Occured encrypt exception.", e.getCause());
		}
		return rtnByte;
	}

	/**
	 * Do decryption
	 * @param message
	 * @return
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] message) throws Exception {
		if (message == null)
			return null;

		byte[] rtnByte = null;
		try {
			rtnByte = encryptor.decrypt(message);
		} catch(Exception e) {
			throw new OdenException("Occured decrypt exception.", e.getCause());
		}
		return rtnByte;

	}
}
