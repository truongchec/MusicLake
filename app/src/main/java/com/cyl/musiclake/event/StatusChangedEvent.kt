package com.cyl.musiclake.event



class StatusChangedEvent(var isPrepared: Boolean, var isPlaying: Boolean, var percent: Long = 0)
