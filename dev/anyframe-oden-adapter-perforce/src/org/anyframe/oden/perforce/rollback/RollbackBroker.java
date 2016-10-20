package org.anyframe.oden.perforce.rollback;

import org.apache.tools.ant.Task;

public class RollbackBroker extends Task {
	private String requestId;

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public void execute() {
		try {
			Rollback rollback = new Rollback(getProject(),
					convertJson(requestId));

			// 1. copy backup -> reference(rollback)
			rollback.rollback();
			// 2. delete backup directory
			rollback.delete();

		} catch (Exception e) {
			getProject().fireBuildFinished(e);
			System.exit(-1);
		}

	}

	private String convertJson(String request) {
		String trans = request;

		trans = trans.replace("[{", "[{\"");
		trans = trans.replace(":", "\":\"");
		trans = trans.replace(",", "\",\"");
		trans = trans.replace("}", "\"}");

		trans = trans.replace("\"{", "{\"");
		trans = trans.replace("}\"", "}");

		return trans;
	}
}
