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
 * This class implements Compare result Table Sorting.
 * 
 * @author LEE Sujeong
 * @version 1.1.0
 * 
 */
public class CompareResultReportSorter extends ViewerSorter {
	private int column;
	private static final int DESCENDING = 1;
	private int direction = DESCENDING;
	
	public CompareResultReportSorter() {
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
		CompareResultInfo p1 = (CompareResultInfo) e1;
		CompareResultInfo p2 = (CompareResultInfo) e2;

		switch (column) {
		case 1:
			// file name
			rc = collator.compare(p1.getFileName(), p2.getFileName());
			break;
		case 2:
			// directory
			rc = collator.compare(p1.getDirectory(), p2.getDirectory());
			break;
		default:
		}
		// If descending order, flip the direction
		if (direction == DESCENDING)
			rc = -rc;
		return rc;
	}
}
