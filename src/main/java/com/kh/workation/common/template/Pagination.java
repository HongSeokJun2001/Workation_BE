package com.kh.workation.common.template;

import com.kh.workation.common.model.vo.PageInfo;

public class Pagination {
	
	// 7개의 변수를 잘 셋팅해서 하나의 PageInfo 객체로 만들어주는 메소드
	// > 4개의 기본 변수를 매개변수로 받아서 나머지 3개의 변수를 계산한 후
	//   최종적으로 7개의 변수를 PageInfo 로 가공해서 리턴
	public static PageInfo getPageInfo(int listCount, int currentPage,
									   int pageLimit, int listLimit) {
		
		// 지금부터 위의 4개의 변수를 가지고 아래의 3개의 변수를 계산해서 구해볼 것!!
		
		// * maxPage : 가장 마지막 페이지가 몇 번 페이지인지
		// > 총 몇페이지가 나오는지 계산해야함
		/*
		 * maxPage 구하기 - listCount, boardLimit 의 영향을 받음
		 * 
		 * - 공식 구하기
		 * 단, boardLimit 가 10 이라는 가정 하에 규칙을 구해보자
		 * 
		 * 총 갯수 (listCount)		boardLimit 			maxPage
		 * 100.0 개				/	10 개 => 10.0		10
		 * 101.0 개				/	10 개 => 10.1		11
		 * 102.0 개				/	10 개 => 10.2		11
		 * 109.0 개				/	10 개 => 10.9		11
		 * 110.0 개				/	10 개 => 11.0		11
		 * 111.0 개				/	10 개 => 11.1		12
		 * 112.0 개				/	10 개 => 11.2		12
		 * ... 
		 * => 실수로써 나눗셈 한 결과를 올림처리 한다면? maxPage 값 도출
		 * 
		 * 1) listCount 를 double 타입으로 강제형변환
		 * 2) listCount / boardLimit
		 * 3) 결과를 올림처리 (Math.ceil)
		 * 4) 최종 결과를 int 로 강제형변환
		 * 
		 * maxPage = (int)Math.ceil((double)listCount / boardLimit);
		 */
		
		int maxPage = (int)Math.ceil((double)listCount / listLimit);
		
		// * startPage : 페이지 하단에 보여질 페이징바의 시작수
		// > 페이징 바의 시작수는 현재 내가 보고있는 페이지 기준으로 계산해서 도출
		
		/*
		 * startPage 구하기 - pageLimit, currentPage 에 영향을 받음
		 * 
		 * - 공식 구하기
		 * 단, pageLimit 가 10 이라는 가정 하에 규칙을 구해보자!!
		 * 
		 * 만약에 pageLimit 가 10 이였다면?
		 * startPage : 1, 11, 21, 31, 41, 51, ...
		 * > pageLimit 의 배수 + 1 = startPage 값
		 *   (n 이 0부터 시작한다 라는 가정 하에 n * 10 + 1)
		 * 
		 * 만약에 pageLimit 가 5 였다면?
		 * startPage : 1, 6, 11, 16, 21, 26, 31, ...
		 * > pageLimit 의 배수 + 1 = startPage 값
		 *   (n 이 0부터 시작한다 라는 가정 하에 n * 5 + 1)
		 * 
		 * 즉, startPage = n * pageLimit + 1;
		 * (단, n 은 0부터 시작함)
		 * 
		 * 위의 전체적인 공식의 틀에서 이제는 0부터 시작되는 n 까지 규칙을 구해야함!!
		 * (n 을 구하는 공식도 뽑아내야함)
		 * 
		 * pageLimit 가 10 이라는 가정 하에
		 * 
		 * currentPage 		startPage
		 * 1				1
		 * 5				1
		 * 9				1
		 * 10				1
		 * 11				11
		 * 13				11
		 * 19				11
		 * 20				11
		 * 21				21
		 * 26				21
		 * 30 				21
		 * ...
		 * 
		 * => currentPage 가 
		 *    1 ~ 10 페이지인 경우 : startPage = 1 = n * 10 + 1			n = 0
		 *    11 ~ 20 페이지인 경우 : startPage = 11 = n * 10 + 1		n = 1
		 *    21 ~ 30 페이지인 경우 : startPage = 21 = n * 10 + 1		n = 2
		 *    31 ~ 40 페이지인 경우 : startPage = 31 = n * 10 + 1		n = 3
		 *    ...
		 * 
		 * 최종 n 을 구해보자!!
		 * 															n 
		 * (currentPage - 1) / pageLimit =  (1 - 1) / 10 = 0		0
		 * 							 		(2 - 1) / 10 = 0		0
		 * 							 		(3 - 1) / 10 = 0		0
		 * 							 		(9 - 1) / 10 = 0 		0
		 * 							 		(10 - 1) / 10 = 0		0
		 * 							 		(11 - 1) / 10 = 1		1
		 * 							 		(12 - 1) / 10 = 1		1
		 * 							 		(19 - 1) / 10 = 1		1
		 * 							 		(20 - 1) / 10 = 1		1
		 * 							 		(21 - 1) / 20 = 2		2
		 * 							 		...
		 * 즉, n = (currentPage - 1) / pageLimit;
		 * 
		 * startPage = n * pageLimit + 1;
		 * n = (currentPage - 1) / pageLimit;
		 * 
		 * 이 두 공식을 합쳐보자!!
		 * startPage = 					n 				* pageLimit + 1;
		 * 			 = (currentPage - 1) / pageLimit	* pageLimit + 1;
		 */
		
		int startPage = (currentPage - 1) / pageLimit * pageLimit + 1;
		
		// * endPage : 페이지 하단에 보여질 페이징바의 끝수
		// > 시작수와 pageLimit 값 기준으로 계산해서 도출해낸다.
		
		/*
		 * endPage 구하기 - startPage, pageLimit 의 영향을 받음
		 * 				  단, maxPage 도 종종 영향을 줌
		 * 
		 * - 공식 구하기
		 * 단, pageLimit 가 10이라는 가정 하에 규칙을 구해보자
		 * 
		 * startPage = 1 		endPage = 10
		 * startPage = 11		endPage = 20
		 * startPage = 21		endPage = 30
		 * ...
		 * 
		 * => 즉, endPage = startPage + pageLimit - 1;
		 */
		
		int endPage = startPage + pageLimit - 1;
		
		// 단, startPage 가 11 이여서 endPage 가 20 으로 계산되는 상황에서
		// maxPage 가 17 까지 밖에 안된다면?
		// > 이 경우에는 maxPage 가 곧 endPage 가 되어버림!!
		if(endPage > maxPage) {
			
			endPage = maxPage;
		}
		
		return new PageInfo(listCount, currentPage, pageLimit, listLimit,
							maxPage, startPage, endPage);
		
	}

}
