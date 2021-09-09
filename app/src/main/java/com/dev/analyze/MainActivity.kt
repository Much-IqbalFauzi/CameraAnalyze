package com.dev.analyze

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.window.WindowManager
import kotlinx.android.synthetic.main.activity_main.*
import org.tensorflow.lite.Interpreter
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class MainActivity : AppCompatActivity() {


    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var windowManager: WindowManager

    private var preview: Preview? = null
    private var imageAnalyzer: ImageAnalysis? = null
    private var camera: Camera? = null
    private var model: Interpreter? = null
    private lateinit var yuvToRgbConv: YuvToRgbConv
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_REQUIRED, PERMISSIONS_REQUEST_CODE)
        } else {

            windowManager = WindowManager(this)
            cameraExecutor = Executors.newSingleThreadExecutor()
            yuvToRgbConv = YuvToRgbConv(this)
            Log.d(TAG, "this is main $this")
            var tfLite = TFLite()
//             model = TFLite().create(this.assets)

            previewView.post {
                setupCamera()
            }

            linearContainer.postDelayed({
                linearContainer.systemUiVisibility = FLAGS_FULLSCREEN
            }, IMMERSIVE_FLAG_TIMEOUT)
            Toast.makeText(this, "Permission already grandted", Toast.LENGTH_LONG).show()
        }




    }

    fun setupCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {

            // CameraProvider
            cameraProvider = cameraProviderFuture.get()

            // Build and bind the camera use cases
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindCameraUseCases() {
//        Log.d(TAG,"==========================================Camera Fragment Bind Camera Usecase")
        val metrics = windowManager.getCurrentWindowMetrics().bounds
//        Log.d(TAG, "Screen metrics: ${metrics.width()} x ${metrics.height()}")
        val screenAspectRatio = aspectRatio(metrics.width(), metrics.height())
        Log.d(TAG, "Preview aspect ratio: $screenAspectRatio")

        val rotation = previewView.display.rotation
        val cameraProvider = cameraProvider
            ?: throw IllegalStateException("Camera initialization failed.")

        val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

        // Preview
        preview = Preview.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(rotation)
            .build()
        // ImageAnalysis
        Log.d(TAG,"==========================================Camera Fragment >>> Analyze")


        imageAnalyzer = ImageAnalysis.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalyzer!!.setAnalyzer(cameraExecutor, ImageAnalysis.Analyzer { image ->
            //YUV420 img
            val bitmap = yuvToRgbConv.yuvToRgb(image = image)


            Log.d(TAG, "this is image| width= ${image.width}, height = ${image.height}")
            Log.d(TAG, "image info=${image.imageInfo}")
            image.close()
        })

        try {
            camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview, imageAnalyzer)

            // Attach the viewfinder's surface provider to preview use case
            preview?.setSurfaceProvider(previewView.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }

//        cameraProvider.unbindAll()
    }

    private fun aspectRatio(width: Int, height: Int): Int {
//        Log.d(TAG,"==========================================Camera Fragment Call Aspect Ratio")
        val previewRatio = max(width, height).toDouble() / min(width, height)
        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }



    fun allPermissionGranted() = PERMISSIONS_REQUIRED.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (allPermissionGranted()) {
                Toast.makeText(this, "Hei, Permission has been granted by user!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Hei, Permission not granted by user!", Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        private val TAG = "MainActivity"
        private const val PERMISSIONS_REQUEST_CODE = 10
        private val PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.CAMERA)
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
        const val FLAGS_FULLSCREEN =
            View.SYSTEM_UI_FLAG_LOW_PROFILE or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        private const val IMMERSIVE_FLAG_TIMEOUT = 500L
    }

}