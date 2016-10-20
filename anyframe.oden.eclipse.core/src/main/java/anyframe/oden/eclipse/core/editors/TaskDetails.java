package anyframe.oden.eclipse.core.editors;

import java.util.List;

public class TaskDetails {

	private String TaskName;

	private String Description;

	private String PolicyName;

	private String PolicyDesc;

	public TaskDetails() {

	}

	public TaskDetails(String TaskName, String Description,
			String PolicyName, String PolicyDesc) {
		this.TaskName = TaskName;
		this.Description = Description;
		this.PolicyName = PolicyName;
		this.PolicyDesc = PolicyDesc;
	}

	public String getTaskName() {
		return TaskName;
	}

	public void setTaskName(String taskName) {
		TaskName = taskName;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getPolicyName() {
		return PolicyName;
	}

	public void setPolicyName(String policyName) {
		PolicyName = policyName;
	}

	public String getPolicyDesc() {
		return PolicyDesc;
	}

	public void setPolicyDesc(String policyDesc) {
		PolicyDesc = policyDesc;
	}

	
}
