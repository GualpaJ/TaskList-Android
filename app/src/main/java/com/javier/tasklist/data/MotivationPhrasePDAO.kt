package com.javier.tasklist.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.javier.tasklist.utils.DatabaseManager

class MotivationPhrasePDAO(val context: Context) {

    private lateinit var db: SQLiteDatabase

    fun open() {
        db = DatabaseManager(context).writableDatabase
    }

    fun close() {
        db.close()
    }

    fun save(phrase: MotivationalPhrase) {
        if (phrase.id != -1) {
            update(phrase)
        } else {
            insert(phrase)
        }
    }

    fun insert(phrase: MotivationalPhrase) {
        open()

        val values = ContentValues()
        values.put(MotivationalPhrase.COLUMN_TEXT, phrase.text)

        try {
            val newRowId = db.insert(
                MotivationalPhrase.TABLE_NAME,
                null,
                values
            )

            Log.i("DATABASE", "Inserted phrase with id $newRowId")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }
    }

    fun update(phrase: MotivationalPhrase) {
        open()

        val values = ContentValues()
        values.put(MotivationalPhrase.COLUMN_TEXT, phrase.text)

        try {
            val updatedRows = db.update(
                MotivationalPhrase.TABLE_NAME,
                values,
                "${MotivationalPhrase.COLUMN_ID} = ${phrase.id}",
                null
            )

            Log.i("DATABASE", "Updated $updatedRows phrase(s)")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }
    }

    fun delete(phrase: MotivationalPhrase) {
        open()

        try {
            val deletedRows = db.delete(
                MotivationalPhrase.TABLE_NAME,
                "${MotivationalPhrase.COLUMN_ID} = ${phrase.id}",
                null
            )

            Log.i("DATABASE", "Deleted $deletedRows phrase(s)")
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }
    }

    fun getRandom(): MotivationalPhrase? {
        open()

        var result: MotivationalPhrase? = null

        try {
            val cursor = db.rawQuery(
                "SELECT * FROM ${MotivationalPhrase.TABLE_NAME} ORDER BY RANDOM() LIMIT 1",
                null
            )

            if (cursor.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(MotivationalPhrase.COLUMN_ID))
                val text = cursor.getString(cursor.getColumnIndexOrThrow(MotivationalPhrase.COLUMN_TEXT))

                result = MotivationalPhrase(id, text)
            }

            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }

        return result
    }

    fun getAll(): List<MotivationalPhrase> {
        open()

        val list = mutableListOf<MotivationalPhrase>()

        try {
            val cursor = db.query(
                MotivationalPhrase.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
            )

            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(MotivationalPhrase.COLUMN_ID))
                val text = cursor.getString(cursor.getColumnIndexOrThrow(MotivationalPhrase.COLUMN_TEXT))

                list.add(MotivationalPhrase(id, text))
            }

            cursor.close()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }

        return list
    }
}