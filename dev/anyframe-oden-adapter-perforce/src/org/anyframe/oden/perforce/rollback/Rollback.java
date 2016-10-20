package org.anyframe.oden.perforce.rollback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.anyframe.oden.perforce.domain.BuildInfo;
import org.anyframe.oden.perforce.domain.CfgBuildDetail;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Copy;
import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.types.FileSet;
import org.json.JSONArray;
import org.json.JSONObject;

public class Rollback {

	private Project project;

	private String requestId;

	private List<String> ciId;

	private String baseDir;

	public Rollback(Project project, String requestId) {
		this.project = project;
		this.requestId = requestId;
		this.baseDir = project.getBaseDir().toString();
	}

	public void rollback() throws Exception {
		this.ciId = toBuildObject();
		// copy backup -> reference

		for (String requestId : ciId) {
			String src = baseDir + "/backup/" + requestId;
			String dest = baseDir + "/reference/classes";

			Copy copy = new Copy();
			copy.setProject(project);
			copy.init();

			copy.setTaskName("Rollback Build Resource");

			copy.setTodir(new File(dest));

			FileSet fs = new FileSet();
			fs.setDir(new File(src));

			copy.addFileset(fs);
			try {
				copy.execute();
			} catch (RuntimeException e) {
				throw e;
			}
		}

	}

	public void delete() throws Exception {
		// delete backup
		Delete del = new Delete();

		for (String requestId : ciId) {
			String src = baseDir + "/backup/" + requestId;
			del.setDir(new File(src));
			try {
				del.execute();
			} catch (RuntimeException e) {
				throw e;
			}
		}
	}

	public List<String> toBuildObject() throws Exception {
		List<String> rtn = new ArrayList<String>();

		if (!(requestId == null) && !("".equals(requestId))) {
			JSONArray array = new JSONArray(requestId);
			if (!(array.length() == 0)) {
				int recordSize = array.length();
				for (int i = 0; i < recordSize; i++) {
					JSONObject object = (JSONObject) array.get(i);
					rtn.add(object.getString("buildId"));
				}
			}
		}
		return rtn;
	}
}

