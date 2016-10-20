package org.anyframe.oden.dimmension.compile;

import java.io.File;

import org.anyframe.oden.dimmension.domain.BuildInfo;
import org.anyframe.oden.dimmension.domain.CfgBuildDetail;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.taskdefs.Mkdir;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

public class Compile {

	protected final Log logger = LogFactory.getLog(getClass());

	private Project project;

	private String classes = "./reference/classes";

	private String libs = "./reference/libs";

	private String dist = "./dist/";

	public Compile(Project project) {
		this.project = project;
	}

	public void execute(BuildInfo build) throws Exception {
		// String projectName = build.getProjectName().toLowerCase();
		String srcAppd = build.getSrcAppd().equals(".") ? "" : "/"
				+ build.getSrcAppd() + "/";

		for (CfgBuildDetail detail : build.toBuildObject()) {
			// 0. mkdir(build)
			Mkdir mkdir = new Mkdir();
			String target = "./build/" + detail.getBuildId() + srcAppd;

			mkdir.setDir(new File(target));
			mkdir.execute();

			// 1. compile
			Javac javac = new Javac();
			javac.setProject(project);
			javac.init();

			javac.setTaskName("compile");

			// String src = dist + detail.getBuildId() + "/" + projectName + "/"
			// + build.getSrcRoot();
			String src = dist + detail.getBuildId() + "/" + build.getSrcRoot();
			javac.setSrcdir(new Path(project, src));
			javac.setDestdir(new File(target));

			javac.setDebug(false);
			javac.setDebugLevel("lines,vars,source");
			javac.setEncoding("utf-8");
			javac.setDeprecation(false);
			javac.setOptimize(false);

			// 2. set class path
			Path classPath = new Path(project);

			String classesDir = classes;
			Path classesPath = classPath.createPath();
			classesPath.setPath(classesDir);
			classPath.add(classesPath);

			FileSet fs = new FileSet();
			fs.setDir(new File(libs));
			fs.setIncludes("*.jar");
			classPath.addFileset(fs);

			javac.setClasspath(classPath);

			try {
				javac.execute();
			} catch (RuntimeException e) {
				javac.setFailonerror(true);
				throw e;
			}
		}
	}

	public void webTransfer(BuildInfo build) throws Exception {
		// String projectName = build.getProjectName().toLowerCase();
		String webRoot = build.getWebRoot();
		String webAppd = build.getSrcAppd().equals(".") ? "" : "/"
				+ build.getSrcAppd() + "/";

		for (CfgBuildDetail detail : build.toBuildObject()) {
			if (!("".equals(webRoot) || ".".equals(webRoot))) {
				String src = dist + detail.getBuildId() + "/"
						+ build.getWebRoot();
				String dest = "./build/" + detail.getBuildId() + webAppd;

				Copy copy = new Copy();
				copy.setProject(project);
				copy.init();

				copy.setTaskName("Copy Web Resource");

				File f = new File(dest);
				if (f.exists()) {
					copy.setTodir(f);

					FileSet fs = new FileSet();
					fs.setDir(new File(src));
					fs.setExcludes("**/.metadata/**");
					fs.setExcludes("**/META-INF/**");

					copy.addFileset(fs);
					try {
						copy.execute();
					} catch (RuntimeException e) {
						throw e;
					}
				}
			}
		}
	}

	public void copyBackup(BuildInfo build) throws Exception {
		// copy to backup/Dxxxxx/buildNo
		for (CfgBuildDetail detail : build.toBuildObject()) {
			String src = "./build/" + detail.getBuildId();
			String dest = "./backup/" + detail.getBuildId();

			Copy copy = new Copy();
			copy.setProject(project);
			copy.init();

			copy.setTaskName("Copy Backup Resource");

			copy.setTodir(new File(dest));

			FileSet fs = new FileSet();
			fs.setDir(new File(src));
			fs.setExcludes("**/.metadata/**");
			fs.setExcludes("**/META-INF/**");

			copy.addFileset(fs);
			try {
				copy.execute();
			} catch (RuntimeException e) {
				throw e;
			}
		}
	}

	public void copyReference(BuildInfo build) throws Exception {
		// reference/classes ~~~~

		for (CfgBuildDetail detail : build.toBuildObject()) {
			String src = "./build/" + detail.getBuildId();
			String dest = "./reference/classes/";

			Copy copy = new Copy();
			copy.setProject(project);
			copy.init();

			copy.setTaskName("Copy Refernce Resource");

			copy.setTodir(new File(dest));

			FileSet fs = new FileSet();
			fs.setDir(new File(src));
			fs.setExcludes("**/.metadata/**");
			fs.setExcludes("**/META-INF/**");

			copy.addFileset(fs);
			try {
				copy.execute();
			} catch (RuntimeException e) {
				throw e;
			}
		}

	}
}
