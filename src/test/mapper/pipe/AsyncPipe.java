package test.mapper.pipe;

import java.nio.channels.Pipe;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class AsyncPipe implements PipeContext{

	private List<Stage> sList;
	private CountDownLatch latch;

	public AsyncPipe() {
		sList = new LinkedList<Stage>();
	}

	public void add(Stage stage) {
		stage.setCtx(this);
		stage.setIndex(sList.size());
		sList.add(stage);
	}

	@Override
	public Stage next(int index) {
		 if(index + 1 >= sList.size()) return null;

		return sList.get(index + 1);
	}

	@Override
	public void finish(int index) {
		//latch의 카운터에서 -1
		if(latch != null) latch.countDown();

		Stage nextStage = next(index);
		if(nextStage != null) nextStage.stop();
	}

	public Stage getStage(int index) {
		return sList.get(index);
	}

	public void put(Object src) {
		if(sList.isEmpty()) return;

		Stage firstStage = sList.get(0);
		firstStage.put(src);
	}

	public void start() {
		latch = new CountDownLatch(sList.size());

		for(Stage i : sList) {
			i.start();
		}
	}

	public void stop() {
		if(latch.getCount() == 0 )return ;
		if(sList.isEmpty()) return;
		Stage firstStage = sList.get(0);
		firstStage.stop();
	}

	public void await() {
		try {
			if(latch.getCount() == 0) return;
			latch.await();
			System.out.println("latch await end");
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AsyncPipe pipeline = new AsyncPipe();

		pipeline.add(new Stage());
		pipeline.add(new Stage());

		try {
			Thread.currentThread().sleep(1000);
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}

		pipeline.start();
		for(int i=0; i<10; i++) {
			pipeline.put(i+" input data ");
		}

		pipeline.stop();

		pipeline.await();
	}

}
