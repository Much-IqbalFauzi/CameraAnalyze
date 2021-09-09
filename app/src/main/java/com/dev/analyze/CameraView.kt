package com.dev.analyze

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.LinearLayout
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.window.WindowManager
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.ExecutorService
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class CameraView(context: Context) {

    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var windowManager: WindowManager

    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null

    private lateinit var cameraExecutor: ExecutorService

    val context: Context

    init {
        this.context = context
    }

//    fun setupCamera() {
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
//        cameraProviderFuture.addListener(Runnable {
//
//            // CameraProvider
//            cameraProvider = cameraProviderFuture.get()
//
//            // Build and bind the camera use cases
//            bindCameraUseCases()
//        }, ContextCompat.getMainExecutor(this))
//    }
//
//    private fun bindCameraUseCases() {
////        Log.d(TAG,"==========================================Camera Fragment Bind Camera Usecase")
//        val metrics = windowManager.getCurrentWindowMetrics().bounds
////        Log.d(TAG, "Screen metrics: ${metrics.width()} x ${metrics.height()}")
//        val screenAspectRatio = aspectRatio(metrics.width(), metrics.height())
//        Log.d(MainActivity.TAG, "Preview aspect ratio: $screenAspectRatio")
//
//        val rotation = previewView.display.rotation
//        val cameraProvider = cameraProvider
//            ?: throw IllegalStateException("Camera initialization failed.")
//
//        val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
//
//        // Preview
//        preview = Preview.Builder()
//            .setTargetAspectRatio(screenAspectRatio)
//            .setTargetRotation(rotation)
//            .build()
//        // ImageAnalysis
//        Log.d(MainActivity.TAG,"==========================================Camera Fragment >>> Analyze")
//
//
//        imageAnalyzer = ImageAnalysis.Builder()
//            .setTargetAspectRatio(screenAspectRatio)
//            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//            .build()
//
//        imageAnalyzer!!.setAnalyzer(cameraExecutor, ImageAnalysis.Analyzer { image ->
//            Log.d(MainActivity.TAG, "this is image| width= ${image.width}, height = ${image.height}")
//            Log.d(MainActivity.TAG, "image info=${image.imageInfo}")
//            image.close()
//        })
//
//        try {
//            // A variable number of use-cases can be passed here -
//            // camera provides access to CameraControl & CameraInfo
//            camera = cameraProvider.bindToLifecycle(
//                this, cameraSelector, preview, imageAnalyzer)
//
//            // Attach the viewfinder's surface provider to preview use case
//            preview?.setSurfaceProvider(previewView.surfaceProvider)
//        } catch (exc: Exception) {
//            Log.e(MainActivity.TAG, "Use case binding failed", exc)
//        }
//
////        cameraProvider.unbindAll()
//    }
//
//    private fun aspectRatio(width: Int, height: Int): Int {
////        Log.d(TAG,"==========================================Camera Fragment Call Aspect Ratio")
//        val previewRatio = max(width, height).toDouble() / min(width, height)
//        if (abs(previewRatio - MainActivity.RATIO_4_3_VALUE) <= abs(previewRatio - MainActivity.RATIO_16_9_VALUE)) {
//            return AspectRatio.RATIO_4_3
//        }
//        return AspectRatio.RATIO_16_9
//    }


}