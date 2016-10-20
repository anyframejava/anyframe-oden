package anyframe.oden.bundle.samsunglife;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import anyframe.oden.bundle.common.FileUtil;
import anyframe.oden.bundle.common.Logger;

/**
 * This class contain and return transaction ids what transfer fetchlog success.
 * 
 * @author joon1k
 * 
 */
class SpectrumRecorder {
	public final static String LOG_FILE = "meta/spectrum.log";

	private Object latch = new Object();

	public void writeId(String id) throws IOException {
		File logf = new File(LOG_FILE);
		synchronized (latch) {
			FileUtil.mkdirs(logf);

			PrintStream out = null;
			try {
				out = new PrintStream(new FileOutputStream(logf, true));
				out.println(id);
			} finally {
				if (out != null)
					out.close();
			}
		}
	}

	public List<String> loadIds() {
		File logf = new File(LOG_FILE);
		if (!logf.exists())
			return Collections.EMPTY_LIST;

		List<String> list = new ArrayList<String>();
		BufferedReader reader = null;
		synchronized (latch) {
			try {
				reader = new BufferedReader(new FileReader(logf));
				String line = null;
				while ((line = reader.readLine()) != null) {
					list.add(line.trim());
				}
			} catch (IOException e) {
				Logger.error(e);
			}
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
				}
		}
		return list;
	}
}
