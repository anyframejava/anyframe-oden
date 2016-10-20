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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.nebula.widgets.calendarcombo.CalendarCombo;
import org.eclipse.nebula.widgets.calendarcombo.ICalendarListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.json.JSONArray;
import org.json.JSONObject;

import anyframe.oden.eclipse.core.CommandNotFoundException;
import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.alias.Server;
import anyframe.oden.eclipse.core.brokers.OdenBrokerImpl;
import anyframe.oden.eclipse.core.brokers.OdenBrokerService;
import anyframe.oden.eclipse.core.dashboard.actions.DashboardRefreshAction;
import anyframe.oden.eclipse.core.dashboard.actions.DashboardSorter;
import anyframe.oden.eclipse.core.editors.Page;
import anyframe.oden.eclipse.core.history.DeploymentHistoryView;
import anyframe.oden.eclipse.core.messages.CommandMessages;
import anyframe.oden.eclipse.core.messages.CommonMessages;
import anyframe.oden.eclipse.core.messages.UIMessages;
import anyframe.oden.eclipse.core.utils.CommonUtil;
import anyframe.oden.eclipse.core.utils.DialogUtil;
import anyframe.oden.eclipse.core.utils.ImageUtil;

/**
 * Page of dashboard. This class impl Page class.
 * 
 * @author LEE Sujeong
 * @version 1.1.0
 * 
 */
public class DashboardPage implements Page {

	private String shellURL;
	private String serverNickName;
	protected static OdenBrokerService OdenBroker = new OdenBrokerImpl();

	private Server server;

	private Button checkSuccess;
	private boolean deployFail;
	private Button checkTrasferResult;
	private boolean transferFail;

	public Text dateFrom;
	public Text dateTo;

	private CalendarCombo fromCombo;
	private CalendarCombo toCombo;

	private Label dateDash;
	private Button dateSearch;

	private Table infoTable;
	private TableViewerColumn columnTXID;
	private TableViewerColumn columnDate;
	private TableViewerColumn columnDeployItemNumber;
	private TableViewerColumn columnDeployNumber;
	private TableViewerColumn columnDeploySuccess;
	private TableViewerColumn columnResultTransferSuccess;

	private Table resultTable;
	private TableViewerColumn columnResult;
	private TableViewerColumn columnTemp1;
	private TableViewerColumn columnResultDeployItemNumber;
	private TableViewerColumn columnResultDeployNumber;

	private FormToolkit toolkit;
	private TableViewer dashboardViewer;
	private TableViewer resultViewer;

	private String selectedID;
	private String transferResult;

	private boolean boolSSL;

	private TableItem[] selection;

	private boolean isFail;

	private SelectionListener trensferListener;

	private CommonUtil util = new CommonUtil();

	public void setSelection(TableItem[] selection) {
		this.selection = selection;
	}

	public TableItem[] getSelection() {
		return selection;
	}

	public String getShellURL() {
		return shellURL;
	}

	public void setShellURL(String shellURL) {
		this.shellURL = shellURL;
	}

	public TableViewer getDashboardViewer() {
		return dashboardViewer;
	}

	public void setDashboardViewer(TableViewer dashboardViewer) {
		this.dashboardViewer = dashboardViewer;
	}

	public TableViewer getResultViewer() {
		return resultViewer;
	}

	public void setResultViewer(TableViewer resultViewer) {
		this.resultViewer = resultViewer;
	}

	public boolean isDeployFail() {
		return deployFail;
	}

	public void setDeployFail(boolean deployFail) {
		this.deployFail = deployFail;
	}

	public boolean isTransferFail() {
		return transferFail;
	}

	public void setTransferFail(boolean transferFail) {
		this.transferFail = transferFail;
	}

	public void setSelectedID(String string) {
		this.selectedID = string;
	}

	public String getSelectedID() {
		return selectedID;
	}

	public String getTransferResult() {
		return transferResult;
	}

	public void setTransferResult(String transferResult) {
		this.transferResult = transferResult;
	}

	private void addContextMenu() {
		final DashboardActionGroup actionGroup = new DashboardActionGroup();
		MenuManager menuManager = new MenuManager(
				UIMessages.ODEN_DASHBOARD_DashboardPage_MenuManager);
		menuManager.setRemoveAllWhenShown(true);
		Menu contextMenu = menuManager.createContextMenu(infoTable);
		infoTable.setMenu(contextMenu);

		menuManager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(final IMenuManager manager) {
				actionGroup.fillContextMenu(manager);
			}
		});
	}

	public Composite getPage(Composite parent) {

		toolkit = new FormToolkit(parent.getDisplay());
		server = Dashboard.getServer();
		serverNickName = server.getNickname();
		setShellURL(CommonMessages.ODEN_CommonMessages_ProtocolString_HTTP
				+ server.getUrl()
				+ CommonMessages.ODEN_CommonMessages_ProtocolString_HTTPsuf);

		final ScrolledForm form = toolkit.createScrolledForm(parent);
		createHeadSection(form, toolkit);
		GridLayout layout = new GridLayout();
		form.getBody().setLayout(layout);

		Composite whole = toolkit.createComposite(form.getBody());
		whole.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		whole.setLayoutData(gd);

		createDashBoardSection(form, toolkit, whole);

		checkSSLValid();
		loadInitData();
		fillResultData();
		setListener();

		addContextMenu();

		return form;
	}

	private void checkSSLValid() {
		String cmd = CommandMessages.ODEN_CLI_COMMAND_spectrum_fetchlist + " "//$NON-NLS-1$
				+ CommandMessages.ODEN_CLI_OPTION_json;
		try {
			String spectrum = OdenBroker.sendRequest(getShellURL(), cmd);
			boolSSL = true;
		} catch (OdenException e) {
			OdenActivator.error(
					"Exception occured while check command is valid.", e);
		} catch (CommandNotFoundException e) {
			// TODO
			OdenActivator.error("Anyframe Oden command not found.", e);
			checkTrasferResult.setEnabled(false);
			columnResultTransferSuccess.getColumn().removeSelectionListener(
					trensferListener);
			boolSSL = false;
		}
	}

	private void createDashBoardSection(final ScrolledForm form,
			FormToolkit toolkit, Composite parent) {

		Composite dash = toolkit.createComposite(parent);
		dash.setLayout(new GridLayout());

		GridData gd = new GridData(GridData.FILL_BOTH
				| GridData.VERTICAL_ALIGN_BEGINNING);

		dash.setLayoutData(gd);
		createDashboardSection(form, toolkit,
				UIMessages.ODEN_DASHBOARD_DashboardPage_DashboardTitle, dash);

	}

	private void createHeadSection(final ScrolledForm form, FormToolkit toolkit) {
		Image titleImage = new Image(form.getDisplay(), getClass()
				.getResourceAsStream(
						UIMessages.ODEN_DASHBOARD_DashboardPage_DashboardIcon));
		form
				.setText(UIMessages.ODEN_DASHBOARD_DashboardPage_DashboardPageTitle);
		form.setBackgroundImage(ImageUtil.getImage(ImageUtil
				.getImageDescriptor("icons/form_banner.gif"))); //$NON-NLS-1$
		form.setImage(titleImage);
		form.setMessage(UIMessages.ODEN_DASHBOARD_DashboardPage_DashboardDesc,
				SWT.NONE);

		fillLocalToolBar(form.getForm().getToolBarManager());
		form.getForm().getToolBarManager().update(true);
		toolkit.decorateFormHeading(form.getForm());

	}

	private void fillLocalToolBar(IToolBarManager toolBarManager) {
		toolBarManager.add(new DashboardRefreshAction(Dashboard.getServer()
				.getNickname()));
	}

	private void createDashboardSection(final ScrolledForm form,
			FormToolkit toolkit, String title, Composite parent) {

		Section section = toolkit.createSection(parent, Section.DESCRIPTION
				| Section.TITLE_BAR);

		section.setActiveToggleColor(toolkit.getHyperlinkGroup()
				.getActiveForeground());
		section.setToggleColor(toolkit.getColors().getColor(
				FormColors.SEPARATOR));

		Composite client = toolkit.createComposite(section, SWT.WRAP);

		GridLayout layout = new GridLayout();
		// layout.numColumns = 10;
		layout.numColumns = 12;
		client.setLayout(layout);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		client.setLayoutData(gd);

		gd.widthHint = 200;

		fillDashboard(client);

		section.setText(title);
		section
				.setDescription(UIMessages.ODEN_DASHBOARD_DashboardPage_DashboardPageDesc);
		section.setClient(client);
		section.setExpanded(true);

		gd = new GridData(GridData.FILL_BOTH);
		section.setLayoutData(gd);
	}

	private void fillDashboard(Composite client) {

		checkSuccess = new Button(client, SWT.CHECK);
		checkSuccess
				.setText(UIMessages.ODEN_DASHBOARD_DashboardPage_CheckDeployFail);
		GridData gdButtonSuccess = new GridData(GridData.BEGINNING);
		gdButtonSuccess.heightHint = 30;
		checkSuccess.setLayoutData(gdButtonSuccess);
		checkSuccess.setSelection(false);
		deployFail = checkSuccess.getSelection(); // 체크하면 fail만

		checkTrasferResult = new Button(client, SWT.CHECK);
		checkTrasferResult
				.setText(UIMessages.ODEN_DASHBOARD_DashboardPage_CheckFetchlogFail);
		GridData gdButtonTransfer = new GridData(GridData.BEGINNING);
		gdButtonTransfer.heightHint = 30;
		checkTrasferResult.setLayoutData(gdButtonTransfer);
		checkTrasferResult.setSelection(false);
		transferFail = checkTrasferResult.getSelection(); // 체크하면 fail만

		Label space = new Label(client, SWT.NONE);
		space.setText(""); //$NON-NLS-1$

		Label date = new Label(client, SWT.NONE);
		date.setText(UIMessages.ODEN_DASHBOARD_DashboardPage_LabelDateFromTo);

		// add calendar by HONG 10.01.20
		fromCombo = new CalendarCombo(client, SWT.READ_ONLY);
		fromCombo.setDate(util.getWeekDate());

		fromCombo.addCalendarListener(new ICalendarListener() {

			public void popupClosed() {
				// TODO Auto-generated method stub
			}

			public void dateRangeChanged(Calendar start, Calendar end) {
				// TODO Auto-generated method stub
			}

			public void dateChanged(Calendar date) {
				// TODO Auto-generated method stub
			}
		});
		// end from date calendar

		dateDash = new Label(client, SWT.NONE);
		dateDash.setText("-"); //$NON-NLS-1$

		// add calendar by HONG 10.01.20
		toCombo = new CalendarCombo(client, SWT.READ_ONLY);
		toCombo.setDate(new Date());
		toCombo.addCalendarListener(new ICalendarListener() {

			public void popupClosed() {
				// TODO Auto-generated method stub
			}

			public void dateRangeChanged(Calendar start, Calendar end) {
				// TODO Auto-generated method stub
			}

			public void dateChanged(Calendar date) {
				// TODO Auto-generated method stub
			}
		});
		// end from date calendar

		dateSearch = new Button(client, SWT.PUSH);
		dateSearch
				.setText(UIMessages.ODEN_DASHBOARD_DashboardPage_ButtonDateSearch);

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.verticalSpan = 5;
		gridData.horizontalSpan = 10;

		gridData.widthHint = 200;
		gridData.heightHint = 225;
		// Task Table
		infoTable = new Table(client, SWT.FULL_SELECTION | SWT.MULTI
				| SWT.BORDER | SWT.V_SCROLL);
		setDashboardViewer(new TableViewer(infoTable));
		dashboardViewer.setSorter(new DashboardSorter());
		getDashboardViewer().setContentProvider(new DashboardContentProvider());

		infoTable.setHeaderVisible(true);
		infoTable.setLinesVisible(true);

		columnTXID = new TableViewerColumn(getDashboardViewer(), SWT.None);
		columnTXID.getColumn().setText(
				UIMessages.ODEN_DASHBOARD_DashboardPage_ColumnTxID);
		columnTXID.getColumn().setWidth(100);
		columnTXID.setLabelProvider(new TxIDColumnLabelProvider());

		columnDate = new TableViewerColumn(getDashboardViewer(), SWT.None);
		columnDate.getColumn().setText(
				UIMessages.ODEN_DASHBOARD_DashboardPage_ColumnDate);
		columnDate.getColumn().setWidth(120);
		columnDate.setLabelProvider(new DateColumnLabelProvider());

		columnDeployItemNumber = new TableViewerColumn(getDashboardViewer(),
				SWT.None);
		columnDeployItemNumber.getColumn().setText(
				UIMessages.ODEN_DASHBOARD_DashboardPage_ColumnItem);
		columnDeployItemNumber.getColumn().setWidth(110);
		columnDeployItemNumber
				.setLabelProvider(new NumItemColumnLabelProvider());

		columnDeployNumber = new TableViewerColumn(getDashboardViewer(),
				SWT.None);
		columnDeployNumber.getColumn().setText(
				UIMessages.ODEN_DASHBOARD_DashboardPage_ColumnDeploy);
		columnDeployNumber.getColumn().setWidth(110);
		columnDeployNumber.setLabelProvider(new NumDeployColumnLabelProvider());

		columnDeploySuccess = new TableViewerColumn(getDashboardViewer(),
				SWT.None);
		columnDeploySuccess.getColumn().setText(
				UIMessages.ODEN_DASHBOARD_DashboardPage_ColumnDeployFail);
		columnDeploySuccess.getColumn().setWidth(130);
		columnDeploySuccess
				.setLabelProvider(new SuccessDeployColumnLabelProvider());

		columnResultTransferSuccess = new TableViewerColumn(
				getDashboardViewer(), SWT.None);
		columnResultTransferSuccess.getColumn().setText(
				UIMessages.ODEN_DASHBOARD_DashboardPage_ColumnFetchlogFail);
		columnResultTransferSuccess.getColumn().setWidth(190);
		columnResultTransferSuccess
				.setLabelProvider(new SuccessTransferColumnLabelProvider());

		infoTable.setLayoutData(gridData);

		GridData gridDataResult = new GridData(GridData.FILL_HORIZONTAL);
		gridDataResult.horizontalSpan = 10;
		gridDataResult.widthHint = 200;
		gridDataResult.heightHint = -4;

		resultTable = new Table(client, SWT.SINGLE | SWT.BORDER
				| SWT.FULL_SELECTION);
		setResultViewer(new TableViewer(resultTable));
		getResultViewer().setContentProvider(
				new DashboardResultContentProvider());

		resultTable.setHeaderVisible(false);
		resultTable.setLinesVisible(true);

		columnResult = new TableViewerColumn(getResultViewer(), SWT.None);
		columnResult.getColumn().setWidth(columnTXID.getColumn().getWidth());
		columnResult.setLabelProvider(new ColumnResultLabelProvider());

		columnTemp1 = new TableViewerColumn(getResultViewer(), SWT.None);
		columnTemp1.getColumn().setWidth(columnDate.getColumn().getWidth());
		columnTemp1.setLabelProvider(new ColumnResultTempLabelProvider());

		columnResultDeployItemNumber = new TableViewerColumn(getResultViewer(),
				SWT.None);
		columnResultDeployItemNumber.getColumn().setWidth(
				columnDeployItemNumber.getColumn().getWidth());
		columnResultDeployItemNumber
				.setLabelProvider(new ColumnResultDeployItemNumberLabelProvider());

		columnResultDeployNumber = new TableViewerColumn(getResultViewer(),
				SWT.None);
		columnResultDeployNumber.getColumn().setWidth(
				columnDeployNumber.getColumn().getWidth());
		columnResultDeployNumber
				.setLabelProvider(new ColumnResultLabelDeployNumberProvider());

		resultTable.setLayoutData(gridDataResult);

		tableEvent();
	}

	private void tableEvent() {
		columnTXID.getColumn().addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				((DashboardSorter) dashboardViewer.getSorter()).setColumn(1);
				int dir = dashboardViewer.getTable().getSortDirection();
				if (dashboardViewer.getTable().getSortColumn() == columnTXID
						.getColumn()) {
					dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
				} else {
					dir = SWT.DOWN;
				}
				dashboardViewer.getTable().setSortDirection(dir);
				dashboardViewer.getTable()
						.setSortColumn(columnTXID.getColumn());
				dashboardViewer.refresh();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		columnTXID.getColumn().addControlListener(new ControlListener() {
			public void controlResized(ControlEvent e) {
				columnResult.getColumn().setWidth(
						columnTXID.getColumn().getWidth());
			}

			public void controlMoved(ControlEvent e) {
			}
		});

		columnDate.getColumn().addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				((DashboardSorter) dashboardViewer.getSorter()).setColumn(2);
				int dir = dashboardViewer.getTable().getSortDirection();
				if (dashboardViewer.getTable().getSortColumn() == columnDate
						.getColumn()) {
					dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
				} else {
					dir = SWT.DOWN;
				}
				dashboardViewer.getTable().setSortDirection(dir);
				dashboardViewer.getTable()
						.setSortColumn(columnDate.getColumn());
				dashboardViewer.refresh();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});
		columnDate.getColumn().addControlListener(new ControlListener() {
			public void controlResized(ControlEvent e) {
				columnTemp1.getColumn().setWidth(
						columnDate.getColumn().getWidth());
			}

			public void controlMoved(ControlEvent e) {
			}
		});

		columnDeployItemNumber.getColumn().addSelectionListener(
				new SelectionListener() {
					public void widgetSelected(SelectionEvent e) {
						((DashboardSorter) dashboardViewer.getSorter())
								.setColumn(3);
						int dir = dashboardViewer.getTable().getSortDirection();
						if (dashboardViewer.getTable().getSortColumn() == columnDeployItemNumber
								.getColumn()) {
							dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
						} else {
							dir = SWT.DOWN;
						}
						dashboardViewer.getTable().setSortDirection(dir);
						dashboardViewer.getTable().setSortColumn(
								columnDeployItemNumber.getColumn());
						dashboardViewer.refresh();
					}

					public void widgetDefaultSelected(SelectionEvent e) {
						widgetSelected(e);
					}
				});
		columnDeployItemNumber.getColumn().addControlListener(
				new ControlListener() {
					public void controlResized(ControlEvent e) {
						columnResultDeployItemNumber.getColumn().setWidth(
								columnDeployItemNumber.getColumn().getWidth());
					}

					public void controlMoved(ControlEvent e) {
					}
				});

		columnDeployNumber.getColumn().addSelectionListener(
				new SelectionListener() {
					public void widgetSelected(SelectionEvent e) {
						((DashboardSorter) dashboardViewer.getSorter())
								.setColumn(4);
						int dir = dashboardViewer.getTable().getSortDirection();
						if (dashboardViewer.getTable().getSortColumn() == columnDeployNumber
								.getColumn()) {
							dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
						} else {
							dir = SWT.DOWN;
						}
						dashboardViewer.getTable().setSortDirection(dir);
						dashboardViewer.getTable().setSortColumn(
								columnDeployNumber.getColumn());
						dashboardViewer.refresh();
					}

					public void widgetDefaultSelected(SelectionEvent e) {
						widgetSelected(e);
					}
				});
		columnDeployNumber.getColumn().addControlListener(
				new ControlListener() {
					public void controlResized(ControlEvent e) {
						columnResultDeployNumber.getColumn().setWidth(
								columnDeployNumber.getColumn().getWidth());
					}

					public void controlMoved(ControlEvent e) {
					}
				});

		columnDeploySuccess.getColumn().addSelectionListener(
				new SelectionListener() {
					public void widgetSelected(SelectionEvent e) {
						((DashboardSorter) dashboardViewer.getSorter())
								.setColumn(5);
						int dir = dashboardViewer.getTable().getSortDirection();
						if (dashboardViewer.getTable().getSortColumn() == columnDeploySuccess
								.getColumn()) {
							dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
						} else {
							dir = SWT.DOWN;
						}
						dashboardViewer.getTable().setSortDirection(dir);
						dashboardViewer.getTable().setSortColumn(
								columnDeploySuccess.getColumn());
						dashboardViewer.refresh();
					}

					public void widgetDefaultSelected(SelectionEvent e) {
						widgetSelected(e);
					}
				});

		trensferListener = new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				((DashboardSorter) dashboardViewer.getSorter()).setColumn(6);
				int dir = dashboardViewer.getTable().getSortDirection();
				if (dashboardViewer.getTable().getSortColumn() == columnResultTransferSuccess
						.getColumn()) {
					dir = dir == SWT.UP ? SWT.DOWN : SWT.UP;
				} else {
					dir = SWT.DOWN;
				}
				dashboardViewer.getTable().setSortDirection(dir);
				dashboardViewer.getTable().setSortColumn(
						columnResultTransferSuccess.getColumn());
				dashboardViewer.refresh();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		};

		columnResultTransferSuccess.getColumn().addSelectionListener(
				trensferListener);
	}

	private class TxIDColumnLabelProvider extends ColumnLabelProvider {
		public String getText(Object element) {
			return ((DashboardData) element).getId();
		}
	}

	private class DateColumnLabelProvider extends ColumnLabelProvider {
		public String getText(Object element) {
			return ((DashboardData) element).getDate();
		}
	}

	private class NumItemColumnLabelProvider extends ColumnLabelProvider {
		public String getText(Object element) {
			return ((DashboardData) element).getNumItem() + ""; //$NON-NLS-1$
		}
	}

	private class NumDeployColumnLabelProvider extends ColumnLabelProvider {
		public String getText(Object element) {
			int numSuccess = ((DashboardData) element).getNumSuccessDeploy();
			int numExcute = ((DashboardData) element).getNumExcuteDeploy();
			return numSuccess + "/" + numExcute; //$NON-NLS-1$
		}
	}

	private class SuccessDeployColumnLabelProvider extends ColumnLabelProvider {
		public String getText(Object element) {
			if (((DashboardData) element).isBoolDeploySuccess()) {
				return "Success"; //$NON-NLS-1$
			} else {
				return "Fail"; //$NON-NLS-1$
			}
		}

		public Color getForeground(Object element) {
			if (((DashboardData) element).isBoolDeploySuccess()) {
				return Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
			} else {
				return Display.getDefault().getSystemColor(SWT.COLOR_RED);
			}
		}

	}

	private class SuccessTransferColumnLabelProvider extends
			ColumnLabelProvider {
		public String getText(Object element) {
			String aa = ((DashboardData) element).getTransferSuccess();
			if (((DashboardData) element).getTransferSuccess().equals("true")) { //$NON-NLS-1$
				return UIMessages.ODEN_DASHBOARD_DashboardPage_FetchlogDone;
			} else if (((DashboardData) element).getTransferSuccess().equals(
					"false")) { //$NON-NLS-1$
				return UIMessages.ODEN_DASHBOARD_DashboardPage_FetchlogNotyet;
			} else {
				return ""; //$NON-NLS-1$
			}
		}

		public Color getForeground(Object element) {
			if (((DashboardData) element).getTransferSuccess().equals("true")) { //$NON-NLS-1$
				return Display.getDefault().getSystemColor(SWT.COLOR_BLACK);
			} else {
				return Display.getDefault().getSystemColor(SWT.COLOR_RED);
			}
		}
	}

	private class ColumnResultLabelProvider extends ColumnLabelProvider {
		public String getText(Object element) {
			return (String) ((ArrayList) element).get(0);
		}
	}

	private class ColumnResultTempLabelProvider extends ColumnLabelProvider {
		public String getText(Object element) {
			return (String) ((ArrayList) element).get(1);
		}
	}

	private class ColumnResultDeployItemNumberLabelProvider extends
			ColumnLabelProvider {
		public String getText(Object element) {
			return (String) ((ArrayList) element).get(2);
		}
	}

	private class ColumnResultLabelDeployNumberProvider extends
			ColumnLabelProvider {
		public String getText(Object element) {
			return (String) ((ArrayList) element).get(3);
		}
	}

	private void setListener() {

		checkSuccess.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				deployFail = checkSuccess.getSelection(); // 체크하면 fail만
				new DashboardRefreshAction(Dashboard.getServer().getNickname())
						.refreshFilteredTable();
			}
		});

		checkTrasferResult.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				transferFail = checkTrasferResult.getSelection(); // 체크하면 fail만
				new DashboardRefreshAction(Dashboard.getServer().getNickname())
						.refreshFilteredTable();
			}
		});

		dateSearch.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				widgetDefaultSelected(e);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				String from = CommonUtil.replaceIgnoreCase(CommonUtil
						.replaceIgnoreCase(fromCombo.getDateAsString().trim(),
								".", ""), " ", "");
				String to = CommonUtil.replaceIgnoreCase(CommonUtil
						.replaceIgnoreCase(toCombo.getDateAsString().trim(),
								".", ""), " ", "");

				if (from == null || from.equals("")) { //$NON-NLS-1$
					from = UIMessages.ODEN_DASHBOARD_DashboardPage_DateFrom;
				}
				if (to == null || to.equals("")) { //$NON-NLS-1$
					to = UIMessages.ODEN_DASHBOARD_DashboardPage_DateTo;
				}
				if (!checkValidation(from, to)) {
					DialogUtil
							.openMessageDialog(
									"Warning", //$NON-NLS-1$
									UIMessages.ODEN_DASHBOARD_DashboardPage_DateWarningMsg,
									MessageDialog.WARNING);
				} else {
					new DashboardRefreshAction(Dashboard.getServer()
							.getNickname()).refreshFilteredTable();
				}
			}
		});

		infoTable.addMouseListener(new MouseListener() {
			public void mouseUp(MouseEvent e) {
			}

			public void mouseDown(MouseEvent e) {
			}

			public void mouseDoubleClick(MouseEvent e) {
				String id = getSelectedID();
				if (id == null || id.equals("")) { //$NON-NLS-1$
				} else {
					okPressed(getHistoryView());
				}
			}
		});

		infoTable.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				TableItem item = (TableItem) e.item;

				setSelectedID(item.getText(0));
				setTransferResult(item.getText(5));

				setSelection(infoTable.getSelection());
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}
		});

	}

	private boolean checkValidation(String from, String to) {// 48~57
		boolean result = false;
		int numFrom = Integer.parseInt(from);
		int numTo = Integer.parseInt(to);
		if (from.length() != 8 || to.length() != 8) {
			if (from.length() == 0 && to.length() == 0) {
				result = true;
			} else {
				result = false;
			}
		} else if (numFrom > numTo) {
			result = false;
		} else {
			for (int i = 0; i < 8; i++) {
				int n = from.charAt(i);
				int m = to.charAt(i);
				if (n < 48 || n > 57) {
					result = false;
					break;
				} else if (m < 48 || m > 57) {
					result = false;
					break;
				} else {
					result = true;
				}
			}
		}
		return result;
	}

	public void loadInitData() {
		//		String cmd = CommandMessages.ODEN_CLI_COMMAND_spectrum_fetchlist + " "//$NON-NLS-1$
		// + CommandMessages.ODEN_CLI_OPTION_json;
		// try {
		// String spectrum = OdenBroker.sendRequest(getShellURL(), cmd);
		// boolSSL = true;
		// } catch (OdenException e) {
		// OdenActivator.error(
		// "Exception occured while check command is valid.", e);
		// } catch (CommandNotFoundException e) {
		// //TODO
		// OdenActivator.error("Anyframe Oden command not found.", e);
		// checkTrasferResult.setEnabled(false);
		// columnResultTransferSuccess.getColumn()
		// .removeSelectionListener(trensferListener);
		// boolSSL = false;
		// }

		ArrayList<DashboardData> dashboardDataList = new ArrayList<DashboardData>();
		dashboardDataList = getDataList();

		getDashboardViewer().setInput(dashboardDataList);
		dashboardViewer.getTable().select(0);
	}

	public void fillResultData() {

		ArrayList<String> list = new ArrayList<String>();

		// column 1
		list.add(UIMessages.ODEN_DASHBOARD_DashboardPage_ResultTotal);

		// column 2(empty cell)
		list.add(""); //$NON-NLS-1$

		// column 3 & 4
		int sumItem = 0;
		int sumSuccess = 0;
		int sumExcute = 0;

		int n = infoTable.getItemCount();

		for (int i = 0; i < n; i++) {
			TableItem item = infoTable.getItem(i);
			sumItem += Integer.parseInt(item.getText(2));
			String columnTemp = item.getText(3);
			StringTokenizer token = new StringTokenizer(columnTemp, "/"); //$NON-NLS-1$
			sumSuccess += Integer.parseInt(token.nextToken());
			sumExcute += Integer.parseInt(token.nextToken());
		}

		list.add(sumItem + ""); //$NON-NLS-1$
		list.add(sumSuccess + "/" + sumExcute); //$NON-NLS-1$

		ArrayList result = new ArrayList();
		result.add(list);

		getResultViewer().setInput(result);
	}

	private ArrayList<DashboardData> getDataList() {
		ArrayList<DashboardData> dashboardDataList = new ArrayList<DashboardData>();

		ArrayList<DashboardData> resultDashboardDataList = new ArrayList<DashboardData>();

		String commnd = CommandMessages.ODEN_CLI_COMMAND_history_info
				+ " "//$NON-NLS-1$
				+ CommandMessages.ODEN_CLI_OPTION_date
				+ " "//$NON-NLS-1$
				+ UIMessages.ODEN_DASHBOARD_DashboardPage_DateFrom
				+ " " + UIMessages.ODEN_DASHBOARD_DashboardPage_DateTo //$NON-NLS-1$
				+ " " + CommandMessages.ODEN_CLI_OPTION_detail + " "//$NON-NLS-1$ //$NON-NLS-2$
				+ CommandMessages.ODEN_CLI_OPTION_json;

		String cmd = CommandMessages.ODEN_CLI_COMMAND_spectrum_fetchlist + " "//$NON-NLS-1$
				+ CommandMessages.ODEN_CLI_OPTION_json;

		try {
			String result = OdenBroker.sendRequest(getShellURL(), commnd);

			String spectrum = "";
			if (boolSSL) {
				spectrum = OdenBroker.sendRequest(getShellURL(), cmd);
			} else {
			}

			if (result != null && !(result.equals(""))) { //$NON-NLS-1$
				JSONArray array = new JSONArray(result);

				for (int i = 0; i < array.length(); i++) {
					JSONObject object = (JSONObject) array.get(i);
					DashboardData dashboard = new DashboardData();
					String txID = (String) object.get("id"); //$NON-NLS-1$

					String desc = (String) object.get("desc"); //$NON-NLS-1$

					if (desc
							.equals(CommandMessages.ODEN_CLI_COMMAND_spectrum_run)) {
						JSONArray spectrumArray = new JSONArray(spectrum);
						if (spectrumArray.length() == 0) {
							dashboard.setTransferSuccess("false"); //$NON-NLS-1$
						} else {
							for (int a = 0; a < spectrumArray.length(); a++) {
								String fetchID = spectrumArray.get(a) + ""; //$NON-NLS-1$
								if (txID.equals(fetchID)) {
									dashboard.setTransferSuccess("true"); //$NON-NLS-1$
									break;
								} else {
									dashboard.setTransferSuccess("false"); //$NON-NLS-1$
								}
							}
						}
					} else {
						dashboard.setTransferSuccess(""); //$NON-NLS-1$
					}

					int item = Integer.parseInt(object.get("nitems") + ""); //$NON-NLS-1$ //$NON-NLS-2$

					int success = Integer.parseInt(object.get("nsuccess") + ""); //$NON-NLS-1$ //$NON-NLS-2$

					int excute = Integer.parseInt(object.get("total") + ""); //$NON-NLS-1$ //$NON-NLS-2$

					String status = (String) object.get("status"); //$NON-NLS-1$

					String temp = object.get("date") + ""; //$NON-NLS-1$ //$NON-NLS-2$
					String txDate = chgDateFormat2(temp);

					dashboard.setId(txID);
					dashboard.setNumItem(item);
					dashboard.setNumSuccessDeploy(success);
					dashboard.setNumExcuteDeploy(excute);
					dashboard.setDate(txDate);

					if (status
							.equals(UIMessages.ODEN_DASHBOARD_DashboardPage_DeploySuccessMark)) {
						dashboard.setBoolDeploySuccess(true);
					} else {
						dashboard.setBoolDeploySuccess(false);
					}

					String tempDate = object.get("date") + ""; //$NON-NLS-1$ //$NON-NLS-2$
					String date = chgDateFormat(tempDate);

					String strFrom = CommonUtil.replaceIgnoreCase(CommonUtil
							.replaceIgnoreCase(fromCombo.getDateAsString()
									.trim(), ".", ""), " ", "");
					String strTo = CommonUtil.replaceIgnoreCase(CommonUtil
							.replaceIgnoreCase(
									toCombo.getDateAsString().trim(), ".", ""),
							" ", "");

					int intData = Integer.parseInt(date);

					int intFrom = strFrom.equals("") ? 0 : Integer
							.parseInt(strFrom);
					int intTo = strTo.equals("") ? 99999999 : Integer
							.parseInt(strTo);

					if (deployFail && transferFail) {
						// deploy fail, transfer fail 동시
						String transfer = dashboard.getTransferSuccess();
						if (!dashboard.isBoolDeploySuccess()
								&& transfer.equals("false")) { //$NON-NLS-1$
							if ((intData >= intFrom && intData <= intTo)
									|| (strFrom.equals("") && strTo.equals(""))) { //$NON-NLS-1$ //$NON-NLS-2$
								dashboardDataList.add(dashboard);
							} else {
							}
						} else {
							// 둘중 하나만 or 둘다 아님
						}
					} else if (deployFail) {// deploy만 fail
						if (!dashboard.isBoolDeploySuccess()) {
							if ((intData >= intFrom && intData <= intTo)
									|| (strFrom.equals("") && strTo.equals(""))) { //$NON-NLS-1$ //$NON-NLS-2$
								dashboardDataList.add(dashboard);
							} else {
							}
						} else {
						}
					} else if (transferFail) {// transfer만 fail
						String transfer = dashboard.getTransferSuccess();
						if (transfer.equals("false")) { //$NON-NLS-1$
							if ((intData >= intFrom && intData <= intTo)
									|| (strFrom.equals("") && strTo.equals(""))) { //$NON-NLS-1$ //$NON-NLS-2$
								dashboardDataList.add(dashboard);
							} else {
							}
						} else {
						}
					} else {
						if ((intData >= intFrom && intData <= intTo)
								|| (strFrom.equals("") && strTo.equals(""))) { //$NON-NLS-1$ //$NON-NLS-2$
							dashboardDataList.add(dashboard);
						} else {
						}
					}
				}
			}
		} catch (OdenException e) {
		} catch (Exception odenException) {
			OdenActivator
					.error(
							UIMessages.ODEN_DASHBOARD_DashboardPage_Exception_SearchingDeploy,
							odenException);
		}

		// bottom to up(최신것부터)
		for (int i = dashboardDataList.size() - 1; i >= 0; i--) {
			resultDashboardDataList.add(dashboardDataList.get(i));
		}
		return resultDashboardDataList;
	}

	private String chgDateFormat(String input) {
		return new SimpleDateFormat(
				UIMessages.ODEN_DASHBOARD_DashboardPage_DateFormat).format(Long
				.valueOf(input));
	}

	private String chgDateFormat2(String input) {
		return new SimpleDateFormat(
				UIMessages.ODEN_DASHBOARD_DashboardPage_ColumnDateFormat)
				.format(Long.valueOf(input));
	}

	private DeploymentHistoryView getHistoryView() {
		IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		DeploymentHistoryView view = null;
		if (page != null) {
			view = (DeploymentHistoryView) page
					.findView(DeploymentHistoryView.class.getName());
			if (view == null) {
				try {
					view = (DeploymentHistoryView) page
							.showView(DeploymentHistoryView.class.getName());
				} catch (PartInitException partInitException) {
					partInitException.printStackTrace();
				}
			} else {
				// already open history view
				page.activate(view);
			}
		}
		return view;
	}

	protected void okPressed(final DeploymentHistoryView historyview) {
		if (historyview != null) {
			historyview.getTree().removeAll();
			historyview.refreshAgentCombo();
			historyview.getAgentNameCombo().setText(serverNickName);
			isFail = historyview.getOnlyFail().getSelection();

			try {
				historyview
						.getUtil()
						.setSHELL_URL(
								CommonMessages.ODEN_CommonMessages_ProtocolString_HTTP
										+ server.getUrl()
										+ CommonMessages.ODEN_CommonMessages_ProtocolString_HTTPsuf);
			} catch (Exception odenException) {
				OdenActivator
						.error(
								UIMessages.ODEN_DASHBOARD_DashboardPage_Exception_ShowingHistory,
								odenException);
			}
		}

		final Job creatingMarkersJob = new Job("Creating Markers...") { //$NON-NLS-1$

			protected IStatus run(IProgressMonitor monitor) {

				monitor.beginTask("Creating Markers...", 1000); //$NON-NLS-1$
				monitor.subTask("Getting Queries..."); //$NON-NLS-1$

				monitor.done();
				return Status.OK_STATUS;
			}
		};

		creatingMarkersJob.setSystem(false);
		creatingMarkersJob.setUser(false);

		Job gettingQueryIdsJob = new Job("Searching Queries...") { //$NON-NLS-1$
			protected IStatus run(IProgressMonitor monitor) {

				monitor.beginTask("Searching Queries...", 1000); //$NON-NLS-1$
				monitor.subTask("Getting Queries..."); //$NON-NLS-1$

				final ArrayList arrayList = historyview.gettingHistories(
						monitor, getSelectedID(), true, isFail);

				PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {

					public void run() {

						historyview.setHistoryList(arrayList);
						historyview.setTreeData();

						historyview
								.getHistorySearchView()
								.setText(
										UIMessages.ODEN_HISTORY_DeploymentHistoryView_Item_Found
												+ historyview.getTree()
														.getItemCount()
												+ " " //$NON-NLS-1$
												+ "transactions" //$NON-NLS-1$
												+ " " //$NON-NLS-1$
												+ historyview.getCount()
												+ " " //$NON-NLS-1$
												+ UIMessages.ODEN_HISTORY_DeploymentHistoryView_Items);

					}
				});
				monitor.done();
				return Status.OK_STATUS;
			}
		};

		gettingQueryIdsJob.setSystem(false);
		gettingQueryIdsJob.setUser(true);

		gettingQueryIdsJob.schedule();
	}
}
