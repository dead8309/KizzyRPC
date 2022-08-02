package kizzy.rpc

import android.app.Activity
import android.widget.EditText
import android.os.Bundle
import android.content.Intent
import android.widget.Button


class MainActivity : Activity() {
    private lateinit var editText: EditText
    private lateinit var start: Button
    private lateinit var stop: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText = findViewById(R.id.token)
        start = findViewById(R.id.start_btn)
        stop = findViewById(R.id.stop_btn)
        start.setOnClickListener {
            token = editText.text.toString()
            startService(Intent(this, MyService::class.java))
        }
        stop.setOnClickListener { stopService(Intent(this, MyService::class.java)) }
    }

    companion object {
        @JvmField
        var token: String? = null
    }
}