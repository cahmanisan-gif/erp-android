package com.rajavavapor.app.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Rect
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resume
import kotlin.math.max
import kotlin.math.min

object FaceDetectionHelper {

    private val detector by lazy {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setMinFaceSize(0.3f)
            .build()
        FaceDetection.getClient(options)
    }

    data class FaceResult(
        val face: Face,
        val isValid: Boolean,
        val message: String
    )

    /**
     * Detect face from ImageProxy (CameraX real-time analysis).
     * Returns null if no face detected.
     */
    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    suspend fun detectFromImageProxy(imageProxy: ImageProxy): FaceResult? {
        val mediaImage = imageProxy.image ?: return null
        val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        return suspendCancellableCoroutine { cont ->
            detector.process(inputImage)
                .addOnSuccessListener { faces ->
                    if (faces.isEmpty()) {
                        cont.resume(null)
                    } else {
                        val face = faces[0]
                        val result = validateFace(face)
                        cont.resume(result)
                    }
                }
                .addOnFailureListener {
                    cont.resume(null)
                }
        }
    }

    /**
     * Detect face from Bitmap (for captured photo).
     */
    suspend fun detectFromBitmap(bitmap: Bitmap): FaceResult? {
        val inputImage = InputImage.fromBitmap(bitmap, 0)

        return suspendCancellableCoroutine { cont ->
            detector.process(inputImage)
                .addOnSuccessListener { faces ->
                    if (faces.isEmpty()) {
                        cont.resume(FaceResult(
                            face = faces.firstOrNull() ?: return@addOnSuccessListener cont.resume(null),
                            isValid = false,
                            message = "Wajah tidak terdeteksi"
                        ))
                    } else {
                        cont.resume(validateFace(faces[0]))
                    }
                }
                .addOnFailureListener {
                    cont.resume(null)
                }
        }
    }

    private fun validateFace(face: Face): FaceResult {
        // Check head angle — must be roughly frontal
        val yAngle = face.headEulerAngleY // left-right
        val zAngle = face.headEulerAngleZ // tilt

        if (Math.abs(yAngle) > 25) {
            return FaceResult(face, false, "Hadapkan wajah ke depan")
        }
        if (Math.abs(zAngle) > 15) {
            return FaceResult(face, false, "Luruskan kepala Anda")
        }

        // Check eyes open
        val leftEyeOpen = face.leftEyeOpenProbability
        val rightEyeOpen = face.rightEyeOpenProbability
        if (leftEyeOpen != null && rightEyeOpen != null) {
            if (leftEyeOpen < 0.4f || rightEyeOpen < 0.4f) {
                return FaceResult(face, false, "Buka mata Anda")
            }
        }

        // Check face size (bounding box should be large enough)
        val bounds = face.boundingBox
        if (bounds.width() < 100 || bounds.height() < 100) {
            return FaceResult(face, false, "Dekatkan wajah ke kamera")
        }

        return FaceResult(face, true, "Wajah terdeteksi")
    }

    /**
     * Crop face from bitmap with padding, compress, and save to file.
     * Returns compressed JPEG file ready for upload (~30-80KB).
     */
    fun cropAndCompressFace(bitmap: Bitmap, face: Face, outputFile: File): File {
        val bounds = face.boundingBox
        val padding = (bounds.width() * 0.3).toInt()

        val cropRect = Rect(
            max(0, bounds.left - padding),
            max(0, bounds.top - padding),
            min(bitmap.width, bounds.right + padding),
            min(bitmap.height, bounds.bottom + padding)
        )

        val cropped = Bitmap.createBitmap(
            bitmap, cropRect.left, cropRect.top, cropRect.width(), cropRect.height()
        )

        // Resize to 480px max dimension for fast upload
        val maxDim = 480
        val scale = maxDim.toFloat() / max(cropped.width, cropped.height)
        val resized = if (scale < 1f) {
            Bitmap.createScaledBitmap(
                cropped,
                (cropped.width * scale).toInt(),
                (cropped.height * scale).toInt(),
                true
            )
        } else {
            cropped
        }

        // Compress to JPEG quality 85
        FileOutputStream(outputFile).use { fos ->
            resized.compress(Bitmap.CompressFormat.JPEG, 85, fos)
        }

        if (resized !== cropped) resized.recycle()
        if (cropped !== bitmap) cropped.recycle()

        return outputFile
    }

    /**
     * Convert ImageProxy to Bitmap (for capture).
     */
    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {
        val buffer = imageProxy.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return null

        val rotation = imageProxy.imageInfo.rotationDegrees
        return if (rotation != 0) {
            val matrix = Matrix().apply { postRotate(rotation.toFloat()) }
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } else {
            bitmap
        }
    }
}
