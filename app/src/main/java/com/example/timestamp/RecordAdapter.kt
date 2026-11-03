package com.example.timestamp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RecordAdapter(private val records: List<Record>) :
    RecyclerView.Adapter<RecordAdapter.RecordViewHolder>() {

    class RecordViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRecordTime: TextView = view.findViewById(R.id.tvRecordTime)
        val tvRecordValue: TextView = view.findViewById(R.id.tvRecordValue)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_record, parent, false)
        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {
        val record = records[position]
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        holder.tvRecordTime.text = sdf.format(Date(record.timestamp))
        holder.tvRecordValue.text = record.value
    }

    override fun getItemCount() = records.size
}
