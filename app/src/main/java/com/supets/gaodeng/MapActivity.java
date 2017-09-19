package com.supets.gaodeng;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.location.demo.R;
import com.supets.gaodeng.map.AddressInfo;
import com.supets.gaodeng.map.MapAdapter;
import com.supets.gaodeng.overlay.PoiOverlay;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MapActivity extends BaseLocationActivity {


    MapView mapView;
    LocationSource.OnLocationChangedListener mListener;


    MapAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        ListView listView = findViewById(R.id.list);
        mAdapter = new MapAdapter();
        listView.setAdapter(mAdapter);


        mapView = findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        AMap aMap = mapView.getMap();

        aMap.setTrafficEnabled(false);
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 卫星地图模式


        initCenterLocation(aMap);

//        aMap.setLocationSource(new LocationSource() {
//            @Override
//            public void activate(OnLocationChangedListener onLocationChangedListener) {
//                mListener = onLocationChangedListener;
//                startLocation();
//            }
//
//            @Override
//            public void deactivate() {
//                stopLocation();
//            }
//        });// 设置定位监听（1）


        //doSearchQuery();

        //------------------------------------------添加中心标记
        MarkerOptions mMarkerOptions = new MarkerOptions();
        mMarkerOptions.draggable(false);//可拖放性
        mMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_round));
        mMarkerOptions.autoOverturnInfoWindow(true);
        mCenterMarker = aMap.addMarker(mMarkerOptions);
        ViewTreeObserver vto = mapView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mapView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mCenterMarker.setPositionByPixels(mapView.getWidth() >> 1, mapView.getHeight() >> 1);
                mCenterMarker.showInfoWindow();
            }
        });


        mapView.getMap().setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                boolean show = marker.isInfoWindowShown();
                if (show) {
                    marker.hideInfoWindow();
                }
                return show;
            }
        });

        aMap.setInfoWindowAdapter(new AMap.InfoWindowAdapter() {
            /**
             * 监听自定义infowindow窗口的infocontents事件回调
             */
            @Override
            public View getInfoContents(Marker marker) {
//                if (radioOption.getCheckedRadioButtonId() != R.id.custom_info_contents) {
//                    return null;
//                }
//                View infoContent = getLayoutInflater().inflate(
//                        R.layout.custom_info_contents, null);
//                render(marker, infoContent);
                return null;
            }

            /**
             * 监听自定义infowindow窗口的infowindow事件回调
             */
            @Override
            public View getInfoWindow(Marker marker) {
//                if (radioOption.getCheckedRadioButtonId() != R.id.custom_info_window) {
//                    return null;
//                }
                View infoWindow = getLayoutInflater().inflate(
                        R.layout.h, null);

                render(marker, infoWindow);
                return infoWindow;
            }
        });


        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                LatLonPoint mCurrentPoint = new LatLonPoint(cameraPosition.target.
                        latitude, cameraPosition.target.longitude);
                doSearchQuery2(mCurrentPoint);
            }
        });//手动移动地图监听 （2）

    }

    /**
     * 自定义infowinfow窗口
     */
    public void render(Marker marker, View view) {
//        if (radioOption.getCheckedRadioButtonId() == R.id.custom_info_contents) {
//            ((ImageView) view.findViewById(R.id.badge))
//                    .setImageResource(R.drawable.badge_sa);
//        } else if (radioOption.getCheckedRadioButtonId() == R.id.custom_info_window) {
//            ImageView imageView = (ImageView) view.findViewById(R.id.badge);
//            imageView.setImageResource(R.drawable.badge_wa);
//        }

        String title = marker.getTitle();
        TextView titleUi = ((TextView) view.findViewById(R.id.title));
        if (title != null) {
            SpannableString titleText = new SpannableString(title);
            titleText.setSpan(new ForegroundColorSpan(Color.RED), 0,
                    titleText.length(), 0);
            titleUi.setTextSize(15);
            titleUi.setText(titleText);

        } else {
            titleUi.setText("");
        }
        String snippet = marker.getSnippet();
        TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
        if (snippet != null) {
            SpannableString snippetText = new SpannableString(snippet);
            snippetText.setSpan(new ForegroundColorSpan(Color.GREEN), 0,
                    snippetText.length(), 0);
            snippetUi.setTextSize(20);
            snippetUi.setText(snippetText);
        } else {
            snippetUi.setText("");
        }
    }

    private void initCenterLocation(AMap aMap) {
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        mapView.getMap().setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        mapView.getMap().setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap.moveCamera(CameraUpdateFactory.zoomTo(12f));
    }

    double mLongitude;
    double mLatitude;
    String cityCode;


    @Override
    protected void locationSuccess(AMapLocation location) {
        mLongitude = location.getLongitude();//经度
        mLatitude = location.getLatitude();//纬度
        cityCode = location.getCityCode();//citycode

        mListener.onLocationChanged(location);

        // dian();
    }


    Marker marker;
    Marker mCenterMarker;

    public void dian() {
        //绘制marker
        marker = mapView.getMap().addMarker(new MarkerOptions()
                .position(new LatLng(mLongitude, mLatitude))
                .icon(BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(getResources(), R.drawable.icon_markg)
                ))
                .draggable(true));
        // marker.setPositionByPixels(360, 360);

    }


    PoiOverlay poiOverlay;

    /**
     * 开始进行poi搜索
     * //
     */
//    protected void doSearchQuery() {
//
//        if (poiOverlay == null) {
//            poiOverlay = new PoiOverlay(mapView.getMap(), this);
//        }
//
//        LatLonPoint lp = new LatLonPoint(39.986919, 116.353369);
//
//        PoiSearch.Query query = new PoiSearch.Query("酒店", "", "北京市");
//        query.setPageSize(20);// 设置每页最多返回多少条poiitem
//        query.setPageNum(0);// 设置查第一页
//
//        PoiSearch poiSearch = new PoiSearch(this, query);
//        poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
//            @Override
//            public void onPoiSearched(PoiResult poiResult, int i) {
//
//                if (poiOverlay != null) {
//                    poiOverlay.removeFromMap();
//                }
//
//                poiOverlay.setPois(poiResult.getPois());
//                poiOverlay.addToMap();
//
//            }
//
//            @Override
//            public void onPoiItemSearched(PoiItem poiItem, int i) {
//                movecenter(poiItem);
//            }
//
//        });
//        poiSearch.setBound(new PoiSearch.SearchBound(lp, 5000, true));
//        poiSearch.searchPOIAsyn();// 异步搜索
//    }
    public void movecenter(PoiItem poiItem) {
        mapView.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude()), 20));
    }


    protected void doSearchQuery2(final LatLonPoint mCurrentPoint) {
        if (poiOverlay == null) {
            poiOverlay = new PoiOverlay(mapView.getMap(), this);
        }
        GeocodeSearch mGeocoderSearch = new GeocodeSearch(this);
        mGeocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                //     if (rCode == 0) {
                if (regeocodeResult != null && regeocodeResult.getRegeocodeAddress() != null &&
                        regeocodeResult.getRegeocodeAddress().getFormatAddress() != null) {
                    mAdapter.data.clear();
                    mAdapter.data.add(new AddressInfo(
                            mCurrentPoint,
                            regeocodeResult.getRegeocodeAddress().getFormatAddress()));
                    mAdapter.notifyDataSetChanged();

                    mCenterMarker.remove();
                    MarkerOptions mMarkerOptions = new MarkerOptions();
                    mMarkerOptions.draggable(false);//可拖放性
                    mMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher_round));
                    mCenterMarker = mapView.getMap().addMarker(mMarkerOptions);
                    mCenterMarker.setPositionByPixels(mapView.getWidth() >> 1, mapView.getHeight() >> 1);
                    mCenterMarker.showInfoWindow();

                    mCenterMarker.setSnippet(regeocodeResult.getRegeocodeAddress().getFormatAddress());
                    mCenterMarker.showInfoWindow();

//                    crop();

                    String mType = "汽车服务|汽车销售|汽车维修|摩托车服务|餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|金融保险服务|公司企业|道路附属设施|地名地址信息|公共设施";

                    PoiSearch.Query mPoiQuery = new PoiSearch.Query("", mType,
                            regeocodeResult.getRegeocodeAddress().getCityCode());
                    mPoiQuery.setPageSize(20);// 设置每页最多返回多少条poiitem
                    mPoiQuery.setPageNum(0);//设置查第一页
                    PoiSearch poiSearch = new PoiSearch(MapActivity.this, mPoiQuery);
                    poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
                        @Override
                        public void onPoiSearched(PoiResult poiResult, int i) {
//                            if (poiOverlay != null) {
//                                poiOverlay.removeFromMap();
//                            }
//
//                            poiOverlay.setPois(poiResult.getPois());
//                            poiOverlay.addToMap();

                            for (PoiItem item : poiResult.getPois()) {
                                AddressInfo info = new AddressInfo(
                                        item.getLatLonPoint(),
                                        item.getProvinceName()
                                                + item.getCityName()
                                                + item.getAdName()
                                                + item.getSnippet());
                                info.name = item.getTitle();

//                                PoiBean bean=new PoiBean();
//                                bean.setTitleName(poiItem.getTitle());
//                                bean.setCityName(poiItem.getCityName());
//                                bean.setAd(poiItem.getAdName());
//                                bean.setSnippet(poiItem.getSnippet());
//                                bean.setPoint(poiItem.getLatLonPoint());
//                                Log.e("yufs",""+poiItem.getTitle()+","+poiItem.getProvinceName()+","
//                                        +poiItem.getCityName()+","
//                                        +poiItem.getAdName()+","//区
//                                        +poiItem.getSnippet()+","
//                                        +poiItem.getLatLonPoint()+"\n");

                                mAdapter.data.add(info);
                            }

                            mAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onPoiItemSearched(PoiItem poiItem, int i) {

                        }
                    });//设置数据返回的监听器 (5)
                    //设置周边搜索的中心点以及区域
                    poiSearch.setBound(new PoiSearch.SearchBound(mCurrentPoint, 1500, true));
                    poiSearch.searchPOIAsyn();//开始搜索
                } else {
                    //  ToastUtil.show(mContext, R.string.no_result);
                }
                //   } else {
                //  ToastUtil.show(mContexts, rCode);
                //  }
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

            }
        });
        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        RegeocodeQuery query = new RegeocodeQuery(mCurrentPoint, 200, GeocodeSearch.AMAP);
        mGeocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求

    }

    public void crop() {
        mapView.getMap().getMapScreenShot(new AMap.OnMapScreenShotListener() {
            @Override
            public void onMapScreenShot(Bitmap bitmap) {

            }

            @Override
            public void onMapScreenShot(Bitmap bitmap, int i) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                String pathFolder = Environment.getExternalStorageDirectory().toString() + "/myou/cacheimage/";
                String savePath = pathFolder + "t_" + sdf.format(new Date()) + ".png";
                Log.e("yufs", "截图保存路径：" + savePath);
                if (null == bitmap) {
                    return;
                }
                try {
                    File file = new File(pathFolder);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    FileOutputStream fos = new FileOutputStream(new File(savePath));
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
