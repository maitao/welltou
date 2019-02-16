package com.welltou.btprintdemo;


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


@SuppressLint("NewApi")
public class BtchooesActivity extends AppCompatActivity {
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_btchoose);

        setTitle("选择蓝牙设备");

        mHandler = new Handler();

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "没有蓝牙", Toast.LENGTH_SHORT).show();
            finish();
        }

        // 初始化一个蓝牙适配器。对API 18级以上，可以参考 bluetoothmanager。
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        //  检查是否支持蓝牙的设备。
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "设备不支持", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        /*隐式打开蓝牙*/
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }


        btlist = (ListView) findViewById(R.id.list);
        listItem = new ArrayList<HashMap<String, Object>>();
        adapter = new SimpleAdapter(this, listItem, android.R.layout.simple_expandable_list_item_2,
                new String[]{"name", "andrass"}, new int[]{android.R.id.text1, android.R.id.text2});
        btlist.setAdapter(adapter);


        btlist.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int arg2, long arg3) {
                // TODO Auto-generated method stub
                BluetoothDevice device = (BluetoothDevice) listItem.get(arg2).get("device");
                Log.e("a", "点击的按钮" + arg2 + device.getAddress() + "cacaca");

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                intent.putExtra("andrass", device.getAddress());
                intent.putExtra("name", device.getName());


                startActivity(intent);
            }
        });

        Button btstart = (Button) findViewById(R.id.btstart);
        bar = (ProgressBar) findViewById(R.id.bar);
        bar.setVisibility(View.GONE);
        btstart.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                bar.setVisibility(View.VISIBLE);
                startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                bar.setVisibility(View.GONE);

            }
        });
        fillAdapter();


    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        //确保蓝牙是在设备上启用。如果当前没有启用蓝牙，
        //火意图显示一个对话框询问用户授予权限以使它。
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
        fillAdapter();

    }


    private ListView btlist;
    private ArrayList<HashMap<String, Object>> listItem;
    private SimpleAdapter adapter;
    private ProgressBar bar;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 将菜单；这将项目添加到动作条如果真的存在。
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) //得到被点击的item的itemId
        {
            case R.id.action_back:  //对应的ID就是在add方法中所设定的Id
                this.finish();
                break;
            case 2:
                break;
        }
        return true;
    }

    /**
     * 从所有已配对设备中找出打印设备并显示
     */
    private void fillAdapter() {
        //推荐使用 BluetoothUtil.getPairedPrinterDevices()
        listItem.clear();
        adapter.notifyDataSetChanged();

        List<BluetoothDevice> printerDevices = getPairedDevices();
        for (int i = 0; i < printerDevices.size(); i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            BluetoothDevice device = printerDevices.get(i);
            map.put("name", device.getName());
            map.put("andrass", device.getAddress());
            map.put("device", device);
            listItem.add(map);
        }
        adapter = new SimpleAdapter(this, listItem, android.R.layout.simple_expandable_list_item_2,
                new String[]{"name", "andrass"}, new int[]{android.R.id.text1, android.R.id.text2});
        btlist.setAdapter(adapter);
//        adapter.clear();
//        adapter.addAll(printerDevices);
    }

    /**
     * 获取所有已配对的设备
     */
    public static List<BluetoothDevice> getPairedDevices() {
        List deviceList = new ArrayList<>();
        Set<BluetoothDevice> pairedDevices = BluetoothAdapter.getDefaultAdapter().getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                deviceList.add(device);
            }
        }
        return deviceList;
    }


}
