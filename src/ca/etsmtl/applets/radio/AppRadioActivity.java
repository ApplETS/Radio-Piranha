package ca.etsmtl.applets.radio;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import ca.etsmtl.applets.radio.fragments.AboutFragment;
import ca.etsmtl.applets.radio.fragments.WebFragment;
import ca.etsmtl.applets.radio.models.CurrentCalendar;

@SuppressLint("DefaultLocale")
public class AppRadioActivity extends FragmentActivity {
	private static final CharSequence LOADING = "Loading please wait";
	private static final CharSequence EMPTY_TITLE = "";
	private MediaPlayer player;
	private ProgressDialog dialog;
	private AudioManager am;
	private OnAudioFocusChangeListener afChangeListener;
	protected CurrentCalendar currentCalendar;
	private MenuItem itemPlayPause;

	public class MediaListener implements OnErrorListener, OnPreparedListener {

		@Override
		public boolean onError(MediaPlayer player, int what, int extra) {
			dialog.dismiss();
			Toast.makeText(getApplicationContext(),
					"An Error occured, please try again later",
					Toast.LENGTH_SHORT).show();
			return true;
		}

		@Override
		public void onPrepared(MediaPlayer mp) {
			final int result = am.requestAudioFocus(afChangeListener,
					AudioManager.STREAM_MUSIC,
					AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);

			if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
				dialog.dismiss();
				player = mp;
				player.start();
				itemPlayPause.setIcon(android.R.drawable.ic_media_pause);

				Toast.makeText(getApplicationContext(),
						"Thanks for listening Radio Piranha", Toast.LENGTH_LONG)
						.show();
			}
		}

	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());
		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		if (savedInstanceState == null) {
			/**
			 * AUDIO PLAYER
			 */
			am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

			afChangeListener = new OnAudioFocusChangeListener() {
				@Override
				public void onAudioFocusChange(int focusChange) {

					Log.d("FOCUS CHANGE", focusChange + "");

					if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {

						if (player.isPlaying()) {
							player.pause();
						}
						am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
					} else if (focusChange == AudioManager.AUDIOFOCUS_GAIN
							|| focusChange == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT) {

						if (!player.isPlaying()) {
							player.start();
						}
					} else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {

						am.abandonAudioFocus(afChangeListener);
						if (player.isPlaying()) {
							player.pause();
						}
					} else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {

						if (player.isPlaying()) {
							player.pause();
						}
					}
				}
			};

			player = new MediaPlayer();
			final MediaListener listener = new MediaListener();
			player.setAudioStreamType(AudioManager.STREAM_MUSIC);
			player.setOnErrorListener(listener);
			player.setOnPreparedListener(listener);

			try {
				dialog = ProgressDialog.show(this, EMPTY_TITLE, LOADING, true);
				player.setDataSource(getString(R.string.stream));
				player.prepareAsync();
			} catch (final IllegalArgumentException e) {
				displayErrorToast();
			} catch (final IllegalStateException e) {
				displayErrorToast();
			} catch (final IOException e) {
				displayErrorToast();
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("iwasalreadyplaying", "ok");
		super.onSaveInstanceState(outState);
	}

	// @Override
	// public void onConfigurationChanged(Configuration newConfig) {
	// super.onConfigurationChanged(newConfig);
	// return;
	// }

	@Override
	protected void onPause() {
		super.onPause();
		if (player != null) {
			if (player.isPlaying()) {
				player.stop();
			}
			player.release();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (am != null) {
			am.abandonAudioFocus(afChangeListener);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		itemPlayPause = menu.getItem(0);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.main_menu_pause:
			if (player.isPlaying()) {
				itemPlayPause.setIcon(android.R.drawable.ic_media_play);
				player.pause();
			} else {
				itemPlayPause.setIcon(android.R.drawable.ic_media_pause);
				player.start();
			}
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void displayErrorToast() {
		Toast.makeText(this, R.string.error_msg, Toast.LENGTH_LONG).show();
	}

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = null;
			if (position == 0) {
				fragment = new WebFragment();
			} else {
				fragment = new AboutFragment();
			}

			final Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);

			fragment.setArguments(args);

			return fragment;
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.title_section2).toUpperCase();
			case 1:
				return getString(R.string.title_section3).toUpperCase();
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.about, null, false);
			return v;
		}
	}

}