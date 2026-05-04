package com.example.aiexamevaluator.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class EncodedImage(
    val base64Data: String,
    val mimeType: String,
)

object ImageUtils {
    private const val MAX_DIMENSION = 1600
    private const val JPEG_QUALITY = 80

    suspend fun encodeUrisToBase64(
        context: Context,
        uris: List<Uri>,
    ): List<EncodedImage> = withContext(Dispatchers.IO) {
        uris.mapNotNull { uri ->
            encodeSingleUri(context, uri)
        }
    }

    private fun encodeSingleUri(
        context: Context,
        uri: Uri,
    ): EncodedImage? {
        val resolver = context.contentResolver
        val bitmap = decodeBitmapSafely(context, uri) ?: return null
        val resizedBitmap = resizeToMaxDimension(bitmap, MAX_DIMENSION)
        val jpegBytes = compressBitmapToJpeg(resizedBitmap, JPEG_QUALITY) ?: return null
        val base64 = Base64.encodeToString(jpegBytes, Base64.NO_WRAP).orEmpty()

        return try {
            if (base64.isBlank()) {
                null
            } else {
                EncodedImage(base64Data = base64, mimeType = "image/jpeg")
            }
        } catch (_: Exception) {
            null
        } finally {
            if (resizedBitmap !== bitmap) {
                resizedBitmap.recycle()
            }
            bitmap.recycle()
        }
    }

    private fun decodeBitmapSafely(
        context: Context,
        uri: Uri,
    ): Bitmap? {
        val resolver = context.contentResolver

        return try {
            val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            resolver.openInputStream(uri)?.use { input ->
                BitmapFactory.decodeStream(input, null, boundsOptions)
            }

            if (boundsOptions.outWidth <= 0 || boundsOptions.outHeight <= 0) {
                return null
            }

            val sampleSize = calculateInSampleSize(
                width = boundsOptions.outWidth,
                height = boundsOptions.outHeight,
                reqWidth = MAX_DIMENSION,
                reqHeight = MAX_DIMENSION,
            )

            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = sampleSize
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }

            resolver.openInputStream(uri)?.use { input ->
                BitmapFactory.decodeStream(input, null, decodeOptions)
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun calculateInSampleSize(
        width: Int,
        height: Int,
        reqWidth: Int,
        reqHeight: Int,
    ): Int {
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            var halfHeight = height / 2
            var halfWidth = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize.coerceAtLeast(1)
    }

    private fun resizeToMaxDimension(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= 0 || height <= 0) {
            return bitmap
        }

        val maxCurrent = maxOf(width, height)
        if (maxCurrent <= maxDimension) {
            return bitmap
        }

        val scale = maxDimension.toFloat() / maxCurrent.toFloat()
        val targetWidth = (width * scale).toInt().coerceAtLeast(1)
        val targetHeight = (height * scale).toInt().coerceAtLeast(1)
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }

    private fun compressBitmapToJpeg(bitmap: Bitmap, quality: Int): ByteArray? {
        return try {
            val outputStream = ByteArrayOutputStream()
            val compressed = bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            if (!compressed) {
                null
            } else {
                outputStream.toByteArray().takeIf { it.isNotEmpty() }
            }
        } catch (_: Exception) {
            null
        }
    }
}
