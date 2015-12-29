package com.example.its28.helloworld;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity {
    //View
    private TextView textview;
    private TextView textView1;
    private TextView textView2;
    private Button button;

    //紀錄與計算讀出來的值
    private float[] recordX = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private float[] recordY = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private static float accelerationX = 0;
    private static float accelerationY = 0;
    private static float velocityX = 0;
    private static float velocityY = 0;
    private static float positionX = 0;
    private static float positionY = 0;
    private int count = 0;
    private float SENSOR_DELAY_TIME = (float) (1 / 1000);

    //下拉式選單
    private Context mContext;
    private Spinner spinner;
    private ArrayAdapter<String> list;
    private String[] chose = {"x", "y", "z"};
    private int option = 0;

    //Sensor的Thread
    private Handler mThreadHandler;
    private HandlerThread mThread;

    //SensorManager
    private SensorManager sm;
    private SensorEventListener listener;

    //GraphView
    private GraphView mGraphView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FindViews();

        //Sensor的Thread
        mThread = new HandlerThread("name");
        mThread.start();
        mThreadHandler = new Handler(mThread.getLooper());
        mThreadHandler.post(r1);

        //註冊SensorManager
        sm = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);

        //初始GraphView
        mGraphView = (GraphView) findViewById(R.id.graph);

        //按鈕
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGraphView.Invalidate();
            }
        });

        //下拉式選單
        mContext = this.getApplicationContext();
        list = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, chose);
        spinner.setAdapter(list);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                switch (position) {
                    case 0:
                        option = 0;
                        break;
                    case 1:
                        option = 1;
                        break;
                    default:
                        option = 2;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void FindViews() {
        textview = (TextView) this.findViewById(R.id.textView);
        textView1 = (TextView) this.findViewById(R.id.textView1);
        textView2 = (TextView) this.findViewById(R.id.textView2);
        button = (Button) this.findViewById(R.id.button);
        spinner = (Spinner) findViewById(R.id.spinner);
    }

    public Runnable r1 = new Runnable() {
        @Override
        public void run() {
            listener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    final float[] values = event.values;
                    showValue(values);
                    mGraphView.addDataPoint(positionX, positionY);
                    textview.setText("Success");
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                }
            };
        }
    };

    public void showValue(final float[] values) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (count < 9) {
                    recordX[count] = values[0];
                    recordY[count] = values[1];
                } else {
                    accelerationX = Filter(recordX);
                    accelerationY = Filter(recordY);
                    Log.d("acceleration", String.valueOf(accelerationX));
                    Calculate();
                    Log.d("velocity", String.valueOf(velocityX));
                    Log.d("position", String.valueOf(positionX));
                    textView1.setText("x position = " + String.valueOf(positionX));
                    textView2.setText("y position = " + String.valueOf(positionY));
                }
            }
        });
    }

    //計算位移
    public void Calculate() {
        float vxt = accelerationX + velocityX;
        Log.d("calculate", String.valueOf((((velocityX + vxt) / 100) / 2)));
        positionX = (velocityX + vxt) + positionX;
        velocityX = vxt;
        float vyt = accelerationY + velocityY;
        positionY = (((velocityY + vyt) * (SENSOR_DELAY_TIME * 10)) / 2) + positionY;
        velocityY = vyt;
    }

    //整理資料
    private static float Filter(float[] record) {
        float tmp;
        int i, j;
        for (i = record.length - 1; i > 0; i--) {
            for (j = 0; j < i; j++) {
                if (record[j] > record[j + 1]) {
                    tmp = record[j];
                    record[j] = record[j + 1];
                    record[j + 1] = tmp;
                }
            }
        }
        return (((float) ((int) ((record[(record.length / 2)]) * 100))) / 100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mThreadHandler != null) {
            mThreadHandler.removeCallbacks(r1);
        }

        if (mThread != null) {
            mThread.quit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        sm.registerListener(listener, sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_FASTEST);
        //set sensor

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }


}
