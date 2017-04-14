package com.example.digital.sensor_mayu;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public Button btnlin;       // start - stop
    public Button btnstop;      // go on - pause
    public String string = "status: ";
    public TextView status;
    public TextView tvshow;
    public SensorManager sensorManager;
    public Sensor ssrlin;
    public boolean st = false;      // st == true --> work，boolean variable of the left button
    public boolean ps = false;      // ps == true ---> stop,，boolean variable of the right button

    public float xacc = 0;
    public float yacc = 0;
    public float zacc = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvshow = (TextView) findViewById(R.id.textView);
        status = (TextView) findViewById(R.id.status);
        btnlin = (Button) findViewById(R.id.lin);
        btnstop = (Button) findViewById(R.id.stop);

        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        ssrlin = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        btnlin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 按下start
                if(!st){    // if previous status is off
                    st = true;
                    string += "is_on ";
                    status.setText(string);
                    // 注册
                    sensorManager.registerListener(sensorEventListener,ssrlin,SensorManager.SENSOR_DELAY_NORMAL);
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        string += "onstart ";
        status.setText(string);
    }

    @Override
    protected void onResume(){
        super.onResume();
        string += "onresume ";
        status.setText(string);
    }



    /*************     Running  part start     ***************/

    private View.OnClickListener startPause = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            st = !st;       // switch on <--> off
            if(st){
                if (sensorManager == null){
                    sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
                    ssrlin = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);   // 注册
                    sensorManager.registerListener(sensorEventListener,ssrlin,SensorManager.SENSOR_DELAY_NORMAL);
                    string += "is_on ";
                }
            } else {
                if (sensorManager != null) {
                    sensorManager.unregisterListener(sensorEventListener);
                    sensorManager = null;   // 解除监听器注册
                    string += "is_off ";
                    tvshow.setText("linear acceleration: \n x acc:0\n y acc:0\n z acc:0");
                }
            }
            status.setText(string);
        }
    };


    private SensorEventListener sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event_lin) {

                btnlin.setOnClickListener(startPause);
                String linacc = "linear acceleration: \n";
                String x = "x acc:" ;
                String y = "y acc:";
                String z = "z acc:";

                btnstop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ps = !ps;
                    }
                });

                if(event_lin.sensor == null)    return;
                if(event_lin.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                    if(!ps){        // "!ps" means "working"
                        xacc = event_lin.values[0];     // 更新数据
                        yacc = event_lin.values[1];
                        zacc = event_lin.values[2];
               //store data         
                    x2 = x2 + xacc + "\n";
                    y2 = y2 + yacc + "\n";
                    z2 = z2 + zacc + "\n";
                    String str = Environment.getExternalStorageDirectory() + File.separator + "Group6.txt";
                    File file = new File(str);//创建一个文件


                    String date=sdf.format(new Date());//时间
                    try{
                        if(!file.exists()){
                            file.createNewFile();
                        }
                        FileWriter fw = new FileWriter(file,true);
                        fw.write(date+"\n");
                        fw.write(x2);
                        fw.write(y2);
                        fw.write(z2+"\n");
                        fw.flush();
                        fw.close();
                        string += "!!!!! ";
                        status.setText(string);
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                    }
                    x = x + xacc + "\n";
                    y = y + yacc + "\n";
                    z = z + zacc + "\n";
                }

             }

            @Override
            public void onAccuracyChanged(Sensor sensor,int accuracy) {
            }

        };

    /*************    Running  part off   **************/


    @Override
    protected void onPause() {
        super.onPause();
        string += "onpause ";
        status.setText(string);
    }

    @Override
    protected void onStop() {
        super.onStop();
        string += "onstop ";
        status.setText(string);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sensorManager != null) {
            sensorManager.unregisterListener(sensorEventListener);
            sensorManager = null;   // 解除监听器注册
            string += "is_off ";
        }
        string += "ondestroy";
        status.setText(string);
    }



}
