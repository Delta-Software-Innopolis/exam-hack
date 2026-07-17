package com.examhacker.phone_unlock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arkivanov.decompose.defaultComponentContext
import com.examhacker.common.utility.SettingStorage
import com.examhacker.phone_unlock.controller.UnlockComponent
import com.examhacker.phone_unlock.ui.UnlockScreen

class UnlockActivity: ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val settingStorage = SettingStorage(applicationContext)
        val component = UnlockComponent(
            componentContext = defaultComponentContext(),
            settingStorage = settingStorage,
            onCloseQuestion = { finish() }
        )

        setContent {
            UnlockScreen(component)
        }
    }
}