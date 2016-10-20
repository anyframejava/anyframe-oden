package anyframe.oden.bundle.hessiancli;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import com.caucho.hessian.client.HessianProxyFactory;

public class OdenProxyFactory extends HessianProxyFactory {
	@Override
	protected URLConnection openConnection(URL url) throws IOException {
		setReadTimeout(5000);
		URLConnection conn = super.openConnection(url);
		conn.setConnectTimeout(5000);
		return conn;
	}
}
