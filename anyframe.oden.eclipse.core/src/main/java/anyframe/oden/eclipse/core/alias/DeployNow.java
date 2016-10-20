package anyframe.oden.eclipse.core.alias;

public class DeployNow {

	private String DeployPath;

	private String DeployAgent;

	private String DeployItem;
	
	private String DeployRepo;

	

	private String totalDeploy;

	public DeployNow() {

	}

	public DeployNow(String DeployRepo , String DeployPath ,String DeployItem,
			String DeployAgent) {
		this.DeployRepo = DeployRepo;
		this.DeployItem = DeployItem;
		this.DeployPath = DeployPath;
		this.DeployAgent = DeployAgent;
	}

	public String getDeployPath() {
		return DeployPath;
	}

	public void setDeployPath(String deployPath) {
		DeployPath = deployPath;
	}

	public String getDeployAgent() {
		return DeployAgent;
	}

	public void setDeployAgent(String deployAgent) {
		DeployAgent = deployAgent;
	}

	public String getDeployItem() {
		return DeployItem;
	}

	public void setDeployItem(String deployItem) {
		DeployItem = deployItem;
	}
	
	public String getDeployRepo() {
		return DeployRepo;
	}

	public void setDeployRepo(String deployRepo) {
		DeployRepo = deployRepo;
	}
	public String getTotalDeploy() {
		return totalDeploy;
	}

	public void setTotalDeploy(String totalDeploy) {
		this.totalDeploy = totalDeploy;
	}

}
