package com.kakusummer.androidutilsturbo.action.music_service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.IBinder
import android.util.Log
import java.io.File

class MusicService : Service() {

    private var mediaPlayer: MediaPlayer? = null

    private fun stopAndReleaseMediaPlayer() {
        try {
            mediaPlayer?.apply {
                if (isPlaying) {
                    stop()
                }
                release()
            }
            mediaPlayer = null
        } catch (e: Exception) {
            Log.d("yeTest", "Service 停止播放错误: ${e.message}")
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("yeTest", "Service onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 先停止当前播放
        stopAndReleaseMediaPlayer()

        val ringtonePath = intent?.getStringExtra("ringtone_path")
        if (ringtonePath != null) {
            try {
                val ringtoneFile = File(ringtonePath)
                if (ringtoneFile.exists()) {
                    mediaPlayer = MediaPlayer().apply {
                        setDataSource(ringtoneFile.absolutePath)
                        prepare()
                        start()
                    }
                } else {
                    Log.d("yeTest", "AlarmService: Ringtone file not found")
                    stopSelf()
                }
            } catch (e: Exception) {
                Log.d("yeTest", "AlarmService playRingtone error: ${e.message}")
                stopSelf()
            }
        } else {
            Log.d("yeTest", "AlarmService: No ringtone path provided")
            stopSelf()
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAndReleaseMediaPlayer()
    }
}