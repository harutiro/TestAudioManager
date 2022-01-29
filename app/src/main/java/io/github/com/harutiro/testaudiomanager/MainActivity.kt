package io.github.com.harutiro.testaudiomanager

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHeadset
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private var currentBluetoothHeadset: BluetoothHeadset? = null
    private var isBluetoothHeadsetConnected = false

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
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
                BluetoothDevice.ACTION_ACL_CONNECTED    -> {
                    Thread.sleep(2000)

                    Log.d("debag", "Broadcast: ACTION_ACL_CONNECTED")
                    if (currentBluetoothHeadset?.connectedDevices?.size ?: 0 > 0) {
                        isBluetoothHeadsetConnected = true
                        onInsertHeadset()
                        Log.d("debag", "★")

                    }
                }
                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                    Log.d("debag", "Broadcast: ACTION_ACL_DISCONNECTED")
                    isBluetoothHeadsetConnected = false
                    Log.d("debag", "😒")
                }
                else -> {}
            }
        }
    }

    private val bluetoothPolicyListener = object : BluetoothProfile.ServiceListener {
        @SuppressLint("MissingPermission")
        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile?) {
            if (profile == BluetoothProfile.HEADSET) {
                Log.d("debag", "BluetoothProfile onServiceConnected")

                currentBluetoothHeadset = proxy as BluetoothHeadset
                isBluetoothHeadsetConnected = (currentBluetoothHeadset?.connectedDevices?.size ?: 0 > 0)
            }
        }

        override fun onServiceDisconnected(profile: Int) {
            if (profile == BluetoothProfile.HEADSET) {
                Log.d("debag", "BluetoothProfile onServiceDisconnected")

                currentBluetoothHeadset = null
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerReceiver(broadcastReceiver, IntentFilter(Intent.ACTION_HEADSET_PLUG))
        registerReceiver(broadcastReceiver, IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY))

        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        registerReceiver(broadcastReceiver, monitorHeadsetFilter)
        bluetoothAdapter.getProfileProxy(this, bluetoothPolicyListener, BluetoothProfile.HEADSET)

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }

    private fun onInsertHeadset() {
        Log.d("debag", "Headset is inserting.")

        //音楽再生待機状態へ
    }

    private val monitorHeadsetFilter = IntentFilter().apply {
        addAction(AudioManager.ACTION_HEADSET_PLUG)
        addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
        addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
    }

    companion object {
        const val MONITOR_HEADSET_SERVICE_ID = 72
        const val MONITOR_HEADSET_NOTIFY_ID = 69
    }

}