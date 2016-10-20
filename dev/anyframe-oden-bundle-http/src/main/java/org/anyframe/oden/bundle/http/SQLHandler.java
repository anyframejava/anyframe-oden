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
package org.anyframe.oden.bundle.http;

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

/**
 * This is SQLHandler class.
 * 
 * @author Junghwan Hong
 */
public class SQLHandler {
	private static final String DB_URL = "jdbc:hsqldb:file:meta/db";

	@SuppressWarnings("PMD")
	public List<Map<String, String>> executeQuery(String sql)
			throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DriverManager.getConnection(DB_URL);
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			// make array (size: rs's size)
			rs.last();
			List<Map<String, String>> list = new ArrayList<Map<String, String>>(
					rs.getRow());
			rs.beforeFirst();

			// get column names
			ResultSetMetaData meta = rs.getMetaData();
			List<String> cols = new ArrayList<String>(meta.getColumnCount());
			for (int i = 0; i < meta.getColumnCount(); i++) {
				cols.add(meta.getColumnLabel(i));
			}

			while (rs.next()) {
				Map<String, String> m = new HashMap<String, String>();
				for (int i = 0; i < cols.size(); i++) {
					m.put(cols.get(i), rs.getString(i));
				}
				list.add(m);
			}
			return list;
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
			}
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
			}
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
			}
		}
	}

	public void executeUpdate(String sql) throws SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DriverManager.getConnection(DB_URL);
			ps = conn.prepareStatement(sql);
			ps.executeUpdate(sql);
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (SQLException e) {
			}
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException e) {
			}
		}
	}
}
