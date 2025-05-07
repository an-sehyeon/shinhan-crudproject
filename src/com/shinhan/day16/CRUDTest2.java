package com.shinhan.day16;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.shinhan.day15.DBUtil;

public class CRUDTest2 {
	public static void main(String[] args) throws SQLException {
		// 트랜잭션 처리
		// 모두 성공하면 commit
		// 하나라도 실패하면 rollback
		// insert...
		// update...
		
		Connection conn = null;
		Statement st1 = null;
		Statement st2 = null;
		String sql1 ="""
				insert into emp1(employee_id, first_name,last_name, email, hire_date, job_id ) values(3,'aa', 'bb', 'zdaf',sysdate, 'IT')
				""";
		String sql2 ="update emp1 set salary = 1005 where employee_id=206";
		
		conn = DBUtil.getConnection();
		conn.setAutoCommit(false); 				// 자동 커밋 X
		st1 = conn.createStatement();
		int result1 = st1.executeUpdate(sql1);	
		st2 = conn.createStatement();
		int result2 = st2.executeUpdate(sql2);
		
		// 둘다 성공했을때만 commit 아니면 rollback
		if(result1 >= 1 && result2 >=1) {
			conn.commit();
		}else {
			conn.rollback();
		}
		
		
		
	}
	
	
	
	
	
	
	public static void f_4() throws SQLException {
		// delete
		// table or view 에러 : table 일치하지 않음
		Connection conn = null;
		Statement st = null;
		int result = 0;
		String sql = """
				delete from emp1
				where employee_id <100
				""";
		conn = DBUtil.getConnection();
		st = conn.createStatement();
		result = st.executeUpdate(sql);		// 자동으로 commit
		System.out.println(result>=1?result +  "건 delete success":"delete fail");
	}
	
	
	
	
	
	public static void f_3() throws SQLException {
		// update
		// table or view 에러 : table 일치하지 않음
		Connection conn = null;
		Statement st = null;
		int result = 0;
		String sql = """
				update emp1
				set department_id =(select department_id
				                    from employees
				                    where department_id = 102),
				            salary = (select salary
				                    from employees
				                    where department_id = 101)
				where employee_id = 3
				""";
		conn = DBUtil.getConnection();
		st = conn.createStatement();
		result = st.executeUpdate(sql);		// 자동으로 commit
		System.out.println(result>=1? "update success":"update fail");
	}
	
	
	
	
	
	
	public static void f_2() throws SQLException {
		// insert
		// table or view 에러 : table 일치하지 않음
		Connection conn = null;
		Statement st = null;
		int result = 0;
		String sql = """
				insert into emp1 values(4,'an','hyeon','nocount','폰',sysdate,'job',100,0.2,100,'20')
				""";
		conn = DBUtil.getConnection();
		st = conn.createStatement();
		result = st.executeUpdate(sql);		// 자동으로 commit
		System.out.println(result>=1? "insert success":"insert fail");
	}
	
	
	
	

	public static void f_1() throws SQLException {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement st = null;
		ResultSet rs = null;
		String sql = """
				select ename, sal, mgr
				from emp
				where mgr = (
				select empno from emp where ename = 'KING')
				""";
		conn = DBUtil.getConnection();
		st = conn.createStatement();
		rs = st.executeQuery(sql);
		while(rs.next()) {
			String a = rs.getString(1);
			int b = rs.getInt(2);
			int c = rs.getInt(3);
			System.out.println(a + "\t" + b +"\t" + c);
		}
		
		
		DBUtil.dbDisconnect(conn, st, rs);
	}

}
