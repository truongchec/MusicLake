package com.cyl.musiclake.player.cache;

import com.cyl.musiclake.utils.LogUtil;
import com.danikula.videocache.file.Md5FileNameGenerator;


public class CacheFileNameGenerator extends Md5FileNameGenerator {

    @Override
    public String generate(String url) {
        int len = url.split("/").length;

        String newUrl = url.split("/")[len - 1].replace(".mp3", "");

        String newUrl1 = newUrl.split("\\?")[0];
        LogUtil.d("MusicPlayerEngine", "cache oldUrl =" + url);
        LogUtil.d("MusicPlayerEngine", "cache newUrl =" + newUrl);
        LogUtil.d("MusicPlayerEngine", "cache newUrl1 =" + newUrl1);
        return super.generate(newUrl1);
    }
}
