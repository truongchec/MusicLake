package com.cyl.musiclake.ui.settings

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.preference.*
import com.afollestad.materialdialogs.MaterialDialog
import com.cyl.musiclake.Config
import com.cyl.musiclake.MusicApp
import com.cyl.musiclake.R
import com.cyl.musiclake.common.Constants
import com.cyl.musiclake.socket.SocketManager
import com.cyl.musiclake.ui.main.MainActivity
import com.cyl.musiclake.ui.theme.ThemeStore
import com.cyl.musiclake.utils.*



class SettingsFragment : PreferenceFragment(), Preference.OnPreferenceClickListener {

    private var mPreferenceDownloadFile: Preference? = null
    private var mPreferenceCacheFile: Preference? = null
    private var mPreferenceCache: PreferenceScreen? = null
    lateinit var mWifiSwitch: SwitchPreference
    lateinit var mSocketSwitch: SwitchPreference
    lateinit var mNightSwitch: SwitchPreference
    lateinit var mCacheSwitch: SwitchPreference
    lateinit var mLyricCheckBox: CheckBoxPreference
    lateinit var mMusicQualityPreference: ListPreference
    lateinit var mMusicApiPreference: EditTextPreference
    lateinit var mNeteaseApiPreference: EditTextPreference

    private var musicApi: String? = null// = SPUtils.getAnyByKey(SPUtils.SP_KEY_PLATER_API_URL, Constants.BASE_PLAYER_URL);
    private var neteaseApi: String? = null// = SPUtils.getAnyByKey(SPUtils.SP_KEY_NETEASE_API_URL, Constants.BASE_NETEASE_URL);

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_general)
        setHasOptionsMenu(true)

        initView()

        Handler().post {
            val size = DataClearManager.getTotalCacheSize(MusicApp.getAppContext())
            mPreferenceCache!!.summary = size

        }

        mWifiSwitch.isChecked = SPUtils.getWifiMode()
        mWifiSwitch.setOnPreferenceChangeListener { preference, newValue ->
            LogUtil.e("sss", newValue.toString())
            val wifiMode = newValue as Boolean
            mWifiSwitch.isChecked = wifiMode
            SPUtils.saveWifiMode(wifiMode)
            false
        }
        mLyricCheckBox.isChecked = SystemUtils.isOpenFloatWindow()
        mSocketSwitch.isChecked = Config.isOpenSocket
    }

    /**
     * 初始化控件
     */
    private fun initView() {
        mPreferenceCache = findPreference("key_cache") as PreferenceScreen
        mPreferenceDownloadFile = findPreference("key_download_file")
        mPreferenceCacheFile = findPreference("key_cache_file")
        mWifiSwitch = findPreference("wifi_mode") as SwitchPreference
        mSocketSwitch = findPreference("key_socket") as SwitchPreference
        mNightSwitch = findPreference("key_night_mode") as SwitchPreference
        mCacheSwitch = findPreference("key_cache_mode") as SwitchPreference
        mLyricCheckBox = findPreference("key_lyric") as CheckBoxPreference
        mMusicQualityPreference = findPreference("key_music_quality") as ListPreference
        mMusicApiPreference = findPreference("key_music_api") as EditTextPreference
        mNeteaseApiPreference = findPreference("key_netease_api") as EditTextPreference

        mPreferenceCache!!.onPreferenceClickListener = this
        mSocketSwitch.onPreferenceClickListener = this
        mLyricCheckBox.onPreferenceClickListener = this

        mPreferenceDownloadFile!!.summary = FileUtils.getMusicDir()
        mPreferenceCacheFile!!.summary = FileUtils.getMusicCacheDir()

        mMusicQualityPreference.summary = mMusicQualityPreference.entry
        mMusicQualityPreference.setOnPreferenceChangeListener { preference, newValue ->

            val listPreference = preference as ListPreference

            val entries = listPreference.entries

            val index = listPreference.findIndexOfValue(newValue as String)

            listPreference.summary = entries[index]
            ToastUtils.show("Priority playback sound quality：" + entries[index])
            false
        }
        mNightSwitch.isChecked = ThemeStore.THEME_MODE == ThemeStore.NIGHT
        mNightSwitch.setOnPreferenceChangeListener { preference, newValue ->
            val isChecked = newValue as Boolean
            mNightSwitch.isChecked = isChecked
            if (isChecked && ThemeStore.THEME_MODE != ThemeStore.NIGHT) {
                ThemeStore.THEME_MODE = ThemeStore.NIGHT
                ThemeStore.updateThemeMode()
                updateTheme()
            } else if (!isChecked && ThemeStore.THEME_MODE != ThemeStore.DAY) {
                ThemeStore.THEME_MODE = ThemeStore.DAY
                ThemeStore.updateThemeMode()
                updateTheme()
            }
            false
        }
        initCacheSettings()

        initApiSettings()
    }

    private fun initCacheSettings() {
        mPreferenceCacheFile?.isEnabled = mCacheSwitch.isChecked
        mCacheSwitch.setOnPreferenceChangeListener { preference, newValue ->
            val isChecked = newValue as Boolean
            mCacheSwitch.isChecked = isChecked
            mPreferenceCacheFile?.isEnabled = isChecked
            false
        }
    }


    private fun updateTheme() {
        for (i in MusicApp.activities.indices) {
            if (MusicApp.activities[i] is MainActivity) {
                MusicApp.activities[i].recreate()
            }
        }
        startActivity(Intent(activity, SettingsActivity::class.java))
        activity.overridePendingTransition(0, 0)
        activity.finish()
    }



    private fun initApiSettings() {

        musicApi = SPUtils.getAnyByKey(SPUtils.SP_KEY_PLATER_API_URL, Constants.BASE_PLAYER_URL)
        neteaseApi = SPUtils.getAnyByKey(SPUtils.SP_KEY_NETEASE_API_URL, Constants.BASE_NETEASE_URL)
        //
        mMusicApiPreference.summary = musicApi
        mMusicApiPreference.text = musicApi
        mNeteaseApiPreference.summary = neteaseApi
        mNeteaseApiPreference.text = neteaseApi

        mMusicApiPreference.setOnPreferenceChangeListener { preference, newValue ->
            if (newValue.toString() != neteaseApi) {
                musicApi = newValue.toString()
                preference.summary = musicApi
                SPUtils.putAnyCommit(SPUtils.SP_KEY_PLATER_API_URL, musicApi)
                ToastUtils.show(getString(R.string.settings_restart_app))
            }
            false
        }
        mNeteaseApiPreference.setOnPreferenceChangeListener { preference, newValue ->
            if (newValue.toString() != neteaseApi) {
                neteaseApi = newValue.toString()
                preference.summary = neteaseApi
                SPUtils.putAnyCommit(SPUtils.SP_KEY_NETEASE_API_URL, neteaseApi)
                ToastUtils.show(getString(R.string.settings_restart_app))
            }
            false
        }
    }


    override fun onPreferenceClick(preference: Preference): Boolean {
        when (preference.key) {
            "key_about" -> {
                val intent = Intent(activity, AboutActivity::class.java)
                startActivity(intent)
            }
            "key_cache" ->
                MaterialDialog(activity).show {
                    title(R.string.title_warning)
                    message(R.string.setting_clear_cache)
                    positiveButton(R.string.sure) {
                        Handler().post {
                            try {

                                DataClearManager.cleanApplicationData(MusicApp.getAppContext())
                                ToastUtils.show(activity, "Cleared successfully")
                                val size = DataClearManager.getTotalCacheSize(MusicApp.getAppContext())
                                mPreferenceCache!!.summary = size
                            } catch (e: Exception) {

                                ToastUtils.show(activity, "Clear failed")
                                e.printStackTrace()
                            }
                        }
                    }
                }
            "key_socket" -> {
                Config.isOpenSocket = !Config.isOpenSocket
                mSocketSwitch.isChecked = Config.isOpenSocket
                SocketManager.toggleSocket(Config.isOpenSocket)
            }
            "key_lyric" -> checkLyricPermission()
        }
        return true
    }

    /**
     * 检查桌面歌词所需的权限
     */
    private fun checkLyricPermission() {
        try {
            if (!SystemUtils.isOpenFloatWindow()) {
                ToastUtils.show(getString(R.string.float_window_manual_open))
                SystemUtils.applySystemWindow()
                mLyricCheckBox.isChecked = true
            } else {
                mLyricCheckBox.isChecked = true
                ToastUtils.show(getString(R.string.float_window_is_ready))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun onResume() {
        super.onResume()
        mLyricCheckBox.isChecked = SystemUtils.isOpenFloatWindow()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_CODE_FLOAT_WINDOW) {
            if (SystemUtils.isOpenFloatWindow()) {
                checkLyricPermission()
            } else {
                ToastUtils.show(MusicApp.getAppContext(), getString(R.string.float_window_is_refused))
            }
        }
    }

    companion object {


        fun newInstance(): SettingsFragment {
            val args = Bundle()
            val fragment = SettingsFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
