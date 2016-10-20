package org.anyframe.oden.bundle.hessiansvr;

public interface WatchdogListener{	
	public void timedout();
	
	public boolean isFinished();
}
