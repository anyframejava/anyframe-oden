package org.anyframe.oden.bundle.deploy;

import java.io.Serializable;

public class ByteArray implements Serializable {
	private static final long serialVersionUID = -7999398494995182535L;
	
	private byte[] bytes;
	
	public ByteArray(byte[] bytes) {
		this.bytes = bytes;
	}
	
	public ByteArray(byte[] bytes, int sz){
		byte[] buf = new byte[sz];
		System.arraycopy(bytes, 0, buf, 0, sz);
		this.bytes = buf;
	}
	
	public byte[] getBytes(){
		return bytes;
	}
}
