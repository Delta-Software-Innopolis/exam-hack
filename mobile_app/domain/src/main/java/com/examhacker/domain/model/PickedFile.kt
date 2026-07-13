package com.examhacker.domain.model

import android.net.Uri

data class PickedFile(
    val uri: Uri,
    val name: String
)