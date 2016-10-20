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
package anyframe.oden.eclipse.core.jobmanager;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

import anyframe.oden.eclipse.core.OdenTrees.TreeObject;
import anyframe.oden.eclipse.core.OdenTrees.TreeParent;
import anyframe.oden.eclipse.core.messages.UIMessages;
import anyframe.oden.eclipse.core.utils.CommonUtil;
import anyframe.oden.eclipse.core.utils.ImageUtil;

/**
 * Tree label provider for Anyframe Oden Job Manager view outline.
 * 
 * @author HONG JungHwan
 * @version 1.1.0
 *
 */
public class JobManagerViewLabelProvider extends StyledCellLabelProvider implements ILabelProvider {

	// icon for the "Current job" root
	private ImageDescriptor _currentRootImageDescriptor = ImageUtil.getImageDescriptor(UIMessages.ODEN_JOBMANAGER_JobManagerView_TreeRoot_CurrentJobIcon);
	private Image _currentRootImage = ImageUtil.getImage(_currentRootImageDescriptor);
	
	// icon for the "Finished job" root
	private ImageDescriptor _finishedRootImageDescriptor = ImageUtil.getImageDescriptor(UIMessages.ODEN_JOBMANAGER_JobManagerView_TreeRoot_FinishedJobIcon);
	private Image _finishedRootImage = ImageUtil.getImage(_finishedRootImageDescriptor);
	
	// icon for the "Finished job Today" 
	private ImageDescriptor _finishedTodayImageDescriptor = ImageUtil.getImageDescriptor(UIMessages.ODEN_JOBMANAGER_JobManagerView_FinishedJob_TodayIcon);
	private Image _finishedTodayImage = ImageUtil.getImage(_finishedTodayImageDescriptor);
	
	// icon for the "Finished job Week"
	private ImageDescriptor _finishedWeekImageDescriptor = ImageUtil.getImageDescriptor(UIMessages.ODEN_JOBMANAGER_JobManagerView_FinishedJob_WeekIcon);
	private Image _finishedWeekImage = ImageUtil.getImage(_finishedWeekImageDescriptor);
	
	// icon for the "Finished job month"
	private ImageDescriptor _finishedMonthImageDescriptor = ImageUtil.getImageDescriptor(UIMessages.ODEN_JOBMANAGER_JobManagerView_FinishedJob_MonthIcon);
	private Image _finishedMonthImage = ImageUtil.getImage(_finishedMonthImageDescriptor);
	
	// icon for the "Finished job long"
	private ImageDescriptor _finishedLongImageDescriptor = ImageUtil.getImageDescriptor(UIMessages.ODEN_JOBMANAGER_JobManagerView_FinishedJob_LongIcon);
	private Image _finishedLongImage = ImageUtil.getImage(_finishedLongImageDescriptor);
	
	// icon for the success transaction
	private ImageDescriptor _successImageDescriptor = ImageUtil.getImageDescriptor(UIMessages.ODEN_JOBMANAGER_JobManagerView_FinishedJob_SuccessIcon);
	private Image _successImage = ImageUtil.getImage(_successImageDescriptor);
	
	// icon for the fail transaction
	private ImageDescriptor _failImageDescriptor = ImageUtil.getImageDescriptor(UIMessages.ODEN_JOBMANAGER_JobManagerView_FinishedJob_FailIcon);
	private Image _failImage = ImageUtil.getImage(_failImageDescriptor);
	
	// icon for the file
	private ImageDescriptor _fileImageDescriptor = ImageUtil.getImageDescriptor(UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_FileIcon);
	private Image _fileImage = ImageUtil.getImage(_fileImageDescriptor);
	
	// icon for the waiting
	private ImageDescriptor _jobWaitImageDescriptor = ImageUtil.getImageDescriptor(UIMessages.ODEN_JOBMANAGER_JobManagerView_CurrentJob_Wait);
	private Image _jobWaitImage = ImageUtil.getImage(_jobWaitImageDescriptor);
	
	// icon for the running
	private ImageDescriptor _jobRunImageDescriptor = ImageUtil.getImageDescriptor(UIMessages.ODEN_JOBMANAGER_JobManagerView_CurrentJob_Run);
	private Image _jobRunImage = ImageUtil.getImage(_jobRunImageDescriptor);
	
	/**
	 * Constructor
	 */
	public JobManagerViewLabelProvider() {

	}
	
	/**
	 * Disposes all the images
	 */
	public void dispose() {
		//		super.dispose();
		ImageUtil.disposeImage(UIMessages.ODEN_JOBMANAGER_JobManagerView_TreeRoot_CurrentJobIcon);
		ImageUtil.disposeImage(UIMessages.ODEN_JOBMANAGER_JobManagerView_TreeRoot_FinishedJobIcon);
		ImageUtil.disposeImage(UIMessages.ODEN_JOBMANAGER_JobManagerView_FinishedJob_TodayIcon);
		ImageUtil.disposeImage(UIMessages.ODEN_JOBMANAGER_JobManagerView_FinishedJob_WeekIcon);
		ImageUtil.disposeImage(UIMessages.ODEN_JOBMANAGER_JobManagerView_FinishedJob_MonthIcon);
		ImageUtil.disposeImage(UIMessages.ODEN_JOBMANAGER_JobManagerView_FinishedJob_LongIcon);
		ImageUtil.disposeImage(UIMessages.ODEN_EXPLORER_ExplorerViewLabelProvider_FileIcon);
		ImageUtil.disposeImage(UIMessages.ODEN_JOBMANAGER_JobManagerView_FinishedJob_SuccessIcon);
		ImageUtil.disposeImage(UIMessages.ODEN_JOBMANAGER_JobManagerView_FinishedJob_FailIcon);
	}

	/**
	 * Gets an image for a specific element type
	 */
	public Image getImage(Object element) {
		String elementName = ((TreeObject) element).getName();
		TreeParent parent = ((TreeObject) element).getParent();
		String parentName = parent.getName();
		
		if (element instanceof TreeParent) {
			if(parent.getParent() == null) {
				if (parentName.equals("")
						&& elementName.equals(UIMessages.ODEN_JOBMANAGER_JobManagerView_TreeRoot_CurrentJob)) {
					//finished job
					return _currentRootImage;
				} else if (parentName.equals("")
						&& elementName.equals(UIMessages.ODEN_JOBMANAGER_JobManagerView_TreeRoot_FinishedJob)) {
					//finished job
					return _finishedRootImage;
				} 
			} else if (parent.getParent() != null) {
				if (parentName.equals(UIMessages.ODEN_JOBMANAGER_JobManagerView_TreeRoot_FinishedJob)
						&& elementName.substring(0, elementName.indexOf("(")).equals(UIMessages.ODEN_JOBMANAGER_JobManagerView_FinishedJob_Today)) {
					//finished job today
					return _finishedTodayImage;
				} else if (parentName.equals(UIMessages.ODEN_JOBMANAGER_JobManagerView_TreeRoot_FinishedJob)
						&& elementName.substring(0, elementName.indexOf("(")).equals(UIMessages.ODEN_JOBMANAGER_JobManagerView_FinishedJob_Week)) {
					//finished job week
					return _finishedWeekImage;
				} else if (parentName.equals(UIMessages.ODEN_JOBMANAGER_JobManagerView_TreeRoot_FinishedJob)
						&& elementName.substring(0, elementName.indexOf("(")).equals(UIMessages.ODEN_JOBMANAGER_JobManagerView_FinishedJob_Month)) {
					//finished job month
					return _finishedMonthImage;
				} else if (parentName.equals(UIMessages.ODEN_JOBMANAGER_JobManagerView_TreeRoot_FinishedJob)
						&& elementName.substring(0, elementName.indexOf("(")).equals(UIMessages.ODEN_JOBMANAGER_JobManagerView_FinishedJob_Long)) {
					//finished job month
					return _finishedLongImage;
				} else if (parentName.equals(UIMessages.ODEN_JOBMANAGER_JobManagerView_TreeRoot_CurrentJob)) {
					if (elementName.startsWith("2:")) {
						return _jobWaitImage;
					} else if (elementName.startsWith("4:")){
						return _jobRunImage;
					}
				}
			}	
		}
		// items that have no children
		else if (element instanceof TreeObject) {
			if (parent != null) {
				if (parent.getParent().getName().equals("")
						&& parent.getName().equals(UIMessages.ODEN_JOBMANAGER_JobManagerView_TreeRoot_CurrentJob)) {
					if (elementName.startsWith("2:")) {
						return _jobWaitImage;
					} else if (elementName.startsWith("4:")){
						return _jobRunImage;
					}
				} else if (parent.getParent().getName().equals(UIMessages.ODEN_JOBMANAGER_JobManagerView_TreeRoot_FinishedJob)) {
					if (elementName.startsWith("S:")) {
						return _successImage;
					} else if (elementName.startsWith("F:")){
						return _failImage;
					}
				}
			} else {
				return _fileImage;
			}
		}

		return _fileImage;
	}
	
	/**
	 * Gets a visible text upon the current jobs and finished jobs of the element
	 */
	public String getText(Object element) {

		return element.toString();
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return getImage(element);
	}

	public String getColumnText(Object element, int columnIndex) {
		return getText(element);
	}

	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}

	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void update(ViewerCell cell) {

		Object element = cell.getElement();
		TreeParent parent = ((TreeObject) element).getParent();
		
		if (element instanceof TreeParent) {
			cell.setText(element.toString());
		}

		// items that have no children
		else if (element instanceof TreeObject) {
			if (parent.getParent() != null) {
				// Finished job - history
				if (parent.getParent().getName().equals(UIMessages.ODEN_JOBMANAGER_JobManagerView_TreeRoot_FinishedJob)) {
					StyledString styledFileInfoString = stylizeFinishedJobInfo(element);
					cell.setText(styledFileInfoString.toString());
					cell.setStyleRanges(styledFileInfoString.getStyleRanges());
				} else if (parent.getName().equals(UIMessages.ODEN_JOBMANAGER_JobManagerView_TreeRoot_CurrentJob)) {
					StyledString styledFileInfoString = stylizeCurrentJobInfo(element);
					cell.setText(styledFileInfoString.toString());
					cell.setStyleRanges(styledFileInfoString.getStyleRanges());
				} 
			}
		}

		cell.setImage(getImage(element));
		super.update(cell);
	}

	/**
	 * @param element
	 * @return finished job list info
	 */
	private StyledString stylizeFinishedJobInfo(Object element) {
		String fileInfoString = element.toString();
		
		fileInfoString = fileInfoString.substring(fileInfoString.indexOf("[20"));
		StyledString styledFileInfoString = new StyledString(CommonUtil.replaceIgnoreCase(element.toString().substring(2), fileInfoString, ""), null);
		styledFileInfoString.append(" " + fileInfoString, StyledString.DECORATIONS_STYLER);

		return styledFileInfoString;
	}
	
	/**
	 * @param element
	 * @return current job list info
	 */
	private StyledString stylizeCurrentJobInfo(Object element) {
		String fileInfoString = element.toString();
		fileInfoString = fileInfoString.substring(fileInfoString.indexOf("-"));

		StyledString styledFileInfoString = new StyledString(CommonUtil.replaceIgnoreCase(element.toString().substring(2), fileInfoString, ""), null);
		styledFileInfoString.append(" " + fileInfoString, StyledString.DECORATIONS_STYLER);

		return styledFileInfoString;
	}
}
