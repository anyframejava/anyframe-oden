package anyframe.oden.bundle.hessiansvr;

public class Launcher {
	
	Proc proc;
	
	Watchdog watchdog;
	
	public Launcher(Proc proc){
		this(proc, 60000);
	}
	
	public Launcher(Proc proc, long timeout) {
		this.proc = proc;
		watchdog = new Watchdog(proc, timeout);
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
		long start = System.currentTimeMillis();
		long remain;
		while( !listener.isFinished() &&
				(remain = timeout - (System.currentTimeMillis() - start)) > 0){
			synchronized (listener) {
				try {
					listener.wait(remain);
				} catch (InterruptedException e) {
				}	
			}
		}
			
		if(!listener.isFinished())
			listener.timedout();
	}
}
