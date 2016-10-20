package anyframe.oden.bundle.core;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import anyframe.oden.bundle.core.command.PolicyCommandImpl;
import anyframe.oden.bundle.test.OdenTest;

public class PolicyCommandT extends OdenTest{
	
	@Before
	public void before(){
		
	}
	
	@After
	public void after(){
		
	}
	
	@Test
	public void infoTest() throws Exception{
		String out = runCommand("policy info");
		assertTrue(out.equals(""));
	}
	
	@Test
	public void addTest(){
		
	}
	
	@Test
	public void removeTest(){
		
	}
	
	private String runCommand(String line) throws Exception{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayOutputStream err = new ByteArrayOutputStream();
		
		new PolicyCommandImpl().execute(line, new PrintStream(out), new PrintStream(err));
		if(err.size() > 0)
			throw new Exception(err.toString());
		return out.toString();
	}

	@Override
	protected String testBundle(){
		return ODEN_CORE_BND;
	}
}
