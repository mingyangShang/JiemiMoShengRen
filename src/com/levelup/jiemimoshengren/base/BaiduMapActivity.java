package com.levelup.jiemimoshengren.base;

import java.util.List;

import android.graphics.Bitmap;
import android.os.Bundle;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.levelup.jiemimoshengren.R;

public abstract class BaiduMapActivity extends BaseActivity implements OnMarkerClickListener{
	
	protected MapView mMapView = null; //地图mapview控件
	protected BaiduMap map;

	protected long centerLong,centerLan; //
	
	private int defaultMarkerRes = R.drawable.icon_marka_it; //标记物的资源id

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
    public void addMarker(LatLng pos,Bitmap descBmp,Bundle extra,boolean translate){
        BitmapDescriptor descriptor = null;
        if(descBmp==null){
            descriptor = BitmapDescriptorFactory.fromResource(this.defaultMarkerRes);
        }else{
            descriptor = BitmapDescriptorFactory.fromBitmap(descBmp);
        }
        final LatLng convertPos = convert(pos);
        if(descriptor!=null){
            OverlayOptions option = new MarkerOptions().position(convertPos).icon(descriptor).zIndex(4).draggable(true);
            this.mMapView.getMap().addOverlay(option).setExtraInfo(extra);
            if(translate){
            	MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(convertPos, 17.0f);
            	this.mMapView.getMap().animateMapStatus(mapStatusUpdate);
            }
        }else{
            throw new IllegalArgumentException("添加标记物失败");
        }
    }
    /**添加标记自己的标记物*/
    public void addMeMarker(LatLng pos){
    	BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka_i);
    	final LatLng convertPos = convert(pos);
        OverlayOptions option = new MarkerOptions().position(convertPos).icon(descriptor).zIndex(4).draggable(true);
        this.mMapView.getMap().addOverlay(option);
        MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngZoom(convertPos, 20.0f);
        this.mMapView.getMap().animateMapStatus(mapStatusUpdate);
    }
    public void addMarker(LatLng pos,Bitmap descBmp,Bundle extra){
    	addMarker(pos,descBmp,extra,false);
    }

    /**
     * 在地图上添加一系列标记
     * @param poss 位置集合
     * @param descBmps 标记物图片集合
     */
    public void addMarkers(List<LatLng> poss,List<Bitmap> descBmps,Bundle extra){
        if(poss.size() != descBmps.size()){
            throw new IllegalArgumentException("坐标数和标记图片数量必须一致");
        }
        for(int i=0,size=poss.size();i<size;++i){
            addMarker(poss.get(i),descBmps.get(i),extra);
        }
    }

    public void addMarker(LatLng pos,Bundle extra){
        addMarker(pos,null,extra);
    }
    public void addMarker(long latitude,long longitude,Bitmap descBmp,Bundle extra){
        addMarker(new LatLng(latitude,longitude),descBmp,extra);
    }
    public void addMarker(long latitude,long longitude,Bundle extra){
        addMarker(latitude,longitude,null,extra);
    }
    
    /**添加文字覆盖物*/
    public void addTextMarker(LatLng pos,final String msg,Bundle extra){
    	final LatLng convertPos = convert(pos);
    	OverlayOptions textOption = new TextOptions().bgColor(0xAAFFFF00).fontSize(24).fontColor(0xFFFF00FF).text(msg).rotate(-30).position(convertPos);
    	this.mMapView.getMap().addOverlay(textOption).setExtraInfo(extra);
    }
    /**添加文字和图片覆盖物*/
    public void addTextImgMarler(LatLng pos,final String msg,Bundle extra){
    	final LatLng convertPos = convert(pos);
    	BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka_it);
    	OverlayOptions option = new MarkerOptions().position(convertPos).icon(descriptor).zIndex(4).draggable(true);
    	OverlayOptions textOption = new TextOptions().bgColor(0x00FFFFFF).fontSize(24).zIndex(5).fontColor(0xFF00FF00).text(msg).rotate(-30).position(convertPos);
        this.mMapView.getMap().addOverlay(option).setExtraInfo(extra);
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
