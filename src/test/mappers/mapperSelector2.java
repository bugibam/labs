package test.mappers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class mapperSelector2 {
	final static String destination = "D:/DETECTOR/";
	final static String mapper_home = "D:/backOffice/works/localpay_backoffice/src/main/resources/com/kt/localpay/backoffice/sqlmap/";
	final static String java_home = "D:/backOffice/works/localpay_backoffice/src/main/java/com/kt/localpay/backoffice/server/mapper/";

	final static String SLVAE_MAPPER = "SlaveMapper";
	
	static int index = 1;
	static int javIndex = 1;

	public static void main(String[] args) throws IOException {
		HashMap<String,String> anos = getAnos(java_home);
		String rst = getMappers(anos);
		writeFiles(rst);
	}

	public static void writeFiles(String param) throws IOException {
		File deshome = new File(destination);
		FileOutputStream fos = null;
		try {
			if(!deshome.exists()) deshome.mkdirs();
			Thread.sleep(500);

			if(!deshome.exists()) {
				throw new Exception("결과 파일을 저장할 경로폴더가 생성이 안됨!!");
			}
			fos = new FileOutputStream(new File(destination,"RESULT.txt"));
			fos.write(param.getBytes());
			System.out.println("정상종료.>>" + destination + "/RESULT.txt");
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(fos != null) {
				fos.flush();
				fos.close();
			}
		}
	}

	public static HashMap<String,String> getAnos(String java_home){
		HashMap<String,String> rst = new HashMap<String, String>();
		BufferedReader br = null;
		File HOME = new File(java_home);
		File[] files = HOME.listFiles();
		String fileName ="";
		String exceptMapper = "";
			for(File item : files) {
				try {
					fileName = item.getName();
					exceptMapper = fileName.substring(0,
								fileName.indexOf("Mapper")
							);
					br = new BufferedReader(new InputStreamReader(new FileInputStream(item), StandardCharsets.UTF_8));
					String lineData = "";
					while ((lineData = br.readLine()) != null) {
						lineData.trim();
						if(!lineData.contains("Mapper(")) { continue;}	//매퍼 명시한곳 아니면 패스

						rst.put(exceptMapper.toUpperCase(),lineData.toUpperCase());
					}
				}catch (Exception e) {
					System.out.println(String.format("%s 파일을 읽는 도중 에러 발생 !! MSG : %s",fileName,e.getMessage()));
				}
				javIndex++;
			}
		return rst;
	}

	public static String getMappers(HashMap<String,String> anos) throws IOException {
		File home = new File(mapper_home);
		BufferedReader bufferedReader = null;
		StringBuilder sb = new StringBuilder();
		if(!home.canRead()) {
			System.out.println("못읽는다");
		}
		else {
			File[] all = home.listFiles();
			for(File item : all) {
				String fileName = item.getName();
				String fileNamewithOutExt = fileName.substring(0,fileName.indexOf("."));
				String newFileName = fileNamewithOutExt + SLVAE_MAPPER;

				String annotation = anos.getOrDefault(fileNamewithOutExt.toUpperCase(),"선언되지 않음");
				sb.append(String.format(
						"\n ------ [ %d번째,어노테이션 : %-20s,파일이름 : %-20s,SLVAE NAME : %-20s] ------ "
						,index,annotation,fileName,newFileName));
				if(!item.canRead()) {
					System.out.println(fileName + "못읽는다.");
				}
				//if(index ==5) break;
				bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(item), StandardCharsets.UTF_8));
				String lineData = "";
				while ((lineData = bufferedReader.readLine()) != null) {
					lineData.trim();
					if(!lineData.contains("id=")) { continue;}		// 쿼리 ID 선언줄 아니면 제외
					else if(lineData.contains("#")) { continue;}			// 쿼리 ID 선언줄 아니면 제외 EX) nid=#{nid} 이런식
					else if(lineData.contains("ON")) { continue;}
					else if(lineData.contains("<include")) { continue;}

					else if(		  lineData.contains("select id=")
							||lineData.contains("insert id=")
							||lineData.contains("delete id=")
							||lineData.contains("update id=")) {

						try {
							String sub = lineData.substring(
									lineData.indexOf(" id=")+4,
									lineData.lastIndexOf(">")
								);
						String[] spt = sub.split(" ");
						String pureName = spt[0].replace("\"", "");
							   pureName.replace("\'","");

						String isSelect = "[N]";

						if(lineData.contains("<select id=")) {
							// select 포함
							isSelect = "[Y]";
						}
						sb.append(String.format("\n\t-쿼리 이름: %-50s ,셀렉트인가? %s",pureName,isSelect));
						}catch (Exception e) {
							sb.append(String.format("\n[ERROR!!!] , [LINE] : %s [MSG] : %s\n",lineData,e.getMessage()));
						}
					}
				}
				if(bufferedReader != null) {
					bufferedReader.close();
				}
				index++;
				sb.append("\n");		//파일끝
			}		// end 파일 리스트 반복문
			sb.append("매퍼개수 : " + index + " 자바 개수 : " + javIndex);
		}
		return sb.toString();
	}
}
;
