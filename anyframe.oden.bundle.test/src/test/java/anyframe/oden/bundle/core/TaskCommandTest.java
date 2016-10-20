package anyframe.oden.bundle.core;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.osgi.framework.InvalidSyntaxException;

import anyframe.oden.bundle.core.command.PolicyCommandImpl;
import anyframe.oden.bundle.core.command.TaskCommandImpl;
import anyframe.oden.bundle.test.OdenTest;
import anyframe.oden.bundle.test.PrintStreamWrapper;

public class TaskCommandTest extends OdenTest{

	@Override
	protected String testBundle() {
		return ODEN_CORE_BND;
	}
	
	/**
	 * task policy-create1 >> policy-create2 >> 
	 * info >> remove >> create >> info >> modify >> info  >> 
	 * run >> remove >> info >> policy-info
	 */
	@Test
	public void taskTest() throws Exception{
		Object policyCmd = getPolicyCommand();
		Object taskCmd = getTaskCommand();
		
		PrintStreamWrapper wout = new PrintStreamWrapper();
		PrintStreamWrapper werr = new PrintStreamWrapper();
		String cmdline = "policy add p0 -r file://" + srcPath() +  " -i ** -d agent0";
		invokeMethod(policyCmd, "execute", cmdline, wout.printStream(), werr.printStream());
		System.out.println(werr.contents());
		assertTrue(werr.contents().length() == 0);
		
		wout = new PrintStreamWrapper();
		werr = new PrintStreamWrapper();
		cmdline = "policy add p1 -r file://" + srcPath() +  " -i ** -d agent0";
		invokeMethod(policyCmd, "execute", cmdline, wout.printStream(), werr.printStream());
		System.out.println(werr.contents());
		assertTrue(werr.contents().length() == 0);
		
		
	}
	
	private Object getPolicyCommand() throws InvalidSyntaxException{
		return getService(org.ungoverned.osgi.service.shell.Command.class,
				PolicyCommandImpl.class);
	}

	private Object getTaskCommand() throws InvalidSyntaxException{
		return getService(org.ungoverned.osgi.service.shell.Command.class,
				TaskCommandImpl.class);
	}
	
}
