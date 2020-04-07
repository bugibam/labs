package test.main;

public class crontabParser {
	static final String EVERY = " 매";
	static final String AND = " 그리고 ";


	enum TIME_UNIT{
		DAY(4,"요일 ",7) {
			@Override
			String[] getUnitList() {
				String[] list = {"일요일","월요일","화요일","수요일","목요일","금요일","토요일"};
				return list;
			}
		},
		MONTH(3,"월 ",12) {
			@Override
			String[] getUnitList() {
				String[] rst = new String[this.getSize()+1];
				for(int i=0; i<size+1; i++) {
					rst[i] = i + this.getKorSuffix();
				}
				return rst;
			}
		},
		DATE(2,"일 ",31) {
			@Override
			String[] getUnitList() {
				String[] rst = new String[this.getSize()+1];
				for(int i=0; i<size+1; i++) {
					rst[i] = i + this.getKorSuffix();
				}
				return rst;
			}
		},
		HOUR(1,"시 ",24) {
			@Override
			String[] getUnitList() {
				String[] rst = new String[this.getSize()+1];
				for(int i=0; i<size+1; i++) {
					rst[i] = i + this.getKorSuffix();
				}
				return rst;
			}
		},
		MIN(0,"분 ",60) {
			@Override
			String[] getUnitList() {
				String[] rst = new String[this.getSize()+1];
				for(int i=0; i<size+1; i++) {
					rst[i] = i + this.getKorSuffix();
				}
				return rst;
			}
		};

		int index = 0; String korSuffix = ""; int size = 0;
		private TIME_UNIT(int index, String korSuffix, int size) {
			this.index = index; this.korSuffix = korSuffix; this.size = size;
		}
		public int getIndex() {return this.index;}
		public String getKorSuffix() {return this.korSuffix;}
		public int getSize() {return this.size;}
		abstract String[] getUnitList();
	}

	//
	/***
	 * 걍 char로 하나하나 짤라서  치환하는식이었으면?
	 * replace써서 바꾸는?
	*/

	public static void main(String[] args) throws Exception{
		String[] map2 = crontabParser.tmp.split("\n");
		String[] map1 = {"0 22 1-2,5-6,8-10,11-12 * 1-4,6,0 이거실행이거이거"};
		for(String item : map2) {
			String[] cut = item.split(" ");
			String rst = cut[0] +" "
						+cut[1] +" "
						+cut[2] +" "
						+cut[3] +" "
						+cut[4] +" "
						+cut[5];
			gogo(rst);
		}

	}

	public static void gogo(String param) {

		//0     1  2  3  4        분 시 일 월 요일
		String[] splitArgs = param.split(" ");
		StringBuilder sb = new StringBuilder();
		int size = splitArgs.length;
		for(int i= size; i>0 ; i--) {
			sb.append(parse(i-1,splitArgs[i-1]));
		}
		sb.append("마다 명령문 실행");
		System.out.println("<<<<<<<<<<<<< [ RESULT ] >>>>>>>>>>>>>>>> \n"
					+ param +"\n"
					+ sb.toString());
	}

	public static String parse(int index, String CHAR) {
		StringBuilder sb = new StringBuilder();
		TIME_UNIT unit =  null;
		switch (index) {
			case 4:		//요일
				unit = TIME_UNIT.DAY;
				sb.append(builder(unit, CHAR));
				break;
			case 3:		//월
				unit = TIME_UNIT.MONTH;
				sb.append(builder(unit, CHAR));
				break;
			case 2:		//일
				unit = TIME_UNIT.DATE;
				sb.append(builder(unit, CHAR));
				break;
			case 1:		//시
				unit = TIME_UNIT.HOUR;
				sb.append(builder(unit, CHAR));
				break;
			case 0:		//분
				unit = TIME_UNIT.MIN;
				sb.append(builder(unit, CHAR));
				break;
			default:

				break;
		}
		return sb.toString();
	}

	public static boolean isWildCard(String CHAR) {
		return CHAR.contains("*");
	}

	//EX> 10-12 시, 10시부터 12시까지 (매분마다, 매시마다, 15분45분에)
	public static String fromAtoB(String[] unitList,String param) {
		int hyphenIndex = param.indexOf("-");
		String FROM = param.substring(0,hyphenIndex);
		int intFROM  = Integer.valueOf(FROM);
		String korFROM = unitList[intFROM];

		String TO = param.substring(hyphenIndex+1,param.length());
		int intTO = Integer.valueOf(TO);
		String korTO = unitList[intTO];
		return korFROM + " 부터 " + korTO + " 까지 ";
	}

	public static String builder(TIME_UNIT unit, String param) {
		String[] unitList 	= unit.getUnitList();
		String	 suffix		= unit.getKorSuffix();

		StringBuilder sb = new StringBuilder();
		if(isWildCard(param)) {											// 모든 일자를 포함하는 *
			sb.append(EVERY);
			if(param.contains("/")) {									// 특별한 주기를 표현하는 * (TODO 짝수일 표현법 추가해야함)
				int index = param.indexOf("/");
				String pattern = param.substring(index+1,index+2);		// 주기를 뽑아내서 n주기로를 붙인다
				sb.append(pattern);
			}
			sb.append(suffix);
		}
		else if(param.contains(",")) {										// 특정일자를 표현하는 ,
			String[] list = param.split(",");								// EX> 1,2,5-6,7 크게 콤마로 짜르고 하나의 아이템을 처리
			for(int i=0; i<list.length; i++) {
				String item = list[i];
				if(i !=0 ) sb.append(AND);
				if(item.contains("-")) {								// 각 아이템에 특정기간이 포함되어 있나?
					sb.append(fromAtoB(unitList,param));
				}
				else {
					int val = Integer.valueOf(item);
					sb.append(unitList[val]);
				}

			}
		}
		else if(param.contains("-")) {
			sb.append(fromAtoB(unitList,param));
		}
		else {
			int val = Integer.valueOf(param);
			sb.append(unitList[val]);
		}
		return sb.toString();
	}



	static String tmp = "*/5 17-18 * * 1-5 java -jar /home/batch/app/approvalBatch/localpay-batch-bsRcvTxReconcJob.jar bsRcvTxReconcJob PKT03212\r\n" +
			"*/5 17-18 * * 1-5 java -jar /home/batch/app/approvalBatch/localpay-batch-bsRcvTxReconcJob.jar bsRcvTxReconcJob PKT03222\r\n" +
			"50 4 * * * crontab -l > /home/batch/app/approvalBatch/crontab_bak.txt\r\n" +
			"* * * * * java -jar /home/batch/app/approvalBatch/localpay-batch-publishCancel.jar publishCancelJob\r\n" +
			"0 2 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-userExpire.jar userExpireJob\r\n" +
			"0 1 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-presentAutoReject.jar presentAutoRejectJob\r\n" +
			"30 0 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-bcComparison.jar bcComparisonJob beforeday\r\n" +
			"30 5 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-budgetComparison.jar budgetComparisonJob beforeday\r\n" +
			"0 3 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-statisticsInfoCollect.jar statisticsInfoCollectJob itemModifyInfoCollectJob\r\n" +
			"30 3 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-statisticsInfoCollect.jar statisticsInfoCollectJob payLogCollectJob\r\n" +
			"0 4 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-statisticsInfoCollect.jar statisticsInfoCollectJob refundInfoCollectJob\r\n" +
			"30 4 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-statisticsInfoCollect.jar statisticsInfoCollectJob refundInfoCompleteCollectJob\r\n" +
			"0 5 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-statisticsInfoCollect.jar statisticsInfoCollectJob itemModifyInfoAcceptCollectJob\r\n" +
			"30 5 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-statisticsInfoCollect.jar statisticsInfoCollectJob refundInfoCancelCollectJob\r\n" +
			"0 6 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-statisticsInfoCollect.jar statisticsInfoCollectJob companyInfoCollectJob\r\n" +
			"30 2 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-statisticsSum.jar statisticsSumJob moneyStatisticsJob\r\n" +
			"0 2 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-statisticsBalanceCollect.jar statisticsBalanceCollectJob\r\n" +
			"0 2 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-statisticsDepoWithSum.jar statisticsDepoWithSumJob\r\n" +
			"0 2 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-statisticsInsideSwitchSum.jar statisticsInsideSwitchSumJob\r\n" +
			"0 2 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-blockChainAmbassador.jar blockChainAmbassadorJob\r\n" +
			"0 2 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-settleAmbassador.jar settleAmbassadorJob\r\n" +
			"0 7 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-statisticsJoinSum.jar joinStatisticsJob\r\n" +
			"0 4 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-cardPublish.jar cardPublishJob\r\n" +
			"3 4 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-cardCompare.jar cardCompareJob\r\n" +
			"6 4 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-cardPuchaseCancel.jar cardPuchaseCancelJob\r\n" +
			"30 4 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-cardAmbassador.jar cardAmbassadorJob\r\n" +
			"0 22 * * 5 /home/batch/app/approvalBatch/cardFileBackup.sh\r\n" +
			"55 4 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-cardBalance.jar cardBalanceJob\r\n" +
			"30 6 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-hanacardBalance.jar hanacardBalanceJob\r\n" +
			"0 14 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-hanacardSettle.jar hanacardSettleJob\r\n" +
			"0 12 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-settleCashReceiptComparison.jar settleCashReceiptComparisonJob\r\n" +
			"0 23 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-settleCashReceipt.jar settleCashReceiptJob\r\n" +
			"30 7 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-statisticsJoinSubStoreJob.jar statisticsJoinSubStoreJob\r\n" +
			"30 1 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-statisticsCurrencyIssue.jar statisticsCurrencyIssueJob\r\n" +
			"20 6 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-dashboardJob.jar dashboardJob\r\n" +
			"0 9 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-autoCharge.jar autoChargeJob\r\n" +
			"10 8 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-statisticsCardUsage.jar statisticsCardUsageJob\r\n" +
			"20 8 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-statisticsLocalCurrencyUse.jar statisticsLocalCurrencyUseJob\r\n" +
			"40 3 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-statisticsRewardCashJob.jar statisticsRewardCashJob\r\n" +
			"55 2 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-hanacardStoreJob.jar hanacardStoreJob\r\n" +
			"0 5 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-dormancyProcessingJob.jar dormancyProcessingJob\r\n" +
			"10,30,50 * * * * /home/BCGatorCli/bcgator.sh\r\n" +
			"0 4 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-bcCardPublishCollectDataProcess.jar bcCardPublishCollectDataProcessJob\r\n" +
			"30 4 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-bcCardCompareCollectDataProcess.jar bcCardCompareCollectDataProcessJob\r\n" +
			"6 4 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-bcCardPurchaseCancelCollectDataProcess.jar bcCardPurchaseCancelCollectDataProcessJob\r\n" +
			"55 4 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-bcCardSettleDataProcess.jar bcCardSettleDataProcessJob\r\n" +
			"30 6 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-bcCardSettleCollect.jar bcCardSettleCollectJob\r\n" +
			"55 2 * * * java -jar /home/batch/app/approvalBatch/localpay-batch-bcCardStoreCollect.jar bcCardStoreCollectJob\r\n" +
			"0 22 * * 5 /home/batch/app/approvalBatch/bcCardFileBackup.sh"+
			"0 22 1-2,5-6,8-10,11-12 * 5 이거실행 이거이거";
}
