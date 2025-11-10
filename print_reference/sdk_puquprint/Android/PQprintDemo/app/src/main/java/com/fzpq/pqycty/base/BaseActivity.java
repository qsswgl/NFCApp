package com.fzpq.pqycty.base;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;



import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017-03-31.
 */

public abstract class BaseActivity extends AppCompatActivity {

    //public BaseApplication application;
    private MyApplication application;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置布局内容
        setContentView(getLayoutId());
        //初始化黄油刀控件绑定框架
        ButterKnife.bind(this);
        //初始化application
        application = (MyApplication) getApplication();
        //初始化数据
        initData();
        //初始化控件
        initView();
    }

    protected abstract int getLayoutId();
    protected abstract void initView();
    protected abstract void initData();


    protected MyApplication getMyApplication() {
        return application;
    }


}
