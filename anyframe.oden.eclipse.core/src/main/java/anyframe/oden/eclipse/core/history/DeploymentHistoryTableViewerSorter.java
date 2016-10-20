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
package anyframe.oden.eclipse.core.history;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;


/**
 * This class implements Deployment History Table Column Sorting.
 * 
 * @author HONG JungHwan
 * @version 1.0.0
 * @since 1.0.0 M3
 * 
 */
class DeploymentHistoryTableViewerSorter extends ViewerSorter {
	private int column;

	private static final int DESCENDING = 1;
	
	private int direction = DESCENDING;

	public DeploymentHistoryTableViewerSorter() {
		this.column = 0;
		direction = DESCENDING;
	}

	public void setColumn(int column) {
		if (column == this.column) {
			// Same column as last sort; toggle the direction
			direction = 1 - direction;
		} else {
			// New column; do an ascending sort
			this.column = column;
			direction = DESCENDING;
		}
	}

	@SuppressWarnings("deprecation")
	public int compare(Viewer viewer, Object e1, Object e2) {
		int rc = 0;
		DeploymentHistoryViewDetails p1 = (DeploymentHistoryViewDetails) e1;
		DeploymentHistoryViewDetails p2 = (DeploymentHistoryViewDetails) e2;

		switch (column) {
		case 1:
			// ID
			rc = collator.compare(p1.getDeployId(), p2.getDeployId());
			break;
		case 2:
			// Item
			rc = collator.compare(p1.getDeployItem(), p2.getDeployItem());
			break;
		case 3:
			// Size
			rc = collator.compare(p1.getDeployItemSize(), p2.getDeployItemSize());
			break;
		case 4:
			// Mode
			rc = collator.compare(p1.getDeployItemMode(), p2.getDeployItemMode());
			break;
		case 5:
			// Agent
			rc = collator.compare(p1.getDeployServer(), p2.getDeployServer());
			break;
		case 6:
			// DeployPath
			rc = collator.compare(p1.getDeployPath(), p2.getDeployPath());
			break;
		case 7:
			// Date
			rc = collator.compare(p1.getDeployDate(), p2.getDeployDate());
			break;
		case 8:
			// UserIp
			rc = collator.compare(p1.getDeployerIp(), p2.getDeployerIp());
			break;
		case 9:
			// Status
			rc = collator.compare(p1.getDeployStatus(), p2.getDeployStatus());
			break;
		default:
			rc = 0;
		}
		// If descending order, flip the direction
		if (direction == DESCENDING)
			rc = -rc;
		return rc;
	}
}
