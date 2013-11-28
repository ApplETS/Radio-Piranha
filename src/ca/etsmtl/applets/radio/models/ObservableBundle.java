package ca.etsmtl.applets.radio.models;

import java.util.Observable;

public class ObservableBundle extends Observable {

	private Object content;

	public ObservableBundle() {
	}

	public ObservableBundle(final Object content) {
		setContent(content);
	}

	public void setContent(final Object content) {
		this.content = content;
		setChanged();
		notifyObservers(this.content);
	}
}