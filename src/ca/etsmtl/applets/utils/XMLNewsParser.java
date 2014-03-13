package ca.etsmtl.applets.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import android.util.Log;
import ca.etsmtl.applets.radio.models.News;
import ca.etsmtl.applets.radio.models.ObservableBundle;

/**
 * Inspiré du tutoriel de la page suivante :
 * http://thibault-koprowski.fr/2010/10/15/tutoriel-parsing-xml-sous-android/
 * 
 * @author Rodrigo Manyari, Philippe David
 * @version 2.0
 */
public class XMLNewsParser extends XMLAbstractHandler {

	private static final String TAG = "ca.etsmtl.applets.etsmobile.tools.xml.XMLNewsParser";

	// Les champs d'une nouvelle (Selon le feed RSS).
	private static final String TITLE = "title";
	private static final String DESCRIPTION = "description";
	private static final String PUBDATE = "pubDate";
	private static final String GUID = "guid";
	private static final String LINK = "link";
	public final static String RSS_ETS = "rssETS";
	public final static String FACEBOOK = "facebook";
	public final static String TWITTER = "twitter";

	// Des variables qui nous permettent de faire le traitement.
	private final String source;

	private String title;

	private String date;

	private String description;

	private String guid;

	private String link;
	private final ArrayList<String> guids;
	private boolean inItem;
	private boolean isThere;
	private News news;

	private final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"EEE, d MMM yyyy HH:mm:ss z", Locale.CANADA);

	public XMLNewsParser(final String source, final ArrayList<String> guids,
			final ObservableBundle bundle) {
		super(bundle);
		this.source = source;
		this.guids = guids;
	}

	@Override
	public void endElement(final String uri, final String localName,
			final String qName) throws SAXException {

		// Quand on arrive à la fin d'un élément, la méthode characters a gardé
		// en mémoire tout le
		// texte entre les tags, à ce moment là, on fait juste prendre ce texte
		// et le rentrer dans
		// les champs appropriés.

		if (localName.equalsIgnoreCase(XMLNewsParser.TITLE) && inItem) {
			title = buffer.toString();
		}

		// Même chose pour les champs description et pubdate.
		if (localName.equalsIgnoreCase(XMLNewsParser.DESCRIPTION) && inItem) {
			description = buffer.toString();
		}
		if (localName.equalsIgnoreCase(XMLNewsParser.PUBDATE) && inItem) {
			date = buffer.toString();
		}
		if (localName.equalsIgnoreCase(XMLNewsParser.LINK) && inItem) {
			link = buffer.toString();
		}
		if (localName.equalsIgnoreCase(XMLNewsParser.GUID) && inItem) {
			guid = buffer.toString();
		}

		if (localName.equalsIgnoreCase("item") & inItem) {
			isThere = false;
			for (final String g : guids) {
				if (guid.equals(g)) {
					isThere = true;
				}
			}
			if (!isThere) {
				try {
					news = new News(title, description, guid, source,
							dateFormat.parse(date), link);
					bundle.setContent(news);
				} catch (final ParseException e) {
					Log.e(XMLNewsParser.TAG, e.toString());
				}
			}
			inItem = false;
		}
		buffer = null;
	}

	@Override
	public void startElement(final String uri, final String localName,
			final String qName, final Attributes attributes)
			throws SAXException {

		// On reinitialise le buffer à chaque fois qu'on trouver un nouveau tag
		// d'ouverture xml.
		buffer = new StringBuffer();

		// Si le tag est un "item", on crée un nouveau "news" vide et on indique
		// qu'on est dans un "item"
		if (localName.equalsIgnoreCase("item")) {
			inItem = true;
		}
	}
}