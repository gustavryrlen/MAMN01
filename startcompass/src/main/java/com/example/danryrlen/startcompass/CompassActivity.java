package com.example.danryrlen.startcompass;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;


public class CompassActivity extends ActionBarActivity implements SensorEventListener {


    Float angle;  // View to draw a compass

    public class CustomDrawableView extends View {
        Paint lines = new Paint();
        Paint course = new Paint();

        public CustomDrawableView(Context context) {
            super(context);
            lines.setColor(0xff444444);
            lines.setStyle(Style.STROKE);
            lines.setStrokeWidth(1);
            lines.setAntiAlias(true);

            course.setColor(0xff000000);
            course.setStyle(Style.STROKE);
            course.setStrokeWidth(1);
            course.setAntiAlias(true);
        }

        protected void onDraw(Canvas canvas) {
            int width = getWidth();
            int height = getHeight();
            int centerx = width/2;
            int centery = height/2;


            if (angle != null)
                canvas.rotate(-angle*360/(2*3.14159f), centerx, centery);

            lines.setColor(0xff888888);

            canvas.drawLine(centerx, -10000, centerx, +10000, lines);
            canvas.drawLine(-10000, centery, +10000, centery, lines);

            canvas.drawText("North", centerx, centery-150, course);
            canvas.drawText("East", centerx+150, centery, course);
            canvas.drawText("West", centerx-180, centery, course);
            canvas.drawText("South", centerx, centery+150, course);

            canvas.drawCircle(centerx, centery, 120, course);

            lines.setColor(0xff444444);
        }
    }

    CustomDrawableView mCustomDrawableView;
    private SensorManager mSensorManager;
    Sensor accelerometer;
    Sensor magnetometer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCustomDrawableView = new CustomDrawableView(this);
        setContentView(mCustomDrawableView);
         // Register the sensor listeners
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {  }

    float[] mGravity;
    float[] mGeomagnetic;
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            mGravity = event.values;
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            mGeomagnetic = event.values;
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                angle = orientation[0]; // orientation contains: azimut, pitch and roll
            }
        }
        mCustomDrawableView.invalidate();
    }
}