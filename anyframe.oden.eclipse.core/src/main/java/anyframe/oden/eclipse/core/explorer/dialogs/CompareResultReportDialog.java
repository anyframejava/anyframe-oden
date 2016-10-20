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

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.json.JSONArray;
import org.json.JSONObject;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.messages.UIMessages;

/**
 * Create a compare result dialog.
 * 
 * @author LEE Sujeong
 * @version 1.1.0
 * 
 */
public class CompareResultReportDialog extends Dialog {

	private Button checkFail;
	private Label lblFollowingTrue;
	private boolean matchFail;

	private Object[] obj;
	ArrayList<JSONObject> list = new ArrayList<JSONObject>();
	private Table resultTable;
	private TableViewer resultTableViewer;
	private TableViewerColumn columnFileName;
	private TableViewerColumn columnDirectory;

	private int success = 0;
	private int fail = 0;

	public CompareResultReportDialog(Shell shell, Object[] obj,
			ArrayList<JSONObject> list) {
		super(shell);
		this.obj = obj;
		this.list = list;
	}

	protected void configureShell(Shell newShell) {
		newShell
				.setText(UIMessages.ODEN_EXPLORER_Dialogs_CompareResultReportDialogDialogTitle);
		super.configureShell(newShell);
		int n = 300;
		int width = n + (obj.length * 200);
		if (width > 1000) {
			width = 1000;
		} else {
		}
		newShell.setBounds(300, 200, width, 600);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		int serverN = obj.length;

		GridLayout gridLayout = new GridLayout();
		parent.setLayout(gridLayout);

		final Group grpMain = new Group(parent, SWT.V_SCROLL);
		GridData grpData = new GridData(GridData.FILL_BOTH);
		gridLayout = new GridLayout();
		grpMain.setLayout(gridLayout);
		grpMain.setLayoutData(grpData);

		GridData gd = new GridData(GridData.FILL_BOTH);
		parent.setLayoutData(gd);

		lblFollowingTrue = new Label(grpMain, SWT.BOLD);
		GridData layoutData = new GridData(GridData.BEGINNING);
		lblFollowingTrue.setLayoutData(layoutData);

		checkFail = new Button(grpMain, SWT.CHECK);
		checkFail
				.setText(UIMessages.ODEN_EXPLORER_Dialogs_CompareResultReportDialogButtonOnlyFailFile);
		checkFail.setSelection(true);

		GridData gridData = new GridData(GridData.FILL_BOTH);

		grpMain.setLayoutData(gridData);

		resultTable = new Table(grpMain, SWT.FULL_SELECTION | SWT.SINGLE
				| SWT.BORDER | SWT.V_SCROLL);
		setResultTableViewer(new TableViewer(resultTable));
		resultTableViewer.setSorter(new CompareResultReportSorter());
		getResultTableViewer().setContentProvider(
				new CompareResultContentProvider());

		resultTable.setHeaderVisible(true);
		resultTable.setLinesVisible(true);

		columnFileName = new TableViewerColumn(getResultTableViewer(), SWT.None);
		columnFileName
				.getColumn()
				.setText(
						UIMessages.ODEN_EXPLORER_Dialogs_CompareResultReportDialogColumnFileName);
		columnFileName.getColumn().setWidth(100);
		columnFileName.setLabelProvider(new FileNameLabelProvider());

		columnDirectory = new TableViewerColumn(getResultTableViewer(),
				SWT.None);
		columnDirectory
				.getColumn()
				.setText(
						UIMessages.ODEN_EXPLORER_Dialogs_CompareResultReportDialogColumnDirectory);
		columnDirectory.getColumn().setWidth(100);
		columnDirectory.setLabelProvider(new DirectoryLabelProvider());

		for (int i = 0; i < serverN; i++) {

			StringTokenizer token = new StringTokenizer(obj[i] + "", " "); //$NON-NLS-1$ //$NON-NLS-2$
			String temp = token.nextToken();
			String name = temp.substring(1, temp.length());

			TableViewerColumn server = new TableViewerColumn(
					getResultTableViewer(), SWT.None);
			server.getColumn().setText(name);
			server.getColumn().setWidth(180);
			server.setLabelProvider(new ServerLabelProvider(name));

		}
		resultTable.setLayoutData(gridData);

		matchFail = checkFail.getSelection();
		loadInitData();
		setListener();

		lblFollowingTrue
				.setText(UIMessages.ODEN_EXPLORER_Dialogs_CompareResultReportDialogItemResult
						+ " [ Total(" //$NON-NLS-2$
						+ (fail + success)
						+ ") Success(" + success + ") Fail(" + fail //$NON-NLS-1$ //$NON-NLS-2$
						+ ") ]" + "                        ");//빈칸으로 자리 확보 //$NON-NLS-1$ //$NON-NLS-2$
		return parent;
	}

	private void loadInitData() {

		success = 0;
		fail = 0;
		ArrayList<CompareResultInfo> infoList = new ArrayList<CompareResultInfo>();

		for (int i = 0; i < list.size(); i++) {
			CompareResultInfo info = new CompareResultInfo();

			try {
				String match = list.get(i).getString("match"); //$NON-NLS-1$
				String temp = list.get(i).getString("path"); //$NON-NLS-1$
				String path = ""; //$NON-NLS-1$
				String name = ""; //$NON-NLS-1$
				int n = temp.lastIndexOf("/"); //$NON-NLS-1$
				if (n == -1) {
					name = temp;
				} else {
					path = temp.substring(0, n);
					name = temp.substring(n + 1);
				}

				JSONArray array = (JSONArray) list.get(i).get("agents"); //$NON-NLS-1$

				String[] agentArray = new String[array.length()];
				String[] dateArray = new String[array.length()];
				String[] sizeArray = new String[array.length()];

				for (int a = 0; a < array.length(); a++) {
					JSONObject obj = (JSONObject) array.get(a);

					String agent = obj.getString("agent"); //$NON-NLS-1$

					String fileSize = obj.getString("size"); //$NON-NLS-1$
					Long longSize = Long.parseLong(fileSize);
					String resultWithUnit = ""; //$NON-NLS-1$
					if (longSize < 1024) {
						BigDecimal bd = new BigDecimal(longSize);
						BigDecimal fileSizeKB = bd.setScale(0,
								BigDecimal.ROUND_UP);

						DecimalFormat df = (DecimalFormat) NumberFormat
								.getInstance();
						df = new DecimalFormat("###,###,###"); //$NON-NLS-1$
						Long size = Long.parseLong(fileSizeKB.toString());
						String fileSizeComma = df.format(size);
						resultWithUnit = fileSizeComma
								+ UIMessages.ODEN_SNAPSHOT_SnapshotView_FileSizeTailBytes;
					} else {
						double doubleSize = longSize / 1024;

						BigDecimal bd = new BigDecimal(doubleSize);
						BigDecimal fileSizeKB = bd.setScale(0,
								BigDecimal.ROUND_UP);

						DecimalFormat df = (DecimalFormat) NumberFormat
								.getInstance();
						df = new DecimalFormat("###,###,###"); //$NON-NLS-1$
						Long size = Long.parseLong(fileSizeKB.toString());
						String fileSizeComma = df.format(size);
						resultWithUnit = fileSizeComma
								+ UIMessages.ODEN_SNAPSHOT_SnapshotView_FileSizeTailKB;
					}

					String date = obj.getString("date"); //$NON-NLS-1$
					date = chgDateFormat(date);

					agentArray[a] = agent;
					sizeArray[a] = resultWithUnit;
					dateArray[a] = date;
				}

				info.setFileName(name);
				info.setDirectory(path);
				info.setMatch(match);
				info.setAgents(agentArray);
				info.setSize(sizeArray);
				info.setDate(dateArray);

				if (!matchFail) {// 모두
					infoList.add(info);

					if (match.equals("true")) { //$NON-NLS-1$
						success++;
					} else {
						fail++;
					}
				} else {// 실패만
					if (match
							.equals(UIMessages.ODEN_EXPLORER_Dialogs_CompareResultReportDialog23)) {
						success = 0;
					} else {
						infoList.add(info);
						fail++;
					}
				}

			} catch (Exception e) {
				OdenActivator
						.error(
								UIMessages.ODEN_EXPLORER_Actions_CompareAgentAction_Exception_CompareAgent,
								e);
			}
		}
		getResultTableViewer().setInput(infoList);
	}

	private void setListener() {
		columnFileName.getColumn().addSelectionListener(
				new SelectionListener() {
					public void widgetSelected(SelectionEvent e) {
						((CompareResultReportSorter) resultTableViewer
								.getSorter()).setColumn(1);
						int dir = resultTableViewer.getTable()
								.getSortDirection();
						if (resultTableViewer.getTable().getSortColumn() == columnFileName
								.getColumn()) {
							dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
						} else {
							dir = SWT.DOWN;
						}
						resultTableViewer.getTable().setSortDirection(dir);
						resultTableViewer.getTable().setSortColumn(
								columnFileName.getColumn());
						resultTableViewer.refresh();
					}

					public void widgetDefaultSelected(SelectionEvent e) {
						widgetSelected(e);
					}
				});

		columnDirectory.getColumn().addSelectionListener(
				new SelectionListener() {
					public void widgetSelected(SelectionEvent e) {
						((CompareResultReportSorter) resultTableViewer
								.getSorter()).setColumn(2);
						int dir = resultTableViewer.getTable()
								.getSortDirection();
						if (resultTableViewer.getTable().getSortColumn() == columnDirectory
								.getColumn()) {
							dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
						} else {
							dir = SWT.DOWN;
						}
						resultTableViewer.getTable().setSortDirection(dir);
						resultTableViewer.getTable().setSortColumn(
								columnDirectory.getColumn());
						resultTableViewer.refresh();
					}

					public void widgetDefaultSelected(SelectionEvent e) {
						widgetSelected(e);
					}
				});

		checkFail.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				matchFail = checkFail.getSelection();
				loadInitData();
				lblFollowingTrue
						.setText(UIMessages.ODEN_EXPLORER_Dialogs_CompareResultReportDialogItemResult
								+ " [ Total(" + (fail + success) + ") Success(" //$NON-NLS-1$ //$NON-NLS-2$
								+ success + ") Fail(" + fail + ") ]"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	public void setResultTableViewer(TableViewer resultTableViewer) {
		this.resultTableViewer = resultTableViewer;
	}

	public TableViewer getResultTableViewer() {
		return resultTableViewer;
	}

	protected void setShellStyle(int newShellStyle) {
		super.setShellStyle(newShellStyle | SWT.RESIZE);
	}

	private String chgDateFormat(String input) {
		return new SimpleDateFormat(
				UIMessages.ODEN_EXPLORER_Dialogs_CompareResultReportDialog30)
				.format(Long.valueOf(input));
	}

	private class CompareResultContentProvider implements
			IStructuredContentProvider {
		public Object[] getElements(Object inputElement) {
			ArrayList obj = (ArrayList) inputElement;
			return obj.toArray();
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	private class FileNameLabelProvider extends ColumnLabelProvider {
		public String getText(Object element) {
			return ((CompareResultInfo) element).getFileName();
		}

		public Color getForeground(Object element) {
			String match = ((CompareResultInfo) element).getMatch();
			if (match
					.equals(UIMessages.ODEN_EXPLORER_Dialogs_CompareResultReportDialog31)) {// 동일
				return Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
			} else {// 다름
				return Display.getDefault().getSystemColor(SWT.COLOR_RED);
			}
		}
	}

	private class DirectoryLabelProvider extends ColumnLabelProvider {
		public String getText(Object element) {
			return ((CompareResultInfo) element).getDirectory();
		}
	}

	private class ServerLabelProvider extends ColumnLabelProvider {
		String serverName = ""; //$NON-NLS-1$

		public ServerLabelProvider(String name) {
			this.serverName = name;
		}

		public String getText(Object element) {

			String size = ""; //$NON-NLS-1$
			String date = ""; //$NON-NLS-1$
			String result = ""; //$NON-NLS-1$

			for (int i = 0; i < ((CompareResultInfo) element).getAgents().length; i++) {
				String server = ((CompareResultInfo) element).getAgents()[i];

				if (server.equals(serverName)) {
					size = ((CompareResultInfo) element).getSize()[i];
					date = ((CompareResultInfo) element).getDate()[i];
					result = date + "  |  " + size; //$NON-NLS-1$
					break;
				} else {
					result = ""; //$NON-NLS-1$
				}
			}
			return result;
		}
	}

}
