/*
 * Copyright 2009 SAMSUNG SDS Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package anyframe.oden.eclipse.core.explorer.dialogs;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;


/**
 * This class implements Deploy PreView Column Sotring.
 * 
 * @author HONG JungHwan
 * @version 1.0.0
 * @since 1.0.0 M3
 * 
 */
class TableViewerSorter extends ViewerSorter {
	private static final int ASCENDING = 0;

	private static final int DESCENDING = 1;

	private int column;

	private int direction;

	public void doSort(int column) {
		if (column == this.column) {
			direction = 1 - direction;
		} else {
			this.column = column;
			direction = ASCENDING;
		}
	}

	public int compare(Viewer viewer, Object e1, Object e2) {
		int rc = 0;
		DeployNowInfo p1 = (DeployNowInfo) e1;
		DeployNowInfo p2 = (DeployNowInfo) e2;

		switch (column) {
		case 1:
			// path column
			rc = collator.compare(p1.getDeployPath(), p2.getDeployPath());
			break;
		case 2:
			rc = collator.compare(p1.getDeployItem(), p2.getDeployItem());
			break;
		}
		if (direction == DESCENDING)
			rc = -rc;
		return rc;
	}
}
