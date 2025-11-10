package com.fzpq.pqycty.activity.print;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.fzpq.pqycty.R;
import com.fzpq.pqycty.adapter.PrintDeviceAdapter;
import com.fzpq.pqycty.base.BaseActivity;
import com.puqu.sdk.Base.PuQuPrint;
import com.puqu.sdk.PuQuPrintManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;

public class BlueToothActivity extends BaseActivity {
    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.rl_nowdevice)
    RelativeLayout rlNowdevice;
    @BindView(R.id.tv_nulldevices)
    TextView tvNulldevices;
    @BindView(R.id.rv_devices)
    RecyclerView rvDevices;
    @BindView(R.id.sl_bluetooth)
    SwipeRefreshLayout slBluetooth;

    private PrintDeviceAdapter adapter;
    private List<BluetoothDevice> deviceList;
    private static int REQUEST_ENABLE_BT = 115;

    //蓝牙适配
    private BluetoothAdapter mBtAdapter;

    private PuQuPrintManager printDeviceManager;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_bluetooth;
    }

    @Override
    protected void initView() {
        rvDevices.setLayoutManager(new LinearLayoutManager(this));
        rvDevices.setAdapter(adapter);
        rvDevices.setNestedScrollingEnabled(false);

        printDeviceManager = getMyApplication().getPrintDeviceManager();
        printDeviceManager.setConnectSuccess(new PuQuPrint.ConnectSuccess() {
            @Override
            public void success() {
                tvName.setText(printDeviceManager.getAddress());
//                tvMac.setText(printDevice.getDeviceAddress());
                tvType.setText("已连接");
                tvType.setBackgroundResource(R.drawable.bg_color2_void_c3);
                tvType.setTextColor(getResources().getColor(R.color.white));
                rlNowdevice.setVisibility(View.VISIBLE);
            }
        });
        printDeviceManager.setConnectFailed(new PuQuPrint.ConnectFailed() {
            @Override
            public void failed() {

                tvType.setText("未连接");
                tvType.setBackgroundResource(R.drawable.bg_b1_void_c3);
                tvType.setTextColor(getResources().getColor(R.color.text3));
                rlNowdevice.setVisibility(View.GONE);
                tvName.setText("");
            }
        });
        printDeviceManager.setConnectClosed(new PuQuPrint.ConnectClosed() {
            @Override
            public void closed() {
                tvType.setText("未连接");
                tvType.setBackgroundResource(R.drawable.bg_b1_void_c3);
                tvType.setTextColor(getResources().getColor(R.color.text3));
                rlNowdevice.setVisibility(View.GONE);
                tvName.setText("");
            }
        });
        if (printDeviceManager.isConnected()) {
            tvName.setText(printDeviceManager.getBluetoothAdapter().getName());
//            tvMac.setText(printDevice.getDeviceAddress());

            tvType.setText("已连接");
            tvType.setBackgroundResource(R.drawable.bg_color2_void_c3);
            tvType.setTextColor(getResources().getColor(R.color.white));
            rlNowdevice.setVisibility(View.VISIBLE);
        }
        slBluetooth.setColorSchemeResources(R.color.sl1, R.color.sl2, R.color.sl3, R.color.sl4);
        slBluetooth.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDevice();
            }
        });
        slBluetooth.setRefreshing(true);

        if (printDeviceManager.isConnected()) {
            tvName.setText(printDeviceManager.getAddress());
            tvType.setText("已连接");
            tvType.setBackgroundResource(R.drawable.bg_color2_void_c3);
            tvType.setTextColor(getResources().getColor(R.color.white));
            rlNowdevice.setVisibility(View.VISIBLE);
        }
        getDevice();
    }


    //过滤蓝牙
    private boolean judge(BluetoothDevice device, List<BluetoothDevice> devices) {
        if (device == null) {
            return true;
        }
        if (TextUtils.isEmpty(device.getName())) {
            return true;
        }
        for (int i = 0; i < devices.size(); i++) {
            if (devices.get(i).getAddress().equals(device.getAddress())) {
                return true;
            }
        }
        return false;
    }

    private void getDevice() {
        deviceList.clear();
        tvNulldevices.setVisibility(View.GONE);
        rvDevices.setVisibility(View.VISIBLE);
        // Get the list of attached devices
        Set<BluetoothDevice> bondedDevices = mBtAdapter.getBondedDevices();
        for (BluetoothDevice device : bondedDevices) {
                if (judge(device, deviceList))
                    continue;
                deviceList.add(device);
                mBtAdapter.cancelDiscovery();
         }
        adapter.setDatas(deviceList);

        if (!mBtAdapter.isEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
            return;
        }
        //判断是否正在搜索
        if (mBtAdapter.isDiscovering()) {
            //停止搜索设备
            mBtAdapter.cancelDiscovery();
        }
        //开始搜索设备
        mBtAdapter.startDiscovery();


    }

    //蓝牙开启回调
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                getDevice();
            } else {
                if (slBluetooth.isRefreshing()) {
                    slBluetooth.setRefreshing(false);
                }
                if (deviceList.size() == 0) {
                    rvDevices.setVisibility(View.GONE);
                    tvNulldevices.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    //搜索蓝牙回调
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //搜索蓝牙
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (!judge(device, deviceList)) {
                    deviceList.add(device);
                    adapter.setDatas(deviceList);
                }

            }//搜索结束
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (slBluetooth.isRefreshing()) {
                    slBluetooth.setRefreshing(false);
                }
                if (deviceList.size() == 0) {
                    rvDevices.setVisibility(View.GONE);
                    tvNulldevices.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    @Override
    protected void initData() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        deviceList = new ArrayList<>();

        adapter = new PrintDeviceAdapter(this);
        adapter.setOnClickItemListener(new PrintDeviceAdapter.onClickItemListener() {
            @Override
            public void onClick(int position) {
                printDeviceManager.openPrinter(deviceList.get(position).getAddress());
            }
        });

        //蓝牙搜索监听
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);
    }

    //返回键
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.iv_return, R.id.rl_nowdevice})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_nowdevice:
                printDeviceManager.closePrinter();
                break;
            case R.id.iv_return:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(mReceiver);
    }

}
