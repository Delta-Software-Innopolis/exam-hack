package com.examhacker.common.utility

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.LifecycleOwner
import com.examhacker.common.data.PickedFile
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.UUID

interface FilePicker {

    suspend fun pickFile(
        mimeTypes: Array<String> = arrayOf("*/*")
    ): PickedFile?

    suspend fun pickFiles(
        mimeTypes: Array<String> = arrayOf("*/*")
    ): List<PickedFile>
}

class AndroidFilePicker(
    private val registry: ActivityResultRegistry,
    lifecycleOwner: LifecycleOwner,
    private val contentResolver: ContentResolver,
) : FilePicker {

//    private val singleKey = UUID.randomUUID().toString()
//    private val multipleKey = UUID.randomUUID().toString()

    private var singleContinuation: CancellableContinuation<PickedFile?>? = null
    private var multipleContinuation: CancellableContinuation<List<PickedFile>>? = null

    private val singleLauncher = registry.register(
        SINGLE_KEY,
        lifecycleOwner,
        ActivityResultContracts.OpenDocument(),
    ) { uri ->
        val continuation = singleContinuation
        singleContinuation = null

        continuation?.resume(uri?.let(::toPickedFile)) {_,_,_ ->  }
    }

    private val multipleLauncher = registry.register(
        MULTIPLE_KEY,
        lifecycleOwner,
        ActivityResultContracts.OpenMultipleDocuments(),
    ) { uris ->
        val continuation = multipleContinuation
        multipleContinuation = null

        continuation?.resume(uris.map(::toPickedFile)) {_,_,_ ->  }
    }

    override suspend fun pickFile(
        mimeTypes: Array<String>,
    ): PickedFile? =
        suspendCancellableCoroutine { continuation ->

            check(singleContinuation == null && multipleContinuation == null) {
                "Another file picker request is already active."
            }

            singleContinuation = continuation

            continuation.invokeOnCancellation {
                singleContinuation = null
            }

            singleLauncher.launch(mimeTypes)
        }

    override suspend fun pickFiles(
        mimeTypes: Array<String>,
    ): List<PickedFile> =
        suspendCancellableCoroutine { continuation ->

            check(singleContinuation == null && multipleContinuation == null) {
                "Another file picker request is already active."
            }

            multipleContinuation = continuation

            continuation.invokeOnCancellation {
                multipleContinuation = null
            }

            multipleLauncher.launch(mimeTypes)
        }

    private fun toPickedFile(uri: Uri): PickedFile {
        try {
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION,
            )
        } catch (_: Exception) {
            // Some providers don't support persistable permissions.
        }

        val name = contentResolver.query(
            uri,
            arrayOf(OpenableColumns.DISPLAY_NAME),
            null,
            null,
            null,
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                cursor.getString(
                    cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)
                )
            } else {
                null
            }
        } ?: uri.lastPathSegment.orEmpty()

        return PickedFile(
            uri = uri,
            name = name,
        )
    }

    companion object {
        private const val SINGLE_KEY = "file_picker_single"
        private const val MULTIPLE_KEY = "file_picker_multiple"
    }
}