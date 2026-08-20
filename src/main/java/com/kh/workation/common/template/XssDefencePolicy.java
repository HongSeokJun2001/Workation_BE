package com.kh.workation.common.template;

public class XssDefencePolicy {

	/**
	 * XSS 공격 방지용 공통 코드 메소드
	 * @param originText => <> 등이 담겨있는 원본 문자열
	 * @return => <> 등을 다 안전하게 치환해둔 결과 문자열
	 */
	public static String defence(String originText) {
		
		String changeText = originText;
		
		changeText = changeText.replace("<", "&lt;");
		changeText = changeText.replace(">", "&gt;");
		changeText = changeText.replace("\"", "&quot;");
		changeText = changeText.replace("'", "&apos;");
		// ...
		// > 위와 같이 막고자 하는 html 예약어를 replace 한다!!
		
		return changeText;
	}
	// > defence 메소드는 NoticeController, BoardController 등에서
	//   두고두고 다양하게 가져다 사용 가능!! (공통코드 작업)
	
}
