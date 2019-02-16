package com.welltou.btprintdemo;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.welltou.btprintdemo.BOLUTEKBLE.PrintTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Button button_connect;
    Button button_disconnect;
    Button button_btlist;
    static TextView edit_receive;
    EditText edit_send;
    Button button_send;

    SoundPool snd = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
    int hitOk;
    int sendOk;
    int disconnectOk;
    int hitButton;

    String address = null;
    public static BluetoothDevice btDev;
    public static BluetoothSocket btSocket;
    static final String SPP_UUID = "00001101-0000-1000-8000-00805F9B34FB";
    private BluetoothAdapter myBluetoothAdapter = null;
    BluetoothAdapter adapter;
    boolean bluetoothConnectFlag = false;
    boolean hitFlag = false;
    Method m;

    private Timer timer = new Timer();
    private TimerTask task;
    private Timer timer_surveillance = new Timer();
    private TimerTask task_surveillance;
    byte[] cmd = new byte[]{0x23, 0x30, 0x04, 0x0D};
    private String receiver;
    private int leng;
    byte[] buffer = new byte[20];
    Thread thread;
    private String mDeviceAddress;
    private String mDeviceName;
    Toast toast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        mDeviceAddress = intent.getStringExtra("andrass");
        mDeviceName = intent.getStringExtra("name");
        button_connect = (Button) findViewById(R.id.bn1);
        button_disconnect = (Button) findViewById(R.id.bn2);
        button_btlist = (Button) findViewById(R.id.bn33);

        button_send = (Button) findViewById(R.id.bn3);

        edit_send = (EditText) findViewById(R.id.ed2);
        edit_receive = (TextView) findViewById(R.id.ed1);

        hitOk = snd.load(MainActivity.this, R.raw.ping_short, 5);
        sendOk = snd.load(MainActivity.this, R.raw.send, 5);
        disconnectOk = snd.load(MainActivity.this, R.raw.button20, 5);
        hitButton = snd.load(MainActivity.this, R.raw.button44, 5);

        handler.postDelayed(runnable, 1000 * 5);

        button_connect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mDeviceAddress == null) {
                    toast = Toast.makeText(getApplicationContext(),
                            "请在蓝牙列表选择蓝牙设备！", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    if (bluetoothConnectFlag) {
                        if (btSocket == null) {
                            connectBluetooth();
                        } else {
                            edit_receive.setText("蓝牙连接成功！");
                        }
                    } else {
                        connectBluetooth();
                    }
                }
            }
        });

        button_disconnect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                cleanBt();
                toast = Toast.makeText(getApplicationContext(),
                        "断开蓝牙成功！", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });

        button_btlist.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BtchooesActivity.class);
                startActivity(intent);
            }
        });

        button_send.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (btSocket != null) {

                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test2);

                    PrintTest.print(btSocket, edit_send.getText().toString(),bitmap);


                    toast = Toast.makeText(getApplicationContext(),
                            "打印命令已发送！", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    toast = Toast.makeText(getApplicationContext(),
                            "蓝牙还没连接成功！", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });

    }

    private void connectBluetooth() {
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (myBluetoothAdapter == null || mDeviceAddress == null) {
            return;
        }
        if (!myBluetoothAdapter.isEnabled() && !hitFlag) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(intent);
            hitFlag = true;
        } else {
            try {
                address = mDeviceAddress;
                UUID uuid = UUID.fromString(SPP_UUID);
                adapter = BluetoothAdapter.getDefaultAdapter();
                btDev = adapter.getRemoteDevice(address);
                adapter.cancelDiscovery();
                btSocket = null;
                btSocket = btDev.createRfcommSocketToServiceRecord(uuid);
                Log.i("运行标记", "运行到了btSocket.connect()之前......");
                btSocket.connect();
                bluetoothConnectFlag = btSocket.isConnected();
            } catch (IOException e) {
                bluetoothConnectFlag = false;
                Log.e("蓝牙连接异常", "异常情况");
                e.printStackTrace();
            }
        }
    }

    /**
     * 覆写返回键监听
     **/
    public void onBackPressed() {
        ext();
    }

    /**
     * 该方法用于退出程序
     **/
    private void ext() {
        new AlertDialog.Builder(MainActivity.this).setTitle("退出系统提示：")
                .setMessage("确定要退出系统吗？").setPositiveButton("是",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cleanBt();
                        int nPid = android.os.Process.myPid();
                        android.os.Process.killProcess(nPid);
                        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                        am.restartPackage(getPackageName());
                    }
                }).setNegativeButton("否", null).show();
    }

    private void cleanBt() {
        try {
            if (btSocket != null) {
                try {
                    btSocket.close();
                    btSocket = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (myBluetoothAdapter.isEnabled()) {
                try {
                    myBluetoothAdapter.disable();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            int len = is.available();
            if(len != 0){
//                while ((line = reader.readLine()) != null) {
//                    sb.append(line + "/n");
//                    break;
//                }

                byte[] buffer = new byte[1024];
                int bytes;
                while (true) {  //无限循环读取数据
                    try {
                        // Read from the InputStream
                        if(is.available()!=0){
                            if ((bytes = is.read(buffer)) > 0) {
                                byte[] buf_data = new byte[bytes];
                                for (int i = 0; i < bytes; i++) {
                                    buf_data[i] = buffer[i];
                                }
                                String s = new String(buf_data);
                                sb.append(s);
                            }
                        } else {
                            break;
                        }

                    } catch (IOException e) {
                        try {
                            is.close();
                        } catch (IOException e1) {
                        }
                        break;  //异常的时候break跳出无限循环
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
//            try {
//                //is.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
        return sb.toString();
    }

    private Handler handler = new Handler();
    public InputStream inStream = null;

    private Runnable runnable = new Runnable() {
        public void run() {
            try {
                if(btSocket != null){
                    inStream=btSocket.getInputStream();
                    if(inStream != null){
                        String ss = convertStreamToString(inStream);

                        if(!"".equals(ss) && ss != null){
                            edit_receive.setText(("接收数据:"+ss).toCharArray(), 0, ss.length()+5);
                            toast = Toast.makeText(getApplicationContext(),
                                    "*设备接收数据*"+ss, Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                }
                handler.postDelayed(this, 100);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };
}
