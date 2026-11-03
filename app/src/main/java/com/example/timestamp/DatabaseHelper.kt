package com.example.timestamp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "timestamp.db"
        private const val DATABASE_VERSION = 1

        const val TABLE_REPORTS = "reports"
        const val COLUMN_REPORT_ID = "id"
        const val COLUMN_START_TIME = "start_time"
        const val COLUMN_END_TIME = "end_time"
        const val COLUMN_DURATION = "duration"
        const val COLUMN_TOTAL_RECORDS = "total_records"

        const val TABLE_RECORDS = "report_records"
        const val COLUMN_RECORD_ID = "id"
        const val COLUMN_RECORD_REPORT_ID = "report_id"
        const val COLUMN_TIMESTAMP = "timestamp"
        const val COLUMN_VALUE = "value"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createReportsTable = ("CREATE TABLE $TABLE_REPORTS ("
                + "$COLUMN_REPORT_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_START_TIME INTEGER, "
                + "$COLUMN_END_TIME INTEGER, "
                + "$COLUMN_DURATION INTEGER, "
                + "$COLUMN_TOTAL_RECORDS INTEGER)")
        db.execSQL(createReportsTable)

        val createRecordsTable = ("CREATE TABLE $TABLE_RECORDS ("
                + "$COLUMN_RECORD_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_RECORD_REPORT_ID INTEGER, "
                + "$COLUMN_TIMESTAMP INTEGER, "
                + "$COLUMN_VALUE TEXT, "
                + "FOREIGN KEY($COLUMN_RECORD_REPORT_ID) REFERENCES $TABLE_REPORTS($COLUMN_REPORT_ID))")
        db.execSQL(createRecordsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_RECORDS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_REPORTS")
        onCreate(db)
    }

    fun insertReport(report: Report): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_START_TIME, report.startTimestamp)
            put(COLUMN_END_TIME, report.endTimestamp)
            put(COLUMN_DURATION, report.duration)
            put(COLUMN_TOTAL_RECORDS, report.totalRecords)
        }
        val id = db.insert(TABLE_REPORTS, null, values)
        db.close()
        return id
    }

    fun updateReport(report: Report): Int {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_END_TIME, report.endTimestamp)
            put(COLUMN_DURATION, report.duration)
            put(COLUMN_TOTAL_RECORDS, report.totalRecords)
        }
        val success = db.update(TABLE_REPORTS, values, "$COLUMN_REPORT_ID = ?", arrayOf(report.id.toString()))
        db.close()
        return success
    }

    fun insertRecord(record: Record): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_RECORD_REPORT_ID, record.reportId)
            put(COLUMN_TIMESTAMP, record.timestamp)
            put(COLUMN_VALUE, record.value)
        }
        val id = db.insert(TABLE_RECORDS, null, values)
        db.close()
        return id
    }

    fun getAllReports(): List<Report> {
        val reportList = ArrayList<Report>()
        val selectQuery = "SELECT * FROM $TABLE_REPORTS ORDER BY $COLUMN_REPORT_ID DESC"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val report = Report(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_REPORT_ID)),
                    startTimestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_START_TIME)),
                    endTimestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_END_TIME)),
                    duration = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DURATION)),
                    totalRecords = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_RECORDS))
                )
                reportList.add(report)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return reportList
    }

    fun getReportById(id: Long): Report? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_REPORTS, null, "$COLUMN_REPORT_ID = ?", arrayOf(id.toString()),
            null, null, null
        )
        var report: Report? = null
        if (cursor.moveToFirst()) {
            report = Report(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_REPORT_ID)),
                startTimestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_START_TIME)),
                endTimestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_END_TIME)),
                duration = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_DURATION)),
                totalRecords = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_RECORDS))
            )
        }
        cursor.close()
        db.close()
        return report
    }

    fun getRecordsForReport(reportId: Long): List<Record> {
        val recordList = ArrayList<Record>()
        val selectQuery = "SELECT * FROM $TABLE_RECORDS WHERE $COLUMN_RECORD_REPORT_ID = $reportId ORDER BY $COLUMN_RECORD_ID ASC"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            do {
                val record = Record(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_RECORD_ID)),
                    reportId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_RECORD_REPORT_ID)),
                    timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP)),
                    value = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_VALUE))
                )
                recordList.add(record)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return recordList
    }
}
