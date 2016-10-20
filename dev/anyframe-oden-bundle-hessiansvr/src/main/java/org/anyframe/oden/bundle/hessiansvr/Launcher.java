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
package org.anyframe.oden.bundle.hessiansvr;

import org.anyframe.oden.bundle.deploy.CfgReturnScript;

public class Launcher {
	
	Proc proc;
	
	Watchdog watchdog;
	
	public Launcher(Proc proc){
		this(proc, 20000);
	}
	
	public Launcher(Proc proc, long timeout) {
		this.proc = proc;
		// proc will be finished in timeout
		// but some case, it is not finished
		// after waiting some secs, and kill that process forcefully.
		watchdog = new Watchdog(proc, timeout+3000);
	}

	public CfgReturnScript start() throws Exception{
		Thread th = new Thread(proc);
		th.start();
		watchdog.start();
		th.join();
		return proc.getResult();
	}
}

class Watchdog extends Thread{
	
	WatchdogListener listener;
	
	long timeout;
	
	public Watchdog(WatchdogListener listener, long timeout){
		this.listener = listener;
		this.timeout = timeout;
	}
	
	@Override
	public void run() {
		if(timeout == -1L)
			return;
		
		long start = System.currentTimeMillis();
		long remain;
		while( !listener.isFinished() &&
				(remain = timeout - (System.currentTimeMillis() - start)) > 0){
			synchronized (listener) {
				try {
//					listener.wait(remain);
					listener.wait(1000);
				} catch (InterruptedException e) {
				}	
			}
		}
		listener.timedout();
	}
}
