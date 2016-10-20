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
package anyframe.oden.eclipse.core.utils;

import java.util.Dictionary;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;

/**
 * This utility class finds out versions of
 * Eclipse platform or Eclipse product.
 * 
 * @author RHIE Jihwan
 * @since 1.0.0 RC1
 * @version 1.0.0
 *
 */
public class VersionUtil {

	/**
	 * Gets Eclipse platform version
	 * @return Eclipse platform version (eg. 3.5.0.I20090604-2000)
	 */
	@SuppressWarnings({ "restriction", "unchecked" })
	public static String getPlatformVersion() {
		String version = null;

		try {
			Dictionary dictionary = org.eclipse.ui.internal.WorkbenchPlugin.getDefault().getBundle().getHeaders();
			version = (String) dictionary.get("Bundle-Version"); //$NON-NLS-1$
		} catch (NoClassDefFoundError e) {
			version = getProductVersion();
		}

		return version;
	}

	/**
	 * Gets Eclipse product version
	 * @return Eclipse product version (eg. 3.5.0)
	 */
	public static String getProductVersion() {
		String version = null;

		try {
			IProduct product = Platform.getProduct();
			String aboutText = product.getProperty("aboutText"); //$NON-NLS-1$

			String pattern = "Version: (.*)\n"; //$NON-NLS-1$
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(aboutText);
			boolean found = m.find();

			if (found) {
				version = m.group(1);
			}
		} catch (Exception e) {

		}

		return version;
	}

}
