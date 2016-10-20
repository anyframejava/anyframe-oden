/*
 * Copyright 2009, 2010 SAMSUNG SDS Co., Ltd. All rights reserved.
 *
 * No part of this "source code" may be reproduced, stored in a retrieval
 * system, or transmitted, in any form or by any means, mechanical,
 * electronic, photocopying, recording, or otherwise, without prior written
 * permission of SAMSUNG SDS Co., Ltd., with the following exceptions:
 * Any person is hereby authorized to store "source code" on a single
 * computer for personal use only and to print copies of "source code"
 * for personal use provided that the "source code" contains SAMSUNG SDS's
 * copyright notice.
 *
 * No licenses, express or implied, are granted with respect to any of
 * the technology described in this "source code". SAMSUNG SDS retains all
 * intellectual property rights associated with the technology described
 * in this "source code".
 *
 */
package anyframe.oden.eclipse.core.jobmanager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.json.JSONArray;
import org.json.JSONObject;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.OdenTrees.TreeObject;
import anyframe.oden.eclipse.core.OdenTrees.TreeParent;
import anyframe.oden.eclipse.core.brokers.OdenBrokerImpl;
import anyframe.oden.eclipse.core.brokers.OdenBrokerService;
import anyframe.oden.eclipse.core.messages.CommandMessages;
import anyframe.oden.eclipse.core.messages.UIMessages;
import anyframe.oden.eclipse.core.utils.CommonUtil;

/**
 * The content provider class is responsible for providing objects to the view.
 * It can wrap existing objects in adapters or simply return objects as-is.
 * These objects may be sensitive to the current input of the view, or ignore it
 * and always show the same content (like Task List, for example).
 * 
 * @author HONG JungHwan
 * @version 1.1.0
 * 
 */
public class JobManagerViewContentProvider implements ITreeContentProvider,
		IStructuredContentProvider {

	private TreeParent invisibleRoot;

	protected OdenBrokerService OdenBroker = new OdenBrokerImpl();

	private LinkedHashMap<String, Integer> doneList;
	
	private ArrayList<JobManagerInfo> jobdata;
	
	private ArrayList<JobManagerInfo> finisheddata;
	
	private final static String FILE_NAME_DATE_PATTERN = "yyyyMMdd";
	
	private final static String FILE_NAME_DATE_PATTERN_FULL= "yyyy.MM.dd aa hh:mm:ss";

	private static final String START_TIME = "00";		// 00h 
	
	private static final String END_TIME = "23";			// 23h
	
	private String today;

	private String weekStartDate;

	private String weekEndDate;

	private String monthStartDate;

	private String monthEndDate;
	
	private String otherStartDate;
	
	private String otherEndDate;
	
	private String shellURL;
	
	CommonUtil util = new CommonUtil();
	
	/**
	 * Gets children of the element
	 */
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof TreeParent) {
			return ((TreeParent) parentElement).getChildren();
		}
		return new Object[0];
	}

	/**
	 * Gets a parent of the element
	 */
	public Object getParent(Object element) {

		return null;
	}

	/**
	 * Returns boolean value on existence of child element
	 */
	public boolean hasChildren(Object element) {

		Object[] object = getChildren(element);

		return object != null && object.length != 0;
	}

	/**
	 * Returns Object array with child elements
	 */
	public Object[] getElements(Object inputElement) {
		if (invisibleRoot == null) {
			initialize(inputElement);
			return getChildren(invisibleRoot);
		}
		return getChildren(inputElement);
	}

	/**
	 * 
	 */
	public void dispose() {

	}

	/**
	 * 
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

	private void initialize(Object inputElement) {
		String[] roots = { UIMessages.ODEN_JOBMANAGER_JobManagerView_TreeRoot_CurrentJob , UIMessages.ODEN_JOBMANAGER_JobManagerView_TreeRoot_FinishedJob };
		invisibleRoot = new TreeParent("");
		String progress = "";
		
		for (String rootnm : roots) {
			TreeParent root = new TreeParent(rootnm);
			invisibleRoot.addChild(root);

			if (rootnm.equals(UIMessages.ODEN_JOBMANAGER_JobManagerView_TreeRoot_CurrentJob)) {
				// 2 Level Current jobs
				getInit1stRoot();
				for (int i = 0; i < jobdata.size(); i++) {
					JobManagerInfo currentjob = (JobManagerInfo) jobdata.get(i);
					progress = currentjob.getProgress().equals("0") ? "[Preparing...]" : "["
							+ currentjob.getProgress() + "%]" ;
					
					TreeObject sub = new TreeObject(currentjob.getStatus()
							+ ":" + currentjob.getTxId() + "-"
							+ currentjob.getDesc() + progress);
					root.addChild(sub);
				}
				
			} else if (rootnm.equals(UIMessages.ODEN_JOBMANAGER_JobManagerView_TreeRoot_FinishedJob)) {
				// 2 Level Finished Jobs
				LinkedHashMap<String, Integer> hm = getInit2ndRoot();;
				Set<String> keys = hm.keySet();
				
				for(Iterator<String> it = keys.iterator() ; it.hasNext();) {
					String key = it.next();
					TreeParent sub = new TreeParent(key + "(" + hm.get(key) + ")");
					root.addChild(sub);
				}
			}
		}
		
	}

	private void getInit1stRoot() {
		String shellURL = this.getShellURL();
		String commnd = CommandMessages.ODEN_CLI_COMMAND_status_info + " "
				+ CommandMessages.ODEN_CLI_OPTION_json;
		jobdata = new ArrayList<JobManagerInfo>();
		String result = "";
		
		try {
			result = OdenBroker.sendRequest(shellURL, commnd);
			
			if (result != null && !(result.equals(""))) {
				JSONArray array = new JSONArray(result);

				for (int i = 0; i < array.length(); i++) {
					JobManagerInfo jobdone = new JobManagerInfo();
					jobdone.setStatus((String) String.valueOf(((JSONObject) array.get(i))
							.get("status")));
					jobdone.setDate((String) String.valueOf(((JSONObject) array.get(i))
							.get("date")));
					jobdone.setDesc((String) ((JSONObject) array.get(i))
							.get("desc"));
					jobdone.setTxId((String) ((JSONObject) array.get(i))
							.get("id"));
//					jobdone.setProgress(String.valueOf(
//							((JSONObject) array.get(i)).getInt("progress"))
//							.equals("") ? "Preparing..." : String
//							.valueOf(((JSONObject) array.get(i))
//									.getInt("progress")));
					jobdone.setProgress(String.valueOf(((JSONObject) array
							.get(i)).getInt("progress")));
					
					jobdata.add(jobdone);
				}
			}

		} catch (OdenException e) {
			
		} catch (Exception odenException) {
			OdenActivator.error("exception while getting agent status info",
					odenException);
			odenException.printStackTrace();
		}
	}

	private LinkedHashMap<String, Integer> getInit2ndRoot() {
		String shellURL = this.getShellURL();
		String commnd = CommandMessages.ODEN_CLI_COMMAND_history_info + " "
				+ CommandMessages.ODEN_CLI_OPTION_date + " "
				+ "00000000 99999999" + " "
				+ CommandMessages.ODEN_CLI_OPTION_json;
		finisheddata = new ArrayList<JobManagerInfo>();

		try {
			String result = OdenBroker.sendRequest(shellURL, commnd);
			if (result != null && !(result.equals(""))) {
				JSONArray array = new JSONArray(result);

				for (int i = 0; i < array.length(); i++) {
					JobManagerInfo jobdone = new JobManagerInfo();
					jobdone.setStatus((String) ((JSONObject) array.get(i))
							.get("status"));
					jobdone.setDate((String) String.valueOf(((JSONObject) array.get(i))
							.get("date")));
					jobdone.setDesc((String) ((JSONObject) array.get(i))
							.get("desc"));
					jobdone.setTxId((String) ((JSONObject) array.get(i))
							.get("id"));

					String desc = jobdone.getDesc();
					String parent = desc.startsWith("history redeploy") ? desc.substring(desc.indexOf("history redeploy")+ 16): "";
					
					jobdone.setParent(parent);
					
					finisheddata.add(jobdone);
				}
			}

		} catch (OdenException e) {
	
		} catch (Exception odenException) {
			OdenActivator.error("exception while getting agent status info",
					odenException);
			odenException.printStackTrace();
		}

		return this.getTxCount(finisheddata);
	}

	private LinkedHashMap<String, Integer> getTxCount(ArrayList<JobManagerInfo> jobcount) {
		// 1: today 2: weeks 3: month 4: others
		doneList = new LinkedHashMap <String , Integer>();
		doneList.put(UIMessages.ODEN_JOBMANAGER_JobManagerView_FinishedJob_Today , 0);
		doneList.put(UIMessages.ODEN_JOBMANAGER_JobManagerView_FinishedJob_Week , 0);
		doneList.put(UIMessages.ODEN_JOBMANAGER_JobManagerView_FinishedJob_Month , 0);
		doneList.put(UIMessages.ODEN_JOBMANAGER_JobManagerView_FinishedJob_Long , 0);
		
		try {
//			today = this.getDate();
			today = util.getDateFormat("yyyyMMdd");
			
			weekStartDate = this.beforedate(today, 0, -7);
			weekEndDate = this.beforedate(today, 0, -1);
			monthStartDate = this.beforedate(today, 1, -1);
			monthEndDate = this.beforedate(today, 0, -8);
			otherStartDate = "00000000";
			otherEndDate = this.beforedate(monthStartDate, 0, -1);
		} catch (OdenException odenException) {
			OdenActivator.error("exception while getting agent status info",odenException);
		}
		
		for (JobManagerInfo jobdone : jobcount) {
			switch (this.chkTxKind(new SimpleDateFormat(FILE_NAME_DATE_PATTERN)
					.format(Long.valueOf(jobdone.getDate())))) {
			case 1:
				// today
				doneList.put(UIMessages.ODEN_JOBMANAGER_JobManagerView_FinishedJob_Today, doneList.get(UIMessages.ODEN_JOBMANAGER_JobManagerView_FinishedJob_Today) + 1);
				break;
			case 2:
				// weeks
				doneList.put(UIMessages.ODEN_JOBMANAGER_JobManagerView_FinishedJob_Week, doneList.get(UIMessages.ODEN_JOBMANAGER_JobManagerView_FinishedJob_Week) + 1);
				break;
			case 3:
				// month
				doneList.put(UIMessages.ODEN_JOBMANAGER_JobManagerView_FinishedJob_Month, doneList.get(UIMessages.ODEN_JOBMANAGER_JobManagerView_FinishedJob_Month) + 1);
				break;
			default:
				// others
				doneList.put(UIMessages.ODEN_JOBMANAGER_JobManagerView_FinishedJob_Long, doneList.get(UIMessages.ODEN_JOBMANAGER_JobManagerView_FinishedJob_Long) + 1);
				break;
			}
		}

		return doneList;
	}

	/*
	 * add done work list
	 */
	public ArrayList<String> addJobDoneChild(String elementName) {
		ArrayList<String> returnList = new ArrayList<String>();
		String fromdate;
		String todate;
		
		if(elementName.equals(UIMessages.ODEN_JOBMANAGER_JobManagerView_FinishedJob_Today)) {
			fromdate = today;
			todate = today;
		} else if(elementName.equals(UIMessages.ODEN_JOBMANAGER_JobManagerView_FinishedJob_Week)) {
			fromdate = weekStartDate;
			todate = weekEndDate;
		} else if(elementName.equals(UIMessages.ODEN_JOBMANAGER_JobManagerView_FinishedJob_Month)) {
			fromdate = monthStartDate;
			todate = monthEndDate;
		} else {
			fromdate = otherStartDate;
			todate = otherEndDate;
		}
		for(JobManagerInfo jobdone : finisheddata) {
			try {
				if (this.acceptedDate(Long.valueOf(jobdone.getDate()), this
						.longTime(fromdate + START_TIME), this.longTime(todate
						+ END_TIME))) {
					returnList.add(jobdone.getStatus() + ":" + jobdone.getTxId() + "[" + chgDateFormat(jobdone.getDate())
							+ "-" + jobdone.getDesc() + "]");
					
				}
			} catch (Exception odenException) {
				OdenActivator.error("exception while adding finished jobs",odenException);
			} 
		}
		return returnList;
	}
	
	private int chkTxKind (String date) {
		String inputdate = date.substring(0, 8);
		try {
			if (this.acceptedDate(this.longTime(inputdate + START_TIME), this
					.longTime(today + START_TIME), this.longTime(today
					+ END_TIME))) {
				// today
				return 1;
			} else if (this.acceptedDate(this.longTime(inputdate + START_TIME),
					this.longTime(weekStartDate + START_TIME), this
							.longTime(weekEndDate + END_TIME))) {
				// weeks
				return 2;
			} else if (this.acceptedDate(this.longTime(inputdate + START_TIME),
					this.longTime(monthStartDate + START_TIME), this
							.longTime(monthEndDate + END_TIME))) {
				// month
				return 3;
			} 
			
		} catch (Exception odenException) {
			OdenActivator.error("exception while parsing date processing.",odenException);
		}
		return 4;		
	}

	/*
	 * option(0: day , 1: month) , day : ex -1 before 1 day or month
	 */
	private String beforedate(String date, int option, int day) {
		SimpleDateFormat formatter = new SimpleDateFormat(FILE_NAME_DATE_PATTERN);
		Date now;
		try {
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.set(Integer.parseInt(date.substring(0, 4)), Integer
					.parseInt(date.substring(4, 6)) - 1, Integer.parseInt(date
					.substring(6)));
			if (option == 0)
				calendar.add(Calendar.DATE, day);
			else
				calendar.add(Calendar.MONTH, day);
			now = calendar.getTime();
		} catch (Exception ie) {
			return "";
		}
		return formatter.format(now);
	}

	private boolean acceptedDate(long inputdate, long startdate, long enddate) {
		return inputdate >= startdate && inputdate <= enddate;
	}
	
	private long longTime(String s) throws ParseException {
		return new SimpleDateFormat("yyyyMMddHH").parse(s).getTime();
	}

	private String chgDateFormat(String input) {
		return new SimpleDateFormat(FILE_NAME_DATE_PATTERN_FULL).format(Long.valueOf(input));
	}
	
	/*
	 * getter data(today, week start/end , month start/end
	 * others start/end
	 */
	public String getToday() {
		return today;
	}

	public String getWeekStartDate() {
		return weekStartDate;
	}

	public String getWeekEndDate() {
		return weekEndDate;
	}

	public String getMonthStartDate() {
		return monthStartDate;
	}

	public String getMonthEndDate() {
		return monthEndDate;
	}

	public String getOtherStartDate() {
		return otherStartDate;
	}

	public String getOtherEndDate() {
		return otherEndDate;
	}

	/*
	 * setter url
	 */
	public void setShellURL(String shellURL) {
		this.shellURL = shellURL;
	}
	
	public String getShellURL() {
		return shellURL;
	}
}
