package com.fzpq.pqycty.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.fzpq.pqycty.R;
import com.fzpq.pqycty.activity.print.BlueToothActivity;
import com.fzpq.pqycty.base.BaseActivity;
import com.puqu.sdk.Base.PuQuPrint;
import com.puqu.sdk.PuQuPrintManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/*
登录界面
 */

public class LoginActivity extends BaseActivity {

    @BindView(R.id.tv_type)
    TextView tvType;
    @BindView(R.id.ll_bluetooth)
    LinearLayout llBluetooth;
    @BindView(R.id.tv_send)
    TextView tvSend;


    private PuQuPrintManager pqManager;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        pqManager = getMyApplication().getPrintDeviceManager();
        pqManager.setConnectSuccess(new PuQuPrint.ConnectSuccess() {
            @Override
            public void success() {
                Log.i("pq","success");
                tvType.setText("已连接");
                tvType.setBackgroundResource(R.drawable.bg_color2_void_c3);
                tvType.setTextColor(getResources().getColor(R.color.white));
            }
        });
        pqManager.setConnectFailed(new PuQuPrint.ConnectFailed() {
            @Override
            public void failed() {
                Log.i("pq","failed");
                tvType.setText("未连接");
                tvType.setBackgroundResource(R.drawable.bg_b1_void_c3);
                tvType.setTextColor(getResources().getColor(R.color.text3));
            }
        });
        pqManager.setConnectClosed(new PuQuPrint.ConnectClosed() {
            @Override
            public void closed() {
                Log.i("pq","closed");
                tvType.setText("未连接");
                tvType.setBackgroundResource(R.drawable.bg_b1_void_c3);
                tvType.setTextColor(getResources().getColor(R.color.text3));
            }
        });
        if (pqManager.isConnected()) {
            tvType.setText("已连接");
            tvType.setBackgroundResource(R.drawable.bg_color2_void_c3);
            tvType.setTextColor(getResources().getColor(R.color.white));
        }
        startJob();
    }

    @Override
    protected void initData() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                    , Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS, Manifest.permission.ACCESS_FINE_LOCATION
                    , Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }

    @OnClick({R.id.ll_bluetooth, R.id.tv_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_bluetooth:
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, BlueToothActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_send:
                startPrint();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (pqManager.isConnected()) {
            tvType.setText("已连接");
            tvType.setBackgroundResource(R.drawable.bg_color2_void_c3);
            tvType.setTextColor(getResources().getColor(R.color.white));
        }
    }

    private void startJob() {
        pqManager.startJob(400,-1);

        pqManager.setLeft(0);
        pqManager.setFontSize(36);
        pqManager.setAlignment(1);
        pqManager.setBold(true);
        pqManager.addText("医疗保险结算单");
        pqManager.addBlank(16);

        pqManager.setLeft(16);
        pqManager.setBold(false);
        pqManager.setFontSize(24);
        pqManager.setAlignment(0);
        pqManager.addText("就诊号:0000001");
        pqManager.addText("结算日期：2022-05-24");
        pqManager.addText("疾病诊断：伤寒普通型");
        pqManager.addText("- - - - - - - - - - - - - - - - -");
        //记录位置
        float position = pqManager.getPosition();
        pqManager.addText("药品名");
        pqManager.setPosition(position);
        pqManager.setLeft(250);
        pqManager.addText("价格");
        pqManager.setLeft(16);

        //记录位置
        float position1 = pqManager.getPosition();
        pqManager.addText("999感冒灵");
        pqManager.setPosition(position1);
        pqManager.setLeft(250);
        pqManager.addText("￥19.99");
        pqManager.setLeft(16);

        //记录位置
        float position2 = pqManager.getPosition();
        pqManager.addText("云南白药");
        pqManager.setPosition(position2);
        pqManager.setLeft(250);
        pqManager.addText("￥29.50");
        pqManager.setLeft(16);

        pqManager.addText("- - - - - - - - - - - - - - - - -");
        pqManager.addText("账号余额:1000");
        pqManager.addText("付款人签名：张三");
        pqManager.addText("联系电话：123456789");
        pqManager.setLeft(50);
        pqManager.addBlank(24);
        pqManager.addBarCode("12345678",200,100);
        pqManager.setLeft(16);
        pqManager.addBlank(80);
    }

    private void startPrint() {
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> e) {
                pqManager.printJob();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Integer i) {
                    }
                });
    }
}
