package com.examhacker.mobile.root

import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.value.Value

interface IRootComponent {

    val stack: Value<ChildStack<*, Child>>
    sealed class Child
}