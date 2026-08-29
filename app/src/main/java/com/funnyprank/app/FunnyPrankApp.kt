package com.funnyprank.app

import android.app.Application
import com.funnyprank.app.data.db.SoundDatabase
import com.funnyprank.app.data.repo.SoundRepository

class FunnyPrankApp : Application() {

    val database: SoundDatabase by lazy { SoundDatabase.getInstance(this) }
    val repository: SoundRepository by lazy { SoundRepository(database.soundDao()) }
}
