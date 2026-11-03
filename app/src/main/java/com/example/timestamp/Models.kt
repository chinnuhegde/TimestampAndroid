package com.example.timestamp

data class Report(
    val id: Long = 0,
    var startTimestamp: Long = 0,
    var endTimestamp: Long = 0,
    var duration: Long = 0,
    var totalRecords: Int = 0
)

data class Record(
    val id: Long = 0,
    val reportId: Long = 0,
    val timestamp: Long = 0,
    val value: String
)
