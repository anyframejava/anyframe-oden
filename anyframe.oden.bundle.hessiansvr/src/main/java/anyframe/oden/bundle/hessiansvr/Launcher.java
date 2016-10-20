package anyframe.oden.bundle.hessiansvr;

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

	public String start() throws Exception{
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
