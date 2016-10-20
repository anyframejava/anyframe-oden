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
package org.anyframe.oden.bundle.core.record;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * This is kind of FileOutputStream which provides a way to store Object to file
 * with append mode.
 * 
 * @author Junghwan Hong
 */
public class FileObjectOutputStream extends FileOutputStream {

	public FileObjectOutputStream(File file, boolean append)
			throws FileNotFoundException {
		super(file, append);
	}

	/**
	 * append object to the file
	 * 
	 * @param o
	 * @throws IOException
	 */
	public void writeObject(Object o) throws IOException {
		ObjectOutputStream oos = null;
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bout);
			oos.writeObject(o);
			oos.flush();

			byte[] contents = bout.toByteArray();
			write(size(contents.length));
			write(contents);
		} finally {
			try {
				if (oos != null) {
					oos.close();
				}
			} catch (IOException e) {
			}
		}
	}
	
	@SuppressWarnings("PMD")
	private byte[] size(int length) {
		byte[] size = new byte[4];
		for (int i = 0; i < 4; i++) {
			size[i] = (byte) (length & 0x000000ff);
			// System.out.printf("%x %x\n", length, size[i]);
			length = length >> 8;
		}
		return size;
	}
}
