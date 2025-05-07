package com.shinhan.emp;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import com.shinhan.common.CommonControllerInterface;

// MVC2모델
// FrontController -> Controller선택 -> Service -> DAO -> DB
//						 <-				  <-	 	 <-
public class EmpController implements CommonControllerInterface{
	
	static Scanner sc = new Scanner(System.in);
	static EmpService empService = new EmpService();
	
	@Override
	public void execute() {
		boolean isStop = false;
		while(!isStop) {
			menuDisplay();
			int job = sc.nextInt();
			switch(job) {
			case 1->{f_selectAll();}
			case 2->{f_selectById();}
			case 3->{f_selectByDept();}
			case 4->{f_selectByJob();}
			case 5->{f_selectByJobAndDept();}
			case 6->{f_selectByCondition();}
			case 7->{f_deleteByEmpId();}
			case 8->{f_insertEmp();}
			case 9->{f_updateEmp();}
			case 10->{f_sp_call();}
			case 100->{isStop = true;}
			
			}
		}
		System.out.println("==========Good Bye==========");
	}
	private void f_sp_call() {
		System.out.println("조회할 직원ID>>");
		int employee_id = sc.nextInt();
		EmpDTO emp = empService.execure_sp(employee_id);
		String message = "해당직원은 존재하지 않습니다.";
		if(emp!=null) {
			message = emp.getEmail() + " --- " + emp.getSalary();
		}
				
		EmpView.display(message);
	}
	private void f_updateEmp() {
		System.out.println("수정할 직원ID>>");
		int employee_id = sc.nextInt();
		EmpDTO exist_emp = empService.selectById(employee_id);
		if(exist_emp == null) {
			EmpView.display("존재하지않는 직원입니다.");
			return;
		}
		EmpView.display("존재하는 직원정보입니다.");
		EmpView.display(exist_emp);
		int result = empService.empUpdate(makeEmp(employee_id));
		EmpView.display(result + "건 수정");
		
		
		
	}
	private void f_insertEmp() {
		System.out.print("신규직원ID>>");
		int employee_id = sc.nextInt();
		
		int result = empService.empInsert(makeEmp2());
		EmpView.display(result + "건 입력");
		
	}
	
	
	static EmpDTO makeEmp(int employee_id) {
		// 수정용
		System.out.print("first_name>>");
		String first_name = sc.next();
		
		System.out.print("last_name>>");
		String last_name = sc.next();
		
		System.out.print("email>>");
		String email = sc.next();
		
		System.out.print("phone_number>>");
		String phone_number = sc.next();
		
		System.out.print("hdate(yyyy-MM-dd)>>");
		String hdate = sc.next();
		Date hire_date = null;
		if(!hdate.equals("0"))
			   hire_date = DateUtil.converToSQLDate(DateUtil.converToDate(hdate));
							
		System.out.print("job_id(FK:IT_PROG)>>");
		String job_id = sc.next();
		
		System.out.print("salary>>");
		Double salary = sc.nextDouble();
		System.out.print("commission_pct(0.2)>>");
		Double commission_pct = sc.nextDouble();
		System.out.print("manager_id(FK:100)>>");
		Integer manager_id = sc.nextInt();
		System.out.print("department_id(FK:60,90)>>");
		Integer department_id = sc.nextInt();
		
		if(first_name.equals("0")) first_name = null;
		if(last_name.equals("0")) last_name = null;
		if(email.equals("0")) email = null;
		if(phone_number.equals("0")) phone_number = null;
		if(job_id.equals("0")) job_id = null;
		if(salary==0) salary = null;
		if(commission_pct==0) commission_pct = null;
		if(manager_id==0) manager_id = null;
		if(department_id==0) department_id = null;
		
		EmpDTO emp = EmpDTO.builder().commission_pct(commission_pct).department_id(department_id).email(email)
				.employee_id(employee_id).first_name(first_name).hire_date(hire_date).job_id(job_id)
				.last_name(last_name).manager_id(manager_id).phone_number(phone_number).salary(salary).build();
		return emp;
	}
	
	
	
	static EmpDTO makeEmp2() {
		// 입력용
		System.out.print("first_name>>>");
		String first_name = sc.next();
		System.out.print("last_name>>>");
		String last_name = sc.next();
		System.out.print("email>>>");
		String email = sc.next();
		System.out.print("phone_number>>>");
		String phone_number = sc.next();
		System.out.print("hire_date(yyyy-mm-dd)>>>");
		String hdate = sc.next();
		Date hire_date = DateUtil.converToSQLDate(DateUtil.converToDate(hdate));
		System.out.print("job_id>>>");
		String job_id = sc.next();      
		System.out.print("salary>>>");
		double salary = sc.nextDouble();    
		System.out.print("commission_pct(0.2)>>>");
		double commission_pct = sc.nextDouble();
		System.out.print("manager_id(100)>>>");
		int manager_id = sc.nextInt();      
		System.out.print("department_id(60,90)>>>");
		int department_id = sc.nextInt(); 
		EmpDTO emp = EmpDTO.builder()
				.commission_pct(commission_pct)
				.department_id(department_id)
				.email(email)
//				.employee_id(employee_id)
				.first_name(first_name)
				.hire_date(hire_date)
				.job_id(job_id)
				.last_name(last_name)
				.manager_id(manager_id)
				.phone_number(phone_number)
				.salary(salary)
				.build();
		return emp;
	}
	

	private void f_deleteByEmpId() {
		System.out.print("삭제할 직원ID >>>>");
		int empid = sc.nextInt();
		int result = empService.empDeleteById(empid);
		EmpView.display(result + "건 삭제");
		
	}

	private void f_selectByCondition() {
		// = 부서, like 직책, >=급여, >=입사일
		System.out.print("조회할 부서>>>");
		String[] str_deptid = sc.next().split(",");
		Integer[] deptArr = Arrays.stream(str_deptid)
				.map(Integer::parseInt).toArray(Integer[]::new);
		System.out.print("조회할 직책ID>>>");
		String jobid = sc.next();
		System.out.print("조회할 salary(이상)>>");
		int salary = sc.nextInt();
		System.out.print("조회할 입사일(yyyy-mm-dd 이상)>>");
		String hdate = sc.next();
		List<EmpDTO> emplist = empService.selectByCondition(deptArr, jobid,salary,hdate);
		EmpView.display(emplist);
		
	}

	private void f_selectByJobAndDept() {
		System.out.print("조회할 직책ID,부서ID>>>");	// IT_PROG,60
		String data = sc.next();
		String[] arr = data.split(",");
		String jobid = arr[0];
		int deptid = Integer.parseInt(arr[1]);
		List<EmpDTO> emplist = empService.selectByJobAndDept(jobid, deptid);
		EmpView.display(emplist);
		
	}

	private void f_selectByJob() {
		System.out.print("조회할 직책ID>>>");
		String jobid = sc.next();
		List<EmpDTO> emplist = empService.selectByJob(jobid);
		EmpView.display(emplist);
		
	}

	private void f_selectByDept() {
		System.out.print("조회할 부서ID>>>");
		int deptid = sc.nextInt();
		List<EmpDTO> emplist = empService.selectByDept(deptid);
		EmpView.display(emplist);
		
	}

	private void f_selectById() {
		System.out.print("조회할 ID >>>>");
		int empid = sc.nextInt();
		EmpDTO emp = empService.selectById(empid);
		EmpView.display(emp);
	}

	private void f_selectAll() {
		List<EmpDTO> emplist = empService.selectAll();
		EmpView.display(emplist);
		
	}

	private void menuDisplay() {
		System.out.println("-----------------------------");
		System.out.println("1모두조회 2.조회(직원번호empid) 3.조회(부서deptid) 4.조회(직책jobid) 5.조건조회(직책,부서)");
		System.out.println("6.조건조회(다중) 7.직원삭제 8.직원추가 9.업데이트 10.SP호출 100.끝 ");
		System.out.println("-----------------------------");
		System.out.print("작업 선택>>>");
	}

}
