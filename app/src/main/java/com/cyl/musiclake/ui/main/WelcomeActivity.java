package com.cyl.musiclake.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.cyl.musicapi.BaseApiImpl;
import com.cyl.musiclake.BuildConfig;
import com.cyl.musiclake.MusicApp;
import com.cyl.musiclake.common.Constants;
import com.google.android.material.snackbar.Snackbar;

import android.widget.ImageView;

import com.cyl.musiclake.R;
import com.cyl.musiclake.ui.base.BaseActivity;
import com.cyl.musiclake.utils.SPUtils;
import com.cyl.musiclake.utils.SystemUtils;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.util.FileDownloadLog;
import com.sina.weibo.sdk.WbSdk;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.tencent.tauth.Tencent;

import butterknife.BindView;


public class WelcomeActivity extends BaseActivity {

    @BindView(R.id.wel_container)
    ConstraintLayout container;
    @BindView(R.id.iv_header_cover)
    ImageView heardCoverIv;
    RxPermissions rxPermissions;

    private final String[] mPermissionList = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,

            Manifest.permission.READ_PHONE_STATE,
    };

    @Override
    protected void listener() {

    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_welcome;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        //初始化WebView
      //  BaseApiImpl.INSTANCE.initWebView(MusicApp.getInstance());
      //  initLogin();

        rxPermissions = new RxPermissions(this);
        if (SystemUtils.isMarshmallow()) {
            checkPermissionAndThenLoad();
        } else {
            initWelcome();
        }
    }


    private void initLogin() {

        WbSdk.install(this, new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE));

        MusicApp.mTencent = Tencent.createInstance(Constants.APP_ID, this);

//        SocketManager.INSTANCE.initSocket();
    }


    @Override
    protected void initInjector() {

    }


    @SuppressLint("CheckResult")
    private void checkPermissionAndThenLoad() {
        rxPermissions.request(mPermissionList)
                .subscribe(granted -> {
                    if (granted) {
                        initWelcome();
                    } else {
                        Snackbar.make(container, getResources().getString(R.string.permission_hint),
                                Snackbar.LENGTH_INDEFINITE)
                                .setAction(getResources().getString(R.string.sure), view -> checkPermissionAndThenLoad()).show();
                    }
                });
    }


    private void initWelcome() {
        boolean isFirst = SPUtils.getAnyByKey(SPUtils.SP_KEY_FIRST_COMING, true);
        if (isFirst) {
            getCoverImageUrl();
            SPUtils.putAnyCommit(SPUtils.SP_KEY_FIRST_COMING, false);
        } else {
            mHandler.postDelayed(WelcomeActivity.this::startMainActivity, 1000);
        }
    }


    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(getIntent().getAction());
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    private void getCoverImageUrl() {
        mHandler.postDelayed(WelcomeActivity.this::startMainActivity, 1000);
    }

}
