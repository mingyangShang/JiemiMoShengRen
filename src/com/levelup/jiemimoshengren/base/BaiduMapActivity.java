package com.levelup.jiemimoshengren.base;

import java.util.List;

import android.graphics.Bitmap;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.levelup.jiemimoshengren.R;

public abstract class BaiduMapActivity extends BaseActivity {
	
	protected MapView mMapView = null; //地图mapview控件
	protected BaiduMap map;

	protected long centerLong,centerLan; //
	
	private int defaultMarkerRes = R.drawable.icon_marka; //标记物的资源id

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管�?
		mMapView.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管�?
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}
	
    /**
     * 在地图上添加标记
     * @param pos 位置，（经度，纬度）,记得要用convert函数转换在地图上显示的坐标
     * @param descBmp 标记物图片，如果是空的话显示默认的图片,see{setDefaultMarkerRes()}
     */
    public void addMarker(LatLng pos,Bitmap descBmp){
        BitmapDescriptor descriptor = null;
        if(descBmp==null){
            descriptor = BitmapDescriptorFactory.fromResource(this.defaultMarkerRes);
        }else{
            descriptor = BitmapDescriptorFactory.fromBitmap(descBmp);
        }
        final LatLng convertPos = convert(pos);
        if(descriptor!=null){
            OverlayOptions option = new MarkerOptions().position(convertPos).icon(descriptor).zIndex(4).draggable(true);
            this.mMapView.getMap().addOverlay(option);
            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(convertPos, 17.0f);
            this.mMapView.getMap().animateMapStatus(mapStatusUpdate);
        }else{
            throw new IllegalArgumentException("添加标记物失败");
        }
    }

    /**
     * 在地图上添加一系列标记
     * @param poss 位置集合
     * @param descBmps 标记物图片集合
     */
    public void addMarkers(List<LatLng> poss,List<Bitmap> descBmps){
        if(poss.size() != descBmps.size()){
            throw new IllegalArgumentException("坐标数和标记图片数量必须一致");
        }
        for(int i=0,size=poss.size();i<size;++i){
            addMarker(poss.get(i),descBmps.get(i));
        }
    }

    public void addMarker(LatLng pos){
        addMarker(pos,null);
    }
    public void addMarker(long latitude,long longitude,Bitmap descBmp){
        addMarker(new LatLng(latitude,longitude),descBmp);
    }
    public void addMarker(long latitude,long longitude){
        addMarker(latitude,longitude,null);
    }
    
    
    public void addTextMarker(LatLng pos,final String msg){
    	final LatLng convertPos = convert(pos);
    	OverlayOptions textOption = new TextOptions().bgColor(0xAAFFFF00).fontSize(24).fontColor(0xFFFF00FF).text(msg).rotate(-30).position(convertPos);
    	this.mMapView.getMap().addOverlay(textOption);
    }
    
    /**转换坐标*/
    public LatLng convert(LatLng latlng){
    	CoordinateConverter converter= new CoordinateConverter();
    	converter.coord(latlng);
    	converter.from(CoordinateConverter.CoordType.COMMON);
    	return converter.convert();
    }

    /**设置默认的标记图片*/
	public void setDefaultMarkerRes(int defaultMarkerRes) {
		this.defaultMarkerRes = defaultMarkerRes;
	}

	@Override
	protected void initData() {
		
	}

	@Override
	protected void initView() {
		
	}
}
