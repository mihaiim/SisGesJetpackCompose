package com.mihaiim.sisgesjetpackcompose.others

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import com.mihaiim.sisgesjetpackcompose.BuildConfig
import com.mihaiim.sisgesjetpackcompose.R
import java.io.File

class PictureFileProvider : FileProvider(R.xml.provider_paths) {

    companion object {

        fun getTempFileUri(context: Context): Uri {
            val tmpFile = File.createTempFile("temp_image_file", ".png").apply {
                createNewFile()
                deleteOnExit()
            }
            return getUriForFile(
                context,
                "${BuildConfig.APPLICATION_ID}.provider",
                tmpFile
            )
        }
    }
}