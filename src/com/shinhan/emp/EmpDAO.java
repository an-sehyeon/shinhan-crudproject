package com.shinhan.emp;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.shinhan.day15.DBUtil;

// DAO(Data Access Object) : DB에 CRUD작업(select, insert, update, delete)
public class EmpDAO {
	
	
	// Stored Procedure를 실행하기 (직원번호를 받아서 이메일과 급여를 return)
	public EmpDTO execure_sp(int empid) {
		EmpDTO emp = null;
		Connection conn = DBUtil.getConnection();
		CallableStatement st = null;
		String sql ="{call sp_empinfo2(?,?,?)}";
		
		try {
			st = conn.prepareCall(sql);
			st.setInt(1, empid);
			st.registerOutParameter(2, Types.VARCHAR);
			st.registerOutParameter(3, Types.DECIMAL);
			
			boolean result = st.execute();
			
			emp = new EmpDTO();
			String email = st.getString(2);
			double salary = st.getInt(3);
			emp.setEmail(email);
			emp.setSalary(salary);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return emp;
	}
	
	
	// 업데이트
	public int empUpdate(EmpDTO emp) {
		int result = 0;
		Connection conn = DBUtil.getConnection();
		PreparedStatement st = null;
		
		Map<String, Object> dynamicSQL = new HashMap<>();
		
		if(emp.getFirst_name()!=null) dynamicSQL.put("FIRST_NAME", emp.getFirst_name());
		if(emp.getLast_name()!=null) dynamicSQL.put("Last_name", emp.getLast_name());
		if(emp.getSalary()!=null) dynamicSQL.put("Salary", emp.getSalary());
		if(emp.getHire_date()!=null) dynamicSQL.put("Hire_date", emp.getHire_date());
		
		String sql = "update employees set ";
		String sql2= "where EMPLOYEE_ID = ?";
		
		int colCount = dynamicSQL.size();
		int col = 1;
		for(String key:dynamicSQL.keySet()) {
			sql+=key+"="+"?"+(col!=colCount?",":"");
			col++;
		}
		sql+=sql2;
		System.out.println(sql);
		
		
		try {
			st = conn.prepareStatement(sql);
			int i=1;
			for(String key:dynamicSQL.keySet()) {
		 		st.setObject(i++, dynamicSQL.get(key));
		 	}
			st.setInt(i, emp.getEmployee_id());
			result = st.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	
	
	
	// 입력
		public int empInsert(EmpDTO emp) {
			int result = 0;
			Connection conn = DBUtil.getConnection();
			PreparedStatement st = null;
			String sql = """
					insert into employees(
					EMPLOYEE_ID,   
					FIRST_NAME,    
					LAST_NAME,     
					EMAIL,         
					PHONE_NUMBER,  
					HIRE_DATE,     
					JOB_ID,        
					SALARY,        
					COMMISSION_PCT,
					MANAGER_ID,    
					DEPARTMENT_ID) 
					values( ?,?,?,?,?,?,?,?,?,?,?)
					""";
			try {
				st = conn.prepareStatement(sql);
				st.setInt(1, emp.getEmployee_id());
				st.setString(2, emp.getFirst_name());
				st.setString(3, emp.getFirst_name());
				st.setString(4, emp.getEmail());
				st.setString(5, emp.getPhone_number());
				st.setDate(6, emp.getHire_date());
				st.setString(7, emp.getJob_id());
				st.setDouble(8, emp.getSalary());
				st.setDouble(9, emp.getCommission_pct());
				st.setInt(10, emp.getManager_id());
				st.setInt(11, emp.getDepartment_id());
				result = st.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return result;
		}
	
	
	
	// 삭제
	public int empDeleteById(int empid) {
		int result = 0;
		Connection conn = DBUtil.getConnection();
		PreparedStatement st = null;
		String sql = "delete from employees where employee_id = ? ";
		try {
			st = conn.prepareStatement(sql);
			st.setInt(1, empid);
			result = st.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	
	// deptArr(다중 조회),job, salary, hire_date로 조회
	public List<EmpDTO> selectByCondition(Integer[] arr, String jobid, int salary, String hdate) {
		 List<EmpDTO> emplist = new ArrayList<>();
		 Connection conn = DBUtil.getConnection();
		 PreparedStatement st = null;									
		 ResultSet rs = null;	
		 String deptStr = Arrays.stream(arr)
					.map(id->"?").collect(Collectors.joining(",")); //"?,?,?"
		 
		 String sql = "select *"
		 		+ " from employees"
		 		+ " where job_id like ? "
		 		+ " and department_id = ?"		
				+ " and salary >= ?"		
				+ " and hire_date >= ?";		
		 
		 try {
			st = conn.prepareStatement(sql);							// sql문을 준비
			st.setString(1, "%"+jobid+"%"); 							// 1번째 ?에 값을 setting한다.
			st.setInt(2, salary);
			Date d = DateUtil.converToSQLDate(DateUtil.converToDate(hdate));
			st.setDate(3, d);
			
			int col = 4;
			for(int i=0;i<arr.length;i++) {
				st.setInt(col++, arr[i]);
			}
			
			rs = st.executeQuery();
			while(rs.next()) {
				EmpDTO emp = makeEmp(rs);
				emplist.add(emp);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.dbDisconnect(conn, st, rs);
		}
		 
		 return emplist;
				 
		
	}
	
	
	// job, dept으로 직원조회
			public List<EmpDTO> selectByJobAndDept(String job, int dept) {
				 List<EmpDTO> emplist = new ArrayList<>();
				 Connection conn = DBUtil.getConnection();
				 PreparedStatement st = null;									
				 ResultSet rs = null;											
				 String sql = "select * from employees where job_id = ? and department_id = ?";		
				 
				 try {
					st = conn.prepareStatement(sql);							// sql문을 준비
					st.setString(1, job); 										// 1번째 ?에 값을 setting한다.
					st.setInt(2, dept);
					rs = st.executeQuery();
					while(rs.next()) {
						EmpDTO emp = makeEmp(rs);
						emplist.add(emp);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					DBUtil.dbDisconnect(conn, st, rs);
				}
				 
				 return emplist;
						 
			}
	
	
	
	
	
	// job으로 직원조회
		public List<EmpDTO> selectByJob(String job) {
			 List<EmpDTO> emplist = new ArrayList<>();
			 Connection conn = DBUtil.getConnection();
			 PreparedStatement st = null;									// statement는 sql문을 보내는 통로 -> 바인딩 변수 지원X
			 ResultSet rs = null;											// PreparedStatement : statement 상속받음, 바인딩 변수 지원O
			 String sql = "select * from employees where job_id like ?";		// 바인딩 변수, 성능↑
			 
			 try {
				st = conn.prepareStatement(sql);							// sql문을 준비
				st.setString(1, "%"+job+"%"); 										// 1번째 ?에 값을 setting한다.
				rs = st.executeQuery();
				while(rs.next()) {
					EmpDTO emp = makeEmp(rs);
					emplist.add(emp);
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				DBUtil.dbDisconnect(conn, st, rs);
			}
			 
			 return emplist;
					 
		}
	
	
	
	// 부서의 직원조회
	public List<EmpDTO> selectByDept(int deptid) {
		 List<EmpDTO> emplist = new ArrayList<>();
		 Connection conn = DBUtil.getConnection();
		 Statement st = null;
		 ResultSet rs = null;
		 String sql = "select * from employees where department_id = " + deptid;
		 
		 try {
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while(rs.next()) {
				EmpDTO emp = makeEmp(rs);
				emplist.add(emp);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.dbDisconnect(conn, st, rs);
		}
		 
		 return emplist;
				 
	}
	
	
	
	// 직원번호로 직원의 정보를 상세보기
	public EmpDTO selectById(int empid) {

		 Connection conn = DBUtil.getConnection();
		 EmpDTO emp = null;
		 Statement st = null;
		 ResultSet rs = null;
		 String sql = "select * from employees where employee_id = " + empid;
		 
		 try {
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			if(rs.next()) {
				emp = makeEmp(rs);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.dbDisconnect(conn, st, rs);
		}
		 
		 return emp;
				 
	}
	
	
	// 모든 직원 조회
	public List<EmpDTO> selectAll() {
		 List<EmpDTO> emplist = new ArrayList<>();
		 Connection conn = DBUtil.getConnection();
		 Statement st = null;
		 ResultSet rs = null;
		 String sql = "select * from employees";
		 
		 try {
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while(rs.next()) {
				EmpDTO emp = makeEmp(rs);
				emplist.add(emp);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DBUtil.dbDisconnect(conn, st, rs);
		}
		 
		 return emplist;
				 
	}

	private EmpDTO makeEmp(ResultSet rs) throws SQLException {
		EmpDTO emp = EmpDTO.builder()
				.email(rs.getString("email"))
				.manager_id(rs.getInt("manager_id"))
				.phone_number(rs.getString("phone_number"))
				.salary(rs.getDouble("salary"))
				.last_name(rs.getString("last_name"))
				.job_id(rs.getString("job_id"))
				.hire_date(rs.getDate("hire_date"))
				.first_name(rs.getString("first_name"))
				.employee_id(rs.getInt("employee_id"))
				.department_id(rs.getInt("department_id"))
				.commission_pct(rs.getDouble("commission_pct"))
				.build();
		
		
		return emp;
	}





	
	
	
}
