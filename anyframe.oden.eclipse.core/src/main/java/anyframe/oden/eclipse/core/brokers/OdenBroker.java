package anyframe.oden.eclipse.core.brokers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import anyframe.oden.eclipse.core.OdenActivator;
import anyframe.oden.eclipse.core.OdenException;
import anyframe.oden.eclipse.core.OdenMessages;
import anyframe.oden.eclipse.core.alias.Agent;

public class OdenBroker{

	private final static int TIMEOUT = 15000; // millis
	public final static String KNOWN_EXCEPTION = "KnownException";
	public final static String UNKNOWN_EXCEPTION = "java.lang.Exception";
	public static String SHELL_URL = ""; //$NON-NLS-1$
	public OdenBroker() {}

	@SuppressWarnings("finally")
	public static String sendRequest(String shellUrl, String msg)
			throws OdenException {
		PrintWriter writer = null;
		String result = null;

		if(shellUrl != null){
			try {
				
				URLConnection conn = init(shellUrl);
				
				// send
				writer = new PrintWriter(conn.getOutputStream());
				writer.println(msg);
				writer.flush();
				
				// receive
				result = handleResult(conn);
			} catch (OdenException e) {
					throw new OdenException(e.getMessage());	
			} catch (ConnectException e) {
				OdenActivator.warning(OdenMessages.ODEN_CommonMessages_UnableToConnectServer);
			} catch (Exception e) {
				OdenActivator.error(OdenMessages.ODEN_CommonMessages_Exception_KnownExceptionLog, new Throwable(e.getMessage()));
				throw new OdenException(e.getMessage());
			} finally {
				if (writer != null){
					writer.close();
				}
			}
			return result;
		}else{
			return ""; //$NON-NLS-1$
		}
	}

	private static String handleResult(URLConnection conn) throws OdenException, IOException {
		String result = null;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(conn
					.getInputStream(),"utf-8"));

			result = readAll(reader);

			// check if shell exception or unknown exception
			JSONArray array = new JSONArray(result);
			determineException(array);

		} catch (Exception e) {
			throw new OdenException(e.getMessage());
		} finally {
			if (reader != null)
				reader.close();
		}
		return result;
	}
	
	/**
	 * ja가 exception이 들어있는 JSONArray인지 검사. exception이 들어있으면 해당 exception을
	 * throw..
	 * 
	 * @param ja
	 * @throws JSONException
	 * @throws OdenException
	 */
	private static void determineException(JSONArray ja) throws OdenException,
			IOException {
		
		if (ja == null || ja.length() != 1) {
			return;
		}

		try {
			Object obj = ja.get(0);
			if (obj instanceof JSONObject) {
				final JSONObject json = (JSONObject) obj;
				if (json.has(KNOWN_EXCEPTION)) {
					
					Display.getDefault().asyncExec(new Runnable(){
						public void run() {
							try {
								MessageDialog.openError(Display.getDefault().getActiveShell(), OdenMessages.ODEN_CommonMessages_Exception_KnownDlgTitle, json
										.getString(KNOWN_EXCEPTION));
								OdenActivator.error(OdenMessages.ODEN_CommonMessages_Exception_KnownExceptionLog, new Throwable(json.getString(KNOWN_EXCEPTION)));
								
							} catch (JSONException e) {
								OdenActivator.error(OdenMessages.ODEN_CommonMessages_Exception_JSONExceptionLog, e);
							}
						}
					});
					throw new OdenException(json.getString(KNOWN_EXCEPTION));	
				} else if (json.has(UNKNOWN_EXCEPTION)) {
					
					Display.getDefault().asyncExec(new Runnable(){
						public void run() {
							try {
								MessageDialog.openError(Display.getDefault()
										.getActiveShell(), OdenMessages.ODEN_CommonMessages_Exception_UnknownDlgTitle , OdenMessages.ODEN_CommonMessages_Exception_UnknownDlgText);
								OdenActivator.error(OdenMessages.ODEN_CommonMessages_Exception_UnknownExceptionLog, new Throwable(json.getString(UNKNOWN_EXCEPTION)));
							} catch (JSONException e) {
								OdenActivator.error(OdenMessages.ODEN_CommonMessages_Exception_JSONExceptionLog, e);
							}
						}
					});
					throw new OdenException(json.getString(UNKNOWN_EXCEPTION));
				} else {
					// this is not a kind of exception
				}
			}
		} catch (JSONException jexc) {
			throw new IOException(jexc.getMessage());
		}
	}
	private static String readAll(BufferedReader reader) throws IOException {
		StringBuffer buf = new StringBuffer();
		String line = null;
		while ((line = reader.readLine()) != null) {
			buf.append(line + "\n"); //$NON-NLS-1$
		}
		return buf.toString();
	}

	private static URLConnection init(String url) throws MalformedURLException,
			IOException {
		URLConnection con = new URL(url).openConnection();
		con.setUseCaches(false);
		con.setDoOutput(true);
		con.setDoInput(true);
		con.setConnectTimeout(TIMEOUT);
		con.setRequestProperty("Accept-Charset","utf-8");
		
		return con;
	}
	
	public static void AgentcomboEvent(final Combo agentCombo) {
		agentCombo.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				SHELL_URL = "http://" //$NON-NLS-1$
						+ OdenActivator.getDefault().getAliasManager()
								.getAgentManager().getAgent(
										agentCombo.getText()).getUrl()
						+ "/shell"; //$NON-NLS-1$
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				SHELL_URL = "http://" //$NON-NLS-1$
						+ OdenActivator.getDefault().getAliasManager()
								.getAgentManager().getAgent(
										agentCombo.getText()).getUrl()
						+ "/shell"; //$NON-NLS-1$
			}
		});
	}
	public static void InitAgentCombo(final Combo agentCombo) {
		// TODO : Agent Combo setting
		Collection<Agent> col = OdenActivator.getDefault().getAliasManager()
				.getAgentManager().getAgents();
		for (Agent combo : col){
			agentCombo.add(combo.getNickname());
		}
		agentCombo.select(0);
		
		if(col.size() > 0){
			SHELL_URL = "http://" //$NON-NLS-1$
				+ OdenActivator.getDefault().getAliasManager()
						.getAgentManager().getAgent(
								agentCombo.getText()).getUrl()
				+ "/shell"; //$NON-NLS-1$
		} 
	}

}
