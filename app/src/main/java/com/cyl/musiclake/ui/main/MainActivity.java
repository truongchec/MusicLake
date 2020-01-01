package com.cyl.musiclake.ui.main;

import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.cyl.musiclake.R;
import com.cyl.musiclake.bean.Music;
import com.cyl.musiclake.bean.SocketOnlineEvent;
import com.cyl.musiclake.common.Constants;
import com.cyl.musiclake.common.NavigationHelper;
import com.cyl.musiclake.event.CountDownEvent;
import com.cyl.musiclake.event.LoginEvent;
import com.cyl.musiclake.event.MetaChangedEvent;
import com.cyl.musiclake.player.PlayManager;
import com.cyl.musiclake.socket.SocketManager;
import com.cyl.musiclake.ui.UIUtilsKt;
import com.cyl.musiclake.ui.base.BaseActivity;
import com.cyl.musiclake.ui.chat.ChatActivity;
import com.cyl.musiclake.ui.music.importplaylist.ImportPlaylistActivity;
import com.cyl.musiclake.ui.music.search.SearchActivity;
import com.cyl.musiclake.ui.my.BindLoginActivity;
import com.cyl.musiclake.ui.my.LoginActivity;
import com.cyl.musiclake.ui.my.user.User;
import com.cyl.musiclake.ui.my.user.UserStatus;
import com.cyl.musiclake.ui.settings.AboutActivity;
import com.cyl.musiclake.ui.settings.SettingsActivity;
import com.cyl.musiclake.ui.timing.SleepTimerActivity;
import com.cyl.musiclake.ui.widget.CountDownTimerTextView;
import com.cyl.musiclake.utils.CountDownUtils;
import com.cyl.musiclake.utils.CoverLoader;
import com.cyl.musiclake.utils.LogUtil;
import com.cyl.musiclake.utils.SPUtils;
import com.cyl.musiclake.utils.ToastUtils;
import com.cyl.musiclake.utils.Tools;
import com.google.android.material.internal.NavigationMenuView;
import com.google.android.material.navigation.NavigationView;
//import com.tencent.bugly.beta.Beta;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

//import static com.cyl.musiclake.ui.UIUtilsKt.logout;
import static com.cyl.musiclake.ui.UIUtilsKt.updateLoginToken;


public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    Switch mSwitchCountDown;
    CountDownTimerTextView mSwitchCountDownTv;

    public ImageView mImageView;
    CircleImageView mAvatarIcon;
    TextView mName;
    ImageView mShowBindIv;
    CircleImageView mBindNeteaseView;
    TextView mOnlineNumTv;

    private static final String TAG = "MainActivity";

    private boolean mIsCountDown = false;
    private boolean mIsLogin = false;

    Class<?> mTargetClass = null;


    @Override
    protected int getLayoutResID() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        initNavView();
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setItemIconTintList(null);
        disableNavigationViewScrollbars(mNavigationView);
     //   checkLoginStatus();
        initCountDownView();

      //  Beta.checkUpgrade(false, false);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMetaChangedEvent(MetaChangedEvent event) {
        updatePlaySongInfo(event.getMusic());
    }

    private void updatePlaySongInfo(Music music) {
        if (music != null) {
            CoverLoader.INSTANCE.loadBigImageView(this, music, mImageView);
        }
    }

    private void initNavView() {
        View mHeaderView = mNavigationView.getHeaderView(0);
        mImageView = mHeaderView.findViewById(R.id.header_bg);
        mAvatarIcon = mHeaderView.findViewById(R.id.header_face);
        mName = mHeaderView.findViewById(R.id.header_name);
        mBindNeteaseView = mHeaderView.findViewById(R.id.heard_netease);
        mShowBindIv = mHeaderView.findViewById(R.id.show_sync_iv);

    }

    @Override
    protected void initData() {
        updatePlaySongInfo(PlayManager.getPlayingMusic());
        initShortCutsIntent();
    }


    private void initShortCutsIntent() {
        String action = getIntent().getAction();
        if (action != null) {
            LogUtil.d(TAG, "Received start ACTION  " + action);
            if (action.contains("ACTION_SEARCH")) {
                Intent intent = new Intent(this, SearchActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            } else if (action.contains("ACTION_LOCAL")) {
            } else if (action.contains("ACTION_HISTORY")) {
            } else {
                navigateLibrary.run();
            }
        }
    }

    @Override
    protected void initInjector() {
    }


    @Override
    protected void listener() {
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                mSwitchCountDown.setChecked(CountDownUtils.INSTANCE.getType() != 0);
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                if (mTargetClass != null) {
                    turnToActivity(mTargetClass);
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    /**
     *
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.nav_menu_playQueue:
                NavigationHelper.INSTANCE.navigatePlayQueue(this);
                break;
            case R.id.nav_menu_setting:
                mTargetClass = SettingsActivity.class;
                break;

            case R.id.nav_menu_count_down:
                mTargetClass = SleepTimerActivity.class;
                break;
            case R.id.nav_menu_feedback:
                Tools.INSTANCE.feeback(this);
                break;
            case R.id.nav_menu_about:
                mTargetClass = AboutActivity.class;
                break;
            case R.id.nav_menu_equalizer:
                NavigationHelper.INSTANCE.navigateToSoundEffect(this);
                break;
            case R.id.nav_menu_exit:
                mTargetClass = null;
                finish();
                break;
        }
        mDrawerLayout.closeDrawers();
        return false;
    }

    /**
     *
     *
     * @param cls
     */
    private void turnToActivity(Class<?> cls) {
        Intent intent = new Intent(MainActivity.this, cls);
        startActivity(intent);
        mTargetClass = null;
    }


    private Runnable navigateLibrary = () -> {
        Fragment fragment = MainFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment).commitAllowingStateLoss();
    };


    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else if (isNavigatingMain()) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (isNavigatingMain()) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                } else {
                    super.onBackPressed();
                }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isNavigatingMain() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        return (currentFragment instanceof MainFragment);
    }


    private void setUserStatusInfo(Boolean isLogin, User user) {
        mIsLogin = isLogin;
        if (mIsLogin && user != null) {
            SocketManager.INSTANCE.toggleSocket(true);
            String url = user.getAvatar();
            CoverLoader.INSTANCE.loadImageView(this, url, R.drawable.ic_account_circle, mAvatarIcon);
            mName.setText(user.getNick());

        } else {
            SocketManager.INSTANCE.toggleSocket(false);
            mAvatarIcon.setImageResource(R.drawable.ic_account_circle);
            mName.setText(getResources().getString(R.string.app_name));

        }
    }

    /**
     *
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateOnlineInfo(SocketOnlineEvent event) {
        if (mOnlineNumTv != null) {
            mOnlineNumTv.setText(String.valueOf(event.getNum()));
        }
    }

    /**
     *
     */
//    private void checkLoginStatus() {
//        if (UserStatus.getLoginStatus() && !UserStatus.getTokenStatus()) {
//            updateLoginToken();
//        } else if (UserStatus.getLoginStatus()) {
//            //updateUserInfo(new LoginEvent(true, UserStatus.getUserInfo()));
//        }
//        checkBindNeteaseStatus(true);
//    }

    /**
     *
     *
     * @param navigationView
     */
    public static void disableNavigationViewScrollbars(NavigationView navigationView) {
        if (navigationView != null) {
            NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
            if (navigationMenuView != null) {
                navigationMenuView.setVerticalScrollBarEnabled(false);
            }
        }
    }

    /**
     *
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void finishCountDown(CountDownEvent event) {
        if (event.isStop()) {
            mSwitchCountDown.setChecked(false);
            mSwitchCountDownTv.setVisibility(View.GONE);
        } else {
            mSwitchCountDown.setChecked(true);
            mSwitchCountDownTv.setVisibility(View.VISIBLE);
        }
    }



    private void initCountDownView() {
       // View numItem = mNavigationView.getMenu().findItem(R.id.nav_menu_online_num).getActionView();
    //    mOnlineNumTv = numItem.findViewById(R.id.msg_num_tv);
     //   mOnlineNumTv.setText("0");
        View item = mNavigationView.getMenu().findItem(R.id.nav_menu_count_down).getActionView();
        mSwitchCountDown = item.findViewById(R.id.count_down_switch);
        mSwitchCountDownTv = item.findViewById(R.id.count_down_tv);
        mSwitchCountDown.setOnClickListener(v -> UIUtilsKt.showCountDown(MainActivity.this, checked -> {
            mSwitchCountDown.setChecked(checked);
            return null;
        }));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e(TAG, "onNewIntent");
        initData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_CODE_LOGIN) {

            String uid = SPUtils.getAnyByKey(SPUtils.SP_KEY_NETEASE_UID, "");
            if (uid != null && uid.length() > 0) {
                LogUtil.d(TAG, "Bind successfully uid = " + uid);
                //checkBindNeteaseStatus(true);
            }
        }
    }

}
