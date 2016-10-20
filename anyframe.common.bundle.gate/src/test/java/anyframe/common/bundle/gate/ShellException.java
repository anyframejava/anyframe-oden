package anyframe.common.bundle.gate;

public class ShellException extends Exception {
	public ShellException(){
		
	}
	
	public ShellException(String s){
		super(s);
	}
	
	public ShellException(String s, Throwable throwable) {
		super(s, throwable);
	}
	
	public ShellException(Throwable throwable) {
		super(throwable);
	}
}
