package com.ng.btobd2poc

import android.app.ProgressDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothAdapter.*
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.control_layout.*
import java.io.IOException
import java.util.*

class ControlActivity: AppCompatActivity() {
    companion object {
        var _myUUID = UUID.fromString("44B319AA-F71D-42A0-A895-5D4659582C50")
        var _bluetoothSocket: BluetoothSocket? = null
        lateinit var _progress: ProgressDialog
        lateinit var _bluetoothAdapter: BluetoothAdapter
        var _isConnected: Boolean = false
        lateinit var _address: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.control_layout)
        _address = intent.getStringExtra(MainActivity.EXTRA_ADDRESS)

        ConnectToDevice(this).execute()

        this.control_led_on.setOnClickListener { sendCommand("a") }
        this.control_led_off.setOnClickListener { sendCommand("b") }
        this.control_led_disconnect.setOnClickListener { disconnect() }
    }

    private fun sendCommand(input: String) {
        if (_bluetoothSocket != null) {
            try {
                _bluetoothSocket!!.outputStream.write(input.toByteArray())
            } catch (error: IOException) {
                error.printStackTrace()
            }
        }
    }

    private fun disconnect() {
        if (_bluetoothSocket != null) {
            try {
                _bluetoothSocket!!.close()
                _bluetoothSocket = null
                _isConnected = false
            } catch (error: IOException) {
                error.printStackTrace()
            }
        }

        finish()
    }

    private class ConnectToDevice(context: Context) : AsyncTask<Void, Void, String>() {
        private var connectSuccess: Boolean = true
        private val context: Context

        init {
            this.context = context
        }

        override fun onPreExecute() {
            super.onPreExecute()
            _progress = ProgressDialog.show(context, "Connecting ...", "please wat")
        }

        override fun doInBackground(vararg params: Void?): String {
            try {
                if (_bluetoothSocket == null || !_isConnected) {
                    var device: BluetoothDevice = _bluetoothAdapter.getRemoteDevice(_address)
                    _bluetoothSocket = device.createInsecureRfcommSocketToServiceRecord(_myUUID)
                    getDefaultAdapter().cancelDiscovery()
                    _bluetoothSocket!!.connect()
                }
            } catch  (error: IOException) {
                connectSuccess = false
                error.printStackTrace()
            }

            return "success"
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            if (!connectSuccess) {
                Log.i("data", "couldn't connect")
            }

            _progress.dismiss()
        }
    }
}