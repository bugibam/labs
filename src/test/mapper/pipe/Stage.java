package test.mapper.pipe;

import java.util.concurrent.LinkedBlockingQueue;

public class Stage implements Runnable{

	public static final int MAX_QUE_CAPACITY = 500;

	protected LinkedBlockingQueue<Object> que;
	protected PipeContext ctx;
	protected int index;
	protected boolean stopFlag;


	public Stage() {
		System.out.println("STAGE 생성자");
		que = new LinkedBlockingQueue<Object>(MAX_QUE_CAPACITY);
	}

	public void setCtx(PipeContext ctx) {
		this.ctx = ctx;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void put(Object src) {
		try {
			System.out.println(index + " stage 큐 사이즈 : " +que.size());
			//큐가 90퍼 이상 찼을 때 스립
			if(MAX_QUE_CAPACITY - (MAX_QUE_CAPACITY/10) < que.size()) {
				Thread.currentThread().sleep(1000);		//
			}
			que.put(src);
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void finish() {
		System.out.println(index + " 스테이지 종료");
		ctx.finish(index);
	}

	public void next(Object src) {
		Stage next = ctx.next(index);
		if(next == null || src == null) return;

		next.put(src);
	}

	@Override
	public void run() {
		System.out.println(index + " 스테이지 가동중");
		while(true) {
			try {
				if(stopFlag || que.isEmpty()) break;

				Object obj = que.poll();

				if(obj == null) {
					System.out.println(index + "스테이지 쓰레드 슬립");
					Thread.currentThread().sleep(100);
					continue;
				}

				next(null);

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		finish();
	}

	public void start() {
		Thread th = new Thread(this);
		th.start();
	}

	public void stop() {
		System.out.println(index + " 스테이지 스톱 호출됨 큐 사이즈는 " + que.size());
		stopFlag = true;

	}

}
