package com.amap.location.findchildren;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationClientOption.AMapLocationProtocol;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationQualityReport;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.MyLocationStyle;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static com.amap.location.findchildren.RequestConnect.*;
import static java.nio.file.Paths.get;

/**
 * 高精度定位模式
 *
 * @创建时间： 2018年11月24日 下午5:22:42
 * @项目名称： FindYourChildren2.x
 * @author mo
 * @文件名称: Hight_Accuracy_Activity.java
 * @类型名称: Hight_Accuracy_Activity
 */
public class Location_Activity extends CheckPermissionsActivity
		implements
			OnCheckedChangeListener,
			OnClickListener{
	private RadioGroup rgLocationMode;
	private EditText etInterval;
	private EditText etHttpTimeout;
	private CheckBox cbOnceLocation;
	private CheckBox cbAddress;
	private CheckBox cbGpsFirst;
	private CheckBox cbCacheAble;
	private CheckBox cbOnceLastest;
	private CheckBox cbSensorAble;
	private TextView tvResult;
	private Button btLocation;

	private AMapLocationClient locationClient = null;
	private AMapLocationClientOption locationOption = null;
	private AMapLocation aMapLocation;
	private LocationSource.OnLocationChangedListener mListener;

	private Timer timer= null;
	private MapView mMapView;
	private AMap mAMap;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);
		setTitle(R.string.title_location);
		
		initView();
		mMapView = (MapView) findViewById(R.id.map);
		mMapView.onCreate(savedInstanceState);
		//初始化定位
		initLocation();

		btLocation.setText(getResources().getString(
				R.string.stopLocation));
		tvResult.setText("locating...");
		startLocation();
		String type = (String) SPUtils.get(getApplicationContext(),"type","");
		 timer = new Timer();
		if(type.contentEquals("children")){
			timer.schedule(new TimerTask() {
				@Override
				public void run() {

					if(aMapLocation != null){
						uploadLocationData(aMapLocation);
					}

				}
			},new Date(), 10000);
		}else if(type.contains("parent")){

		};

	}
	
	//初始化控件
	private void initView(){
		rgLocationMode = (RadioGroup) findViewById(R.id.rg_locationMode);
		
		etInterval = (EditText) findViewById(R.id.et_interval);
		etHttpTimeout = (EditText) findViewById(R.id.et_httpTimeout);
		
		cbOnceLocation = (CheckBox)findViewById(R.id.cb_onceLocation);
		cbGpsFirst = (CheckBox) findViewById(R.id.cb_gpsFirst);
		cbAddress = (CheckBox) findViewById(R.id.cb_needAddress);
		cbCacheAble = (CheckBox) findViewById(R.id.cb_cacheAble);
		cbOnceLastest = (CheckBox) findViewById(R.id.cb_onceLastest);
		cbSensorAble = (CheckBox)findViewById(R.id.cb_sensorAble);

		tvResult = (TextView) findViewById(R.id.tv_result);
		btLocation = (Button) findViewById(R.id.bt_location);
		
		rgLocationMode.setOnCheckedChangeListener(this);
		btLocation.setOnClickListener(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(timer != null){
			timer.purge();
			timer.cancel();
		}
		destroyLocation();
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (null == locationOption) {
			locationOption = new AMapLocationClientOption();
		}
		switch (checkedId) {
			case R.id.rb_batterySaving :
				locationOption.setLocationMode(AMapLocationMode.Battery_Saving);
				break;
			case R.id.rb_deviceSensors :
				locationOption.setLocationMode(AMapLocationMode.Device_Sensors);
				break;
			case R.id.rb_hightAccuracy :
				locationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
				break;
			default :
				break;
		}

	}

	/**
	 * 设置控件的可用状态
	 */
	private void setViewEnable(boolean isEnable) {
		for(int i=0; i<rgLocationMode.getChildCount(); i++){
			rgLocationMode.getChildAt(i).setEnabled(isEnable);
		}
		etInterval.setEnabled(isEnable);
		etHttpTimeout.setEnabled(isEnable);
		cbOnceLocation.setEnabled(isEnable);
		cbGpsFirst.setEnabled(isEnable);
		cbAddress.setEnabled(isEnable);
		cbCacheAble.setEnabled(isEnable);
		cbOnceLastest.setEnabled(isEnable);
		cbSensorAble.setEnabled(isEnable);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.bt_location) {
			if (btLocation.getText().equals(
					getResources().getString(R.string.startLocation))) {
				setViewEnable(false);
				btLocation.setText(getResources().getString(
						R.string.stopLocation));
				tvResult.setText("正在定位...");
				startLocation();
			} else {
				setViewEnable(true);
				btLocation.setText(getResources().getString(
						R.string.startLocation));
				stopLocation();
				tvResult.setText("定位停止");
			}
		}
	}
	
	/**
	 * 初始化定位
	 * 
	 * @since 2.8.0
	 * @author developer
	 *
	 */
	private void initLocation(){
		if (mAMap == null) {
			mAMap = mMapView.getMap();
			mAMap.getUiSettings().setRotateGesturesEnabled(false);
			mAMap.moveCamera(CameraUpdateFactory.zoomBy(6));
			setUpMap();
		}
		//初始化client
		locationClient = new AMapLocationClient(this.getApplicationContext());
		locationOption = getDefaultOption();
		//设置定位参数
		locationClient.setLocationOption(locationOption);
		// 设置定位监听
		locationClient.setLocationListener(locationListener);
	}

	private void setUpMap() {


		mAMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		// 自定义定位蓝点图标
		myLocationStyle.myLocationIcon(
				BitmapDescriptorFactory.fromResource(R.drawable.gps_point));
		// 自定义精度范围的圆形边框颜色
		myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
		// 自定义精度范围的圆形边框宽度
		myLocationStyle.strokeWidth(0);
		// 设置圆形的填充颜色
		myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));
		// 将自定义的 myLocationStyle 对象添加到地图上
		mAMap.setMyLocationStyle(myLocationStyle);
		mAMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		// 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
		mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
	}
	/**
	 * 默认的定位参数
	 * @since 2.8.0
	 * @author developer
	 *
	 */
	private AMapLocationClientOption getDefaultOption(){
		AMapLocationClientOption mOption = new AMapLocationClientOption();
		mOption.setLocationMode(AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
		mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
		mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
		mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
		mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
		mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
		mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
		AMapLocationClientOption.setLocationProtocol(AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
		mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
		mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
		mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
		return mOption;
	}
	
	/**
	 * 定位监听
	 */
	AMapLocationListener locationListener = new AMapLocationListener() {
		@Override
		public void onLocationChanged(AMapLocation location) {
			if (null != location) {

				StringBuffer sb = new StringBuffer();
				//errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
				if(location.getErrorCode() == 0){
					sb.append("定位成功，后台会自动上传定位数据..." + "\n");
					sb.append("定位类型: " + location.getLocationType() + "\n");
					sb.append("经    度    : " + location.getLongitude() + "\n");
					sb.append("纬    度    : " + location.getLatitude() + "\n");
					sb.append("国    家    : " + location.getCountry() + "\n");
					sb.append("省            : " + location.getProvince() + "\n");
					sb.append("市            : " + location.getCity() + "\n");
					sb.append("城市编码 : " + location.getCityCode() + "\n");
					sb.append("区            : " + location.getDistrict() + "\n");
					sb.append("区域 码   : " + location.getAdCode() + "\n");
					sb.append("地    址    : " + location.getAddress() + "\n");
					sb.append("兴趣点    : " + location.getPoiName() + "\n");
					//定位完成的时间
					sb.append("定位时间: " + Utils.formatUTC(location.getTime(), "yyyy-MM-dd HH:mm:ss") + "\n");
					sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
					sb.append("提供者    : " + location.getProvider() + "\n");

					sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
					sb.append("角    度    : " + location.getBearing() + "\n");
					// 获取当前提供定位服务的卫星个数
					sb.append("星    数    : " + location.getSatellites() + "\n");
				} else {
					//定位失败
					sb.append("定位失败" + "\n");
					sb.append("错误码:" + location.getErrorCode() + "\n");
					sb.append("错误信息:" + location.getErrorInfo() + "\n");
					sb.append("错误描述:" + location.getLocationDetail() + "\n");
				}
				sb.append("***定位质量报告***").append("\n");
				sb.append("* WIFI开关：").append(location.getLocationQualityReport().isWifiAble() ? "开启":"关闭").append("\n");
				sb.append("* GPS状态：").append(getGPSStatusString(location.getLocationQualityReport().getGPSStatus())).append("\n");
				sb.append("* GPS星数：").append(location.getLocationQualityReport().getGPSSatellites()).append("\n");
				sb.append("****************").append("\n");
				//定位之后的回调时间
				sb.append("回调时间: " + Utils.formatUTC(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + "\n");

				aMapLocation = location;
				//解析定位结果，
				String result = sb.toString();
				tvResult.setText(result);
			} else {
				tvResult.setText("定位失败，loc is null");
			}
		}
	};


	/**
	 * 获取GPS状态的字符串
	 * @param statusCode GPS状态码
	 * @return
	 */
	private String getGPSStatusString(int statusCode){
		String str = "";
		switch (statusCode){
			case AMapLocationQualityReport.GPS_STATUS_OK:
				str = "GPS状态正常";
				break;
			case AMapLocationQualityReport.GPS_STATUS_NOGPSPROVIDER:
				str = "手机中没有GPS Provider，无法进行GPS定位";
				break;
			case AMapLocationQualityReport.GPS_STATUS_OFF:
				str = "GPS关闭，建议开启GPS，提高定位质量";
				break;
			case AMapLocationQualityReport.GPS_STATUS_MODE_SAVING:
				str = "选择的定位模式中不包含GPS定位，建议选择包含GPS定位的模式，提高定位质量";
				break;
			case AMapLocationQualityReport.GPS_STATUS_NOGPSPERMISSION:
				str = "没有GPS定位权限，建议开启gps定位权限";
				break;
		}
		return str;
	}
	// 根据控件的选择，重新设置定位参数
	private void resetOption() {
		// 设置是否需要显示地址信息
		locationOption.setNeedAddress(cbAddress.isChecked());
		/**
		 * 设置是否优先返回GPS定位结果，如果30秒内GPS没有返回定位结果则进行网络定位
		 * 注意：只有在高精度模式下的单次定位有效，其他方式无效
		 */
		locationOption.setGpsFirst(cbGpsFirst.isChecked());
		// 设置是否开启缓存
		locationOption.setLocationCacheEnable(cbCacheAble.isChecked());
		// 设置是否单次定位
		locationOption.setOnceLocation(cbOnceLocation.isChecked());
		//设置是否等待设备wifi刷新，如果设置为true,会自动变为单次定位，持续定位时不要使用
		locationOption.setOnceLocationLatest(cbOnceLastest.isChecked());
		//设置是否使用传感器
		locationOption.setSensorEnable(cbSensorAble.isChecked());
		//设置是否开启wifi扫描，如果设置为false时同时会停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
		String strInterval = etInterval.getText().toString();
		if (!TextUtils.isEmpty(strInterval)) {
			try{
				// 设置发送定位请求的时间间隔,最小值为1000，如果小于1000，按照1000算
				locationOption.setInterval(Long.valueOf(strInterval));
			}catch(Throwable e){
				e.printStackTrace();
			}
		}
		
		String strTimeout = etHttpTimeout.getText().toString();
		if(!TextUtils.isEmpty(strTimeout)){
			try{
				// 设置网络请求超时时间
			     locationOption.setHttpTimeOut(Long.valueOf(strTimeout));
			}catch(Throwable e){
				e.printStackTrace();
			}
		}
	}

	/**
	 * 开始定位
	 * 
	 * @since 2.8.0
	 * @author developer
	 *
	 */
	private void startLocation(){
		//根据控件的选择，重新设置定位参数
		resetOption();
		// 设置定位参数
		locationClient.setLocationOption(locationOption);
		// 启动定位
		locationClient.startLocation();
	}
	
	/**
	 * 停止定位
	 * 
	 * @since 2.8.0
	 * @author developer
	 *
	 */
	private void stopLocation(){
		// 停止定位
		locationClient.stopLocation();
	}
	
	/**
	 * 销毁定位
	 * 
	 * @since 2.8.0
	 * @author developer
	 *
	 */
	private void destroyLocation(){
		if (null != locationClient) {
			/**
			 * 如果AMapLocationClient是在当前Activity实例化的，
			 * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
			 */
			locationClient.onDestroy();
			locationClient = null;
			locationOption = null;
		}
	}

	@Override
	public void onBackPressed() {
		// Disable going back to the MainActivity
		moveTaskToBack(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.login_out) {
			SPUtils.clear(getApplicationContext());
			if(null!=timer){
				timer.purge();
				timer.cancel();
				timer = null;
			}

			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);

			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void uploadLocationData(final AMapLocation location){



		new Thread(){
			@Override
			public void run() {
				Looper.prepare();
				String email = (String)SPUtils.get(getApplicationContext(),"email","");
				String username = (String)SPUtils.get(getApplicationContext(),"username","");
				String result = myPutData(email,username,location,SERVER_URL+PUT_POSITION);
				try {
					JSONObject json = new JSONObject(result);
					if(json.getString("status").equals("success")){

					}else{
						if(timer != null){
							timer.purge();
							timer.cancel();
							timer = null;
						}

					}
					Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
				} catch (JSONException e) {
					e.printStackTrace();
					if(timer != null){
						timer.purge();
						timer.cancel();
						timer = null;
					}
				}


//				Message msg = Message.obtain();
//				msg.obj = result;
//				handler.sendMessage(msg);

				Looper.loop();
			}
		}.start();


	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if(!hasFocus){
			if(timer != null){
				timer.purge();
				timer.cancel();
				timer = null;
			}
		}
	}
}
