package utils;

public class ParsingResult<T> {
	public T result;
	public int bytesUsed;

	public ParsingResult(T content, int bytesUsed) {
		this.result = content;
		this.bytesUsed = bytesUsed;
	}
}
