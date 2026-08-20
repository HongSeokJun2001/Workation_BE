package com.kh.workation.common.template;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;

public class FileRenamePolicy {

	// MultipartFile, HttpSession, String path 를 전달받아서
	// 파일명을 수정하고 업로드 후 수정파일명을 리턴해주는 메소드
	public static String saveFile(MultipartFile upfile, 
								  HttpSession session, String path) {
		
		// 파일명 수정 작업 후 서버로 업로드 
		// (파일의 이름을 바꿔서 서버 컴퓨터에 해당 파일을 다운로드 하기)
		// > 서버 컴퓨터의 어떤 폴더 안에 업로드된 파일들을 모두 다 저장하는 과정에서
		//   우연치 않게 동일한 이름의 파일 여러개가 업로드 될 수 있으므로!! 이름이 겹치지 않도록!!
		
		// 예) "bono.jpg" -> "2026051315345612345.jpg"
		// > 우리는 최대한 수정한 파일명들이 겹치지 않게끔
		//   년월일시분초 + 랜덤수5자리 + 확장자명 으로 파일명을 수정해줄 예정!!
		
		// 1. 원본파일명 뽑아오기
		String originName = upfile.getOriginalFilename(); // "bono.jpg"
		
		// 2. 시간 형식을 문자열로 뽑아내기
		String currentTime = new SimpleDateFormat("yyyyMMddHHmmss")
								.format(new Date()); // "20260513153954"
		
		// 3. 뒤에 붙을 5자리 랜덤수 뽑기
		// > 10000 ~ 99999 사이로
		int ranNum = (int)(Math.random() * 90000 + 10000); // 13159
		
		// 4. 원본파일명으로부터 확장자명 뽑기
		String ext = originName.substring(originName.lastIndexOf(".")); // ".jpg"
		// > 혹시라도 파일명 중간에 .이 있을 수도 있어서 마지막 .의 위치 기준으로 substring
		
		// 5. 2 + 3 + 4 모두 이어 붙이기
		String changeName = currentTime + ranNum + ext;
		// > "2026051315395413159.jpg"
		
		// 6. 업로드 하고자 하는 서버 폴더의 물리적인 경로를 알아내기
		// > 이 프로젝트 폴더 내부에 저장할 것!! 
		//   (이 프로젝트 1개 == 웹사이트 1개 == applicationScope)
		// > applicationScope 내장객체 (ServletContext 타입) 로부터 저장할 경로를 알아내기
		// > application 내장객체는 session 으로 부터 얻어낼 수 있다!!
		
		// 업로드된 파일들은 webapp 폴더의 resources/board_upfiles 폴더에 저장할 예정!!
		String savePath = session.getServletContext()
								 .getRealPath(path);
		// > 앞의 / 는 webapp 폴더를 나타내고, 뒤의 / 는 해당 폴더 "내부" 를 뜻함
		//   (폴더 안에 파일이 저장되므로)
		// > "C드라이브" 에서 부터 시작되는 board_upfiles 폴더의 풀 경로 (절대경로)
		// > 일반게시판, 사진게시판 등 게시판 종류마다 첨부파일의 경로를 다르게 저장할 예정!!
		
		// 7. 경로와 수정파일명 합체 후 파일 업로드 하기
		try {
			upfile.transferTo(new File(savePath + changeName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		// > 실제 파일이 업로드 된다!!
		
		return changeName;
	}
	// > 일반게시글 작성, 일반게시글 수정, 사진게시글 작성 및 수정 등등에서 계속 쓰일 수 있음!!
	
}
