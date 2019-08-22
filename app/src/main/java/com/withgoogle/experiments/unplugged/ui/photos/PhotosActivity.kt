package com.withgoogle.experiments.unplugged.ui.photos

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.withgoogle.experiments.unplugged.R
import com.withgoogle.experiments.unplugged.ui.AppState
import com.withgoogle.experiments.unplugged.ui.widget.ModuleView
import timber.log.Timber
import java.io.FileInputStream
import java.io.FileOutputStream
import android.os.Environment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

private val PICK_IMAGE_REQUEST = 0x8

class PhotosActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photos)

        with(findViewById<ModuleView>(R.id.module)) {
            setText("P", "Photos")
            isChecked = true
        }

        findViewById<View>(R.id.finish).setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.add).setOnClickListener {
            startPhotoPicker()
        }
    }

    private fun startPhotoPicker() {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.INTERNAL_CONTENT_URI
        ).apply {
            type = "image/*"
        }
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                CoroutineScope(Dispatchers.Main).launch {
                    val path = withContext(Dispatchers.IO) {
                        copyFile(uri)
                    }

                    path?.let { filePath ->
                        Timber.d(filePath)
                        AppState.photoUri.value = Uri.parse(filePath)
                        finish()
                    }
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun copyFile(uri: Uri): String? {
        val file = contentResolver.openFileDescriptor(uri, "r")

        val filesDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val fileDestination = File(filesDir, "paper-photo.jpg")

        file?.let {
            val fileInputStream = FileInputStream(file.fileDescriptor)

            fileInputStream.use {
                val imageDest = FileOutputStream(fileDestination)

                val buffer = ByteArray(1024)
                var length = fileInputStream.read(buffer)

                while (length > 0) {
                    imageDest.write(buffer, 0, length)

                    length = fileInputStream.read(buffer)
                }

                imageDest.flush()
                fileInputStream.close()
                imageDest.close()

                return fileDestination.absolutePath
            }
        }

        return null
    }
}