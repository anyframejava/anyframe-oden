package anyframe.oden.eclipse.core.action;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

public class OdenEditorAction implements IObjectActionDelegate {

	private IProject activeProject;
	
	private static IProject currentProject;
	  
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub

	}
	
	public void run(IAction action) {
		IFile fileSpider = activeProject.getFile("anyframe.config");

		IWorkbenchPage Page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		try {

			Page.openEditor(new FileEditorInput(fileSpider),
					"anyframe.oden.eclipse.core.editors.OdenEditor");

		} catch (Exception e) {
			e.getStackTrace();
			System.out.println(e.getMessage());
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		IResource resource = getSelectedResource(selection);
		activeProject = resource.getProject();
	}

	public void init(IViewPart view) {
	}

	public void dispose() {
	}

	public static IResource getSelectedResource(ISelection selection) {
		ArrayList<Object> resources = null;
		if (!selection.isEmpty()) {
			resources = new ArrayList<Object>();
			Iterator<?> elements = ((IStructuredSelection) selection)
					.iterator();
			while (elements.hasNext()) {
				Object next = elements.next();
				if (next instanceof IResource) {
					resources.add(next);
					continue;
				}
				if (next instanceof IAdaptable) {
					IAdaptable adaptable = (IAdaptable) next;
					Object adapter = adaptable.getAdapter(IResource.class);
					if (adapter instanceof IResource) {
						resources.add(adapter);
						continue;
					}
				}
			}
		}

		if (resources != null && !resources.isEmpty()) {
			IResource[] result = new IResource[resources.size()];
			resources.toArray(result);
			if (result.length >= 1)
				return result[0];
		}
		return null;
	}
}
