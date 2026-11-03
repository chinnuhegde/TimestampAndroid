package com.example.timestamp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var btnStart: Button
    private lateinit var btnStop: Button
    private lateinit var tvStatus: TextView
    private lateinit var rvLiveRecords: RecyclerView
    private lateinit var btnViewReports: Button

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recordAdapter: RecordAdapter
    private val liveRecords = mutableListOf<Record>()

    private var recordingJob: Job? = null
    private var currentReportId: Long = -1L
    private var startTime: Long = 0L
    private var globalCounter: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)

        btnStart = findViewById(R.id.btnStart)
        btnStop = findViewById(R.id.btnStop)
        tvStatus = findViewById(R.id.tvStatus)
        rvLiveRecords = findViewById(R.id.rvLiveRecords)
        btnViewReports = findViewById(R.id.btnViewReports)

        recordAdapter = RecordAdapter(liveRecords)
        rvLiveRecords.layoutManager = LinearLayoutManager(this)
        rvLiveRecords.adapter = recordAdapter

        btnStart.setOnClickListener {
            startRecording()
        }

        btnStop.setOnClickListener {
            stopRecording()
        }

        btnViewReports.setOnClickListener {
            startActivity(Intent(this, ReportActivity::class.java))
        }
    }

    private fun startRecording() {
        btnStart.isEnabled = false
        btnStop.isEnabled = true
        liveRecords.clear()
        recordAdapter.notifyDataSetChanged()

        startTime = System.currentTimeMillis()
        val report = Report(startTimestamp = startTime)

        lifecycleScope.launch(Dispatchers.IO) {
            currentReportId = dbHelper.insertReport(report)

            withContext(Dispatchers.Main) {
                recordingJob = lifecycleScope.launch(Dispatchers.IO) {
                    var count = 0
                    while (isActive) {
                        val currentTime = System.currentTimeMillis()
                        val record = Record(reportId = currentReportId, timestamp = currentTime, value = globalCounter.toString())
                        dbHelper.insertRecord(record)
                        count++
                        
                        globalCounter++

                        withContext(Dispatchers.Main) {
                            liveRecords.add(0, record) // add to top
                            recordAdapter.notifyItemInserted(0)
                            rvLiveRecords.scrollToPosition(0)

                            val durationSeconds = (currentTime - startTime) / 1000
                            tvStatus.text = "Status: Recording\nDuration: ${durationSeconds}s | Count: $count"
                        }

                        delay(1000)
                    }
                }
            }
        }
    }

    private fun stopRecording() {
        btnStart.isEnabled = true
        btnStop.isEnabled = false

        recordingJob?.cancel()
        recordingJob = null

        val endTime = System.currentTimeMillis()
        val duration = endTime - startTime

        lifecycleScope.launch(Dispatchers.IO) {
            val report = dbHelper.getReportById(currentReportId)
            report?.let {
                it.endTimestamp = endTime
                it.duration = duration
                it.totalRecords = liveRecords.size
                dbHelper.updateReport(it)
            }

            withContext(Dispatchers.Main) {
                tvStatus.text = "Status: Stopped\nDuration: ${duration / 1000}s | Total: ${liveRecords.size}"
                Toast.makeText(this@MainActivity, "Report saved", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        recordingJob?.cancel()
    }
}
