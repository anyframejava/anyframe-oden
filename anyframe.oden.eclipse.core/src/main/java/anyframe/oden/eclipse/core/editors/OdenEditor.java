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
package anyframe.oden.eclipse.core.editors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.MultiPageEditorPart;

import anyframe.oden.eclipse.core.alias.Agent;
import anyframe.oden.eclipse.core.alias.AgentEditorInput;

/**
 * Implement Oden editor. This class extends
 * MultiPageEditorPart class.
 * 
 * @author HONG Junghwan
 * @version 1.0.0 RC1
 * 
 */
public class OdenEditor extends MultiPageEditorPart implements
		IResourceChangeListener {
	public static final String ID = "anyframe.oden.eclipse.core.editors.OdenEditor";

	private static OdenEditor instance;
	public static Agent agent;
	private TextEditor textEditor;

	private static IEditorPart editor;

	public static IEditorPart getEditor() {
		return editor;
	}

	public static void setEditor(IEditorPart editor) {
		OdenEditor.editor = editor;
	}

	private static IProject currentProject;

	public static IProject getCurrentProject() {
		return currentProject;
	}

	public static void setCurrentProject(IProject currentProject) {
		OdenEditor.currentProject = currentProject;
	}

	private static Shell shell;

	public static Shell getShell() {
		return shell;
	}

	public static void setShell(Shell shell) {
		OdenEditor.shell = shell;
	}

	private HashMap<Integer, Page> mapPages = new HashMap<Integer, Page>();
	private int indexTextEditor = -1;

	private boolean isDirty;

	private static String[] pageNameKeys = { "Tasks" , "Policies" };

	public OdenEditor() {
		super();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.addResourceChangeListener(this);
		instance = this;
	}

	public static OdenEditor getInstance() {
		return instance;
	}

	protected void createPages() {
		for (int i = 0; i < pageNameKeys.length; i++) {
			createPage(pageNameKeys[i]);
		}
		updateTitle();
	}
	
	
	void createPage(String pageNameKey) {
		Page page = null;
		if (pageNameKey.equals(pageNameKeys[0])) {
			page = TaskPage.getInstance();
		} else if (pageNameKey.equals(pageNameKeys[1])) {
			page = PolicyPage.getInstance();
		}
		agent = (Agent) ((AgentEditorInput) getEditorInput()).getAgent();
		
		int index = addPage(page.getPage(getContainer()));
		setPageText(index, pageNameKey);
		
		mapPages.put(index, page);
	}
	
	void updateTitle() {
		IEditorInput input = getEditorInput();
		setPartName(input.getName());
		setTitleToolTip(input.getToolTipText());
	}
	
	public boolean isDirty() {
//		firePropertyChange(IWorkbenchPartConstants.PROP_DIRTY);
//		return this.isDirty();
		return super.isDirty();
	}
	
	public final void setDirty(boolean isDirty) {
//		if (this.isDirty == isDirty)
//			return;
//		this.isDirty = isDirty;
		
		firePropertyChange(IEditorPart.PROP_DIRTY);
	}

	
	@SuppressWarnings("unchecked")
	public void init(IEditorSite site, IEditorInput editorInput)
			throws PartInitException {
		shell = site.getShell();
		IResource selectionResource = null;
		ISelection selection = site.getPage().getSelection();
		if (selection != null)
			selectionResource = getSelectedResource(selection);

		Locale.setDefault(new Locale("en"));
		
		super.init(site, editorInput);

	}

	public void resourceChanged(final IResourceChangeEvent event) {
		int type = event.getType();

		if (type == IResourceChangeEvent.PRE_CLOSE) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow()
							.getPages();
				}
			});
		}
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	public void doSave(IProgressMonitor monitor) {
		textEditor.doSave(monitor);
	}

	public void doSaveAs() {
		textEditor.doSaveAs();

		String title = textEditor.getTitle();
		IEditorInput editorInput = textEditor.getEditorInput();

		setPageText(indexTextEditor, title);
		setInput(editorInput);
	}

	public void dispose() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.removeResourceChangeListener(this);

		super.dispose();
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
	public void updateTitle(String name) {
//		input = getEditorInput();
		try {
			setPartName(name);
			setTitleToolTip(name);
		} catch (Exception e) {
			// TODO: handle exception
			
		}
		
	}

}
