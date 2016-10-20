package org.anyframe.oden.dimmension;

import java.util.ArrayList;
import java.util.List;

import org.anyframe.oden.dimmension.domain.BuildInfo;
import org.anyframe.oden.dimmension.domain.CfgBuildDetail;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.serena.dmclient.api.DimensionsConnection;
import com.serena.dmclient.api.DimensionsConnectionDetails;
import com.serena.dmclient.api.DimensionsConnectionManager;
import com.serena.dmclient.api.DimensionsRelatedObject;
import com.serena.dmclient.api.Filter;
import com.serena.dmclient.api.ItemRevision;
import com.serena.dmclient.api.Project;
import com.serena.dmclient.api.RepositoryFolder;
import com.serena.dmclient.api.Request;
import com.serena.dmclient.api.SystemAttributes;
import com.serena.dmclient.api.UserRoleCapability;

public class CheckOut {

	protected final Log logger = LogFactory.getLog(getClass());
	private boolean isSucess;

	public void downLoad(BuildInfo build) throws Exception {
		DimensionsConnection conn = null;

		// 1. Dimension 접속
		try {
			conn = connectRepository(build);
		} catch (RuntimeException e) {
			throw e;
		} finally {
			if (conn != null) {
				System.out.println("Dimension Server Success");
			} else {
				System.out.println("Dimension Server Failed");
			}
		}

		// 2. File CheckOut
		try {
			List<CfgBuildDetail> requestList = build.toBuildObject();

			for (int i = 0; i < requestList.size(); i++) {
				CfgBuildDetail cfgBuildDetail = requestList.get(i);
				exportFile(build.getProductName(), build.getProjectName(),
						cfgBuildDetail.getRequestId(), build.getTargetPath()
								+ "/" + cfgBuildDetail.getBuildId() + "/", conn);
			}
			isSucess = true;
		} catch (RuntimeException e) {
			throw e;
		} finally {
			if (isSucess) {
				System.out.println("File CheckOut Success");
				conn.close();
			} else {
				conn.close();
			}
		}
	}

	private DimensionsConnection connectRepository(BuildInfo build) {

		DimensionsConnectionDetails details = new DimensionsConnectionDetails();

		details.setDbName(build.getDbName());
		details.setDbConn(build.getDbConnection());
		details.setServer(build.getServer());
		details.setUsername(build.getUserId());
		details.setPassword(build.getPassword());

		return DimensionsConnectionManager.getConnection(details);
	}

	private boolean exportFile(String productName, String projectName,
			String requestId, String targetPath, DimensionsConnection conn) throws Exception {
		System.out.println("Project: " + productName + ":" + projectName);
		
		Project globalProjectObj = conn.getObjectFactory().getProject(
				productName + ":" + projectName);
		RepositoryFolder folder = globalProjectObj.getRootFolder();
		
		Request requestObj = conn.getObjectFactory().findRequest(requestId);
		requestObj.flushRelatedObjects(ItemRevision.class, true);
		
		Filter filter = new Filter();
		filter.criteria().add(
				new Filter.Criterion(SystemAttributes.IS_LATEST_REV, "Y", 0));

		List relObjs = requestObj.getChildItems(filter,globalProjectObj);
		
		requestObj.flushRelatedObjects(ItemRevision.class, true);
		List revObjs = new ArrayList(relObjs.size());
		
		System.out.println("Object Size: " + relObjs.size());
		for (int i = 0; i < relObjs.size(); ++i) {
			DimensionsRelatedObject relObj = (DimensionsRelatedObject) relObjs
					.get(i);
			ItemRevision itemRevision = (ItemRevision) relObj.getObject();
			
			System.out.println("checkOut File : " + itemRevision.getName());
			
			itemRevision.getCopyToFolder(targetPath, true, true, true);
		}
		return true;
	}

}
