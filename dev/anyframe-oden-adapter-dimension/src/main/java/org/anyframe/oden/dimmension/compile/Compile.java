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
import org.apache.tools.ant.taskdefs.Move;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;

public class Compile {

	protected final Log logger = LogFactory.getLog(getClass());

	private Project project;

	//private String classes = "./reference/classes";
	//private String classes;

	//private String libs = "./reference/libs";
	//private String libs;

	private String dist = "./dist/";

	public Compile(Project project) {
		this.project = project;
	}
	
	public void rscTransfer(BuildInfo build) throws Exception {
		String srcRoot = build.getSrcRoot().equals(".") ? "" : "/"
			+ build.getSrcRoot() + "/";
		String srcAppd = build.getSrcAppd().equals(".") ? "" : "/"
			+ build.getSrcAppd() + "/";
		
		for (CfgBuildDetail detail : build.toBuildObject()) {
//			String rootSrc = dist + detail.getBuildId() + "/" + build.getProjectName();
//			String rootDest = "./build/" + detail.getBuildId();
//		
//			File rootXml = new File(rootSrc);
//			File[] files = rootXml.listFiles();
//			for(File file : files) {
//				if(! file.isDirectory()) {
//					if(file.getName().endsWith(".xml")) {
//						Copy copyRoot = new Copy();
//						copyRoot.setProject(project);
//						copyRoot.init();
//
//						copyRoot.setTaskName("Copy Root Resource File");
//						
//						File frDest = new File(rootDest);
//						
//						if (file.exists()) {
//							copyRoot.setTodir(frDest);
//
//							FileSet fs = new FileSet();
//							fs.setFile(file);
//							
//							copyRoot.addFileset(fs);
//							try {
//								copyRoot.execute();
//							} catch (RuntimeException e) {
//								throw e;
//							}
//						}
//
//					}
//				}
//			}
			
			if (! "".equals(srcRoot)) {
				String src = dist + detail.getBuildId() + "/"
						+ build.getSrcRoot();
				String dest = "./build/" + detail.getBuildId() + srcAppd;

				Copy copy = new Copy();
				copy.setProject(project);
				copy.init();

				copy.setTaskName("Copy Resource File");

				File f = new File(dest);
				File fst = new File(src);
				
				if (fst.exists()) {
					copy.setTodir(f);

					FileSet fs = new FileSet();
					fs.setDir(new File(src));
					fs.setExcludes("**/*.java");
					
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
			
			File f = new File(src);
					
			javac.setSrcdir(new Path(project, src));
			javac.setDestdir(new File(target));

			javac.setDebug(false);
			javac.setDebugLevel("lines,vars,source");
			javac.setEncoding("utf-8");
			javac.setDeprecation(false);
			javac.setOptimize(false);

			// 2. set class path
			Path classPath = new Path(project);

			String classesDir = build.getClassesRoot();
			Path classesPath = classPath.createPath();
			classesPath.setPath(classesDir);
			classPath.add(classesPath);

			FileSet fs = new FileSet();
			//fs.setDir(new File(libs));
			fs.setDir(new File(build.getLibsRoot()));
			fs.setIncludes("*.jar");
			classPath.addFileset(fs);

			javac.setClasspath(classPath);

			try {
				if(f.isDirectory()){
					javac.execute();					
				}
			} catch (RuntimeException e) {
				javac.setFailonerror(true);
				throw e;
			}
		}
	}

	public void webTransfer(BuildInfo build) throws Exception {
		// String projectName = build.getProjectName().toLowerCase();
		String webRoot = build.getWebRoot();
		String webAppd = build.getWebAppd().equals(".") ? "" : "/"
				+ build.getWebAppd() + "/";

		for (CfgBuildDetail detail : build.toBuildObject()) {
			if (!("".equals(webRoot) || ".".equals(webRoot))) {
				String src = dist + detail.getBuildId() + "/"
						+ build.getWebRoot();
				String dest = "./build/" + detail.getBuildId() + webAppd;
				String referenceDest = "./reference";

				Copy copy = new Copy();
				Copy webCopy = new Copy();
				
				copy.setProject(project);
				webCopy.setProject(project);
				copy.init();
				webCopy.init();

				copy.setTaskName("Copy Web Resource");

				File f = new File(dest);
				File fst = new File(src);
				
				if (fst.exists()) {
					copy.setTodir(f);

					FileSet fs = new FileSet();
					fs.setDir(new File(src));
					fs.setExcludes("**/.metadata/**");
					fs.setExcludes("**/META-INF/**");
					fs.setExcludes("**/*.java/**");

					copy.addFileset(fs); 
					
					
					
					webCopy.setTaskName("Copy web Refernce Resource");
					webCopy.setTodir(new File(referenceDest));
					FileSet webFs = new FileSet();
					webFs.setDir(new File(src));
					webFs.setExcludes("**//.metadata//**");
					webFs.setExcludes("**//*META-INF*//**");
					webFs.setExcludes("**//**.java");
					webCopy.addFileset(webFs);
					
					
					try {
						copy.execute();
						webCopy.execute();
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
			String src = "./build/" + detail.getBuildId() + "/" + build.getSrcAppd();
			//String dest = "./reference/classes/";
			String dest = build.getClassesRoot();

			Copy copy = new Copy();
			copy.setProject(project);
			copy.init();

			copy.setTaskName("Copy Refernce Resource");

			copy.setTodir(new File(dest));

			FileSet fs = new FileSet();
			fs.setDir(new File(src));
			fs.setExcludes("**/.metadata/**");
			fs.setExcludes("**/META-INF/**");
			fs.setExcludes("**/"+build.getJarWebRoot()+"/**");
			fs.setExcludes("**/*.jsp");
			fs.setExcludes("**/*.xml");

			copy.addFileset(fs);
			
			
			//String webDest = "./reference/";
			//Copy webCopy = new Copy();
			//webCopy.setProject(project);
			//webCopy.init();
			//webCopy.setTaskName("Copy web Refernce Resource");
			//webCopy.setTodir(new File(webDest));
			//FileSet webFs = new FileSet();
			//webFs.setDir(new File(src));
			//webFs.setExcludes("*.metadata*//**");
			//webFs.setExcludes("**//*META-INF*//**");
			//webFs.setExcludes("**//**.class");
			
			
			try {
				copy.execute();
				//webCopy.execute();
			} catch (RuntimeException e) {
				throw e;
			}
		}
	}
	
	/*public void jarRun(BuildInfo build, String packageType) throws Exception
	{
		for (CfgBuildDetail detail : build.toBuildObject()) {
			Manifest manifest = new Manifest();
			manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
			JarOutputStream target = new JarOutputStream(new FileOutputStream("./build/"+detail.getBuildId()+"/"+build.getPackageName()+".jar"), manifest);
			
			String jarClassesRoot = "." + build.getJarClassesRoot();
			//jarAdd(new File("./reference/WEB-INF/classes"), target, null);
			jarAdd(new File(jarClassesRoot), target, jarClassesRoot.replace(".", ""));
			target.close();			
		}
	}

	public void jarAdd(File source, JarOutputStream target, String classesRoot) throws IOException
	{
		//String referencePath = "/reference/WEB-INF/classes";
		String referencePath = classesRoot;

		BufferedInputStream in = null;
		  try
		  {
			  if (source.isDirectory())
		      {
				  String name = source.getPath().replace("\\", "/");
		          name = name.replace(referencePath, "");
		          
		          if (!name.equals(""))
		          {
		        	  if (!name.endsWith("/"))
		        		  name += "/";
		        	  JarEntry entry = new JarEntry(name);
		        	  entry.setTime(source.lastModified());
		        	  target.putNextEntry(entry);
		        	  target.closeEntry();
		          }
		          for (File nestedFile: source.listFiles())
		        	  jarAdd(nestedFile, target, classesRoot);
		          return;
		      }
		  
			  String entryName = source.getPath().replace("\\", "/");
			  entryName = entryName.replace(referencePath, "");
			  
			  JarEntry entry = new JarEntry(entryName);
			  entry.setTime(source.lastModified());
			  target.putNextEntry(entry);
			  in = new BufferedInputStream(new FileInputStream(source));
		      byte[] buffer = new byte[1024];
		   
		      while (true)
		      {
		    	  int count = in.read(buffer);
		    	  if (count == -1)
		    		  break;
		    	  target.write(buffer, 0, count);
		      }
		      target.closeEntry();
		 }
		 finally
		 {
			 if (in != null)
				 in.close();
		 }
	}*/

	public void packageMove(BuildInfo build, String packageType) throws Exception {
		
		
		for (CfgBuildDetail detail : build.toBuildObject()) {
			String src = build.getReference()+ "/";
			//String src = "./reference/";
			String dest = "./build/" + detail.getBuildId() + "/";
	
			
			
			Move move = new Move();
			move.setProject(project);
			move.init();
			move.setTaskName("Move  War or Jar");
			move.setTodir(new File(dest));
	
			FileSet fs = new FileSet();
			fs.setDir(new File(src));
			fs.setExcludes("**/.metadata/**");
			fs.setExcludes("**/META-INF/**");
			if("war".equals(packageType)){
				fs.setIncludes("**/"+build.getPackageName()+".war");				
			}else if("jar".equals(packageType)){
				fs.setIncludes("**/"+build.getPackageName()+".jar");
			}
			
			move.addFileset(fs);
			move.execute();
		}
	}
	
}
