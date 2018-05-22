
package com.amap.location.findchildren;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.amap.api.fence.GeoFence;
import com.amap.api.fence.GeoFenceClient;
import com.amap.api.fence.GeoFenceListener;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.DPoint;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.PolylineOptions;

import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.amap.location.findchildren.RequestConnect.*;
import static com.amap.location.findchildren.RequestConnect.bindChildren;

/**
 * 多边形地理围栏
 * 
 * @author developer
 * @since 3.2.0
 */
public class GeoFence_Polygon_Activity extends CheckPermissionsActivity
		implements
			OnClickListener,
			GeoFenceListener,
			OnMapClickListener,
			LocationSource,
			AMapLocationListener,
			OnCheckedChangeListener {
	private View view_custom;
	private TextView tvGuide;
	private TextView tvResult;
	private EditText etCustomId;
	private CheckBox cbAlertIn;
	private CheckBox cbAlertOut;
	private CheckBox cbAldertStated;
	private Button btAddFence;
	private Button btRemoveFence;
	private Button btBindChildren;
	private int manIndex = 0;
	private Button btStopFind;
	private Button btReset;
	private Button btChildList;
	private Button btDelete;
	private List<Polygon> polygonList = new ArrayList<Polygon>();
	private Button btGetChildData;
	private AlertDialog alert = null;
	private AlertDialog.Builder builder = null;
	private Timer timer;
	private Vibrator myVibrator;
	private List<Children> childrenInfo = new ArrayList<Children>();
	//problem son;
	final private HashMap<String,String> errorSon = new HashMap();
	/**
	 * 用于显示当前的位置
	 * <p>
	 * 示例中是为了显示当前的位置，在实际使用中，单独的地理围栏可以不使用定位接口
	 * </p>
	 */
	private AMapLocationClient mlocationClient;
	private OnLocationChangedListener mListener;
	private AMapLocationClientOption mLocationOption;

	private MapView mMapView;
	private AMap mAMap;
	// 多边形围栏的边界点
	private List<LatLng> polygonPoints = new ArrayList<LatLng>();

	private List<Marker> markerList = new ArrayList<Marker>();

	// 当前的坐标点集合，主要用于进行地图的可视区域的缩放
	private LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();

	private BitmapDescriptor bitmap = null;
	private MarkerOptions markerOption = null;

	// 地理围栏客户端
	private GeoFenceClient fenceClient = null;

	// 触发地理围栏的行为，默认为进入提醒
	private int activatesAction = GeoFenceClient.GEOFENCE_IN;
	// 地理围栏的广播action
	private static final String GEOFENCE_BROADCAST_ACTION = "com.example.geofence.polygon";

	// 记录已经添加成功的围栏
	private HashMap<String, GeoFence> fenceMap = new HashMap<String, GeoFence>();
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_geofence_new);
		setTitle(R.string.polygonGeoFence);
		myVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);

			// 初始化地理围栏
			fenceClient = new GeoFenceClient(getApplicationContext());

			btAddFence = (Button) findViewById(R.id.bt_addFence);
			btRemoveFence = (Button) findViewById(R.id.bt_remove);
			btGetChildData = (Button) findViewById(R.id.bt_get_data);
			btBindChildren =  (Button)findViewById(R.id.bt_bind_children);
			btStopFind = (Button)findViewById(R.id.bt_stop_children);
			btReset = (Button)findViewById(R.id.bt_reset_children);
			btChildList = (Button)findViewById(R.id.bt_children_list);
			btDelete = (Button)findViewById(R.id.bt_clear_point);
			tvGuide = (TextView) findViewById(R.id.tv_guide);
			tvResult = (TextView) findViewById(R.id.tv_result);
			tvResult.setVisibility(View.GONE);
			etCustomId = (EditText) findViewById(R.id.et_customId);

			cbAlertIn = (CheckBox) findViewById(R.id.cb_alertIn);
			cbAlertOut = (CheckBox) findViewById(R.id.cb_alertOut);
			cbAldertStated = (CheckBox) findViewById(R.id.cb_alertStated);

			mMapView = (MapView) findViewById(R.id.map);
			mMapView.onCreate(savedInstanceState);
			bitmap = BitmapDescriptorFactory
					.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
			markerOption = new MarkerOptions().icon(bitmap).draggable(true);
			init();
			Boolean isLogin = (Boolean) SPUtils.get(getApplicationContext(),"isLogin",false);
			String type = (String) SPUtils.get(getApplicationContext(),"type","parent");
			if(!isLogin){
				Intent intent = new Intent(this, LoginActivity.class);
				startActivity(intent);
				SPUtils.clear(getApplicationContext());
			}else {
                if(type.contentEquals("children")){

                    Intent intent = new Intent(this, Location_Activity.class);
                    startActivity(intent);
                    finish();
                }
            }

	}

	void init() {
		if (mAMap == null) {
			mAMap = mMapView.getMap();
			mAMap.getUiSettings().setRotateGesturesEnabled(false);
			mAMap.moveCamera(CameraUpdateFactory.zoomBy(6));
			setUpMap();
		}

		resetView_polygon();
		setList();
		btAddFence.setOnClickListener(this);
		btRemoveFence.setOnClickListener(this);
		btGetChildData.setOnClickListener(this);
		btBindChildren.setOnClickListener(this);
		btStopFind.setOnClickListener(this);
		btReset.setOnClickListener(this);
		btChildList.setOnClickListener(this);
		btDelete.setOnClickListener(this);
		cbAlertIn.setOnCheckedChangeListener(this);
		cbAlertOut.setOnCheckedChangeListener(this);
		cbAldertStated.setOnCheckedChangeListener(this);

		IntentFilter filter = new IntentFilter();
		filter.addAction(GEOFENCE_BROADCAST_ACTION);
		registerReceiver(mGeoFenceReceiver, filter);
		/**
		 * 创建pendingIntent
		 */
		fenceClient.createPendingIntent(GEOFENCE_BROADCAST_ACTION);
		fenceClient.setGeoFenceListener(this);
		/**
		 * 设置地理围栏的触发行为,默认为进入
		 */
		fenceClient.setActivateAction(GeoFenceClient.GEOFENCE_IN);
	}

	/**
	 * 设置一些amap的属性
	 */
	private void setUpMap() {
		mAMap.setOnMapClickListener(this);
		mAMap.setLocationSource(this);// 设置定位监听
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
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
		deactivate();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
		try {
			unregisterReceiver(mGeoFenceReceiver);
		} catch (Throwable e) {
		}

		if (null != fenceClient) {
			fenceClient.removeGeoFence();
		}
		if (null != mlocationClient) {
			mlocationClient.onDestroy();
		}

		if(null!=timer){
			timer.purge();
			timer.cancel();
			timer = null;
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.bt_addFence :
				addFence();
				break;
			case R.id.bt_remove :
				removeMarkers();
				fenceClient.removeGeoFence();
				for (int i=0;i<polygonList.size();i++){
				    polygonList.get(i).remove();
                }
                polygonList.clear();


				Toast.makeText(getApplicationContext(),"Remove Successfully",Toast.LENGTH_SHORT).show();
				btAddFence.setEnabled(false);
				btRemoveFence.setEnabled(false);
				resetView_polygon();
				break;

			case R.id.bt_get_data :
				String son = (String) SPUtils.get(getApplicationContext(),"son","");
				if(son.contentEquals("")){
					final LayoutInflater inflater = GeoFence_Polygon_Activity.this.getLayoutInflater();
					builder = new AlertDialog.Builder(GeoFence_Polygon_Activity.this);
					view_custom = inflater.inflate(R.layout.alert_layout, null,false);
					builder.setView(view_custom);
					builder.setCancelable(false);
					builder.setTitle("绑定你孩子的邮箱");
					builder.setCancelable(true);
					view_custom.findViewById(R.id.bind_children).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							new Thread(){
								@Override
								public void run() {
									Looper.prepare();
									EditText childEmail = (EditText) view_custom.findViewById(R.id.children_email);
									String children_email = String.valueOf(childEmail.getText());
									String parent_email = (String)SPUtils.get(getApplicationContext(),"email","");
									String result = bindChildren(parent_email,children_email,SERVER_URL+BIND_CHILDREN);
									if(result.indexOf("success")>-1){

										try {
											SPUtils.put(getApplicationContext(),"son",new JSONObject(result).getString("data"));
											setList();
										} catch (JSONException e) {
											e.printStackTrace();
										}
									}


									Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
									alert.dismiss();
									Looper.loop();
								}
							}.start();
						}
					});
					alert = builder.create();
					alert.show();
					view_custom.findViewById(R.id.cancel_children).setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							alert.cancel();
						}
					});
				}else{
					getData();

				}


			break;

			case R.id.bt_bind_children:
				rebindChildren();
				break;
			case R.id.bt_stop_children:
				if(null != timer){
					timer.purge();
					timer.cancel();
					timer= null;
					myVibrator.cancel();
				}
				break;
			case R.id.bt_reset_children:
				if(null != timer){
					timer.purge();
					timer.cancel();
					timer= null;
					myVibrator.cancel();
				}
				this.errorSon.clear();
				break;
			case R.id.bt_children_list:
				Intent intent = new Intent(this,ChildrenList.class);

				intent.putExtra("child",(Serializable)childrenInfo);
				startActivity(intent);


				break;
			case R.id.bt_clear_point:
				new Thread(){
					@Override
					public void run() {
						Looper.prepare();

						String result = delete(SERVER_URL+DELETEDATA);
						if(result.indexOf("success")>-1){
							mAMap.clear();

							Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG).show();

						}

						Looper.loop();
					}
				}.start();

				break;
			default :
				break;
		}
	}

	private void rebindChildren(){
		final LayoutInflater inflater = GeoFence_Polygon_Activity.this.getLayoutInflater();
		builder = new AlertDialog.Builder(GeoFence_Polygon_Activity.this);
		view_custom = inflater.inflate(R.layout.alert_layout, null,false);
		builder.setView(view_custom);
		builder.setCancelable(false);
		builder.setTitle("绑定你孩子的邮箱");
		builder.setCancelable(true);
		view_custom.findViewById(R.id.bind_children).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				new Thread(){
					@Override
					public void run() {
						Looper.prepare();
						EditText childEmail = (EditText) view_custom.findViewById(R.id.children_email);
						String children_email = String.valueOf(childEmail.getText());
						String parent_email = (String)SPUtils.get(getApplicationContext(),"email","");
						String result = bindChildren(parent_email,children_email,SERVER_URL+BIND_CHILDREN);
						if(result.indexOf("success")>-1){
							try {
								SPUtils.put(getApplicationContext(),"son",new JSONObject(result).getString("data"));
								setList();
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
						Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
						alert.dismiss();
						Looper.loop();
					}
				}.start();
			}
		});
		alert = builder.create();
		alert.show();
		view_custom.findViewById(R.id.cancel_children).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alert.cancel();
			}
		});
	}
	private void drawFence(GeoFence fence) {
		switch (fence.getType()) {
			case GeoFence.TYPE_ROUND :
			case GeoFence.TYPE_AMAPPOI :
				drawCircle(fence);
				break;
			case GeoFence.TYPE_POLYGON :
			case GeoFence.TYPE_DISTRICT :
				drawPolygon(fence);
				break;
			default :
				break;
		}

		// 设置所有maker显示在当前可视区域地图中
		LatLngBounds bounds = boundsBuilder.build();
		mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
		polygonPoints.clear();
		removeMarkers();
	}

	private void drawCircle(GeoFence fence) {
		LatLng center = new LatLng(fence.getCenter().getLatitude(),
				fence.getCenter().getLongitude());
		// 绘制一个圆形
		mAMap.addCircle(new CircleOptions().center(center)
				.radius(fence.getRadius()).strokeColor(Const.STROKE_COLOR)
				.fillColor(Const.FILL_COLOR).strokeWidth(Const.STROKE_WIDTH));
		boundsBuilder.include(center);
	}

	private void drawPolygon(GeoFence fence) {
		final List<List<DPoint>> pointList = fence.getPointList();
		if (null == pointList || pointList.isEmpty()) {
			return;
		}
		for (List<DPoint> subList : pointList) {
			List<LatLng> lst = new ArrayList<LatLng>();

			PolygonOptions polygonOption = new PolygonOptions();
			for (DPoint point : subList) {
				lst.add(new LatLng(point.getLatitude(), point.getLongitude()));
				boundsBuilder.include(
						new LatLng(point.getLatitude(), point.getLongitude()));
			}
			polygonOption.addAll(lst);

			polygonOption.strokeColor(Const.STROKE_COLOR)
					.fillColor(Const.FILL_COLOR).strokeWidth(Const.STROKE_WIDTH);
			Polygon polygon = mAMap.addPolygon(polygonOption);
			polygonList.add(polygon);
		}
	}

	Object lock = new Object();
	void drawFence2Map() {
		new Thread() {
			@Override
			public void run() {
				try {
					synchronized (lock) {
						if (null == fenceList || fenceList.isEmpty()) {
							return;
						}
						for (GeoFence fence : fenceList) {
							if (fenceMap.containsKey(fence.getFenceId())) {
								continue;
							}
							drawFence(fence);
							fenceMap.put(fence.getFenceId(), fence);
						}
					}
				} catch (Throwable e) {

				}
			}
		}.start();
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0 :
					StringBuffer sb = new StringBuffer();
					sb.append("Add fence successfully ...");
					String customId = (String)msg.obj;
					if(!TextUtils.isEmpty(customId)){
						sb.append("customId: ").append(customId);
					}
					Toast.makeText(getApplicationContext(), sb.toString(),
							Toast.LENGTH_SHORT).show();
					drawFence2Map();
					break;
				case 1 :
					int errorCode = msg.arg1;
					Toast.makeText(getApplicationContext(),
							"Add failed.. " + errorCode, Toast.LENGTH_SHORT).show();
					break;
				case 2 :
					String statusStr = (String) msg.obj;
					tvResult.setVisibility(View.VISIBLE);
					tvResult.append(statusStr + "\n");
					break;
				default :
					break;
			}
		}
	};

	List<GeoFence> fenceList = new ArrayList<GeoFence>();
	@Override
	public void onGeoFenceCreateFinished(final List<GeoFence> geoFenceList,
			int errorCode, String customId) {
		Message msg = Message.obtain();
		if (errorCode == GeoFence.ADDGEOFENCE_SUCCESS) {
			fenceList = geoFenceList;
			msg.obj = customId;
			msg.what = 0;
		} else {
			msg.arg1 = errorCode;
			msg.what = 1;
		}
		handler.sendMessage(msg);
	}

	/**
	 * 接收触发围栏后的广播,当添加围栏成功之后，会立即对所有围栏状态进行一次侦测，如果当前状态与用户设置的触发行为相符将会立即触发一次围栏广播；
	 * 只有当触发围栏之后才会收到广播,对于同一触发行为只会发送一次广播不会重复发送，除非位置和围栏的关系再次发生了改变。
	 */
	private BroadcastReceiver mGeoFenceReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 接收广播
			if (intent.getAction().equals(GEOFENCE_BROADCAST_ACTION)) {
				Bundle bundle = intent.getExtras();
				String customId = bundle
						.getString(GeoFence.BUNDLE_KEY_CUSTOMID);
				String fenceId = bundle.getString(GeoFence.BUNDLE_KEY_FENCEID);
				//status标识的是当前的围栏状态，不是围栏行为
				int status = bundle.getInt(GeoFence.BUNDLE_KEY_FENCESTATUS);
				StringBuffer sb = new StringBuffer();
				switch (status) {
					case GeoFence.STATUS_LOCFAIL :
						sb.append("定位失败");
						break;
					case GeoFence.STATUS_IN :
						sb.append("进入围栏 ");
						break;
					case GeoFence.STATUS_OUT :
						sb.append("离开围栏 ");
						break;
					case GeoFence.STATUS_STAYED :
						sb.append("停留在围栏内 ");
						break;
					default :
						break;
				}
				if(status != GeoFence.STATUS_LOCFAIL){
					if(!TextUtils.isEmpty(customId)){
						sb.append(" customId: " + customId);
					}
					sb.append(" fenceId: " + fenceId);
				}
				String str = sb.toString();
				Message msg = Message.obtain();
				msg.obj = str;
				msg.what = 2;
				handler.sendMessage(msg);
			}
		}
	};

	@Override
	public void onMapClick(LatLng latLng) {
		if (null == polygonPoints) {
			polygonPoints = new ArrayList<LatLng>();
		}
		polygonPoints.add(latLng);
		addPolygonMarker(latLng);
		tvGuide.setBackgroundColor(getResources().getColor(R.color.gary));
		tvGuide.setText("已选择" + polygonPoints.size() + "个点");
		if (polygonPoints.size() >= 3) {
			btAddFence.setEnabled(true);
			btRemoveFence.setEnabled(true);
		}
	}

	/**
	 * 定位成功后回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (mListener != null && amapLocation != null) {
			if (amapLocation != null && amapLocation.getErrorCode() == 0) {
				tvResult.setVisibility(View.GONE);
				mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
			} else {
				String errText = "定位失败," + amapLocation.getErrorCode() + ": "
						+ amapLocation.getErrorInfo();
				Log.e("AmapErr", errText);
				tvResult.setVisibility(View.VISIBLE);
				tvResult.setText(errText);
			}
		}
	}

	/**
	 * 激活定位
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mlocationClient == null) {
			mlocationClient = new AMapLocationClient(this);
			mLocationOption = new AMapLocationClientOption();
			// 设置定位监听
			mlocationClient.setLocationListener(this);
			// 设置为高精度定位模式
			mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
			// 只是为了获取当前位置，所以设置为单次定位
			mLocationOption.setOnceLocation(true);
			// 设置定位参数
			mlocationClient.setLocationOption(mLocationOption);
			mlocationClient.startLocation();
		}
	}

	/**
	 * 停止定位
	 */
	@Override
	public void deactivate() {
		mListener = null;
		if (mlocationClient != null) {
			mlocationClient.stopLocation();
			mlocationClient.onDestroy();
		}
		mlocationClient = null;
	}

	// 添加多边形的边界点marker
	private void addPolygonMarker(LatLng latlng) {
		markerOption.position(latlng);
		Marker marker = mAMap.addMarker(markerOption);
		markerList.add(marker);
	}

	private void removeMarkers() {
		if (null != markerList && markerList.size() > 0) {

			for (Marker marker : markerList) {
				marker.remove();
			}
			markerList.clear();
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
			case R.id.cb_alertIn :

				break;
			case R.id.cb_alertOut :

				break;
			case R.id.cb_alertStated :

				break;
			default :
				break;
		}
		if (null != fenceClient) {
			fenceClient.setActivateAction(activatesAction);
		}
	}

	private void resetView_polygon() {
		tvGuide.setBackgroundColor(getResources().getColor(R.color.red));
		tvGuide.setText("请点击地图选择围栏的边界点,至少3个点");
		tvGuide.setVisibility(View.VISIBLE);
		tvGuide.setVisibility(View.VISIBLE);
		polygonPoints = new ArrayList<LatLng>();
		btAddFence.setEnabled(false);
		btRemoveFence.setEnabled(false);
	}

	private  void getData(){
		 final 	String son = (String) SPUtils.get(getApplicationContext(),"son","");
		 //现在改成了可以添加多个儿子
		 final String[] sonList = son.split(",");

		 final HashMap<String,List<LatLng>> latlngList = new HashMap<>();
		 final Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {

					String json = (String) msg.obj;
//					Toast.makeText(getApplicationContext(),json,Toast.LENGTH_SHORT).show();

				try {
					JSONObject jsonObject = new JSONObject(json);
					String sonEmail = (String) jsonObject.get("email");
					LatLng point = new LatLng(jsonObject.getJSONObject("point").getDouble("latitude"),jsonObject.getJSONObject("point").getDouble("longitude"));


					try {
						long start = System.currentTimeMillis();
						Calendar calendar = Calendar.getInstance();
						calendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse((String) jsonObject.getJSONObject("point").get("time")));
						long oldTime = calendar.getTimeInMillis();
						if(start - oldTime > 300000){
							modSon(sonEmail,"你的孩子:"+jsonObject.get("username")+",已超出限定时间 5分钟了没发送位置信息了！！");
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
					for (int i=0;i<polygonList.size();i++){
						Boolean isOut = polygonList.get(i).contains(point);
						if(!isOut){

							Toast.makeText(getApplicationContext(),"你的孩子:"+jsonObject.get("username")+",已超出活动范围！！",Toast.LENGTH_SHORT).show();
							myVibrator.vibrate(new long[]{500, 100, 500, 100, 500, 100}, 0);
						}else{
							myVibrator.cancel();
						}

					}
					MarkerOptions newOptions = new MarkerOptions();
					newOptions.position(point);
                    StringBuffer sb = new StringBuffer();
                    if(!latlngList.containsKey(jsonObject.get("email"))){
						latlngList.put((String) jsonObject.get("email"),new ArrayList<LatLng>());
					}
					latlngList.get(jsonObject.get("email")).add(point);
                    sb.append("姓    名    : " + jsonObject.get("username")+ "\n");
                    sb.append("经    度    : " + jsonObject.getJSONObject("point").getDouble("longitude") + "\n");
                    sb.append("纬    度    : " + jsonObject.getJSONObject("point").getDouble("latitude")+ "\n");
                    sb.append("省            : " + jsonObject.getJSONObject("point").get("province") + "\n");
                    sb.append("市            : " + jsonObject.getJSONObject("point").get("city") + "\n");
                    sb.append("区            : " + jsonObject.getJSONObject("point").get("district") + "\n");
                    sb.append("地    址    : " + jsonObject.getJSONObject("point").get("address") + "\n");
                    sb.append("定位时间: " + jsonObject.getJSONObject("point").get("time") );

					newOptions.title((String) jsonObject.get("username"));

					for(Children child : childrenInfo){
						if(child.getEmail().contentEquals(sonEmail)){
							child.setDetail(sb.toString());
						}
					}
					String manIndex = (String) jsonObject.get("index");
					newOptions.icon( BitmapDescriptorFactory.fromAsset("man"+manIndex+".png"));
					List<Marker> mapScreenMarkers = mAMap.getMapScreenMarkers();
					for (int i = 0; i < mapScreenMarkers.size()-sonList.length; i++) {
						Marker marker = mapScreenMarkers.get(i);

						marker.remove();//移除当前Marker

					}

					mAMap.addMarker(newOptions);
					//mAMap.addText(textOptions);
					int dj = 0;
					if(Integer.parseInt(manIndex)%2 == 1){
						dj =0;
					}else{
						dj = 255;
					}
					mAMap.addPolyline(new PolylineOptions().
							addAll(latlngList.get(jsonObject.get("email"))).width(10).color(Color.argb(255, dj, Integer.parseInt(manIndex)*Integer.parseInt(manIndex)*5, Integer.parseInt(manIndex)*46)));
					mAMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(point,18,0,30)),1000,null);

				} catch (JSONException e) {
					e.printStackTrace();
				}


			}
		};

		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				new Thread(){
					@Override
					public void run() {
						Looper.prepare();

						for(int i=0;i<sonList.length;i++){
							String sonItem  = sonList[i];

							if(errorSon.containsKey(sonItem) && !sonItem.contentEquals("")){
								Toast.makeText(getApplicationContext(),errorSon.get(sonItem),Toast.LENGTH_LONG).show();

							}else {

								String result  =	getChildData(sonItem,SERVER_URL+GET_POSITION);
								try {
									JSONObject resultJson = new JSONObject(result);
									resultJson.put("index",i+"");
									result = resultJson.toString();
									if(result.indexOf("success")>-1){
										Message msg =  Message.obtain();
										msg.obj = result;

										handler.sendMessage(msg);
									}else{
										modSon(sonItem,"你的孩子:"+sonItem+"没有相关位置信息");
										for(Children child : childrenInfo){
											if(child.getEmail().equals(sonItem)){
												child.setDetail("你的孩子:"+sonItem+"没有相关位置信息");
											}
										}
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}

							}


						}

						Looper.loop();
					}
				}.start();
			}
		},new Date(),5000);


	}

	private void modSon(String son,String msg){
		if(errorSon.containsKey(son)){
			String nowStr = errorSon.get(son);
			if(nowStr.indexOf(msg) == -1){
				errorSon.remove(son);
				errorSon.put(son,nowStr+"\n"+msg);
			}

		}else{
			errorSon.put(son,msg);
		}
	}
	/**
	 * 添加围栏
	 * 
	 * @since 3.2.0
	 * @author developer
	 *
	 */
	private void addFence() {
		addPolygonFence();
	}


	/**
	 * 添加多边形围栏
	 * 
	 * @since 3.2.0
	 * @author developer
	 *
	 */
	private void addPolygonFence() {
		String customId = etCustomId.getText().toString();
		if (null == polygonPoints || polygonPoints.size() < 3) {
			Toast.makeText(getApplicationContext(), "参数不全", Toast.LENGTH_SHORT)
					.show();
			btAddFence.setEnabled(true);
			return;
		}
		List<DPoint> pointList = new ArrayList<DPoint>();
		for (LatLng latLng : polygonPoints) {
			pointList.add(new DPoint(latLng.latitude, latLng.longitude));
		}
		fenceClient.addGeoFence(pointList, customId);
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
			if(timer!=null){
				timer.purge();
				timer.cancel();
				timer = null;
				myVibrator.cancel();
			}

			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void setList(){
		final 	String son = (String) SPUtils.get(getApplicationContext(),"son","");
		//现在改成了可以添加多个儿子
		final String[] sonList = son.split(",");
		childrenInfo = new ArrayList<Children>();
		for(int i=0;i<sonList.length;i++){
			if(!sonList[i].contentEquals("")){
				childrenInfo.add(new Children(sonList[i],""));
			}

		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		setList();
	}
}
