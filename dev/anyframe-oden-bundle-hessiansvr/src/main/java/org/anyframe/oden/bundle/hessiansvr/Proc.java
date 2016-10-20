package org.anyframe.oden.bundle.hessiansvr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;

import org.anyframe.oden.bundle.common.BundleUtil;
import org.anyframe.oden.bundle.common.FileUtil;


public class Proc implements WatchdogListener, Runnable{
	String command;
	File dir;
	Process process;
	String result;
	long timeout = 0;
	BufferedReader in = null;
	boolean isWin = false;
	Object timeoutLock = new Object();
	boolean timedout = false;
	
	public Proc(String command, String dir, long timeout){
		this.command = command;
		this.dir = new File(dir);
		this.timeout = timeout < 1 ? 20000 : timeout;
	}
	
	public void run2(){
//		StringBuffer buf = new StringBuffer();
		File redirectLog = new File(BundleUtil.odenHome(), "meta/out.log");
		if(redirectLog.exists())
			redirectLog.delete();
		else
			redirectLog.mkdirs();
		
		try {
			synchronized (this) {
				isWin = System.getProperty("os.name").startsWith("Windows");
				ProcessBuilder pb = null;
				if(isWin){
					pb = new ProcessBuilder(new String[]{"cmd", "/c", command +  
							" > " + redirectLog.getAbsolutePath()})
							.directory(dir);
					pb.redirectErrorStream(true);
					process = pb.start();
				} else {
					File cmd = new File(dir, command.split(" ")[0]);
					if(cmd.exists()){
//						List<String> cmdlist = new ArrayList<String>();
	//					for(String s : command.split(" "))
	//						cmdlist.add(s);
//						cmdlist.add(0, FileUtil.combinePath(dir.getAbsolutePath(), command) + " > " + redirectLog.getAbsolutePath());
//						cmdlist.add(0, "sh");
//						cmdlist.add(1, "-c");
//						pb = new ProcessBuilder(cmdlist.toArray(
//								new String[cmdlist.size()]));
						pb = new ProcessBuilder(new String[]{"sh", "-c", 
								FileUtil.combinePath(dir.getAbsolutePath(), command) +
								" > " + redirectLog.getAbsolutePath()});
					}else{
						pb = new ProcessBuilder(new String[]{"sh", "-c", command + 
								" > " + new File(BundleUtil.odenHome().getAbsolutePath(), "/meta/out.log").getAbsolutePath()})
								.directory(dir);
					}
					pb.redirectErrorStream(false);
					process = pb.start();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
//			buf.append(e.getMessage() + "\n");
			forceDestroy();
//		}finally{
//			try{ if(in !=null) in.close(); }catch(Exception e){}
		}
		
		synchronized (timeoutLock) {
			try{
				timeoutLock.wait();
			}catch(InterruptedException e){
				// ignore
			}
		}
		
		process = null;
		
		StringBuffer buf = new StringBuffer();
		BufferedReader in = null;
		try{
			in = new BufferedReader(new InputStreamReader(
					new FileInputStream(redirectLog)));
			String s = null;
			while( (s = in.readLine()) != null){	// being hanged in unix, not being hanged in win32
				buf.append(s+"\n");
			}
		}catch(IOException e){
			buf.append(e.getMessage());
		}
		result = buf.toString();
		
		synchronized (this) {
			notifyAll();	
		}
	}
	
	public void run() {
		StringBuffer buf = new StringBuffer();
		try {
			isWin = System.getProperty("os.name").startsWith("Windows");
			ProcessBuilder pb = null; 
			if(isWin){
				pb = new ProcessBuilder(new String[]{"cmd", "/c", command})
						.directory(dir);
				pb.redirectErrorStream(true);
				process = pb.start();
			} else {
				File cmd = new File(dir, command.split(" ")[0]);
				if(cmd.exists()){
					pb = new ProcessBuilder(new String[]{"sh", "-c", 
						FileUtil.combinePath(dir.getAbsolutePath(), command)})
							.directory(dir);
				}else{
					pb = new ProcessBuilder(new String[]{"sh", "-c", command})
							.directory(dir);
				}
				pb.redirectErrorStream(true);
				process = pb.start();
			}
			
			String enco = System.getProperty("sun.jnu.encoding");
			in = new BufferedReader(
					new InputStreamReader(process.getInputStream(), enco) );
			
			String s = null;
			// being hanged in unix, not being hanged in win32
			while(true){
				while(!timedout && !in.ready()){
					Thread.sleep(200);
				}
				if(timedout || (s = in.readLine()) == null)
					break;
				buf.append(s+"\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
			buf.append(e.getMessage() + "\n");
			forceDestroy();
		}finally{
			try{ if(in !=null) in.close(); }catch(Exception e){}
		}
		try {
			result = URLDecoder.decode(buf.append("\n>> executed: " + command + " in the " + dir + "\n").toString(),"utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		synchronized (this) {
			notifyAll();	
		}
	}

	public synchronized void timedout() {
//		if(!isWin){
//			forceDestroy();
//		}else{
//			try{
//				if(in != null)
//					in.close();
//			}catch(Exception e){}
//		}
		
		synchronized (timeoutLock) {
			timedout = true;
			timeoutLock.notifyAll();
		}
	}
	
	private synchronized void forceDestroy(){
		if(process != null)
			process.destroy();
	}
	
	public synchronized boolean isFinished(){
		try{
			if(process == null || in == null)
				return false;
			process.exitValue();
		}catch(Exception e){	
			// process is not finished
			return false;
		}
		return true;
	}
	
	public String getResult(){
		return result;
	}
}
