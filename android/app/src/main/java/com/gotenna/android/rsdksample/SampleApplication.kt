package com.gotenna.android.rsdksample

import android.app.Application
import android.content.Context
import android.util.Log
import com.gotenna.radio.sdk.GotennaClient
import com.gotenna.radio.sdk.initialize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SampleApplication : Application() {

    private val scope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        instance = this
        initGotennaClient()
    }

    /**
     * Must initialize the GotennaClient before radio commands can be sent
     */
    private fun initGotennaClient() = scope.launch {
        val initialized = GotennaClient.initialize (context = applicationContext, sdkToken = BuildConfig.SDK_TOKEN, appId = BuildConfig.APP_ID, enableDebugLogs = true)
        Log.d("SampleApplication", "initialized: $initialized");
    }

    companion object {
        private lateinit var instance: SampleApplication

        val context: Context
            get() = instance.applicationContext
    }
}