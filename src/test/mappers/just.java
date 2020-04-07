package test.mappers;

import javax.print.DocFlavor.STRING;

import java.util.HashMap;

public class just {

	public static void main(String[] args) {
		HashMap<String, String> map = new HashMap<String,String>();
		map.put("0", "20 6 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-dashboardJob.jar dashboardJob\\r\\n");
		map.put("1", "0 9 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-autoCharge.jar autoChargeJob\\r\\n");
		map.put("2", "c");
		map.put("3", "d");
		System.out.println(map.toString());
	}

}
