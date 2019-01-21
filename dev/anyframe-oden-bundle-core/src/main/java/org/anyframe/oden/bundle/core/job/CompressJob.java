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
package org.anyframe.oden.bundle.core.job;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.anyframe.oden.bundle.common.FileUtil;
import org.anyframe.oden.bundle.core.DeployFile;
import org.osgi.framework.BundleContext;

/**
 * Job to compress deploy files.
 * 
 * @author Junghwan Hong
 * @see org.anyframe.oden.bundle.core.job.Job
 */
@SuppressWarnings("PMD")
public abstract class CompressJob extends Job {

	protected String errorMessage;

	protected Collection<DeployFile> deployFiles = Collections.EMPTY_SET;

	protected Collection<DeployFile> compFile = Collections.EMPTY_SET;

	protected String path;

	private DeployFileResolver resolver;

	private CompressFileResolver compResolver;

	protected int compressWork = 4; // compress, transfer, extract;

	public CompressJob(BundleContext context, String desc, String path,
			DeployFileResolver resolver, CompressFileResolver compResolver) {
		super(context, desc);
		// TODO Auto-generated constructor stub
		this.path = path;
		this.resolver = resolver;
		this.compResolver = compResolver;
	}

	@Override
	void start() {
		status = RUNNING;
		currentWork = "compress deploy files...";
		String compSrc = null;
		String compTarget = null;
		final String path = "temp.zip";

		try {
			deployFiles = resolver.resolveDeployFiles();
			compFile = compResolver.compressDeployFiles();

			for (DeployFile f : compFile) {
				compTarget = f.getRepo().args()[1];
				compSrc = FileUtil
						.replace(f.getRepo().args()[0], "file://", "");
				break;
			}
			compTarget = FileUtil.combinePath(compTarget, path);
			// FileUtil.compress(new File(compSrc), new File(compTarget));
			compress(deployFiles, compSrc, new File(compTarget));

		} catch (Exception e) {
			errorMessage = e.getMessage();
			return;
		}

		totalWorks = deployFiles.size() + compFile.size() + compressWork;
		finishedWorks += 1; // 1 is kind of addtional work
		run();
		finishedWorks = totalWorks - compressWork; // 1 is kind of additional work
	}

	/**
	 * preview 의 파일들만 압축을 수행 deployFiles : preview 를 통한 배포 후보 파일 root :
	 * repository root directory jar : 압축 파일명
	 * 
	 * @param deployFiles
	 * @param jar
	 * @return
	 * @throws IOException
	 */
	private long compress(Collection<DeployFile> deployFiles, String root,
			File jar) throws IOException {
		long total = 0;
		Map<String, DeployFile> path = new HashMap<String, DeployFile>();

		if (jar.exists()) {
			jar.delete();
		}

		ZipOutputStream jout = null;
		try {

			jout = new ZipOutputStream(new BufferedOutputStream(
					new FileOutputStream(jar)));
			for (DeployFile f : deployFiles) {
				if (path.get(f.getPath()) != null)
					continue;
				path.put(f.getPath(), f);
				InputStream in = null;
				
				try {
					String src = FileUtil.replace(f.getRepo().args()[0], "file://",
							"");
					in = new BufferedInputStream(new FileInputStream(new File(
							FileUtil.combinePath(src, f.getPath()))));

					ZipEntry entry = new ZipEntry(FileUtil.combinePath(
							FileUtil.replace(src, root, ""), f.getPath()));
					jout.putNextEntry(entry);
					total += FileUtil.copy(in, jout);
				} catch(FileNotFoundException e){
					e.getStackTrace();
				} finally {
					if (in != null)
						in.close();
				}
			}
		} catch (Exception e) {
			e.getStackTrace();

		} finally {
			if (jout != null)
				jout.close();

		}

		return total;
	}

	@Override
	public int todoWorks() {
		return totalWorks - compressWork;
	}

	@Override
	protected void done() {

	}
}
