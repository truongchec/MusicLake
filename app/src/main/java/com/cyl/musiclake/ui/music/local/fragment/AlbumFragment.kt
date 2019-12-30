package com.cyl.musiclake.ui.music.local.fragment

import android.os.Bundle

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

import com.cyl.musiclake.R
import com.cyl.musiclake.ui.base.BaseLazyFragment
import com.cyl.musiclake.bean.Album
import com.cyl.musiclake.ui.music.local.adapter.AlbumAdapter
import com.cyl.musiclake.ui.music.local.contract.AlbumsContract
import com.cyl.musiclake.ui.music.local.presenter.AlbumPresenter

import java.util.ArrayList

import butterknife.BindView
import com.cyl.musiclake.event.FileEvent
import kotlinx.android.synthetic.main.fragment_recyclerview_notoolbar.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class AlbumFragment : BaseLazyFragment<AlbumPresenter>(), AlbumsContract.View {
    private var mAdapter: AlbumAdapter? = null
    private val albumList = ArrayList<Album>()


    override fun getLayoutId(): Int {
        return R.layout.fragment_recyclerview_notoolbar
    }

    /**

     */
    override fun initViews() {
        mAdapter = AlbumAdapter(albumList)
        recyclerView.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.adapter = mAdapter
        mAdapter?.bindToRecyclerView(recyclerView)
    }

    override fun initInjector() {
        mFragmentComponent.inject(this)
    }

    override fun listener() {}

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onLazyLoad() {
        mPresenter?.loadAlbums("all")
    }

    override fun showLoading() {
        super.showLoading()
    }

    override fun hideLoading() {
        super.hideLoading()
    }

    override fun showAlbums(albumList: List<Album>) {
        mAdapter?.setNewData(albumList)
        if (albumList.isEmpty()) {
            mAdapter?.setEmptyView(R.layout.view_song_empty, recyclerView)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun updateDownloadEvent(event: FileEvent) {
        mPresenter?.loadAlbums("all")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }


    companion object {

        fun newInstance(): AlbumFragment {

            val args = Bundle()
            val fragment = AlbumFragment()
            fragment.arguments = args
            return fragment
        }
    }

}
