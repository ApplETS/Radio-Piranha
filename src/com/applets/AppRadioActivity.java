package com.applets;

import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.applets.radio.R;

public class AppRadioActivity extends Activity {
	private static final CharSequence LOADING = "Loading please wait";
	private static final CharSequence EMPTY_TITLE = "";
	private MediaPlayer player;
	private ProgressDialog dialog;
//	private IntentFilter intentFilter = new IntentFilter(
//			AudioManager.ACTION_AUDIO_BECOMING_NOISY);
	private AudioManager am;
	private OnAudioFocusChangeListener afChangeListener;

	public class MediaListener implements OnErrorListener, OnPreparedListener,
			OnClickListener {

		// private MediaPlayer player;

		public MediaListener(MediaPlayer player) {
			// this.player = player;
		}

		@Override
		public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
			return true;
		}

		@Override
		public void onPrepared(MediaPlayer arg0) {
			int result = am.requestAudioFocus(afChangeListener,
					AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
			if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
				dialog.dismiss();
				player.start();
				findViewById(R.id.pause_btn).setClickable(true);
				((TextView) findViewById(R.id.main_artist_lbl))
						.setText("Coldplay");
				((TextView) findViewById(R.id.main_album_lbl))
						.setText("Viva La Vida Or Death And All His Friends");
				((TextView) findViewById(R.id.main_song_lbl))
						.setText("Death And All His Friends");
				Toast.makeText(getApplicationContext(),
						"Thanks for listening Radio Piranha", Toast.LENGTH_LONG)
						.show();
			}
		}

		@Override
		public void onClick(View v) {
			if (player.isPlaying()) {
				player.pause();
				Toast.makeText(v.getContext(), "PAUSED", Toast.LENGTH_LONG)
						.show();
			} else {
				player.start();
				Toast.makeText(v.getContext(), "STARTED", Toast.LENGTH_LONG)
						.show();
			}
			int ressource = (player.isPlaying()) ? android.R.drawable.ic_media_pause
					: android.R.drawable.ic_media_play;
			((ImageButton) v).setImageResource(ressource);
		}

	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		am = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

		afChangeListener = new OnAudioFocusChangeListener() {
			@Override
			public void onAudioFocusChange(int focusChange) {
				if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
					player.pause();
					am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
				} else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
					player.start();
				} else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
					// am.unregisterMediaButtonEventReceiver(RemoteControlReceiver);
					am.abandonAudioFocus(afChangeListener);
					player.stop();
				}
			}
		};
		
		
		player = new MediaPlayer();
		final MediaListener listener = new MediaListener(player);
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		player.setOnErrorListener(listener);
		player.setOnPreparedListener(listener);

		try {
			dialog = ProgressDialog.show(this, EMPTY_TITLE, LOADING, true);
			player.setDataSource(getString(R.string.stream2));
			player.prepareAsync();
		} catch (IllegalArgumentException e) {
			displayErrorToast();
		} catch (IllegalStateException e) {
			displayErrorToast();
		} catch (IOException e) {
			displayErrorToast();
		}


		findViewById(R.id.pause_btn).setOnClickListener(listener);

	}

	@Override
	protected void onPause() {
		super.onPause();
		// player.pause();
		// if (player != null) {
		// if (player.isPlaying()) {
		// player.stop();
		// ((ImageButton) findViewById(R.id.pause_btn))
		// .setImageResource(android.R.drawable.ic_media_play);
		// }
		// player.release();
		// }
	}

	@Override
	protected void onStop() {
		super.onStop();
		am.abandonAudioFocus(null);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		final MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.main_menu_lyrics:
			startActivity(new Intent(this, LyricsActivity.class));
			break;
		case R.id.main_menu_about:
			startActivity(new Intent(this, AboutActivity.class));
			break;
		default:
			startActivity(new Intent(this, SettingsActivity.class));
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void displayErrorToast() {
		Toast.makeText(this, R.string.error_msg, Toast.LENGTH_LONG).show();

	}

}