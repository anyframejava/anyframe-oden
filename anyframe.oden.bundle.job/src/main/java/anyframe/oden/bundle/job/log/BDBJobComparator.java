package anyframe.oden.bundle.job.log;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Comparator;

public class BDBJobComparator implements Comparator<byte[]>, Serializable {

	private static final long serialVersionUID = -4530227468319107512L;

	public int compare(byte[] b1, byte[] b2) {
		try{
			return new String(b1, "utf-8").compareTo(
					new String(b2, "utf-8"));
		}catch(UnsupportedEncodingException e){
			return 0;	// never be occured.
		}
	}

}
