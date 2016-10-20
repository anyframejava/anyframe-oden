package anyframe.oden.eclipse.core.editors;

import java.util.List;

public class PolicyDetails {

	private String PolicyName;

	private String Description;

	private String BuildRepo;

	private String IncludeItem;

	private String ExcludeItem;

	private String DeployUrl;

	private String DeployRoot;
	
	private String LocationVar;

	public PolicyDetails() {

	}

	public PolicyDetails(String PolicyName, String Description,
			String DeployUrl, String DeployRoot, String LocationVar) {
		this.PolicyName = PolicyName;
		this.Description = Description;
		this.DeployUrl = DeployUrl;
		this.DeployRoot = DeployRoot;
		this.LocationVar = LocationVar;
	}

	public String getPolicyName() {
		return PolicyName;
	}

	public void setPolicyName(String policyName) {
		this.PolicyName = policyName;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		this.Description = description;
	}

	public String getBuildRepo() {
		return BuildRepo;
	}

	public void setBuildRepo(String buildRepo) {
		this.BuildRepo = buildRepo;
	}

	public String getIncludeItem() {
		return IncludeItem;
	}

	public void setIncludeItem(String includeItem) {
		this.IncludeItem = includeItem;
	}

	public String getExcludeItem() {
		return ExcludeItem;
	}

	public void setExcludeItem(String excludeItem) {
		this.ExcludeItem = excludeItem;
	}

	public String getDeployUrl() {
		return DeployUrl;
	}

	public void setDeployUrl(String deployUrl) {
		this.DeployUrl = deployUrl;
	}

	public String getDeployRoot() {
		return DeployRoot;
	}

	public void setDeployRoot(String deployRoot) {
		this.DeployRoot = deployRoot;
	}

	public String getLocationVar() {
		return LocationVar;
	}

	public void setLocationVar(String locationVar) {
		LocationVar = locationVar;
	}

}
