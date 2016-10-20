package anyframe.oden.bundle.ent.http;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLHandler {
	private static final String DB_URL = "jdbc:hsqldb:file:meta/db";
	
	public List<Map<String, String>> executeQuery(String sql)
			throws SQLException{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			conn = DriverManager.getConnection(DB_URL);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			
			// make array (size: rs's size)
			rs.last();
			List<Map<String, String>> list = new ArrayList<Map<String,String>>(rs.getRow());
			rs.beforeFirst();
			
			// get column names
			ResultSetMetaData meta = rs.getMetaData();
			List<String> cols = new ArrayList<String>(meta.getColumnCount());
			for(int i=0; i<meta.getColumnCount(); i++){
				cols.add(meta.getColumnLabel(i));
			}
			
			while(rs.next()){
				Map<String, String> m = new HashMap<String, String>();
				for(int i=0; i<cols.size(); i++)
					m.put(cols.get(i), rs.getString(i));
				list.add(m);
			}
			return list;
		}finally{
			try{ if(rs != null) rs.close(); }catch(SQLException e){}
			try{ if(ps != null) ps.close(); }catch(SQLException e){}
			try{ if(conn != null) conn.close(); }catch(SQLException e){}
		}
	}
	
	public void executeUpdate(String sql) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		try{
			conn = DriverManager.getConnection(DB_URL);
			ps = conn.prepareStatement(sql);
			ps.executeUpdate(sql);
		}finally{
			try{ if(ps != null) ps.close(); }catch(SQLException e){}
			try{ if(conn != null) conn.close(); }catch(SQLException e){}
		}
	}
}
