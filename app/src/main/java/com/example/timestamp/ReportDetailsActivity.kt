package com.example.timestamp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportDetailsActivity : AppCompatActivity() {

    private lateinit var tvSummary: TextView
    private lateinit var rvRecords: RecyclerView
    private lateinit var btnExportCsv: Button
    private lateinit var btnBack: Button
    private lateinit var dbHelper: DatabaseHelper
    private var currentReport: Report? = null
    private var records: List<Record> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_details)

        tvSummary = findViewById(R.id.tvSummary)
        rvRecords = findViewById(R.id.rvRecords)
        btnExportCsv = findViewById(R.id.btnExportCsv)
        btnBack = findViewById(R.id.btnBackDetails)
        dbHelper = DatabaseHelper(this)

        rvRecords.layoutManager = LinearLayoutManager(this)

        val reportId = intent.getLongExtra("REPORT_ID", -1L)
        if (reportId != -1L) {
            loadReportDetails(reportId)
        }

        btnExportCsv.setOnClickListener {
            exportToCsv()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadReportDetails(reportId: Long) {
        lifecycleScope.launch(Dispatchers.IO) {
            currentReport = dbHelper.getReportById(reportId)
            records = dbHelper.getRecordsForReport(reportId)

            withContext(Dispatchers.Main) {
                currentReport?.let { report ->
                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    val start = sdf.format(Date(report.startTimestamp))
                    val end = sdf.format(Date(report.endTimestamp))
                    tvSummary.text = "Start: $start\nEnd: $end\nDuration: ${report.duration / 1000}s\nTotal Records: ${report.totalRecords}"
                }

                rvRecords.adapter = RecordAdapter(records)
            }
        }
    }

    private fun exportToCsv() {
        if (currentReport == null || records.isEmpty()) {
            Toast.makeText(this, "No data to export", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val cachePath = File(cacheDir, "reports")
                cachePath.mkdirs()
                val file = File(cachePath, "report_${currentReport?.id}.csv")
                val writer = FileWriter(file)

                writer.append("Record ID,Timestamp,Value\n")
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

                for (record in records) {
                    val timeString = sdf.format(Date(record.timestamp))
                    writer.append("${record.id},${timeString},${record.value}\n")
                }
                writer.flush()
                writer.close()

                withContext(Dispatchers.Main) {
                    shareCsvFile(file)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ReportDetailsActivity, "Failed to export CSV", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun shareCsvFile(file: File) {
        try {
            val contentUri: Uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(shareIntent, "Share CSV Report"))
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to share file", Toast.LENGTH_SHORT).show()
        }
    }
}
