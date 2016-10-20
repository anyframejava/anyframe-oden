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
package anyframe.oden.eclipse.core.snapshot;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.StringTokenizer;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.OdenTrees.TreeObject;
import anyframe.oden.eclipse.core.OdenTrees.TreeParent;
import anyframe.oden.eclipse.core.messages.CommandMessages;
import anyframe.oden.eclipse.core.messages.UIMessages;
import anyframe.oden.eclipse.core.utils.Cmd;
import anyframe.oden.eclipse.core.utils.ImageUtil;

/**
 * Tree label provider for Snapshot view.
 * 
 * @author LEE Sujeong
 * @version 1.0.0 RC2
 * 
 */
public class SnapshotViewLabelProvider extends StyledCellLabelProvider
		implements ILabelProvider {

	private static String[] SIZE_OPT = { "size" }; //$NON-NLS-1$
	private static String[] DATE_OPT = { "date" }; //$NON-NLS-1$

	ImageDescriptor planImageDescriptor = ImageUtil
			.getImageDescriptor(UIMessages.ODEN_SNAPSHOT_SnapshotViewLabelProvider_PlanIcon);
	Image planImage = ImageUtil.getImage(planImageDescriptor);

	ImageDescriptor fileImageDescriptor = ImageUtil
			.getImageDescriptor(UIMessages.ODEN_SNAPSHOT_SnapshotViewLabelProvider_SnpahotIcon);
	Image fileImage = ImageUtil.getImage(fileImageDescriptor);

	/**
	 * Constructor of SnapshotViewLabelProvider class
	 */
	public SnapshotViewLabelProvider() {
	}

	/**
	 * Gets a visible text upon the nickname of the element
	 */
	public String getText(Object obj) {
		return obj.toString();
	}

	/**
	 * Gets an image for a specific element type
	 */
	public Image getImage(Object obj) {
		if (obj instanceof TreeParent) {
			return planImage;
		} else {
			return fileImage;
		}
	}

	@Override
	public void update(ViewerCell cell) {  

		Object element = cell.getElement();
		if (element instanceof TreeParent) {
			cell.setText(element.toString());
		} else if (element instanceof TreeObject) {
			String decoration = getFileInfo(element.toString());
			StyledString styledString = new StyledString(element.toString(),
					null);
			styledString.append(decoration, StyledString.DECORATIONS_STYLER);

			cell.setText(styledString.toString());
			cell.setStyleRanges(styledString.getStyleRanges());
		}
		if (element instanceof TreeParent) {
			cell.setImage(planImage);
		} else {
			cell.setImage(fileImage);
		}
		super.update(cell);
	}

	private String getFileInfo(String text) {
		String result = ""; //$NON-NLS-1$
		String fileInfo = ""; //$NON-NLS-1$
		
		try {
			fileInfo = SnapshotViewContentProvider.getInfo(
					SnapshotView.SHELL_URL,
					CommandMessages.ODEN_CLI_COMMAND_snapshot_fileinfo + " " //$NON-NLS-1$
							+ text + " -json"); //$NON-NLS-1$
		} catch (OdenException e) {
			OdenActivator.error(UIMessages.ODEN_SNAPSHOT_SnapshotView_Exception_GetSnapshotDetailInfo, e);
		}

		String info = ""; //$NON-NLS-1$

		try {
			JSONArray ja = new JSONArray(fileInfo);
			JSONObject jo = ja.getJSONObject(0);
			info = jo.getString(text);
		} catch (JSONException e) {
			OdenActivator.error(UIMessages.ODEN_SNAPSHOT_SnapshotView_Exception_ParseSnapshotDetailInfo, e);
		}

		Cmd cmd = new Cmd(text + " = " + info); //$NON-NLS-1$
		String fileSize = cmd.getOptionArg(SIZE_OPT);
		String fileDate = cmd.getOptionArg(DATE_OPT);

		StringTokenizer tokenizer = new StringTokenizer(fileDate, "\n"); //$NON-NLS-1$
		fileDate = tokenizer.nextToken();

		Long longSize = Long.parseLong(fileSize);
		String fileSizeComma = "";
		String resultWithUnit = "";
		
		if(longSize<1024){
			BigDecimal bd = new BigDecimal(longSize);
			BigDecimal fileSizeKB = bd.setScale(0, BigDecimal.ROUND_UP);
			
			DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
			df = new DecimalFormat("###,###,###"); //$NON-NLS-1$
			Long size = Long.parseLong(fileSizeKB.toString());
			fileSizeComma = df.format(size);
			resultWithUnit = fileSizeComma + UIMessages.ODEN_SNAPSHOT_SnapshotView_FileSizeTailBytes;
		}else{
			double doubleSize = longSize / 1024;
			
			BigDecimal bd = new BigDecimal(doubleSize);
			BigDecimal fileSizeKB = bd.setScale(0, BigDecimal.ROUND_UP);
			
			DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
			df = new DecimalFormat("###,###,###"); //$NON-NLS-1$
			Long size = Long.parseLong(fileSizeKB.toString());
			fileSizeComma = df.format(size);
			resultWithUnit = fileSizeComma + UIMessages.ODEN_SNAPSHOT_SnapshotView_FileSizeTailKB;
		}

		result = " [" + fileDate + ", " + resultWithUnit + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return result;
	}

	/**
	 * Disposes all the images
	 */
	public void dispose() {
		ImageUtil
				.disposeImage(UIMessages.ODEN_SNAPSHOT_SnapshotViewLabelProvider_PlanIcon);
		ImageUtil
				.disposeImage(UIMessages.ODEN_SNAPSHOT_SnapshotViewLabelProvider_SnpahotIcon);
	}

	public void addListener(ILabelProviderListener listener) {
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
	}

}
