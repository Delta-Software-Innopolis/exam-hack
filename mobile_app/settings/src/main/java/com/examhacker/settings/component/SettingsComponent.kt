package com.examhacker.settings.component

import com.arkivanov.decompose.ComponentContext

interface ISettingsComponent {}

class SettingsComponent(componentContext: ComponentContext)
    : ISettingsComponent, ComponentContext by componentContext {

}