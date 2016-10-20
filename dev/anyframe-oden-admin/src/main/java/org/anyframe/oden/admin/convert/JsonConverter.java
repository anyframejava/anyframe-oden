package org.anyframe.oden.admin.convert;

import org.anyframe.oden.admin.domain.BuildHistory;
import org.anyframe.oden.admin.domain.BuildRun;
import org.anyframe.oden.admin.domain.Command;
import org.anyframe.oden.admin.domain.Job;
import org.anyframe.oden.admin.domain.Log;
import org.anyframe.oden.admin.domain.Mapping;
import org.anyframe.oden.admin.domain.Target;
import org.hsqldb.lib.StringUtil;
import org.json.JSONArray;
import org.json.JSONObject;

public class JsonConverter {

	public static Job jsonToJob(Object object) throws Exception {
		Job job = new Job();

		if (object instanceof JSONObject) {
			JSONObject jsonObject = (JSONObject) object;
			job.setName(jsonObject.getString("name"));
			job.setGroup(jsonObject.getString("group"));
			job.setBuild(jsonObject.getString("build"));

			JSONObject sources = (JSONObject) jsonObject.get("source");
			job.setRepo(sources.getString("dir"));

			JSONArray excludes = (JSONArray) sources.get("excludes");
			String strExcludes = "";
			for (int num = 0; num < excludes.length(); num++) {
				strExcludes = strExcludes.concat(excludes.get(num) + ", ");
			}
			if (!"".equals(strExcludes)) {
				strExcludes = strExcludes.substring(0, strExcludes.length() - 2);
			}
			job.setExcludes(strExcludes);
		}

		return job;
	}

	public static Mapping jsonToMapping(Object object) throws Exception {
		Mapping mapping = new Mapping();

		if (object instanceof JSONObject) {
			JSONObject jsonObject = (JSONObject) object;

			String dir = jsonObject.getString("dir");
			String checkout = jsonObject.getString("checkout-dir");
			String key = dir + "@oden@" + checkout;

			mapping.setDir(dir);
			mapping.setCheckout(checkout);
			mapping.setHiddenname(key);
		}

		return mapping;
	}

	public static JSONObject mappingToJson(String mapping) throws Exception {
		JSONObject object = new JSONObject();

		String[] values = mapping.split("@oden@");
		String dir = values[0];
		String checkout = values[1];

		object.put("dir", dir);
		object.put("checkout-dir", checkout);

		return object;
	}

	public static JSONObject targetToJson(String target) throws Exception {
		JSONObject object = new JSONObject();

		String[] values = target.split("@oden@");
		String name = values[0];
		String url = values[1];
		String path = values[2];

		object.put("name", name);
		object.put("address", url);
		object.put("dir", path);

		return object;
	}

	public static JSONObject commandToJson(String command) throws Exception {
		JSONObject object = new JSONObject();

		String[] values = command.split("@oden@");
		String name = values[0];
		String path = values[1];
		String script = values[2];

		object.put("name", name);
		object.put("dir", path);
		object.put("command", script);

		return object;
	}

	public static BuildHistory jsonToBuildHistory(Object object) throws Exception {
		BuildHistory history = new BuildHistory();
		if (object instanceof JSONObject) {
			JSONObject jsonObject = (JSONObject) object;

			String date = jsonObject.getString("date");
			if (StringUtil.isEmpty(date.trim())) {
				history.setDate(0L);
			} else {
				history.setDate(Long.parseLong(date));
			}
			history.setConsoleUrl(jsonObject.getString("consoleUrl"));
			history.setSuccess(Boolean.parseBoolean(jsonObject.getString("success")));
			history.setJobName(jsonObject.getString("jobName"));
			history.setBuildNo(jsonObject.getString("buildNo"));
		}
		return history;
	}

	public static BuildRun jsonToBuildRun(Object object) throws Exception {
		BuildRun run = new BuildRun();
		if (object instanceof JSONObject) {
			JSONObject jsonObject = (JSONObject) object;
			run.setName(jsonObject.getString("name"));
			run.setBuildNo(jsonObject.getString("buildNo"));
			run.setConsoleUrl(jsonObject.getString("consoleUrl"));
		}
		return run;
	}

	public static Target jsonToTarget(Object object) throws Exception {
		Target target = new Target();
		if (object instanceof JSONObject) {
			JSONObject jsonObject = (JSONObject) object;

			String address = jsonObject.getString("address");
			String name = jsonObject.getString("name");
			String path = jsonObject.getString("dir");
			String status = jsonObject.getString("status");

			target.setName(name);
			target.setUrl(address);
			target.setPath(path);
			target.setStatus(status);
			target.setHiddenname(name);
		}
		return target;
	}

	public static Log jsonToLog(Object object) throws Exception {
		Log log = new Log();
		if (object instanceof JSONObject) {
			JSONObject jsonObject = (JSONObject) object;

			int total = jsonObject.getInt("total");
			int nsuccess = jsonObject.getInt("nsuccess");
			String status = jsonObject.getString("status");
			String txid = jsonObject.getString("txid");
			String job = jsonObject.getString("job");
			String date = jsonObject.getString("date");
			String user = jsonObject.getString("user");

			log.setTxid(txid);
			log.setStatus(status);
			log.setDate(date);
			log.setJob(job);
			log.setCounts(nsuccess + "/" + total);
			log.setUser(user);
		}
		return log;
	}

	public static Command jsonToCommand(Object object) throws Exception {
		Command command = new Command();
		if (object instanceof JSONObject) {
			JSONObject jsonObject = (JSONObject) object;

			String name = jsonObject.getString("name");
			String path = jsonObject.getString("dir");
			String cmd = jsonObject.getString("command");

			command.setName(name);
			command.setPath(path);
			command.setCmd(cmd);
			command.setHiddenname(name);

		}
		return command;
	}

}
