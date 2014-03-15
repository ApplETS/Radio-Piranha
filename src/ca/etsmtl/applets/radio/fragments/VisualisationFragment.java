package ca.etsmtl.applets.radio.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import ca.etsmtl.applets.utils.IcyStreamMeta;
import ca.etsmtl.applets.utils.Utils;
import ca.etsmtl.applets.visualizer.VisualizerView;
import ca.etsmtl.applets.visualizer.renderer.BarGraphRenderer;
import ca.etsmtl.applets.visualizer.renderer.CircleBarRenderer;
import ca.etsmtl.applets.visualizer.renderer.CircleRenderer;
import ca.etsmtl.applets.visualizer.renderer.LineRenderer;
import ca.etsmtl.applets.radio.AppRadioActivity;
import ca.etsmtl.applets.radio.R;

import ca.etsmtl.applets.radio.R.id;
import ca.etsmtl.applets.radio.R.layout;
import ca.etsmtl.applets.radio.R.raw;
import ca.etsmtl.applets.radio.RadioMusicService;

/**
 * Created by Phil on 27/09/13.
 */
public class VisualisationFragment extends Fragment {
    private MediaPlayer mPlayer;
    private VisualizerView mVisualizerView;
    private IcyStreamMeta streamMeta;
	private MetadataTask2 metadataTask2;
	private boolean isInFront =false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.audio_visualizer, container, false);

        // We need to link the visualizer view to the media player so that
        // it displays something
        mVisualizerView = (VisualizerView) v.findViewById(R.id.visualizerView);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        // Start with just line renderer
        addLineRenderer();

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
     	getActivity().setProgressBarIndeterminateVisibility(true);
        isInFront=true;
    
    }

    
    @Override
    public void onResume() {
	
	super.onResume();
	isInFront = true;
	new Thread(new Runnable() {  
		@Override
		public void run() {
			final AppRadioActivity activity = (AppRadioActivity)getActivity();
			while (isInFront){
			

				RadioMusicService mService = activity.getService();
				if(mService!=null){
				      
					if(mPlayer==null){
						mPlayer = mService.getMediaPlayer();
					}
					 
					if( (mPlayer!=null) && mPlayer.isPlaying()){
						if(!mVisualizerView.isLink()){
							mVisualizerView.link(mPlayer);
							activity.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									activity.setProgressBarIndeterminateVisibility(
											false);
								}
							});
						}
					}
					if(Utils.connectedToInternet(getActivity())){
					streamMeta = new IcyStreamMeta();
					try {
						streamMeta.setStreamUrl(new URL(getString(R.string.stream)));
						metadataTask2=new MetadataTask2();
						metadataTask2.execute(new URL(getString(R.string.stream)));
					} catch (MalformedURLException e) {  				
						e.printStackTrace();
					}
					  try {
					        Thread.sleep(3000);
					    } catch (InterruptedException e) {
					        return;
					    } catch (Exception e) {
					        return;
					    }
					}
				}
			}
		}
	}).start();

}
    @Override
    public void onPause() {
        cleanUp();
        super.onPause();
        isInFront=false;
    }

    @Override
    public void onDestroy() {
        cleanUp();
        super.onDestroy();
    }

    private void cleanUp() {
        if (mPlayer != null) {
            mVisualizerView.release();
            isInFront=false;
        }
    }

    // Methods for adding renderers to visualizer
    private void addBarGraphRenderers() {
        Paint paint = new Paint();
        paint.setStrokeWidth(50f);
        paint.setAntiAlias(true);

        paint.setColor(Color.argb(200, 56, 138, 252));
        BarGraphRenderer barGraphRendererBottom = new BarGraphRenderer(16, paint, false);
        mVisualizerView.addRenderer(barGraphRendererBottom);

        Paint paint2 = new Paint();
        paint2.setStrokeWidth(12f);
        paint2.setAntiAlias(true);
        paint2.setColor(Color.argb(200, 181, 111, 233));
        BarGraphRenderer barGraphRendererTop = new BarGraphRenderer(4, paint2, true);
        mVisualizerView.addRenderer(barGraphRendererTop);
    }

    private void addCircleBarRenderer() {
        Paint paint = new Paint();
        paint.setStrokeWidth(8f);
        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN));
        paint.setColor(Color.argb(255, 222, 92, 143));
        CircleBarRenderer circleBarRenderer = new CircleBarRenderer(paint, 32, true);
        mVisualizerView.addRenderer(circleBarRenderer);
    }

    private void addCircleRenderer() {
        Paint paint = new Paint();
        paint.setStrokeWidth(3f);
        paint.setAntiAlias(true);
        paint.setColor(Color.argb(255, 222, 92, 143));
        CircleRenderer circleRenderer = new CircleRenderer(paint, true);
        mVisualizerView.addRenderer(circleRenderer);
    }

    private void addLineRenderer() {
        Paint linePaint = new Paint();
        linePaint.setStrokeWidth(1f);
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.argb(88, 0, 128, 255));

        Paint lineFlashPaint = new Paint();
        lineFlashPaint.setStrokeWidth(5f);
        lineFlashPaint.setAntiAlias(true);
        lineFlashPaint.setColor(Color.argb(188, 255, 255, 255));
        LineRenderer lineRenderer = new LineRenderer(linePaint, lineFlashPaint, true);
        mVisualizerView.addRenderer(lineRenderer);
    }

    // Actions for buttons defined in xml
    public void startPressed(View view) throws IllegalStateException, IOException {
        if (mPlayer.isPlaying()) {
            return;
        }
        mPlayer.prepare();
        mPlayer.start();
    }

    public void stopPressed(View view) {
        mPlayer.stop();
    }

    public void barPressed(View view) {
        addBarGraphRenderers();
    }

    public void circlePressed(View view) {
        addCircleRenderer();
    }

    public void circleBarPressed(View view) {
        addCircleBarRenderer();
    }

    public void linePressed(View view) {
        addLineRenderer();
    }

    public void clearPressed(View view) {
        mVisualizerView.clearRenderers();
    }
    
    protected class MetadataTask2 extends AsyncTask<URL, Void, IcyStreamMeta> 
    {
        @Override
        protected IcyStreamMeta doInBackground(URL... urls) 
        {
            try 
            {
                streamMeta.refreshMeta();
                Log.e("Retrieving MetaData","Refreshed Metadata");
            } 
            catch (IOException e) 
            {
                Log.e(MetadataTask2.class.toString(), e.getMessage());
            }
            return streamMeta;
        }

        @Override
        protected void onPostExecute(IcyStreamMeta result) 
        {
       
                Activity activity = getActivity();
                if(activity !=null){
                 try 
			     {
                	final String title_artist=streamMeta.getStreamTitle();
                	activity.runOnUiThread( new Runnable() {
						
						@Override
						public void run() {
						
							View v = getView();
							if(v!=null){
								  
					
				                Log.v("Retrieved title_artist","Retrieved title_artist " + title_artist);
								TextView title = (TextView)getView().findViewById(R.id.visualizer_title);
								TextView artiste = (TextView)getView().findViewById(R.id.visualizer_artiste);
								if(title_artist!=null && !title_artist.equals("")){
									String[] elem= title_artist.split("-",2);
									
									title.setText(elem[1]);
									artiste.setText(elem[0]);
								}else{
									title.setText("");
								}
						          
							}
						}
					});
			     }
				catch (IOException e) 
	            {
	                Log.e(MetadataTask2.class.toString(), e.getMessage());
	            }
            }
                
        }
         
    }
      
 
    
}
