package com.javier.tasklist.utils

import android.content.Context
import com.javier.tasklist.data.MotivationPhrasePDAO
import com.javier.tasklist.data.MotivationalPhrase

class PhraseSeeder {

    fun seedIfNeeded(context: Context) {

        val dao = MotivationPhrasePDAO(context)

        // si ya hay datos no hacemos nada
        if (dao.getAll().isNotEmpty()) return

        val phrases = loadPhrasesFromAssets(context)

        phrases.forEach { text ->
            dao.insert(
                MotivationalPhrase(
                    id = -1,
                    text = text
                )
            )
        }
    }
}