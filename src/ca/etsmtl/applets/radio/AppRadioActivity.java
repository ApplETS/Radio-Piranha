package ca.etsmtl.applets.radio;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
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
import android.view.Window;
import android.widget.Toast;
import ca.etsmtl.applets.radio.M3UParser.M3UHolder;
import ca.etsmtl.applets.radio.fragments.AboutFragment;
import ca.etsmtl.applets.radio.fragments.FacebookPostFragment;
import ca.etsmtl.applets.radio.fragments.WebFragment;
import ca.etsmtl.applets.radio.models.CurrentCalendar;

@SuppressLint("DefaultLocale")
public class AppRadioActivity extends FragmentActivity {
	private static final CharSequence LOADING = "Loading please wait";
	private static final CharSequence EMPTY_TITLE = "";
	protected CurrentCalendar currentCalendar;
	private MenuItem itemPlayPause;
	private M3UParser mu3Parser;
	
	private boolean musicThreadFinished;
	private RadioMusicService mService;
	private boolean mBound;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		setContentView(R.layout.main);
		String stream = getString(R.string.stream);
		String m3u = getString(R.string.m3u);

		new MyM3UTask(getApplicationContext()).execute(m3u);
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());
		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("iwasalreadyplaying", "ok");
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		return;
	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	protected void onStart() {		
		super.onStart();
		if(!isMyServiceRunning()){
			Intent intent = new Intent(this,RadioMusicService.class);
			bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
			startService(intent);
		}else{
			Intent intent = new Intent(this,RadioMusicService.class);
			bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		}
	}
	@Override
	protected void onStop() {
		musicThreadFinished = true;
		super.onStop();
		if(mBound){
			unbindService(mConnection);
			mBound=false;
		}
//		am.abandonAudioFocus(afChangeListener);
	}
	
	private void runThreadForPlayer(){
		musicThreadFinished = false;
		new Thread(new Runnable() {  
		    @Override
		    public void run() {
		    	  Log.v("AppRadioActivity", "AppRadioActivity musicThreadFinished="+musicThreadFinished);
		        while (!musicThreadFinished) {
		            try {
		                Thread.sleep(1000);
		            } catch (InterruptedException e) {
		                return;
		            } catch (Exception e) {
		                return;
		            }
		            Log.v("AppRadioActivity", "AppRadioActivity thread running.");
		            runOnUiThread(new Runnable() {
		                @Override
		                public void run() {
		                	if(mBound){
			                    if ( mService.isPlaying()) {
			                    	itemPlayPause.setIcon(android.R.drawable.ic_media_pause);
			                    } else {
			                    	itemPlayPause.setIcon(android.R.drawable.ic_media_play);
			                
			                    }
		                	}
		                }
		            });
		        }
		    }
		}).start();
	}
	
	@Override
	protected void onDestroy() {
		stopService(new Intent(this, RadioMusicService.class));
		super.onDestroy();
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
			if(mBound){
				if ( mService.getMediaPlayer() != null) {
					if (   mService.isPlaying()) {
						itemPlayPause.setIcon(android.R.drawable.ic_media_play);
						mService.pause();
					} else {
						Log.v("AppRadioActivity", "AppRadioActivity: play");
						itemPlayPause.setIcon(android.R.drawable.ic_media_pause);
						mService.play();
					}
				}else{
					Log.v("AppRadioActivity", "AppRadioActivity not mService.getMediaPlayer()="+mService.getMediaPlayer() );
				}
			}else{
				Log.v("AppRadioActivity", "AppRadioActivity not bound");
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
			} else if (position == 1) {
				fragment = new AboutFragment();
			} else {
				fragment = new FacebookPostFragment();
			}

			final Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);

			fragment.setArguments(args);

			return fragment;
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.title_section2).toUpperCase();
			case 1:
				return getString(R.string.title_section3).toUpperCase();
			case 2:
				return getString(R.string.title_section4).toUpperCase();
			}
			return null;
		}
	}
	
	private boolean isMyServiceRunning(){
		ActivityManager activityManager=(ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for(RunningServiceInfo service: activityManager.getRunningServices(Integer.MAX_VALUE)){
			 if(RadioMusicService.class.getName().equals(service.service.getClassName())){
				 return true;
			 }
		}
		return false;
	}
	
	
	private ServiceConnection mConnection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
			//Toast.makeText(AppRadioActivity.this, "Deconnecté", Toast.LENGTH_SHORT).show();
			mBound = false;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mService =((RadioMusicService.LocalBinder)service).getService();
			//Toast.makeText(AppRadioActivity.this, "Connecté", Toast.LENGTH_SHORT).show();
			//mService.setVolume();
			runThreadForPlayer();
			mBound = true;
			
			
		}
	};
	

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

	class MyM3UTask extends AsyncTask<String, Void, Void> {

		private Context c;
		private String url;

		public MyM3UTask(Context c) {
			this.c = c;
		}

		@SuppressWarnings("unused")
		@Override
		protected Void doInBackground(String... params) {

			this.url = params[0];
			writeFileToInternalStorage();

			mu3Parser = new M3UParser(c);
			try {
				M3UHolder m3uHolder = mu3Parser.parseFile();
				if (m3uHolder != null) {
					String field0 = m3uHolder.getName(0);
				}
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			return null;
		}

		private void writeFileToInternalStorage() {
			String eol = System.getProperty("line.separator");
			ReadableByteChannel rbc = null;
			try {

				URL website;
				try {
					website = new URL(url);
					rbc = Channels.newChannel(website.openStream());
					openFileOutput("radio_m3u.m3u", MODE_WORLD_WRITEABLE)
							.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
					rbc.close();
					website = null;

				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			}
		}
	}
	
}