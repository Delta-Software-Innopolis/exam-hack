package com.examhacker.profile.component

import com.arkivanov.decompose.ComponentContext

interface IProfileComponent {}

class ProfileComponent(componentContext: ComponentContext)
    : IProfileComponent, ComponentContext by componentContext {

}