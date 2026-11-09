
package com.swin.radar

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import java.util.concurrent.Executors

class MainActivity : Activity() {

    private val handler = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()

    private lateinit var radar: RadarView
    private lateinit var status: TextView
    private lateinit var endpoint: EditText

    private var polling = false
    private var loading = false

    private val pollTask = object : Runnable {
        override fun run() {
            if (!polling) return
            loadRemoteData()
            handler.postDelayed(this, 3000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(18, 18, 18, 12)
            setBackgroundColor(Color.rgb(12, 18, 24))
        }

        val title = TextView(this).apply {
            text = "SWIN RADAR"
            textSize = 26f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        status = TextView(this).apply {
            text = "ДЕМО: тестовые объекты"
            textSize = 14f
            setTextColor(Color.GREEN)
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 10)
        }

        endpoint = EditText(this).apply {
            hint = "HTTPS-адрес API"
            setSingleLine(true)
            textSize = 13f
            setTextColor(Color.WHITE)
            setHintTextColor(Color.LTGRAY)
        }

        val connectButton = Button(this).apply {
            text = "ПОДКЛЮЧИТЬ СЕРВЕР"

            setOnClickListener {
                val value = endpoint.text.toString().trim()

                if (!value.startsWith("https://")) {
                    Toast.makeText(
                        this@MainActivity,
                        "Введи HTTPS-адрес API",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    polling = true
                    handler.removeCallbacks(pollTask)
                    loadRemoteData()
                    handler.postDelayed(pollTask, 3000)
                }
            }
        }

        val demoButton = Button(this).apply {
            text = "ДЕМО-РЕЖИМ"

            setOnClickListener {
                polling = false
                handler.removeCallbacks(pollTask)

                radar.objects = RadarRepository.demoObjects()

                status.text = "ДЕМО: тестовые объекты"
                status.setTextColor(Color.GREEN)
            }
        }

        radar = RadarView(this).apply {
            objects = RadarRepository.demoObjects()
        }

        val zoomLabel = TextView(this).apply {
            text = "МАСШТАБ РАДАРА"
            textSize = 12f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER
        }

        val zoom = SeekBar(this).apply {
            max = 150
            progress = 50

            setOnSeekBarChangeListener(
                object : SeekBar.OnSeekBarChangeListener {

                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        radar.zoom = 0.5f + progress / 100f
                    }

                    override fun onStartTrackingTouch(
                        seekBar: SeekBar?
                    ) = Unit

                    override fun onStopTrackingTouch(
                        seekBar: SeekBar?
                    ) = Unit
                }
            )
        }

        val legend = TextView(this).apply {
            text = "● Игроки   ● Боты   ■ Лут   ■ Ящики"
            textSize = 12f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        root.addView(title)
        root.addView(status)

        root.addView(
            endpoint,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        root.addView(connectButton)
        root.addView(demoButton)

        root.addView(
            radar,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        )

        root.addView(zoomLabel)
        root.addView(zoom)
        root.addView(legend)

        setContentView(root)
    }

    private fun loadRemoteData() {
        if (loading) return

        val address = endpoint.text.toString().trim()

        if (!address.startsWith("https://")) return

        loading = true

        executor.execute {
            try {
                val result = RadarRepository.fetch(address)

                handler.post {
                    radar.objects = result
                    status.text =
                        "СЕРВЕР ПОДКЛЮЧЁН · объектов: " + result.size
                    status.setTextColor(Color.GREEN)
                    loading = false
                }

            } catch (error: Exception) {

                handler.post {
                    status.text =
                        "ОШИБКА: " +
                        (error.message ?: "нет соединения")

                    status.setTextColor(Color.RED)
                    loading = false
                }
            }
        }
    }

    override fun onDestroy() {
        polling = false
        handler.removeCallbacksAndMessages(null)
        executor.shutdownNow()
        super.onDestroy()
    }
}

