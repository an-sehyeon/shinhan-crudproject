package com.shinhan.common;

import java.util.Scanner;

// FrontController패턴 : controller가 여러개인 경우 사용자의 요청과 응답은 출구가 여러개
// 바람직하지 않음
//- 추후 Servlet으로 구현 : DispatcherServlet이 있다.(String은 FrontController가 이미 있다.)
public class FrontController {

	public static void main(String[] args) {
		
		// 사용자가 emp, dept작업할것인지 결정
		Scanner sc = new Scanner(System.in);
		
		
		CommonControllerInterface controller = null;
		boolean isStop = false;
		while(!isStop) {
			System.out.println("emp,dept,job,end >>>");
			String job = sc.next();
			
			switch(job) {
			case "emp"->{controller = ControllerFactory.make("emp");}
			case "dept"->{controller = ControllerFactory.make("dept");}
			case "job"->{controller = ControllerFactory.make("job");}
			case "end"->{isStop = true; continue;}
			default -> {continue;}
			}
			
			// 전략패턴
			controller.execute(); //작업이 달라져도 사용법은 같다. (전략패턴)
		}
		sc.close();
		System.out.println("===MAIN END===");

	}

}
