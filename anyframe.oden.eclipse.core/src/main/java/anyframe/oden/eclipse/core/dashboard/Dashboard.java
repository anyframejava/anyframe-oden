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
package anyframe.oden.eclipse.core.dashboard;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.MultiPageEditorPart;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.alias.Server;

/**
 * Implement Dashboard. This class extends MultiPageEditorPart class.
 * 
 * @author LEE sujeong
 * @version 1.1.0
 * 
 */
public class Dashboard extends MultiPageEditorPart implements
		IResourceChangeListener {

	public static final String ID = OdenActivator.PLUGIN_ID
			+ ".editors.OdenDashboard";

	private IEditorPart editor;
	private TextEditor textEditor;
	private int indexTextEditor = -1;
	private static IProject currentProject;
	private static Shell shell;
	private Dashboard instance;
	private static Server server;

	private DashboardPage dashboardPage;

	public Dashboard() {
		super();
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.addResourceChangeListener(this);
		instance = this;
	}

	public IEditorPart getEditor() {
		return editor;
	}

	public void setEditor(IEditorPart editor) {
		this.editor = editor;
	}

	public static IProject getCurrentProject() {
		return currentProject;
	}

	public static void setCurrentProject(IProject currentProject) {
		Dashboard.currentProject = currentProject;
	}

	public static Shell getShell() {
		return shell;
	}

	public static void setShell(Shell shell) {
		Dashboard.shell = shell;
	}

	public Dashboard getInstance() {
		return instance;
	}

	public static Server getServer() {
		return server;
	}

	@Override
	protected void createPages() {
		dashboardPage = new DashboardPage();
		server = (Server) ((DashboardEditorInput) getEditorInput()).getServer();

		int index = addPage(dashboardPage.getPage(getContainer()));
		setPageText(index, "Dashboard");

		updateTitle();
	}

	void updateTitle() {
		IEditorInput input = getEditorInput();
		setPartName(input.getName());
		setTitleToolTip(input.getToolTipText());
		// Commented out by Jihwan Rhie, 100108, to avoid exception on Mac
		// setTitleImage(input.getImageDescriptor().createImage());
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		textEditor.doSave(monitor);
	}

	@Override
	public void doSaveAs() {
		textEditor.doSaveAs();

		String title = textEditor.getTitle();
		IEditorInput editorInput = textEditor.getEditorInput();

		setPageText(indexTextEditor, title);
		setInput(editorInput);
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	public void resourceChanged(IResourceChangeEvent event) {
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

	public void dispose() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.removeResourceChangeListener(this);

		super.dispose();
	}

	public static Dashboard getDefault(String title) {
		IWorkbenchPage Page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		IEditorPart[] editors = Page.getEditors();
		for (IEditorPart edit : editors)
			if (edit.getTitle().equals(title))
				return (Dashboard) edit;
		return (Dashboard) editors[0];
	}

	public DashboardPage getDashboardPage() {
		return dashboardPage;
	}

}
