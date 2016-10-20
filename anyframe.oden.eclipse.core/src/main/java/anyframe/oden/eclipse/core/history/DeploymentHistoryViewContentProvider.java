package anyframe.oden.eclipse.core.history;

import java.util.ArrayList;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * The content provider class is responsible for providing objects to the
 * view. It can wrap existing objects in adapters or simply return objects
 * as-is. These objects may be sensitive to the current input of the view,
 * or ignore it and always show the same content (like Task List, for
 * example).
 * 
 * @author RHIE Jihwan
 * @version 1.0.0
 * @since 1.0.0 RC2
 *
 */
public class DeploymentHistoryViewContentProvider implements IStructuredContentProvider {

	public Object[] getElements(Object inputElement) {
		ArrayList obj = (ArrayList) inputElement;
		return obj.toArray();
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// TODO Auto-generated method stub

	}

}
