package test.mapper.pipe;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class teps {

	public static void main(String[] args) throws InterruptedException {
		teps app = new teps();
		app.runWorkers();
	}

	private void runWorkers() throws InterruptedException {

		int MAX_Q = 5;
		CountDownLatch countDownLatch = new CountDownLatch(MAX_Q);
		Thread th;
		for(int i=0; i< MAX_Q; i++) {
			th = new Thread(new Worker(i, countDownLatch));
			th.setName(i+"쓰쓰");
			th.start();
			MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
			System.out.println(i+"쓰쓰 >> USAGE : " + mem.getHeapMemoryUsage().toString());
		}
		System.out.println("쓰쓰 끝");
		countDownLatch.await();
		System.out.println("Done awaiting..");

	}

	public class Worker implements Runnable { // 쓰레드
		private CountDownLatch countDownLatch;
		private int index;

		public Worker(final int index, final CountDownLatch countDownLatch) {
			this.index = index;
			this.countDownLatch = countDownLatch;
		}

		@Override
		public void run() {
			Long start = System.currentTimeMillis();
			StringBuilder sb = new StringBuilder();
			int max = new Random().nextInt(10);
			try {
				System.out.println(index +"쓰레드 시작...");
				for(int i=0; i < max; i++) {
					sb.append(i + ",");
					Thread.sleep(2000);
				}
				sb.append("끝");
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				String t = String.valueOf(System.currentTimeMillis() - start);
				String rst = String.format("[%s m/s] [%s 쓰레드 종료], [루프] : %d, [결과] : %d",t,Thread.currentThread().getName(),max,sb.length());
				System.out.println(rst);
				countDownLatch.countDown();
			}
		}
	}
}
