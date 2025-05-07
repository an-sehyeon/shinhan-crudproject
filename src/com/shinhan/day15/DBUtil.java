package com.shinhan.day15;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtil {
	
	// Oracle DB연결을 Util로 만들었음, DB연결하는 함수
	public static Connection getConnection() {
		Connection conn = null;
		String url = "jdbc:oracle:thin:@localhost:1521:xe";		// "jdbc:oracle:thin:@192.168.0.18:1521:xe";
		String userid="hr";	//"hr","scott"
		String userpass="hr"; //"hr","scott"
		
		try {
			try {
				Class.forName("oracle.jdbc.driver.OracleDriver");
				conn = DriverManager.getConnection(url, userid, userpass);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return conn;
	}
	
	// DB연결시 사용한 자원해제 함수
	public static void dbDisconnect(Connection conn, Statement st, ResultSet rs) {
		
		try {
			if(rs!=null)rs.close();
			if(st!=null)st.close();
			if(conn!=null)conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	

}
