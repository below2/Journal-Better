package com.beelow.journalbetter

import android.app.Application
import com.beelow.journalbetter.di.AppContainer

class JournalApplication : Application() {
    val container: AppContainer by lazy { AppContainer(this) }
}