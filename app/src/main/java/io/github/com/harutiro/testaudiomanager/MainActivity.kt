package io.github.com.harutiro.testaudiomanager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action ?: return
            when (action) {
                Intent.ACTION_HEADSET_PLUG -> {
                    Log.d("debag", "Intent.ACTION_HEADSET_PLUG")
                    val state = intent.getIntExtra("state", -1)
                    if (state == 0) {
                        // ヘッドセットが装着されていない・外された
                        Log.d("debag", "😊")
                    } else if (state > 0) {
                        // イヤホン・ヘッドセット(マイク付き)が装着された
                        Log.d("debag", "❤️")
                    }
                }
                AudioManager.ACTION_AUDIO_BECOMING_NOISY -> Log.d(
                    "debag",
                    "AudioManager.ACTION_AUDIO_BECOMING_NOISY"
                )
                else -> {}
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerReceiver(broadcastReceiver, IntentFilter(Intent.ACTION_HEADSET_PLUG))
        registerReceiver(broadcastReceiver, IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY))
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }

}