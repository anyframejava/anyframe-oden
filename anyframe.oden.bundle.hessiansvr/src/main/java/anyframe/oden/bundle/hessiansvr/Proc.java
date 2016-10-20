package anyframe.oden.bundle.hessiansvr;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;


public class Proc implements WatchdogListener, Runnable{

	String command;
	
	String dir;
	
	Process process;
	
	String result;
	
	boolean finished = false;
	
	public Proc(String command, String dir){
		this.command = command;
		this.dir = dir;
	}
	
	public void run() {
		try {
			boolean windows = System.getProperty("os.name").startsWith("Windows") ? true : false;
			if(windows){
				process = new ProcessBuilder("cmd").directory(new File(dir)).start();
				PrintWriter pw = new PrintWriter(process.getOutputStream());
				pw.println(command);
				pw.close();
			} else { 
				process = new ProcessBuilder(command).directory(new File(dir)).start();
			}
			
			try {
				process.waitFor();
				finished();
				
				BufferedReader in = new BufferedReader(
						new InputStreamReader(process.getInputStream()) );
				StringBuffer buf = new StringBuffer();
				String s;
				while( (s = in.readLine()) != null){
					buf.append(s+"\n");
				}
				result = buf.toString();
			} catch (InterruptedException e) {
				result = e.getMessage();
				forceDestroy();
			}	
		} catch (Exception e) {
			result = e.getMessage();
			forceDestroy();
		}		
	}

	public synchronized void timedout() {
		System.out.println("@timed out");
		forceDestroy();
	}
	
	private synchronized void forceDestroy(){
		if(isFinished() || process == null)
			return;
		process.destroy();
		finished();
		System.out.println("@force finished successfully.");
	}
	
	/**
	 * must be called if process is finished. 
	 */
	private synchronized void finished(){
		if(isFinished())
			return;
		
		try{
			if(process != null)
				process.exitValue();
		}catch(IllegalStateException e){	// process is not finished
			return;
		}
		
		finished = true;
		notifyAll();
	}
	
	public synchronized boolean isFinished(){
		return finished;
	}
	
	public String getResult(){
		return result;
	}
}
