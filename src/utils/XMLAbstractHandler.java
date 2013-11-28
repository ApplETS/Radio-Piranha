package utils;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ca.etsmtl.applets.radio.models.ObservableBundle;

public abstract class XMLAbstractHandler extends DefaultHandler {

	protected ObservableBundle bundle;
	protected StringBuffer buffer;

	public XMLAbstractHandler(final ObservableBundle b) {
		bundle = b;
	}

	@Override
	public void characters(final char[] ch, final int start, final int length) throws SAXException {
		final String lecture = new String(ch, start, length);
		if (buffer != null) {
			buffer.append(lecture);
		}
	}

}