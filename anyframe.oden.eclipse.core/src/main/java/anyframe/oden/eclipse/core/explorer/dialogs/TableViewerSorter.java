package anyframe.oden.eclipse.core.explorer.dialogs;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import anyframe.oden.eclipse.core.alias.DeployNow;

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
		DeployNow p1 = (DeployNow) e1;
		DeployNow p2 = (DeployNow) e2;

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
