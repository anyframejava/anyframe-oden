package anyframe.oden.bundle.test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


public class PrintStreamWrapper {
	private ByteArrayOutputStream bout;
	private PrintStream pstream;
	
	public PrintStreamWrapper() {
		bout = new ByteArrayOutputStream();
		pstream = new PrintStream(bout);
	}
	
	public PrintStream printStream(){
		return pstream;
	}
	
	public String contents() {
		return new String(bout.toByteArray());
	}
	
}
