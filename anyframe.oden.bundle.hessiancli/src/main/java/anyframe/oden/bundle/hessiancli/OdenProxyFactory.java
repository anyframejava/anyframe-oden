package anyframe.oden.bundle.hessiancli;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import com.caucho.hessian.client.HessianProxyFactory;

public class OdenProxyFactory extends HessianProxyFactory {
	int timeout;
	long readTimeout;
	
	public OdenProxyFactory(int timeout, long readTimeout) {
		this.timeout = timeout;
		this.readTimeout = readTimeout;
	}

	@Override
	protected URLConnection openConnection(URL url) throws IOException {
		setReadTimeout(readTimeout);
		URLConnection conn = super.openConnection(url);
		conn.setConnectTimeout(timeout);
		return conn;
	}
}
