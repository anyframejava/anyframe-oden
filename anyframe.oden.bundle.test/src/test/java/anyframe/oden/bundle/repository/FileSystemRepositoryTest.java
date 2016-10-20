package anyframe.oden.bundle.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import anyframe.oden.bundle.test.OdenTest;

public class FileSystemRepositoryTest extends OdenTest{
	public void testResolve() {
	}
	
	@Test
	public void testResolveFileRegex() throws Exception{
		List<String> incs = new ArrayList<String>();
		incs.add("**");
		List<String> excs = new ArrayList<String>();
		excs.add("**/*.xml");
		invokeMethod(RepositoryService.class, FileSystemRepositoryImpl.class, 
				"resolveFileRegex", new String[]{"file://./"}, "", "", incs, excs);
	}

	@Override
	protected Map bundleProperties() {
		return Collections.EMPTY_MAP;
	}

	@Override
	protected String testBundle() {
		return ODEN_REPOSITORY_BND;
	}
}
