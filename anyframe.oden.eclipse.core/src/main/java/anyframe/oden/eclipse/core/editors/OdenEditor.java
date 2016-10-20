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
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.MultiPageEditorPart;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.alias.Server;
import anyframe.oden.eclipse.core.alias.ServerEditorInput;

/**
 * Implement Oden editor. This class extends
 * MultiPageEditorPart class.
 * 
 * @author HONG JungHwan
 * @version 1.0.0 RC1
 * 
 */
public class OdenEditor extends MultiPageEditorPart implements IResourceChangeListener {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = OdenActivator.PLUGIN_ID + ".editors.OdenEditor";

	private OdenEditor instance;
	public static Server server;

	private TextEditor textEditor;

	private IEditorPart editor;

	private TaskPage taskPage;
	private PolicyPage policyPage;

	public IEditorPart getEditor() {
		return editor;
	}

	public void setEditor(IEditorPart editor) {
		this.editor = editor;
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
	private static String[] pageNameKeys = { "Tasks" , "Policies" };

	public OdenEditor() {
		super();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.addResourceChangeListener(this);
		instance = this;
	}

	public OdenEditor getInstance() {
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
			taskPage = (TaskPage) page;
		} else if (pageNameKey.equals(pageNameKeys[1])) {
			page = PolicyPage.getInstance();
			policyPage = (PolicyPage) page;
		}
		server = (Server) ((ServerEditorInput) getEditorInput()).getServer();

		int index = addPage(page.getPage(getContainer()));
		setPageText(index, pageNameKey);

		mapPages.put(index, page);
	}

	void updateTitle() {
		IEditorInput input = getEditorInput();
		setPartName(input.getName());
		setTitleToolTip(input.getToolTipText());
	}

	public void init(IEditorSite site, IEditorInput editorInput)
	throws PartInitException {
		shell = site.getShell();
		@SuppressWarnings("unused")
		IResource selectionResource = null;
		ISelection selection = site.getPage().getSelection();
		if (selection != null)
			selectionResource = getSelectedResource(selection);

//		Locale.setDefault(new Locale("en"));

		super.init(site, editorInput);

	}

	public void resourceChanged(final IResourceChangeEvent event) {
		int type = event.getType();

		if (type == IResourceChangeEvent.PRE_CLOSE) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					@SuppressWarnings("unused")
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
		try {
			setPartName(name);
			setTitleToolTip(name);
		} catch (Exception e) {
			// TODO: handle exception

		}
	}
	/**
	 * Returns the shared instance
	 * @return the shared instance
	 */
	@SuppressWarnings("deprecation")
	public static OdenEditor getDefault(String title) {
		IWorkbenchPage Page = PlatformUI.getWorkbench()
		.getActiveWorkbenchWindow().getActivePage();
		IEditorPart[] editors = Page.getEditors();
		for(IEditorPart edit : editors)
			if(edit.getTitle().equals(title))
				return (OdenEditor) edit;
		return (OdenEditor) editors[0];
	}
	public TaskPage getTaskpage() {
		return taskPage;
	}
	public PolicyPage getPolicypage() {
		return policyPage;
	}
}
