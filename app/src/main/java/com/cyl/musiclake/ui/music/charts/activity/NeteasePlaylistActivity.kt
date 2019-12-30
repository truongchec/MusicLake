package com.cyl.musiclake.ui.music.charts.activity

import android.os.Bundle

import com.cyl.musiclake.bean.Playlist
import com.cyl.musiclake.common.Extras
import com.cyl.musiclake.ui.base.BaseActivity
import com.cyl.musiclake.utils.LogUtil


class NeteasePlaylistActivity : BasePlaylistActivity() {
    override fun setEnableMore(): Boolean {
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (mPlaylist == null) {
            mPlaylist = intent.getParcelableExtra(Extras.PLAYLIST)
        }
        mPlaylist?.pid?.let {
            mPresenter?.loadPlaylist(it, mPlaylist?.type)
        }
        LogUtil.d(TAG,"mPlaylist "+mPlaylist.toString())
    }

    override fun retryLoading() {
        super.retryLoading()
        mPlaylist?.pid?.let { mPresenter?.loadPlaylist(it, mPlaylist?.type) }
    }

    override fun getToolBarTitle(): String? {
        if (mPlaylist == null) {
            mPlaylist = intent.getParcelableExtra(Extras.PLAYLIST)
        }
        return mPlaylist?.name
    }

    override fun getmPlaylist(): Playlist? {
        if (mPlaylist == null) {
            mPlaylist = intent.getParcelableExtra(Extras.PLAYLIST)
        }
        return mPlaylist
    }

    companion object {
        private val TAG = "NeteasePlaylistActivity"
    }
}
