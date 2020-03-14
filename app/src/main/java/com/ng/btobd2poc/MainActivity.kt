package com.ng.btobd2poc

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var _bluetoothAdapter: BluetoothAdapter? = null
    private lateinit var _pairedDeveices: Set<BluetoothDevice>
    private var REQUEST_ENABLE_BLUETOOTH = 1

    companion object {
        var EXTRA_ADDRESS: String = "Device_address"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        _bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (_bluetoothAdapter == null) {
            Toast.makeText(
                this@MainActivity,
                "This device doesn't support bluetooth",
                Toast.LENGTH_SHORT).show()
            return
        }

        if (!_bluetoothAdapter!!.isEnabled) {
            val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBluetoothIntent, REQUEST_ENABLE_BLUETOOTH)
        }

        select_device_refresh.setOnClickListener{
            pairedDeviceList()
         }
    }

    private fun pairedDeviceList(): ArrayList<BluetoothDevice> {
        _pairedDeveices = _bluetoothAdapter!!.bondedDevices
        val list: ArrayList<BluetoothDevice> = ArrayList()

        if (!_pairedDeveices.isEmpty()) {
            for (device: BluetoothDevice in _pairedDeveices) {
                list.add(device)
                Log.i("device",""+device)
            }
        } else {
            Toast.makeText(
                this@MainActivity,
                "No paired bluetooth devices found",
                Toast.LENGTH_SHORT).show()
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        select_device_list.adapter = adapter
        select_device_list.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val device: BluetoothDevice = list[position]
            val address: String = device.address

            intent = Intent(this, ControlActivity::class.java)
            intent.putExtra(EXTRA_ADDRESS, address)
            startActivity(intent)
        }

        return list
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {
                if (_bluetoothAdapter!!.isEnabled) {
                    Toast.makeText(
                        this@MainActivity,
                        "Bluetooth has been enabled",
                        Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Bluetooth has been disabled",
                        Toast.LENGTH_SHORT).show()
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(
                    this@MainActivity,
                    "Bluetooth enabling has been canceled",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }
}
