package com.javier.tasklist.utils

import android.content.Context
import com.google.gson.Gson

fun loadPhrasesFromAssets(context: Context): List<String> {
    val json = context.assets
        .open("frases.json")
        .bufferedReader()
        .use { it.readText() }

    return Gson().fromJson(json, Array<String>::class.java).toList()
}