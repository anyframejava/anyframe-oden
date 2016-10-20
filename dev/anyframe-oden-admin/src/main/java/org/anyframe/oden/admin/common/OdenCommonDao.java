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
package org.anyframe.oden.admin.common;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import org.anyframe.oden.admin.domain.Job;
import org.anyframe.oden.admin.domain.Log;
import org.anyframe.oden.admin.exception.BrokerException;
import org.anyframe.pagination.Page;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * This class provides some common methods.
 * 
 * @author Junghwan Hong
 * @author Sujeong Lee
 */
public class OdenCommonDao<T> {
	String domainName = "org.anyframe.oden.admin.domain.";
	String ahrefPre = "<a href=\"";
	String ahrefMid = "\">";
	String ahrefPost = "</a>";
	
	@Inject
	@Named("brokerHandler")
	private BrokerHandler brokerHandler;

	public void update(String cmd) throws Exception {
		brokerHandler.cmdConnect(cmd);
	}

	public void remove(String cmd, String name) throws Exception {
		String command = cmd + " " + "del" + " " + name + " " + "-json";
		brokerHandler.cmdConnect(command);
	}

	public String getResultString(String cmd, String opt) throws Exception {
		return getResultString(cmd, opt, "");
	}

	public String getResultString(String cmd, String opt, String param)
			throws Exception {
		String command = "";
		if (param == null || param.length() == 0) {
			command = cmd + " " + opt + " " + "-json";
		} else {
			command = cmd + " " + opt + " " + param + " " + "-json";
		}

		return brokerHandler.cmdConnect(command);
	}

	public String getResultString(String cmd, String opt, String[] param)
			throws Exception {
		String command = "";
		if (param == null || param.length == 0) {
			command = cmd + " " + opt + " " + "-json";
		} else {
			command = cmd + " " + opt + " " + param + " " + "-json";
		}
		return brokerHandler.cmdConnect(command);
	}

	public Page getList(String cmd, String opt) throws Exception {
		return getList(cmd, opt, null);
	}

	public Page getList(String cmd, String opt, String param) throws Exception {
		String command = "";
		if (param == null || param.length() == 0) {
			command = cmd + " " + opt + " " + "-json";
		} else {
			command = cmd + " " + opt + " " + param + " " + "-json";
		}
		String result = brokerHandler.cmdConnect(command);

		List<T> list = new ArrayList();
		
		if ("status".equals(cmd)) {
			list = strToJsonList1(cmd, result, false);
		} else {
			list = strToJsonList(cmd, result, false);
		}
		
		if (list.isEmpty()) {
			return new Page(list, 1, list.size(), 1, 1);
		} else {
			return new Page(list, 1, list.size(), list.size(), list.size());
		}
	}

	@SuppressWarnings("PMD")
	public List<Job> findJob(String cmd, String opt, List<String> roles)
			throws Exception {
		String command = "";
		List<Job> list = new ArrayList<Job>();

		command = cmd + " " + opt + " " + "-json";

		String result = brokerHandler.cmdConnect(command);
		JSONArray jobs = new JSONArray(result);
		
		for (int i = 0; i < jobs.length(); i++) {
			if (roles.contains(jobs.getString(i))
					|| roles.get(0).equals("ROLE_ADMIN")) {
				Job job = new Job();
				job.setId(String.valueOf(i));
				job.setJobname(jobs.getString(i));
				list.add(job);
			}
		}

		return list;
		// return new Page(list, 1, list.size(), list.size(), list.size());

	}

	public Log findLog(String cmd, String opt, String param) throws Exception {
		String command = "";
		Log log = new Log();

		if (param == null || param.length() == 0) {
			command = cmd + " " + opt + " " + "-json";
		} else {
			command = cmd + " " + opt + " " + "-date" + " " + param + " "
					+ "-json";
		}

		String result = brokerHandler.cmdConnect(command);
		JSONArray logs = new JSONArray(result);
		for (int i = 0; i < logs.length(); i++) {
			JSONObject object = (JSONObject) logs.get(i);
			if (object.has("KnownException")) {
				log.setFilename("");
				log.setContents(object.getString("KnownException"));
			} else {
				log.setFilename(object.getString("date"));
				log.setContents(object.getString("contents"));
			}
		}

		return log;
		// return new Page(list, 1, list.size(), list.size(), list.size());

	}

	// public T findByPk(String cmd, String name) throws Exception {
	//
	// String command = "";
	// command = cmd + " " + "info" + " " + name + " " + "-json";
	// String result = BrokerHandler.cmdConnect(command);
	//
	// List<T> list = strToJsonList(cmd, result, false);
	//
	// return list.get(0);
	// }
	//
	//
	//
	// public void create(String string, Object vo) throws Exception {
	// Class<T> objClass = getClassName(string);
	// T c = objClass.newInstance();
	// c = (T) vo;
	// Field[] fields = c.getClass().getDeclaredFields();
	//
	// String command = string + " " + "add";
	//
	// String name = "";
	// String opt = "";
	//
	// for (int i = 0; i < fields.length; i++) {
	// fields[i].setAccessible(true);
	// String f = fields[i].get(c) + "";
	//
	// if (fields[i].getName().equalsIgnoreCase("name")) {
	// name = "\"" + f + "\"";
	// } else if (f.equalsIgnoreCase("true")) { // just option
	// opt += "-" + fields[i].getName() + " ";
	// } else if (f.equalsIgnoreCase("false")) { // just option
	// } else if (f == null || f.length() == 0 || f.equalsIgnoreCase("")) {
	// } else {
	// opt += "-" + fields[i].getName() + " \"" + f + "\" ";
	// }
	//
	// }
	// command = command.trim() + " " + name.trim() + " " + opt.trim() + " "
	// + "-json";
	//
	// BrokerHandler.cmdConnect(command);
	// }

	private List<T> strToJsonList(String cmd, String result, boolean isList)
			throws Exception {
		boolean multiArr = false;
		if (!(result == null) && !("".equals(result))) {
			JSONArray array = new JSONArray(result);
			if (!(array.length() == 0)) {
				List<T> list = new ArrayList<T>();

				int recordSize = array.length();
				for (int i = 0; i < recordSize; i++) {
					JSONObject object = (JSONObject) array.get(i);
					// String total = object.getString("total");
					// int totalNum = Integer.parseInt(total);
					JSONArray data = (JSONArray) object.get("data");
					if (!(data.length() == 0)) {
						for (int j = 0; j < data.length(); j++) {

							JSONObject dataObj = (JSONObject) data.get(j);
							Iterator itr = dataObj.keys();

							if (isKeyValue(cmd)) {
								// policy, task, snapshot
								while (itr.hasNext()) {

									String key = itr.next() + "";
									String value = dataObj.get(key) + "";

									if (isList) {
										list.addAll(setToList(cmd, key, value));
									} else {
										list
												.addAll(setToObject(cmd, key,
														value));
									}
								}
							} else {
								// other command
								// TODO to get method
								// now only history
								// 이런식으로 CRUD 할 예정
								String[] keys = dataObj.getNames(dataObj);

								for (String key : keys) {
									if ("files".equals(key)) {
										multiArr = true;
									}
								}
								if (multiArr) {
									list = multiJson(cmd, dataObj);
								} else {
									list.add(setOnlyHistory(cmd, dataObj));
								}
							}
						}
					}

				}

				return list;
			} else {
				return new ArrayList();
			}
		} else {
			return new ArrayList();
		}
	}

	private List<T> strToJsonList1(String cmd, String result, boolean isList)
			throws Exception {
		boolean multiArr = false;
		if (!(result == null) && !("".equals(result))) {
			JSONArray array = new JSONArray(result);
			if (!(array.length() == 0)) {
				List<T> list = new ArrayList<T>();

				int recordSize = array.length();
				for (int i = 0; i < recordSize; i++) {
					JSONObject object = (JSONObject) array.get(i);

					Iterator itr = object.keys();

					if (isKeyValue(cmd)) {
						// policy, task, snapshot
						while (itr.hasNext()) {

							String key = itr.next() + "";
							String value = object.get(key) + "";

							if (isList) {
								list.addAll(setToList(cmd, key, value));
							} else {
								list.addAll(setToObject(cmd, key, value));
							}
						}
					} else {
						// other command
						// TODO to get method
						// now only history
						// 이런식으로 CRUD 할 예정
						String[] keys = object.getNames(object);

						for (String key : keys) {
							if ("files".equals(key)) {
								multiArr = true;
							}
						}
						if (multiArr) {
							list = multiJson(cmd, object);
						} else {
							list.add(setOnlyHistory(cmd, object));
						}
					}
				}

				return list;
			} else {
				return new ArrayList();
			}
		} else {
			return new ArrayList();
		}
	}

	private List<T> multiJson(String cmd, JSONObject object)
			throws Exception {
		List<T> list = new ArrayList<T>();
		Class<T> objClass = getClassName(cmd);
		JSONArray detailInfo = (JSONArray) object.get("files");

		for (int i = 0; i < detailInfo.length(); i++) {
			JSONObject obj = (JSONObject) detailInfo.get(i);

			// setter(fields, c , obj);
			list.add(setter(objClass, obj, String.valueOf(i + 1)));
		}

		return list;
	}

	private T setOnlyHistory(String cmd, JSONObject object) throws Exception {
		Class<T> objClass = getClassName(cmd);
		T c = objClass.newInstance();
		Field[] fields = c.getClass().getDeclaredFields();
		String classname = objClass.getName();

		String imgStop = "<img src='images/stop.png' style='vertical-align:middle;'/>";

		for (int i = 0; i < fields.length; i++) {
			fields[i].setAccessible(true);
			String name = fields[i].getName();
			if (object.has(name)) {
				if ("progress".equals(name)) {
					String workfile = (String) object.get("currentWork");

					if (workfile.length() > 40) {
						int num = workfile.length();
						workfile = "..." + workfile.substring(num - 40);
					}

					String progress = object.get(name).equals(0) ? "[Preparing...]"
							: workfile + "[" + object.get(name) + "%]";

					fields[i].set(c, progress + " " + "|" + " " + ahrefPre
							+ "javascript:stopDeploy('"
							+ object.getString("id") + "');" + ahrefMid
							+ imgStop + ahrefPost);
				} else if (classname.endsWith("Status") && "date".equals(name)) {
					fields[i].set(c, chgDateFormat(Long.valueOf(object
							.getString(name)))
							+ "");
				} else {
					fields[i].set(c, object.get(name) + "");
				}
			}
			if ("counts".equals(name)) {
				String counts = object.get("nsuccess") + "/"
						+ object.get("total");
				fields[i].set(c, counts + "");
			}
		}

		return c;
	}

	private T setter(Class<T> objClass, JSONObject obj, String no)
			throws Exception {
		T c = objClass.newInstance();
		Field[] fields = c.getClass().getDeclaredFields();
		
		for (Field field : fields) {
			field.setAccessible(true);
			String name = field.getName();

			if (obj.has(name)) {
				if ("success".equals(name)) {
					field.set(c, obj.get("success").equals("true") ? "Success"
							: "Failed" + "");
				} else {
					field.set(c, obj.get(name) + "");
				}
			} else if ("group".equals(name)) {
				JSONObject agents = getJsonObject(obj.getString("agent"));
				field.set(c, agents.getString("name") + "");
			} else if ("id".equals(name)) {
				field.set(c, no + "");
			}
		}
		return c;
	}
	
	private JSONObject getJsonObject(String agent) throws Exception {
		return new JSONObject(agent);
	}
	
	// TODO
	// json 설정값 정해지면 수정
	private Collection<T> setToObject(String cmd, String key, String value) throws Exception {
		ArrayList<T> list = new ArrayList<T>();
		try {
			Class<T> t = getClassName(cmd);
			T tobj = t.newInstance();
			Field[] f = tobj.getClass().getDeclaredFields();

			Cmd c = new Cmd("foo", "fooAction \"" + key + "\" " + value);

			String[] repoArray = c
					.getOptionArgArray(new String[] { "r", "repo" });
			String[] destArray = c
					.getOptionArgArray(new String[] { "d", "dest" });
//			String desc = c.getOptionArg(new String[] { "desc" });
			String[] includeArray = c.getOptionArgArray(new String[] { "i",
					"include" });
			String[] excludeArray = c.getOptionArgArray(new String[] { "e",
					"exclude" });

			Boolean update = value.indexOf("-u") > 0 ? true : false;
			Boolean del = value.indexOf("-del") > 0 ? true : false;

			String repo = arrayToString(repoArray);
			String dest = arrayToString(destArray);
			String include = arrayToString(includeArray);
			String exclude = arrayToString(excludeArray);

			for (int n = 0; n < f.length; n++) {
				String field = f[n].getName();
				f[n].setAccessible(true);
				if (field.equalsIgnoreCase("name")) {
					f[n].set(tobj, key);
				} else if (field.equalsIgnoreCase("repo")) {
					f[n].set(tobj, repo);
				} else if (field.equalsIgnoreCase("dest")) {
					f[n].set(tobj, dest);
				} else if (field.equalsIgnoreCase("include")) {
					f[n].set(tobj, include);
				} else if (field.equalsIgnoreCase("exclude")) {
					f[n].set(tobj, exclude);
				} else if (field.equalsIgnoreCase("update")) {
					f[n].set(tobj, update + "");
				} else if (field.equalsIgnoreCase("del")) {
					f[n].set(tobj, del + "");
				}
			}
			list.add(tobj);
		} catch (Exception e) {
			throw new BrokerException(e.getMessage());
		}
		return list;
	}

	// name-value
	private List<T> setToList(String cmd, String key, String value) throws Exception {
		List<T> list = new ArrayList<T>();
		try {
			Class<T> t = getClassName(cmd);
			T tobj = t.newInstance();
			Field[] f = tobj.getClass().getDeclaredFields();

			for (int n = 0; n < f.length; n++) {
				String field = f[n].getName();
				f[n].setAccessible(true);
				if (field.equalsIgnoreCase("name")) {
					f[n].set(tobj, key);
				} else if (field.equalsIgnoreCase("detail")) {
					f[n].set(tobj, value);
				}
			}
			list.add(tobj);
		} catch (Exception e) {
			throw new BrokerException(e.getMessage());
		}
		return list;
	}

	private String arrayToString(String[] str) {
		String result = "";
		for (int i = 0; i < str.length; i++) {
			result = result.concat(str[i] + " ");
		}
		return result.trim();
	}

	@SuppressWarnings("unchecked")
	private Class<T> getClassName(String cmd) throws ClassNotFoundException {
		String first = cmd.substring(0, 1);
		String className = first.toUpperCase() + cmd.substring(1);

		return (Class<T>) Class.forName(domainName + className);
	}

	// policy, task, snapshot info -> name, detail 형태로 return
	// 그 외는 option별로 return
	private boolean isKeyValue(String cmd) {

		if (cmd.equalsIgnoreCase("policy") || cmd.equalsIgnoreCase("task")
				|| cmd.equalsIgnoreCase("snapshot")) {
			return true;
		} else {
			return false;
		}

	}

	/*
	 * milisecond 를 yyyymmdd hhmmss 형태로 변환
	 */
	public String chgDateFormat(Long input) {
		return new SimpleDateFormat("yyyy.MM.dd aa hh:mm:ss", Locale
				.getDefault()).format(input);
	}

	// only for job command
	public List getListByList(String string, Object object) throws Exception {
		List result_list = new ArrayList();
		String result = brokerHandler.cmdConnect(string + " "
				+ object.toString() + " " + "-json");

		if (!(result == null) && !("".equals(result))) {
			JSONArray array = new JSONArray(result);
			if (!(array.length() == 0)) {
				int recordSize = array.length();
				for (int i = 0; i < recordSize; i++) {
					result_list.add(array.get(i) + "");
				}
			}
		}
		return result_list;
	}
}
