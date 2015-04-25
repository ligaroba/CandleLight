package com.marvik.apps.candle.light;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;


import com.startapp.android.publish.StartAppAd;
import com.startapp.android.publish.banner.Banner;

public class MainActivity extends Activity implements SensorEventListener{
	Sensor mSensor;
	SensorManager mSensorManager;
	int xAxis,yAxis,zAxis;
	ImageView ivCandleSkin,ivCandleFlame,ivCandleSmoke,ivCandleSkinSMoke;
	int x;
	Runnable runnable;
	Handler handler;
	Bitmap[] bitmaps;
	int count = 0;
	
	SharedPreferences prefs;
	
	Banner mBanner;
	
	int[] CANDLES_A = new int[] { R.drawable.candle_1a, R.drawable.candle_2a,
			R.drawable.candle_3a, R.drawable.candle_4a, R.drawable.candle_5a,
			R.drawable.candle_6a, R.drawable.candle_7a, R.drawable.candle_8a,
			R.drawable.candle_9a, R.drawable.candle_10a};
	
	int[] CANDLES_B = new int[] { R.drawable.candle_1b, R.drawable.candle_2b,
			R.drawable.candle_3b, R.drawable.candle_4b, R.drawable.candle_5b,
			R.drawable.candle_6b, R.drawable.candle_7b, R.drawable.candle_8b,
			R.drawable.candle_9b, R.drawable.candle_10b, };
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// StartApp
		String app_id = getString(R.string.start_app_app_id);
		String dev_id = getString(R.string.start_app_developer_id);
		StartAppAd.init(MainActivity.this, dev_id, app_id);

		init();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mSensorManager.unregisterListener(this);
		StartAppAd startAppAd = new StartAppAd(this);
		startAppAd.onPause();
		Log.i("onPAUSE", "Pausing");
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);
		StartAppAd startAppAd = new StartAppAd(this);
		startAppAd.onResume();
		Log.i("onRESUME", "Resuming");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId()==R.id.item_settings){
			startActivity(new Intent(getApplicationContext(), Settings.class));
			finish();
		}
		return super.onOptionsItemSelected(item);
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}
	private void init() {
	// TODO Auto-generated method stub
		
	//StartApp
	String app_id = getString(R.string.start_app_app_id);
	String dev_id = getString(R.string.start_app_developer_id);
	StartAppAd.init(MainActivity.this,dev_id ,  app_id);
	
	mBanner= (Banner)findViewById(R.id.startAppBanner);
	
				
	prefs = PreferenceManager.getDefaultSharedPreferences(this);
	
	handler = new Handler();
		
	mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
	mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
	
	
	PackageManager mPackageManager = getPackageManager();
	
	if(mPackageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER)){
			Log.i("SensorManager", "Present");
			mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);
		} else {
			Log.i("SensorManager", "Not-Found");
		}
	
	ivCandleFlame = (ImageView)findViewById(R.id.imageView_candle_flame);
	ivCandleSkin = (ImageView)findViewById(R.id.imageView_candle_skin);
	ivCandleSkin.setImageDrawable(getCandleSkin(R.drawable.skin1_candle));
	
	ivCandleSmoke = (ImageView)findViewById(R.id.imageView_candle_smoke);
	ivCandleSkinSMoke = (ImageView)findViewById(R.id.imageView_candle_skin_smoke);
	ivCandleSkinSMoke.setImageDrawable(getCandleSkin(R.drawable.skin1_candle));
	
	ivCandleSkinSMoke.setVisibility(ImageView.INVISIBLE);
	ivCandleSmoke.setVisibility(ImageView.INVISIBLE);
	
	ivCandleFlame.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			showSmoke();
			
			if(ivCandleFlame.isShown()){
				ivCandleFlame.setVisibility(ImageView.INVISIBLE);
				ivCandleSkin.setVisibility(ImageView.INVISIBLE);
				
				ivCandleSkinSMoke.setVisibility(ImageView.VISIBLE);
				ivCandleSmoke.setVisibility(ImageView.VISIBLE);
			}
			
		}
	});
	
	ivCandleSmoke.setOnClickListener(new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			if(ivCandleSmoke.isShown()){
				ivCandleFlame.setVisibility(ImageView.VISIBLE);
				ivCandleSkin.setVisibility(ImageView.VISIBLE);
				
				ivCandleSkinSMoke.setVisibility(ImageView.INVISIBLE);
				ivCandleSmoke.setVisibility(ImageView.INVISIBLE);
			}
		
		}
	});
	
	getSmokes();
	
	}
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		
		xAxis = (int) event.values[0];
		yAxis = (int) event.values[1];
		zAxis = (int) event.values[2];
		
		if (xAxis == 0) {
			ivCandleFlame.setImageDrawable(getCandle(R.drawable.candle_11));
		}
		if (xAxis ==1) {
			ivCandleFlame.setImageDrawable(getCandle(CANDLES_B[9]));
		}
		if (xAxis ==2) {
			ivCandleFlame.setImageDrawable(getCandle(CANDLES_B[8]));
		}
		if (xAxis ==3) {
			ivCandleFlame.setImageDrawable(getCandle(CANDLES_B[7]));
		}
		if (xAxis ==4) {
			ivCandleFlame.setImageDrawable(getCandle(CANDLES_B[6]));
		}
		if (xAxis ==5) {
			ivCandleFlame.setImageDrawable(getCandle(CANDLES_B[5]));
		}
		if (xAxis ==6) {
			ivCandleFlame.setImageDrawable(getCandle(CANDLES_B[4]));
		}
		if (xAxis ==7) {
			ivCandleFlame.setImageDrawable(getCandle(CANDLES_B[3]));
		}
		if (xAxis ==8) {
			ivCandleFlame.setImageDrawable(getCandle(CANDLES_B[2]));
		}
		if (xAxis ==9) {
			ivCandleFlame.setImageDrawable(getCandle(CANDLES_B[1]));
		}
		if (xAxis ==10) {
			ivCandleFlame.setImageDrawable(getCandle(CANDLES_B[0]));
		}
	
		if (xAxis ==-1) {
			ivCandleFlame.setImageDrawable(getCandle(CANDLES_A[9]));
			
		}
		if (xAxis ==-2) {
			ivCandleFlame.setImageDrawable(getCandle(CANDLES_A[8]));
			
		}
		if (xAxis ==-3) {
			ivCandleFlame.setImageDrawable(getCandle(CANDLES_A[7]));
			
		}
		if (xAxis ==-4) {
			ivCandleFlame.setImageDrawable(getCandle(CANDLES_A[6]));
			
		}
		if (xAxis ==-5) {
			ivCandleFlame.setImageDrawable(getCandle(CANDLES_A[5]));
			
		}
		if (xAxis ==-6) {
			ivCandleFlame.setImageDrawable(getCandle(CANDLES_A[4]));
			
		}
		if (xAxis ==-7) {
			ivCandleFlame.setImageDrawable(getCandle(CANDLES_A[3]));
		}
		if (xAxis ==-8) {
			ivCandleFlame.setImageDrawable(getCandle(CANDLES_A[2]));
		}
		if (xAxis ==-9) {
			ivCandleFlame.setImageDrawable(getCandle(CANDLES_A[1]));
		}
		if (xAxis ==-10) {
			ivCandleFlame.setImageDrawable(getCandle(CANDLES_A[0]));
		}
		
		Log.i("onSensorChanged", "X-AXIS" +xAxis +"\nY-AXIS" +yAxis +"\nZ-AXIS" +zAxis);
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	private Drawable getCandleSkin(int skinID){
		Drawable drawable = getResources().getDrawable(skinID);
		if(prefs.getBoolean(Settings.ENABLE_CUSTOMIZATIONS, false)){
			ColorFilter filter = new LightingColorFilter(Color.TRANSPARENT, getUserCandleColor());
			drawable.setColorFilter(filter);
		}
		return drawable;
	}
	private int getUserCandleColor() {
		// TODO Auto-generated method stub

		if(prefs.getBoolean(Settings.ENABLE_TRANSPARENCY, false)){
			return Color.argb(prefs.getInt(Settings.FLAME_ALPHA, 0),
					prefs.getInt(Settings.CANDLE_RED, 0), 
					prefs.getInt(Settings.CANDLE_GREEN, 0), 
					prefs.getInt(Settings.CANDLE_BLUE, 0));
		}else{
			return Color.rgb(
					prefs.getInt(Settings.CANDLE_RED, 0), 
					prefs.getInt(Settings.CANDLE_GREEN, 0), 
					prefs.getInt(Settings.CANDLE_BLUE, 0));
		}
	}
	private Drawable getCandle(int candleID){
		Drawable drawable = getResources().getDrawable(candleID);
		if(prefs.getBoolean(Settings.ENABLE_CUSTOMIZATIONS, false)){
			ColorFilter filter = new LightingColorFilter(Color.TRANSPARENT, getUserFlameColor());
			drawable.setColorFilter(filter);
		}
		return drawable;
	}

	private int getUserFlameColor() {
		// TODO Auto-generated method stub
		if(prefs.getBoolean(Settings.ENABLE_TRANSPARENCY, false)){
			return Color.argb(prefs.getInt(Settings.FLAME_ALPHA, 0),
					prefs.getInt(Settings.FLAME_RED, 0), 
					prefs.getInt(Settings.FLAME_GREEN, 0), 
					prefs.getInt(Settings.FLAME_BLUE, 0));
		}else{
			return Color.rgb(
					prefs.getInt(Settings.FLAME_RED, 0), 
					prefs.getInt(Settings.FLAME_GREEN, 0), 
					prefs.getInt(Settings.FLAME_BLUE, 0));
		}
	}
	
	private void getSmokes(){
		bitmaps = new Bitmap[21];
		bitmaps[0]=BitmapFactory.decodeResource(getResources(), R.drawable.smoke_1);
		bitmaps[1]=BitmapFactory.decodeResource(getResources(), R.drawable.smoke_2);
		bitmaps[2]=BitmapFactory.decodeResource(getResources(), R.drawable.smoke_3);
		bitmaps[3]=BitmapFactory.decodeResource(getResources(), R.drawable.smoke_4);
		bitmaps[4]=BitmapFactory.decodeResource(getResources(), R.drawable.smoke_5);
		bitmaps[5]=BitmapFactory.decodeResource(getResources(), R.drawable.smoke_6);
		bitmaps[6]=BitmapFactory.decodeResource(getResources(), R.drawable.smoke_7);
		bitmaps[7]=BitmapFactory.decodeResource(getResources(), R.drawable.smoke_8);
		bitmaps[8]=BitmapFactory.decodeResource(getResources(), R.drawable.smoke_9);
		bitmaps[9]=BitmapFactory.decodeResource(getResources(), R.drawable.smoke_10);
		bitmaps[10]=BitmapFactory.decodeResource(getResources(), R.drawable.smoke_1);
		bitmaps[11]=BitmapFactory.decodeResource(getResources(), R.drawable.smoke_2);
		bitmaps[12]=BitmapFactory.decodeResource(getResources(), R.drawable.smoke_3);
		bitmaps[13]=BitmapFactory.decodeResource(getResources(), R.drawable.smoke_4);
		bitmaps[14]=BitmapFactory.decodeResource(getResources(), R.drawable.smoke_5);
		bitmaps[15]=BitmapFactory.decodeResource(getResources(), R.drawable.smoke_6);
		bitmaps[16]=BitmapFactory.decodeResource(getResources(), R.drawable.smoke_7);
		bitmaps[17]=BitmapFactory.decodeResource(getResources(), R.drawable.smoke_8);
		bitmaps[18]=BitmapFactory.decodeResource(getResources(), R.drawable.smoke_9);
		bitmaps[19]=BitmapFactory.decodeResource(getResources(), R.drawable.smoke_10);
		bitmaps[20]=BitmapFactory.decodeResource(getResources(), R.drawable.smoke_1);
	}
	private void showSmoke(){
		runnable = new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for(int i=0;i<21;i++){
					
					x =i;
					try {
						Thread.sleep(100);
						handler.post(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								if(x==20){
									if(ivCandleSmoke.isShown()){
										ivCandleFlame.setVisibility(ImageView.VISIBLE);
										ivCandleSkin.setVisibility(ImageView.VISIBLE);
										
										ivCandleSkinSMoke.setVisibility(ImageView.INVISIBLE);
										ivCandleSmoke.setVisibility(ImageView.INVISIBLE);
									}
									//ivCandleSmoke.setImageBitmap(bitmaps[10]);
									
								}else{
									ivCandleSmoke.setImageBitmap(bitmaps[x]);
								}
							}
						});
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		};
		new Thread(runnable).start();
	}
}
