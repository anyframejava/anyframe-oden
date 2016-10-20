package anyframe.oden.eclipse.core.editors;

import java.util.ArrayList;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class PolicyContentProvider implements IStructuredContentProvider {

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
