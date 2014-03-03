package ca.etsmtl.applets.radio.fragments;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import utils.XMLNewsParser;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ca.etsmtl.applets.radio.R;
import ca.etsmtl.applets.radio.models.News;
import ca.etsmtl.applets.radio.models.ObservableBundle;

public class FacebookPostFragment extends ListFragment implements Observer, OnItemClickListener {

	private static final String FACEBOOK_XML_RSS = "https://www.facebook.com/feeds/page.php?id=211557725590576&format=rss20";
	private ObservableBundle bundle;
	private ArrayAdapter<News> adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		bundle = new ObservableBundle();
		bundle.addObserver(this);

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		adapter = new NewsAdapter(getActivity(), new ArrayList<News>());

		setListAdapter(adapter);
		getActivity().setProgressBarIndeterminateVisibility(true);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					final SAXParser saxParser = SAXParserFactory.newInstance()
							.newSAXParser();

					XMLNewsParser parser = new XMLNewsParser(FACEBOOK_XML_RSS,
							new ArrayList<String>(), bundle);

					InputStream is = new URL(FACEBOOK_XML_RSS).openStream();
					saxParser.parse(is, parser);
					is.close();

				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				} catch (SAXException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}

			protected void onPostExecute(Void result) {
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						getActivity().setProgressBarIndeterminateVisibility(
								false);
					}
				});
			}
		}.execute();
	}

	public class NewsAdapter extends ArrayAdapter<News> {

		public NewsAdapter(Context context, List<News> objects) {
			super(context, R.layout.news_list_item, objects);
		}

		class ViewHolder {
			TextView title, date, description, logo;
		}

		private String source, title, description;

		@SuppressLint("SimpleDateFormat")
		private final SimpleDateFormat dateFormat = new SimpleDateFormat(
				"dd MMMMMMMMMM yyyy");

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = LayoutInflater.from(getContext()).inflate(
						R.layout.news_list_item, parent, false);

				holder.title = (TextView) convertView
						.findViewById(R.id.newsListItemTitle);
				holder.date = (TextView) convertView
						.findViewById(R.id.newsListItemDate);
				holder.description = (TextView) convertView
						.findViewById(R.id.newsListItemDescription);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			final News n = getItem(position);

			title = n.getTitle();
			Spanned titleSpanned = Html.fromHtml(title);
			if(titleSpanned.length()>1){
				holder.title.setText(Html.fromHtml(title));
			}else{
				((TextView) convertView
				.findViewById(R.id.newsListItemTitle)).setVisibility(View.GONE);
			}

			holder.date.setText(dateFormat.format(n.getPubDate()));

			description = n.getDescription();
			if (description.length() > 200) {
				Spanned desc = Html.fromHtml(description.substring(
						0, 180));
				if(desc.length()>1){
					holder.description.setText(desc);
				}else{
					holder.description.setText(getString(R.string.new_image));
				}
			} else {
				holder.description.setText(Html.fromHtml(description));
			}

			return convertView;
		}

		@Override
		public void add(News object) {
			super.add(object);
		}
	}
	
	@Override
	public void update(Observable o, final Object obj) {
		if (obj instanceof News) {
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {
					final News n = (News) obj;
					adapter.add(n);
					adapter.notifyDataSetChanged();
					getListView().setOnItemClickListener(FacebookPostFragment.this);
				}
			});
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		final News n = adapter.getItem(position);
		n.getLink();
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(n.getLink()));
		startActivity(intent);
		
	}
}
