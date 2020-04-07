package test.mapper.pipe;

public interface PipeContext {
	public Stage next(int index);
	public void finish(int index);
}
