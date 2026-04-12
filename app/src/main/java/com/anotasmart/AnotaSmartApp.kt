package com.anotasmart

import android.app.Application
import com.anotasmart.data.AppDatabase

class AnotaSmartApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}
