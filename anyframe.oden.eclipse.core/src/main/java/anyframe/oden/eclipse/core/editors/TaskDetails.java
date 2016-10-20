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
package anyframe.oden.eclipse.core.editors;

/**
 * The Model of Task Data
 * 
 * @author HONG JungHwan
 * @version 1.0.0 RC2
 * 
 */
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
