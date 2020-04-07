package test.mappers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class mapperSelector {

	/**
	 * 자바 읽고,
	 * @throws IOException
	 *
	 * */


	public static void main(String[] args) throws IOException {
		String homePath = "D:/tmp/mapper/";
		File home = new File(homePath);

		BufferedReader bufferedReader = null;
		if(!home.canRead()) {
			System.out.println("못읽는다");
		}
		else {
			int index = 1;
//			int javaIndx = 1;
			File[] all = home.listFiles();
//			HashMap<String,String> anoMaps = new HashMap<>();
//			for(int i=0; i<all.length; i++) {
//				String fileName = all[i].getName();
//				if(fileName.contains("java")){
//					String pureName = fileName.substring(0,
//								fileName.indexOf("Mapper")
//							);
//					bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(all[i]), StandardCharsets.UTF_8));
//					String lineData = "";
//					while ((lineData = bufferedReader.readLine()) != null) {
//						lineData.trim();
//						if(!lineData.contains("Mapper(")) { continue;}	//매퍼 명시한곳 아니면 패스
//						else {
//
//							System.out.println("키 : " + pureName + "  선언 : " + lineData);
//							anoMaps.put(pureName, lineData);			//
//							break;
//						}
//					}
//					javaIndx++;
//				}
//			}
//@SumMapper("localStatsMapper")
			StringBuilder sb = new StringBuilder();
			for(File item : all) {
				String itemName = item.getName();

				//System.out.println("KEY : " + key + "  ANO : " + anos);
				sb.append(String.format(
						"\n -------------------------[ %d 번째,  파일이름 : %s] -------------------------"
						,index,itemName));
				try {
					//System.out.println("["+index +  " : " + item.getName() + "] >>>>>>>>> 가즈아. \n");
					if(!item.canRead()) {
						System.out.println(item.getName() + "못읽는다.");
					}
					//if(index ==5) break;
					bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(item), StandardCharsets.UTF_8));
					String lineData = "";
					while ((lineData = bufferedReader.readLine()) != null) {
						lineData.trim();
						if(!lineData.contains("id=")) { continue;}

						String sub = lineData.substring(
									lineData.indexOf("id=")+3,
									lineData.lastIndexOf(">")
								);
						String[] spt = sub.split(" ");
						String pureName = spt[0].replace("\"", "");
						String isSelect = "[N]";

						if(lineData.contains("<select id=")) {
							// select 포함
							isSelect = "[Y]";
						}

						sb.append(String.format("\n쿼리 이름: %-30s 셀렉트인가? %s",pureName,isSelect));
					}
					sb.append("\n");		//파일끝

				}catch (Exception e) {
					e.printStackTrace();
				}
				finally {
					if(bufferedReader != null) {
						bufferedReader.close();
					}
					System.out.println(sb.toString());
					index++;
				}

			}		// end list
			System.out.println("매퍼개수 : " + index);
		//	System.out.println(anoMaps.toString());

		}



	}

}
;
