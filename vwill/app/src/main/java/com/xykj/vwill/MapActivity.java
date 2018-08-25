package com.xykj.vwill;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.xykj.adapter.PoiAdapter;

import java.util.List;

public class MapActivity extends VWillBaseActivity implements SensorEventListener, OnGetGeoCoderResultListener, View.OnClickListener {
    // 定位相关
    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private MyLocationConfiguration.LocationMode mCurrentMode;
    BitmapDescriptor mCurrentMarker;
    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private int mCurrentDirection = 0;

    private float mCurrentAccracy;
    //定位次数
    private int count = 0;

    MapView mMapView;
    BaiduMap mBaiduMap;

    // UI相关
    Button requestLocButton;
    private MyLocationData locData;
    // 搜索模块，也可去掉地图模块独立使用
    GeoCoder mSearch = null;
    private EditText etAddressName;
    //当前选中的位置的名称
    private String address;
    //记录当前选中的点
    private LatLng seleceLocation;
    ListView listPoi;
    PoiAdapter poiAdapter;
    //是否是过来显示某个位置的
    private boolean isShowPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent it = getIntent();
        if (it.hasExtra("location")) {
            isShowPosition = true;
            address = it.getStringExtra("address");
            seleceLocation = it.getParcelableExtra("location");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initLayout() {
        requestLocButton = (Button) findViewById(R.id.change_mode);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);//获取传感器管理服务
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        requestLocButton.setText("普通");
        requestLocButton.setOnClickListener(this);
        Button btnReset = findViewById(R.id.btn_reset);
        etAddressName = findViewById(R.id.et_name);
        listPoi = findViewById(R.id.list_poi);
        // 地图初始化
        mMapView = (MapView) findViewById(R.id.bmapView);
        //地图的操作对象
        mBaiduMap = mMapView.getMap();
        if (isShowPosition) {
            btnReset.setVisibility(View.GONE);
            listPoi.setVisibility(View.GONE);
            //显示位置名称
            etAddressName.setText(address);
            markPos(seleceLocation);
            //聚焦到标记位置上
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(seleceLocation).zoom(18.0f);
            //以动画的形式聚焦到查看的点上
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        } else {
            btnReset.setOnClickListener(this);
            poiAdapter = new PoiAdapter(this);
            listPoi.setAdapter(poiAdapter);
            listPoi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    if (i == 0) {
                        //隐藏列表
                        listPoi.setVisibility(View.GONE);
                    } else {
                        PoiInfo info = poiAdapter.getItem(i);
                        address = info.name;
                        seleceLocation = info.location;
                        //将选中的结果返回给上一个界面
                        backResult();
                    }
                }
            });
            mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
                /**
                 * 单击地图
                 */
                public void onMapClick(LatLng point) {
                    seleceLocation = point;
                    markPos(seleceLocation);
                    //启动地理位置反编码（经纬度-->地理名称）
                    mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                            .location(seleceLocation).newVersion(1));
                }

                /**
                 * 单击地图中的POI点
                 */
                public boolean onMapPoiClick(MapPoi poi) {
                    seleceLocation = poi.getPosition();
                    markPos(seleceLocation);
                    //启动地理位置反编码（经纬度-->地理名称）
                    mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                            .location(seleceLocation).newVersion(1));
                    return true;
                }
            });
            mBaiduMap
                    .setMyLocationConfigeration(new MyLocationConfiguration(
                            mCurrentMode, true, null));
            // 初始化搜索模块，注册事件监听
            mSearch = GeoCoder.newInstance();
            mSearch.setOnGetGeoCodeResultListener(this);
        }
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);   //设置每隔多久定位一次
        mLocClient.setLocOption(option);
        mLocClient.start();
    }

    BitmapDescriptor bdA = BitmapDescriptorFactory
            .fromResource(R.drawable.icon_marka);

    //标记位置
    private void markPos(LatLng ll) {
        MarkerOptions ooA = new MarkerOptions().position(ll).icon(bdA);
        mBaiduMap.clear();
        mBaiduMap.addOverlay(ooA);
    }

    @Override
    protected int getType() {
        if (isShowPosition) {
            return TYPE_BACK;
        }
        return TYPE_CANCEL_SURE;
    }

    @Override
    protected int getLayout() {
        return R.layout.activity_map;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            double lat = 0, lng = 0;
            if (null != seleceLocation) {
                lat = seleceLocation.latitude;
                lng = seleceLocation.longitude;
            }
            locData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(lat)
                    .longitude(lng).build();
            mBaiduMap.setMyLocationData(locData);
        }
        lastX = x;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.change_mode:
                switch (mCurrentMode) {
                    case NORMAL:
                        requestLocButton.setText("跟随");
                        mCurrentMode = MyLocationConfiguration.LocationMode.FOLLOWING;
                        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                                mCurrentMode, true, mCurrentMarker));
                        MapStatus.Builder builder = new MapStatus.Builder();
                        builder.overlook(0);
                        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                        break;
                    case COMPASS:
                        requestLocButton.setText("普通");
                        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
                        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                                mCurrentMode, true, mCurrentMarker));
                        MapStatus.Builder builder1 = new MapStatus.Builder();
                        builder1.overlook(0);
                        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder1.build()));
                        break;
                    case FOLLOWING:
                        requestLocButton.setText("罗盘");
                        mCurrentMode = MyLocationConfiguration.LocationMode.COMPASS;
                        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                                mCurrentMode, true, mCurrentMarker));
                        break;
                    default:
                        break;
                }
                break;
            case R.id.btn_reset:
                //重置选择的点
                mBaiduMap.clear();
                if (!mLocClient.isStarted()) {
                    mLocClient.start();
                }
                break;
        }
    }

    @Override
    protected void onTitleRightViewClick(View v) {
        backResult();
    }

    private void backResult() {
        Intent it = new Intent();
        it.putExtra("location", seleceLocation);
        it.putExtra("address", address);
        setResult(RESULT_OK, it);
        finish();
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
        //获取编码结果（地理名称-->经纬度）
    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        //获取反编码结果（经纬度-->名称（列表））
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            return;
        }
        address = result.getAddress();
        //附近地理名称列表
        List<PoiInfo> list = result.getPoiList();
        if (list != null && !list.isEmpty()) {
            address = list.get(0).name;
        }
        etAddressName.setText(address);
        //周围的地理名称列表展示出来
        if (listPoi.getVisibility() == View.VISIBLE) {
            poiAdapter.setList(list);
        }
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            //百度中的经纬度对象
            seleceLocation = new LatLng(location.getLatitude(),
                    location.getLongitude());
            mCurrentAccracy = location.getRadius(); //角度
            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            if(!isShowPosition) {
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(seleceLocation).zoom(18.0f);
                //以动画的形式聚焦到查看的点上
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
            count++;
            if (count == 3) {
                //如果已经定位了3次则停止定位
                mLocClient.stop();
                count = 0;
                if(!isShowPosition) {
                    //启动地理位置反编码（经纬度-->地理名称）
                    mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                            .location(seleceLocation).newVersion(1));
                }
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
        //取消注册传感器监听
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
        //为系统的方向传感器注册监听器
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        if (mLocClient.isStarted()) {
            mLocClient.stop();
        }
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        if (null != mSearch) {
            mSearch.destroy();
        }
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    @Override
    protected String getActivityTitle() {
        return "地图";
    }
}
