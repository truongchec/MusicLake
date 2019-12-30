package com.cyl.musiclake.ui.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cyl.musiclake.MusicApp;
import com.cyl.musiclake.R;
import com.cyl.musiclake.di.component.DaggerFragmentComponent;
import com.cyl.musiclake.di.component.FragmentComponent;
import com.cyl.musiclake.di.module.FragmentModule;
import com.cyl.musiclake.utils.AnimationUtils;
import com.cyl.musiclake.utils.LogUtil;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.trello.rxlifecycle2.components.support.RxFragment;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.cyl.musiclake.utils.AnimationUtils.animateView;


public abstract class BaseLazyFragment<T extends BaseContract.BasePresenter> extends RxFragment implements BaseContract.BaseView {
    private boolean isLazyLoaded;
    private boolean isPrepared;

    @Nullable
    @Inject
    protected T mPresenter;
    protected FragmentComponent mFragmentComponent;
    public View rootView;
    private Unbinder unbinder;
    @Nullable
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @Nullable
    @BindView(R.id.empty_state_view)
    public View emptyStateView;
    @Nullable
    @BindView(R.id.error_panel)
    public View errorPanelRoot;
    @Nullable
    @BindView(R.id.error_button_retry)
    public Button errorButtonRetry;
    @Nullable
    @BindView(R.id.error_message_view)
    public TextView errorTextView;
    @Nullable
    @BindView(R.id.tv_empty)
    public TextView emptyTextView;
    @Nullable
    @BindView(R.id.loading_progress_bar)
    public ProgressBar loadingProgressBar;
    @Nullable
    @BindView(R.id.swipe_refresh)
    public SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFragmentComponent();
        initInjector();
        attachView();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null)
            rootView = inflater.inflate(getLayoutId(), container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    private void initSwipeLayout() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.RED, Color.YELLOW);
            mSwipeRefreshLayout.setEnabled(true);
            mSwipeRefreshLayout.setOnRefreshListener(() -> {
                LogUtil.d("Pull down to refresh");
                new Handler().postDelayed(() -> {
                    if (mSwipeRefreshLayout != null) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                    lazyLoad();
                }, 1000);
            });
        }
    }

    protected void listener() {
    }

    protected void retryLoading() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        detachView();
    }

    public abstract int getLayoutId();

    public abstract void initViews();

    protected abstract void initInjector();

    protected void loadData() {
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            setUserVisibleHint(true);
        }
    }



    private void initFragmentComponent() {
        mFragmentComponent = DaggerFragmentComponent.builder()
                .applicationComponent(MusicApp.getInstance().getApplicationComponent())
                .fragmentModule(new FragmentModule(this))
                .build();
    }


    private void attachView() {
        if (mPresenter != null) {
            mPresenter.attachView(this);
        }
    }


    private void detachView() {
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    @Override
    public void showLoading() {
        if (mSwipeRefreshLayout != null) mSwipeRefreshLayout.setRefreshing(true);
        if (emptyStateView != null) animateView(emptyStateView, false, 150);
        if (loadingProgressBar != null) animateView(loadingProgressBar, true, 400);
        animateView(errorPanelRoot, false, 150);
    }

    @Override
    public void hideLoading() {
        if (mSwipeRefreshLayout != null) mSwipeRefreshLayout.setRefreshing(false);
        if (emptyStateView != null) animateView(emptyStateView, false, 150);
        if (loadingProgressBar != null) animateView(loadingProgressBar, false, 0);
        animateView(errorPanelRoot, false, 150);
    }

    @Override
    public void showEmptyState() {
        if (emptyStateView != null) animateView(emptyStateView, true, 200);
        if (loadingProgressBar != null) animateView(loadingProgressBar, false, 0);
        animateView(errorPanelRoot, false, 150);
    }

    @Override
    public void showError(String message, boolean showRetryButton) {
        hideLoading();
        if (errorTextView != null)
            errorTextView.setText(message);
        if (errorButtonRetry != null)
            errorButtonRetry.setOnClickListener(v -> retryLoading());
        if (showRetryButton) animateView(errorButtonRetry, true, 600);
        else animateView(errorButtonRetry, false, 0);
        animateView(errorPanelRoot, true, 300);
    }


    @Override
    public <T> LifecycleTransformer<T> bindToLife() {
        return this.bindToLifecycle();
    }

    @Nullable
    @Override
    public Context getContext() {
        return super.getContext();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        initSwipeLayout();
        initViews();
        listener();
        loadData();
        isPrepared = true;
        //只有Fragment onCreateView好了，
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogUtil.d("BaseLazy", getClass().getName() + "isVisibleToUser = " + isVisibleToUser);
        if (isVisibleToUser) {
            lazyLoad();
        }
    }


    private void lazyLoad() {

        if (getUserVisibleHint() && isPrepared && !isLazyLoaded) {
            onLazyLoad();
            LogUtil.d("BaseLazy", getClass().getName() + "getLifecycle() =" + getLifecycle().getCurrentState() + "isPrepared =" + isPrepared + " isLazyLoaded " + getUserVisibleHint());
            isLazyLoaded = true;
        }
    }

    @UiThread
    public abstract void onLazyLoad();

}
