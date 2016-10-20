/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.anyframe.oden.bundle.core.keygen;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * This is DateKeyGenerator class.
 * 
 * @author Junghwan Hong
 * @see anyframe.oden.bundle.core.keygen.KeyGenerator
 */
public class DateKeyGenerator implements KeyGenerator {

	private String lastDate;
	private Map<String, Integer> lastValues = new HashMap<String, Integer>();

	@SuppressWarnings("PMD")
	public synchronized String next(String prefix) {
		String today = today();
		if (!today.equals(lastDate)) {
			initLastValues();
			lastDate = today;
		}

		int seq = 0;
		if (lastValues.containsKey(prefix)) {
			seq = lastValues.get(prefix);
		}
		lastValues.put(prefix, seq + 1);
		return prefix + today + new DecimalFormat("00000").format(seq);
	}

	private void initLastValues() {
		lastValues = new HashMap<String, Integer>();
	}

	private String today() {
		return new SimpleDateFormat("yyMMdd", Locale.getDefault())
				.format(System.currentTimeMillis());
	}

}
