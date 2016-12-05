package ui.supports;

public class FormEvent<T> {
	
	private T item;

	public T getItem() {
		return item;
	}

	public FormEvent(T item) {
		super();
		this.item = item;
	}
}
