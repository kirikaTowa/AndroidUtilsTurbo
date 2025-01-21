package com.kakusummer.androidutilsturbo.action.music_service

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import com.kakusummer.androidutilsturbo.R
import com.kakusummer.androidutilsturbo.databinding.ActivityMusicServiceBinding
import java.io.File
import java.io.FileOutputStream

class MusicServiceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMusicServiceBinding

    private var mediaPlayer: MediaPlayer? = null
    private var selectedRingtoneUri: Uri? = null
    private var privateRingtonePath: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_music_service)

        // 设置状态栏和底部导航栏的内边距
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 恢复保存的铃声路径
        privateRingtonePath = getSharedPreferences("app_prefs", MODE_PRIVATE)
            .getString("private_ringtone_path", null)

        // 验证保存的文件是否存在
        privateRingtonePath?.let { path ->
            val file = File(path)
            if (!file.exists()) {
                privateRingtonePath = null
                getSharedPreferences("app_prefs", MODE_PRIVATE).edit()
                    .remove("private_ringtone_path")
                    .apply()
            }
        }

        findViewById<TextView>(R.id.tv_1).setOnClickListener {
            // 停止当前播放
            stopAndReleaseMediaPlayer()
            // 停止服务中的播放
            stopService(Intent(this@MusicServiceActivity, MusicService::class.java))

            showRingtonePicker(
                this,
                selectedRingtoneUri,
                ringtonePickerRequestCode
            )
        }

        findViewById<TextView>(R.id.tv_start).setOnClickListener {
            try {
                if (privateRingtonePath != null) {
                    // 停止当前播放
                    stopAndReleaseMediaPlayer()

                    val serviceIntent = Intent(this@MusicServiceActivity, MusicService::class.java)
                    serviceIntent.putExtra("ringtone_path", privateRingtonePath)
                    startService(serviceIntent)
                } else {
                    Toast.makeText(this, "请先选择铃声", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "播放失败，请重新选择铃声", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<TextView>(R.id.tv_test).setOnClickListener {
            try {
                if (privateRingtonePath != null) {
                    // 停止服务中的播放
                    stopService(Intent(this@MusicServiceActivity, MusicService::class.java))
                    playRingtoneTest(File(privateRingtonePath!!).toUri())
                } else {
                    Toast.makeText(this, "请先选择铃声", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "播放失败，请重新选择铃声", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val ringtonePickerRequestCode = 1001

    fun showRingtonePicker(
        context: Activity,
        currentUri: Uri?,
        requestCode: Int,
    ) {
        try {
            val pickerIntent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
                putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true)
                putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, currentUri)
                putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
                val defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, defaultUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            }
            context.startActivityForResult(pickerIntent, requestCode)
        } catch (e: Exception) {
            Log.d("yeTest", "showRingtonePicker: 暂无铃声")
            Toast.makeText(context, "无法打开铃声选择器", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null && requestCode == ringtonePickerRequestCode) {
            val uri = data.getParcelableExtra<Uri>(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            uri?.let {
                try {
                    // 复制文件到私有目录
                    val privatePath = copyUriToPrivateStorage(it)
                    if (privatePath != null) {
                        selectedRingtoneUri = it
                        privateRingtonePath = privatePath

                        // 保存私有目录路径
                        getSharedPreferences("app_prefs", MODE_PRIVATE).edit()
                            .putString("private_ringtone_path", privatePath)
                            .apply()

                        playRingtone(File(privatePath).toUri())

                        Log.d("yeTest", "onActivityResult: "+privatePath)
                    } else {
                        Toast.makeText(this, "保存铃声失败", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Log.d("yeTest", "处理选中的铃声失败: ${e.message}")
                    Toast.makeText(this, "无法使用选中的铃声", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun playRingtone(uri: Uri) {
        try {
            stopAndReleaseMediaPlayer()
            // 停止服务中的播放
            stopService(Intent(this@MusicServiceActivity, MusicService::class.java))

            mediaPlayer = MediaPlayer().apply {
                setDataSource(applicationContext, uri)
                prepare()
                start()
            }
        } catch (e: Exception) {
            Log.d("yeTest", "playRingtone: 错误 - ${e.message}")
            Toast.makeText(this, "播放铃声失败", Toast.LENGTH_SHORT).show()
        }
    }

    private fun playRingtoneTest(uri: Uri) {
        try {
            stopAndReleaseMediaPlayer()

            mediaPlayer = MediaPlayer().apply {
                setDataSource(this@MusicServiceActivity, uri)
                prepare()
                start()
            }
        } catch (e: Exception) {
            Log.d("yeTest", "playRingtone: 错误 - ${e.message}")
            Toast.makeText(this, "播放铃声失败", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAndReleaseMediaPlayer()
        // 停止服务中的播放
        stopService(Intent(this, MusicService::class.java))
    }

    override fun onResume() {
        super.onResume()
    }

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
            Log.d("yeTest", "停止播放错误: ${e.message}")
        }
    }

    private fun copyUriToPrivateStorage(uri: Uri): String? {
        return try {
            // 创建私有目录中的铃声文件
            val ringtoneDir = File(filesDir, "ringtones").apply { mkdirs() }
            val ringtoneFile = File(ringtoneDir, "selected_ringtone.mp3")

            // 复制文件内容
            contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(ringtoneFile).use { output ->
                    input.copyTo(output)
                }
            }

            ringtoneFile.absolutePath
        } catch (e: Exception) {
            Log.d("yeTest", "复制铃声文件失败: ${e.message}")
            null
        }
    }
}