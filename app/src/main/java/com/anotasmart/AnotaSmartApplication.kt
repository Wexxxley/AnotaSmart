package com.anotasmart

import android.app.Application
import com.anotasmart.database.AppDatabase

class AnotaSmartApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}
