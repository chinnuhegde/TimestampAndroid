package com.example.timestamp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReportAdapter(
    private val reports: List<Report>,
    private val onItemClick: (Report) -> Unit
) : RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    class ReportViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvReportId: TextView = view.findViewById(R.id.tvReportId)
        val tvReportDate: TextView = view.findViewById(R.id.tvReportDate)
        val tvReportCount: TextView = view.findViewById(R.id.tvReportCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report, parent, false)
        return ReportViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reports[position]
        holder.tvReportId.text = "Report #${report.id}"
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        holder.tvReportDate.text = sdf.format(Date(report.startTimestamp))
        holder.tvReportCount.text = "Records: ${report.totalRecords}"

        holder.itemView.setOnClickListener {
            onItemClick(report)
        }
    }

    override fun getItemCount() = reports.size
}
