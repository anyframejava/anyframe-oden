/*
 * Copyright 2009 SAMSUNG SDS Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package anyframe.oden.eclipse.core.snapshot;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.OdenTrees.TreeObject;
import anyframe.oden.eclipse.core.OdenTrees.TreeParent;
import anyframe.oden.eclipse.core.brokers.OdenBrokerImpl;
import anyframe.oden.eclipse.core.brokers.OdenBrokerService;
import anyframe.oden.eclipse.core.messages.CommandMessages;
import anyframe.oden.eclipse.core.messages.UIMessages;
import anyframe.oden.eclipse.core.utils.Cmd;

/**
 * Tree content provider for Snapshot view. 
 * 
 * @author LEE Sujeong
 * @version 1.0.0 RC2
 * 
 */
public class SnapshotViewContentProvider implements ITreeContentProvider, IStructuredContentProvider {

	protected static TreeParent invisibleRoot;
	protected static String inputText;
	protected static String SHELL_URL;
	
	protected static OdenBrokerService OdenBroker = new OdenBrokerImpl();
	
	private static String[] PLAN_OPT = { "plan", "p" }; //$NON-NLS-1$ //$NON-NLS-2$

	private static final String MSG_SNAPSHOT_INFO_PLAN = CommandMessages.ODEN_SNAPSHOT_SnapshotView_MsgInfoPlan;
	private static final String MSG_SNAPSHOT_INFO_FILE = CommandMessages.ODEN_SNAPSHOT_SnapshotView_MsgInfoFile;

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
	}

	public void dispose() {
	}

	/**
	 * Gets elements of parent
	 */
	public Object[] getElements(Object parent) {
		new SnapshotView();
		invisibleRoot = SnapshotView.invisibleRoot;
		if (invisibleRoot == null) {
			initialize();
		}
		return getChildren(invisibleRoot);
	}

	/**
	 * Gets a parent of the element
	 */
	public Object getParent(Object child) {
		if (child instanceof TreeObject) {
			return ((TreeObject) child).getParent();
		}
		return null;
	}

	/**
	 * Gets children of the element
	 */
	public Object[] getChildren(Object parent) {
		if (parent instanceof TreeParent) {
			return ((TreeParent) parent).getChildren();
		}
		return new Object[0];
	}

	/**
	 * 
	 */
	public boolean hasChildren(Object parent) {
		if (parent instanceof TreeParent)
			return ((TreeParent) parent).hasChildren();
		return false;
	}

	/**
	 * 
	 */
	private void initialize() {

		String selected = SnapshotView.selectedName;
		if (selected == null || "".equals(selected)) { //$NON-NLS-1$
			invisibleRoot = null;
			SHELL_URL = null;
		} else {
			TreeParent plan = null;
			SHELL_URL = SnapshotView.SHELL_URL;
			if(SHELL_URL == null){
			}else{
				ArrayList<String> snapshotPlanList = getSnaphotPlanList(SHELL_URL,
						MSG_SNAPSHOT_INFO_PLAN + " -json"); //$NON-NLS-1$
				JSONArray snapshotFileList = getSnaphotFileList(SHELL_URL,
						MSG_SNAPSHOT_INFO_FILE + " -json"); //$NON-NLS-1$

				invisibleRoot = new TreeParent(""); //$NON-NLS-1$

				for (int i = 0; i < snapshotPlanList.size(); i++) {
					String asb = snapshotPlanList.get(i);
					plan = new TreeParent(asb);

					invisibleRoot.addChild(plan);

					try {
						for (int j = 0; j < snapshotFileList.length(); j++) {

							JSONObject jo = snapshotFileList.getJSONObject(j);

							for(Iterator it = jo.keys(); it.hasNext();){
								String fileName = (String) it.next();
								String fileInfo = jo.getString(fileName);

								Cmd cmd = new Cmd(fileName + " = " +  fileInfo); //$NON-NLS-1$
								String planName = cmd.getOptionArg(PLAN_OPT);
								if (plan.toString().equalsIgnoreCase(planName)) {
									TreeObject snapshotFile = new TreeObject(fileName);
									plan.addChild(snapshotFile);
								}
							}

						}
					} catch (JSONException e) {
						OdenActivator.error(UIMessages.ODEN_SNAPSHOT_SnapshotView_Exception_MakingSnapshotTree, e);
					}
				}
			}
		}
	}

	/**
	 * Gets Snapshot plan list and parsing name and description.
	 * 
	 * @param shellUrl
	 * @param msgSnapshotListPlan
	 * @return list with plan name and plan's description
	 */
	public static ArrayList<String> getSnaphotPlanList(String shellUrl,
			String msgSnapshotListPlan) {

		String result = ""; //$NON-NLS-1$
		ArrayList<String> snapshotPlanFullList = new ArrayList<String>();

		if(shellUrl==null){
			return new ArrayList<String>();
		}else{
			try {
				result = OdenBroker.sendRequest(shellUrl, msgSnapshotListPlan);
				JSONArray ja = new JSONArray(result);
				for(int i=0; i<ja.length(); i++){
					JSONObject jo = ja.getJSONObject(i);
					for(Iterator it = jo.keys(); it.hasNext();){
						String planName = (String) it.next();
						snapshotPlanFullList.add(planName);
						//					String planInfo = jo.getString(planName);
					}
				}
			} catch (OdenException e) {
				OdenActivator.error(UIMessages.ODEN_SNAPSHOT_SnapshotView_Exception_GetSnapshotPlanList, e);
			} catch (Exception e) {
				//				OdenActivator.error("Exception", e);
			}
			return snapshotPlanFullList;
		}
	}

	/**
	 * Gets Snapshot file list and parsing name and other information
	 * 
	 * @param shellUrl
	 * @param msgSnapshotListFile
	 * @return list of file's names
	 */
	protected static JSONArray getSnaphotFileList(String shellUrl,
			String msgSnapshotListFile) {

		String result = ""; //$NON-NLS-1$
		if(shellUrl==null){
			return new JSONArray();
		}else{
			JSONArray ja = null;
			try {
				result = OdenBroker.sendRequest(shellUrl, msgSnapshotListFile);
				ja = new JSONArray(result);
			} catch (OdenException e) {
				OdenActivator.error(UIMessages.ODEN_SNAPSHOT_SnapshotView_Exception_GetSnapshotList, e);
			} catch (Exception e) {
				//				OdenActivator.error("Exception", e);
			}
			return ja;
		}
	}

	/**
	 * Specified snapshot plan or file's information
	 * 
	 * @param shellUrl
	 * @param msg
	 * @return information
	 * @throws OdenException 
	 */
	public static String getInfo(String shellUrl, String msg) throws OdenException {

		String result = ""; //$NON-NLS-1$
		if(shellUrl==null){
			return ""; //$NON-NLS-1$
		}else{
			try {
				result = OdenBroker.sendRequest(shellUrl, msg);
			} catch (OdenException e) {
				throw new OdenException(e);
			} catch (Exception e) {
				OdenActivator.error(UIMessages.ODEN_SNAPSHOT_SnapshotView_Exception_GetInfo, e);
			}
			return result;
		}
	}

	/**
	 * Connect with server side when command's return values are not exist
	 * 
	 * @param shellUrl
	 * @param msg
	 * @throws OdenException 
	 */
	public static void doOdenBroker(String shellUrl, String msg) throws OdenException {

		if(shellUrl==null){
		}else{
			try {
				OdenBroker.sendRequest(shellUrl, msg);
			} catch (OdenException e) {
				throw new OdenException(e);
			} catch (Exception e) {
				OdenActivator.error(UIMessages.ODEN_SNAPSHOT_SnapshotView_Exception_ConnectBroker, e);
			}
		}
	}

	/**
	 * Parsing snapshot plan name
	 * 
	 * @param cmd
	 * @return plan Name
	 */
	public static String getName(String cmd){
		String result = ""; //$NON-NLS-1$
		int n = cmd.indexOf("="); //$NON-NLS-1$
		result = cmd.substring(0, n-1);
		result = result.trim();
		return result;
	}
}