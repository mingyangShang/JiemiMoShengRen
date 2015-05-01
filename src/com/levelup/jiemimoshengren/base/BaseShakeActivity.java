package com.levelup.jiemimoshengren.base;

import android.app.Activity;
import android.app.Service;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;

import com.levelup.jiemimoshengren.R;

public class BaseShakeActivity extends Activity implements SensorEventListener{

    protected SensorManager sensorManager;
    protected Vibrator vibrator;

    public final static int MIN_SHAKE_THRESHOLD = 14; //摇一摇最小变化值

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_round_img);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        vibrator = (Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        float[] values = event.values;
        if(sensorType == Sensor.TYPE_ACCELEROMETER){
            if(shakeSucceed(values)){
                System.out.println("values[0]:"+values[0]);
                System.out.println("values[1]:" + values[1]);
                System.out.println("values[2]"+values[2]);
                vibrator.vibrate(500);
            }
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //当传感器精度改变时的回调方法,do nothing
    }

    //摇一摇是否成功
    private boolean shakeSucceed(float[] sensorValues){
        if(Math.abs(sensorValues[0])>MIN_SHAKE_THRESHOLD ||
                Math.abs(sensorValues[1])>MIN_SHAKE_THRESHOLD ||
                Math.abs(sensorValues[2])>MIN_SHAKE_THRESHOLD){
            return true;
        }
        return  false;
    }
}
