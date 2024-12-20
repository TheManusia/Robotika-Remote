package xyz.themanusia.robotikaremote

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import xyz.themanusia.robotikaremote.ui.theme.RobotikaRemoteTheme
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

private val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RobotikaRemoteTheme {
                MainView()
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainView() {
    val connectStatus = remember { mutableStateOf("Not Connected") }
    val bluetoothPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        android.Manifest.permission.BLUETOOTH_CONNECT
    } else {
        android.Manifest.permission.BLUETOOTH
    }
    val context = LocalContext.current
    val bluetoothManager: BluetoothManager =
        context.getSystemService(BluetoothManager::class.java)
    val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    val activity = context as MainActivity

    val requestPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                connectHC05(bluetoothAdapter)
            } else {
                activity.finish()
            }
        }

    if (ContextCompat.checkSelfPermission(
            context,
            bluetoothPermission
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        Log.i("blt", "MainView: Permission Granted")
        connectHC05(bluetoothAdapter)
    } else {
        LaunchedEffect(true) {
            requestPermissionLauncher.launch(bluetoothPermission)
        }
    }

    LaunchedEffect(key1 = connectStatus.value) {
        Log.i("blt", "MainView: ${connectStatus.value}")
        Toast.makeText(context, connectStatus.value, Toast.LENGTH_SHORT).show()
    }

    var maxSpeed by remember { mutableFloatStateOf(255f) }
    Scaffold(modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Row(modifier = Modifier) {
                Button(onClick = {
                    connectHC05(bluetoothAdapter)
                }) {
                    Text(text = "Connect")
                }
                Box(modifier = Modifier.padding(8.dp))
            }
            Box(modifier = Modifier.padding(horizontal = 64.dp)) {
                Slider(value = maxSpeed / 255, onValueChange = {
                    maxSpeed = it * 255
                    sendData(maxSpeed+20)
                })
            }
            Text(text = "$maxSpeed")
            Box(modifier = Modifier.padding(8.dp))
            Button(onClick = {
                sendData(0)
            }) {
                Text(text = "Switch Mode")
            }
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Button(onClick = {  }, modifier = Modifier.pointerInteropFilter {
                        when (it.action) {
                            MotionEvent.ACTION_DOWN -> {
                                sendData(1)
                            }

                            MotionEvent.ACTION_UP -> {
                                sendData(2)
                            }
                        }
                        true
                    }) {
                        Text("U")
                    }
                    Row {
                        Button(onClick = {  }, modifier = Modifier.pointerInteropFilter {
                            when (it.action) {
                                MotionEvent.ACTION_DOWN -> {
                                    sendData(3)
                                }

                                MotionEvent.ACTION_UP -> {
                                    sendData(4)
                                }
                            }
                            true
                        }) {
                            Text("L")
                        }
                        Box(modifier = Modifier.padding(8.dp))
                        Button(onClick = {  }, modifier = Modifier.pointerInteropFilter {
                        when (it.action) {
                            MotionEvent.ACTION_DOWN -> {
                                sendData(5)
                            }

                            MotionEvent.ACTION_UP -> {
                                sendData(6)
                            }
                        }
                        true
                    }) {
                            Text("R")
                        }
                    }
                    Button(onClick = {  }, modifier = Modifier.pointerInteropFilter {
                        when (it.action) {
                            MotionEvent.ACTION_DOWN -> {
                                sendData(7)
                            }

                            MotionEvent.ACTION_UP -> {
                                sendData(8)
                            }
                        }
                        true
                    }) {
                        Text("D")
                    }
                }
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Button(onClick = {  }, modifier = Modifier.pointerInteropFilter {
                        when (it.action) {
                            MotionEvent.ACTION_DOWN -> {
                                sendData(9)
                            }

                            MotionEvent.ACTION_UP -> {
                                sendData(10)
                            }
                        }
                        true
                    }) {
                        Text("A")
                    }
                    Row {
                        Button(onClick = {  }, modifier = Modifier.pointerInteropFilter {
                        when (it.action) {
                            MotionEvent.ACTION_DOWN -> {
                                sendData(11)
                            }

                            MotionEvent.ACTION_UP -> {
                                sendData(12)
                            }
                        }
                        true
                    }) {
                            Text("B")
                        }
                        Box(modifier = Modifier.padding(8.dp))
                        Button(onClick = {  }, modifier = Modifier.pointerInteropFilter {
                        when (it.action) {
                            MotionEvent.ACTION_DOWN -> {
                                sendData(13)
                            }

                            MotionEvent.ACTION_UP -> {
                                sendData(14)
                            }
                        }
                        true
                    }) {
                            Text("C")
                        }
                    }
                    Button(onClick = {  }, modifier = Modifier.pointerInteropFilter {
                        when (it.action) {
                            MotionEvent.ACTION_DOWN -> {
                                sendData(15)
                            }

                            MotionEvent.ACTION_UP -> {
                                sendData(16)
                            }
                        }
                        true
                    }) {
                        Text("D")
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RobotikaRemoteTheme {
        MainView()
    }
}

fun sendData(data: Int) {
    dataExchaneInstance?.write(data)
}

fun sendData(data: String) {
    dataExchaneInstance?.write(data)
}

@SuppressLint("MissingPermission")
private fun connectHC05(bluetoothAdapter: BluetoothAdapter?) {
    Log.i("blt", "connectHC05: Connecting to HC-05")
    val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
    val hc05Device = pairedDevices?.find { it.address == "58:56:00:00:9F:43" }
    if (hc05Device != null) {
        Log.i("blt", "connectHC05: Device Found")
        ConnectThread(hc05Device).start()
    }
}

@SuppressLint("MissingPermission")
class ConnectThread(private val monDevice: BluetoothDevice) : Thread() {
    private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
        monDevice.createRfcommSocketToServiceRecord(MY_UUID)
    }

    override fun run() {
        try {
            mmSocket?.connect()
            Log.i("blt", "run: Connected")
        } catch (e: IOException) {
            Log.e("blt", "Error connecting to device", e)
        }
        dataExchaneInstance = DataExchange(mmSocket!!)
    }
}

var dataExchaneInstance: DataExchange? = null

class DataExchange(mmSocket: BluetoothSocket) : Thread() {
    private val mmOutStream: OutputStream = mmSocket.outputStream

    fun write(bytes: Int) {
        try {
            mmOutStream.write(bytes)
            Log.i("blt", "write: Data Sent $bytes")
        } catch (e: IOException) {
            Log.i("blt", "write: Error writing to output stream", e)
        }
    }

    fun write(bytes: String) {
        try {
            mmOutStream.write(bytes.toByteArray())
            Log.i("blt", "write: Data Sent $bytes")
        } catch (e: IOException) {
            Log.i("blt", "write: Error writing to output stream", e)
        }
    }
}
