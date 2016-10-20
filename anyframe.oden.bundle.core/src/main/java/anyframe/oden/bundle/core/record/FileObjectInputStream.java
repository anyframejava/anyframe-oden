/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package anyframe.oden.bundle.core.record;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * InputStream can get the Objects from the file. This is used with FileObjectOutputStream.
 * 
 * @author joon1k
 *
 */
public class FileObjectInputStream extends FileInputStream {

	public FileObjectInputStream(File file) throws FileNotFoundException {
		super(file);
	}

	public Object readObject() throws IOException, ClassNotFoundException{
		// get size
		byte[] bsize = new byte[4];
		read(bsize);
		int size = size(bsize);
		if(size < 0)
			throw new IOException();
		
		byte[] contents = new byte[size];
		read(contents);
		
		ObjectInputStream oin = null;
		try{
			oin = new ObjectInputStream(new ByteArrayInputStream(contents)); 
			return oin.readObject();
		}finally {
			try { if(oin != null) oin.close(); } catch (IOException e) { }
		}
	}
	
	private int size(byte[] size) {
		int len = 0;
		for(int i=3; i>=0; i--){ 
			len |= ( (size[i] & 0x000000ff) << (8*i) ); 
//			System.out.printf("%x %x\n", len, size[i]);
		}
		return len;
	}
}
