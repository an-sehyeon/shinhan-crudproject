package com.shinhan.emp;

import java.util.List;

// data를 display하려는 목적, 나중에 웹으로 변경되면 JSP로 대체
public class EmpView {
	
	// 여러건 출력
	public static void display(List<EmpDTO> emplist) {
		if(emplist.size()==0) {
			display("해당하는 직원이 존재하지 않습니다.");
			return;
		}
		System.out.println("====직원 여러건 조회====");
		emplist.stream().forEach(emp->System.out.println(emp));
	}
	
	public static void display(EmpDTO emp) {
		if(emp == null) {
			display("해당하는 직원이 존재하지 않습니다.");
			return;
		}
		System.out.println("==== 직원정보 : " + emp );
	}
	
	public static void display(String massage) {
		System.out.println("==== 알림 : " + massage + "====");
	}

}
