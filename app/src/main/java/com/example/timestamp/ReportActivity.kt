package com.example.timestamp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReportActivity : AppCompatActivity() {

    private lateinit var rvReports: RecyclerView
    private lateinit var dbHelper: DatabaseHelper

    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        rvReports = findViewById(R.id.rvReports)
        rvReports.layoutManager = LinearLayoutManager(this)
        btnBack = findViewById(R.id.btnBackReport)
        dbHelper = DatabaseHelper(this)

        btnBack.setOnClickListener {
            finish()
        }

        loadReports()
    }

    private fun loadReports() {
        lifecycleScope.launch(Dispatchers.IO) {
            val reports = dbHelper.getAllReports()
            withContext(Dispatchers.Main) {
                val adapter = ReportAdapter(reports) { report ->
                    val intent = Intent(this@ReportActivity, ReportDetailsActivity::class.java).apply {
                        putExtra("REPORT_ID", report.id)
                    }
                    startActivity(intent)
                }
                rvReports.adapter = adapter
            }
        }
    }
}
