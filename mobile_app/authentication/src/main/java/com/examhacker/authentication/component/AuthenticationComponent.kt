package com.examhacker.authentication.component

import com.arkivanov.decompose.ComponentContext

interface IAuthenticationComponent {}

class AuthenticationComponent(componentContext: ComponentContext)
    : IAuthenticationComponent, ComponentContext by componentContext {

}