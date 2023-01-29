package kizzy.rpc

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
    private lateinit var editText: EditText
    private lateinit var start: Button
    private lateinit var stop: Button
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS,
                ) != PackageManager.PERMISSION_GRANTED
            )
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }


        editText = findViewById(R.id.token)
        start = findViewById(R.id.start_btn)
        stop = findViewById(R.id.stop_btn)

        start.setOnClickListener {
            startService(Intent(this, MyService::class.java).apply {
                action = MyService.START_ACTIVITY_ACTION
                putExtra("TOKEN", editText.text.toString())
            })
        }
        stop.setOnClickListener { stopService(Intent(this, MyService::class.java)) }
    }
}