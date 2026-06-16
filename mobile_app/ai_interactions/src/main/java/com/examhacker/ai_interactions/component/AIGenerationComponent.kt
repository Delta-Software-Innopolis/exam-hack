package com.examhacker.ai_interactions.component

import com.arkivanov.decompose.ComponentContext

interface IAIGenerationComponent {}

class AIGenerationComponent(componentContext: ComponentContext)
    : IAIGenerationComponent, ComponentContext by componentContext {

}