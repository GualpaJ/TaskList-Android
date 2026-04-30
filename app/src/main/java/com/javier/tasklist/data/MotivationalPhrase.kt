package com.javier.tasklist.data

data class MotivationalPhrase(
    val id: Int,
    var text: String
) {
    companion object {
        const val TABLE_NAME = "phrases"
        const val COLUMN_ID = "id"
        const val COLUMN_TEXT = "text"

        const val SQL_CREATE =
            "CREATE TABLE $TABLE_NAME (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COLUMN_TEXT TEXT)"

        const val SQL_DELETE = "DROP TABLE IF EXISTS $TABLE_NAME"
    }
}