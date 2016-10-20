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
package anyframe.oden.eclipse.core.dashboard.actions;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import anyframe.oden.eclipse.core.dashboard.DashboardData;

/**
 * This class implements Dashboard Table Sorting.
 * 
 * @author LEE Sujeong
 * @version 1.1.0
 * 
 */
public class DashboardSorter extends ViewerSorter{

	private int column;
	private static final int DESCENDING = 1;
	private int direction = DESCENDING;
	
	public DashboardSorter() {
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

	public int compare(Viewer viewer, Object e1, Object e2) {
		int rc = 0;
		DashboardData p1 = (DashboardData) e1;
		DashboardData p2 = (DashboardData) e2;

		switch (column) {
		case 1:
			// txID
			rc = collator.compare(p1.getId(), p2.getId());
			break;
		case 2:
			// date
			rc = collator.compare(p1.getDate(), p2.getDate());
			break;
		case 3:
			// number of items
			if(p1.getNumItem() > p2.getNumItem()){
				rc = -1;
			}else if(p1.getNumItem() == p2.getNumItem()){
				rc = 0;
			}else{
				rc = 1;
			}
			break;
		case 4:
			// number of excuted items
			if(p1.getNumExcuteDeploy() > p2.getNumExcuteDeploy()){
				rc = -1;
			}else if(p1.getNumExcuteDeploy() == p2.getNumExcuteDeploy()){
				rc = 0;
			}else{
				rc = 1;
			}
			break;
		case 5:
			// deploy success
			rc = collator.compare(p1.isBoolDeploySuccess()+"", p2.isBoolDeploySuccess()+"");
			break;	
		case 6:
			// transfer success
			rc = collator.compare(p1.getTransferSuccess()+"", p2.getTransferSuccess()+"");
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
