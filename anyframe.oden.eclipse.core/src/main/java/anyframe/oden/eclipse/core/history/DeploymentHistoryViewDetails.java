package anyframe.oden.eclipse.core.history;

public class DeploymentHistoryViewDetails {

	private String DeployId;

	private String DeployItem;

	private String DeployServer;

	private String DeployPath;

	private String DeployDate;

	private String DeployerIp;

	private String DeployStatus;

	private String totalQuery;

	public DeploymentHistoryViewDetails() {

	}

	public DeploymentHistoryViewDetails(String DeployId, String DeployItem, String DeployPath,
			String DeployDate, String DeployerIp, String DeployStatus,
			String totalQuery, String deployServer) {

		this.DeployId = DeployId;
		this.DeployItem = DeployItem;
		this.DeployPath = DeployPath;
		this.DeployDate = DeployDate;

		this.DeployerIp = DeployerIp;
		this.DeployStatus = DeployStatus;
		this.totalQuery = totalQuery;
		this.totalQuery = totalQuery;
		this.DeployServer = deployServer;
	}

	public String getDeployId() {
		return DeployId;
	}

	public void setDeployId(String deployId) {
		this.DeployId = deployId;
	}

	public String getDeployItem() {
		return DeployItem;
	}

	public void setDeployItem(String deployItem) {
		this.DeployItem = deployItem;
	}

	public String getDeployPath() {
		return DeployPath;
	}

	public void setDeployPath(String deployPath) {
		this.DeployPath = deployPath;
	}

	public String getDeployDate() {
		return DeployDate;
	}

	public void setDeployDate(String deployDate) {
		this.DeployDate = deployDate;
	}

	public String getDeployerIp() {
		return DeployerIp;
	}

	public void setDeployerIp(String deployerIp) {
		this.DeployerIp = deployerIp;
	}

	public String getDeployStatus() {
		return DeployStatus;
	}

	public void setDeployStatus(String deployStatus) {
		this.DeployStatus = deployStatus;
	}

	public String getTotalQuery() {
		return totalQuery;
	}

	public void setTotalQuery(String totalQuery) {
		this.totalQuery = totalQuery;
	}

	public String getDeployServer() {
		return DeployServer;
	}

	public void setDeployServer(String deployServer) {
		DeployServer = deployServer;
	}
}
