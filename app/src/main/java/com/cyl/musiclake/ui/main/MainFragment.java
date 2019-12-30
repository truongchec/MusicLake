package com.cyl.musiclake.ui.main;

import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cyl.musiclake.R;
import com.cyl.musiclake.ui.base.BaseFragment;
import com.cyl.musiclake.ui.music.charts.fragment.ChartsDetailFragment;
import com.cyl.musiclake.ui.music.discover.DiscoverFragment;
import com.cyl.musiclake.ui.music.mv.MvFragment;
import com.cyl.musiclake.ui.music.my.MyMusicFragment;

import butterknife.BindView;

@SuppressWarnings("ConstantConditions")
public class MainFragment extends BaseFragment {
    @BindView(R.id.m_viewpager)
    ViewPager mViewPager;
    @BindView(R.id.tabs)
    TabLayout mTabLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    public static MainFragment newInstance() {
        Bundle args = new Bundle();
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void loadData() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.frag_main;
    }

    @Override
    public void initViews() {
        if (getActivity() != null) {
            mToolbar.setTitle(R.string.app_name);
            AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
            appCompatActivity.setSupportActionBar(mToolbar);
            final ActionBar toggle = appCompatActivity.getSupportActionBar();
            if (toggle != null) {
                toggle.setHomeAsUpIndicator(R.drawable.ic_menu_white);
                toggle.setDisplayHomeAsUpEnabled(true);
            }
        }
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setCurrentItem(0);
        setupViewPager(mViewPager);
        mViewPager.setOffscreenPageLimit(3);
    }

    @Override
    protected void initInjector() {

    }

    private void setupViewPager(ViewPager mViewPager) {
        PageAdapter mAdapter = new PageAdapter(getChildFragmentManager());
        mAdapter.addFragment(MyMusicFragment.Companion.newInstance(), getContext().getString(R.string.my));
      //  mAdapter.addFragment(DiscoverFragment.Companion.newInstance(), getContext().getString(R.string.discover));
       // mAdapter.addFragment(ChartsDetailFragment.Companion.newInstance(), getContext().getString(R.string.charts));
     //   mAdapter.addFragment(MvFragment.newInstance(), getContext().getString(R.string.mv));
        mViewPager.setAdapter(mAdapter);
    }

}

