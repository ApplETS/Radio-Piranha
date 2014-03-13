package ca.etsmtl.applets.radio;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer.TrackInfo;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
/**
 * Service pour permettre à la musique qui continue à jouer même si l'écran est fermé.
 * @author Laurence
 *
 */
public class RadioMusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener{
	private static final String TAG = "RadioMusicService";
	private static String url = "http://radiopiranha.com:8000/radiopiranha.mp3";

	
	private AudioManager am;
	private OnAudioFocusChangeListener afChangeListener;
	
	private MediaPlayer player = null;
	
	NotificationManager notificationManager;
	Notification notification;
	final int NOTIFICATION_ID =6948205;
	private IBinder mBinder = new LocalBinder();
	
	public class LocalBinder extends Binder{
		RadioMusicService getService(){
			return RadioMusicService.this;
		}
	}
	
	enum State{
		Retrieving,
		Stopped,
		Preparing,
		Playing,
		Paused,
		AUDIOFOCUS_REQUEST_GRANTED
	}
	
	State mState = State.Retrieving;
	
	@Override
	public void onCreate() {
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
	}

	public RadioMusicService() {
	}
	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	
			player = new MediaPlayer();
			player.setOnPreparedListener(this);
			player.setOnErrorListener(this);
			player.setAudioStreamType(AudioManager.STREAM_MUSIC);
			initMediaPlayer();
			
		return START_STICKY;
	}
	
	private void initMediaPlayer(){
		
		
		am = (AudioManager) this.getSystemService(getApplicationContext().AUDIO_SERVICE);

		afChangeListener = new OnAudioFocusChangeListener() {
			@Override
			public void onAudioFocusChange(int focusChange) {
				if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
					final int volume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
					am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
					
						Timer t = new Timer();
						TimerTask timerTask = new TimerTask() {
							
							@Override
							public void run() {
								am.setStreamVolume(AudioManager.STREAM_MUSIC,volume, 0);
							}
						};
						t.schedule(timerTask, 1000L);
					
				} else if (focusChange == AudioManager.AUDIOFOCUS_GAIN
						|| focusChange == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT) {

			
			 if(mState == State.Retrieving){
					am.setStreamVolume(AudioManager.STREAM_MUSIC, 5, 0);
					player.start();					mState = State.Playing;
			 }
				} else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {

					am.abandonAudioFocus(afChangeListener);
					if (mState.equals(State.Playing)) {
						player.pause();
						mState = State.Paused;
					}
				} else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {

					if (mState.equals(State.Playing)) {
						player.pause();
						mState = State.Paused;
					}
				}
			}
		};
		

		try{
			player.setDataSource(url);
		}catch(IllegalArgumentException e){
			e.printStackTrace();
		}catch(IllegalStateException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		try{
			player.prepareAsync();
		}catch(IllegalStateException e){
			e.printStackTrace();
		}
		mState = State.Preparing;
	}
	
	@Override
	public void onDestroy() {
		if(player !=null){
			player.release();
		}
		mState = State.Retrieving;
	}
	
	public MediaPlayer getMediaPlayer(){
		return player;
	}
	public void pause(){
		if(mState.equals(State.Playing)){
			player.pause();
			mState = State.Paused;
			udpateNotification("(paused)");
		}
	}
	public void setVolume(){
		am = (AudioManager) this.getSystemService(getApplicationContext().AUDIO_SERVICE);
		int currentVolume  = am.getStreamVolume(AudioManager.STREAM_MUSIC);
		if(currentVolume == 0){
			am.setStreamVolume(AudioManager.STREAM_MUSIC, 2, 0);
		}
	}
	public void play(){
		if(!mState.equals(State.Preparing) && !mState.equals(State.Retrieving)){
			player.start();
			mState = State.Playing;
			udpateNotification("(playing)");
		}
	}
	
	public boolean isPlaying(){
		if(mState.equals(State.Playing)){
			return true;
		}
		return false;
	}
	
	
	public static void setUrl(String mUrl){
	 url = mUrl;
	}
	
	
	private void udpateNotification(String text){
	}
	
	private void setAsForeground(String text){
		PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(),AppRadioActivity.class),
				PendingIntent.FLAG_UPDATE_CURRENT);
		notification = new Notification();
		notification.tickerText = text;
		notification.icon = R.drawable.ic_launcher;
	//	notification.flags |= Notification.FLAG_ONGOING_EVENT;
		notification.flags |= Notification.FLAG_NO_CLEAR ;
		notification.setLatestEventInfo(getApplicationContext(), getResources().getString(R.string.app_name),
				text, pi);
		
		startForeground(NOTIFICATION_ID, notification);
	}
	
	

	
	@Override
	public boolean onError(MediaPlayer player, int what, int extra) {
		Toast.makeText(getApplicationContext(),
				"An Error occured, please try again later",
				Toast.LENGTH_SHORT).show();
		return true;
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		final int result = am.requestAudioFocus(afChangeListener,
				AudioManager.STREAM_MUSIC,
				AudioManager.AUDIOFOCUS_GAIN);

		if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
			mState = State.AUDIOFOCUS_REQUEST_GRANTED;
		    player.start();
		    mState = State.Playing;
			setAsForeground(getString(R.string.notification_text));
		}
	}

}
