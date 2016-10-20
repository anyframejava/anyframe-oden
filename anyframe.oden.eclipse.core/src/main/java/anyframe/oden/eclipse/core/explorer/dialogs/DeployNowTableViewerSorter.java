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
package anyframe.oden.eclipse.core.explorer.dialogs;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;


/**
 * This class implements Deploy PreView Column Sorting.
 * 
 * @author HONG JungHwan
 * @version 1.0.0
 * @since 1.0.0 M3
 * 
 */
class DeployNowTableViewerSorter extends ViewerSorter {
	private int column;

	private static final int DESCENDING = 1;
	
	private int direction = DESCENDING;

	public DeployNowTableViewerSorter() {
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
		DeployNowInfo p1 = (DeployNowInfo) e1;
		DeployNowInfo p2 = (DeployNowInfo) e2;

		switch (column) {
		case 1:
			// Mode
			rc = collator.compare(p1.getMode(), p2.getMode());
			break;
		case 2:
			// Repository
			rc = collator.compare(p1.getDeployRepo(), p2.getDeployRepo());
			break;
		case 3:
			// path column
			rc = collator.compare(p1.getDeployPath(), p2.getDeployPath());
			break;
		case 4:
			// item
			rc = collator.compare(p1.getDeployItem(), p2.getDeployItem());
			break;
		case 5:
			// agent
			rc = collator.compare(p1.getDeployAgent(), p2.getDeployAgent());
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
