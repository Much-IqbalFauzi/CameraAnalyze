package com.dev.analyze

import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.channels.FileChannel

class TFLite {

    val MODEL_FILE: String = "converted_model.tflite"
    val LABEL_FILE: String = "lebelmap.txt"
    lateinit var interpreter: Interpreter

    val INPUT_SIZE_HEIGHT = 128
    val INPUT_SIZE_WIDTH = 128

    private val strides = intArrayOf(8, 16, 16, 16)

    private val ASPECT_RATIOS_SIZE = 1

    private val MIN_SCALE = 0.1484375f
    private val MAX_SCALE = 0.75f

    private val ANCHOR_OFFSET_X = 0.5f
    private val ANCHOR_OFFSET_Y = 0.5f

    private val X_SCALE = 128f
    private val Y_SCALE = 128f
    private val H_SCALE = 128f
    private val W_SCALE = 128f

    private val MIN_SUPPRESSION_THRESHOLD = 0.3f

    //pre-allocated buffers
    private lateinit var intValue: IntArray
    private lateinit var rgbValues: FloatArray
    private lateinit var floatValues: Array<Array<Array<FloatArray>>>
    private lateinit var inputArray: Array<Any>

    private lateinit var inputBuffer: FloatBuffer
    private lateinit var outputBuffer: FloatBuffer
    private val BYTE_SIZE_OF_FLOAT = 4
    private val EMBEDDING_SIZE = 512

    private lateinit var bitmap: Bitmap

    private class Anchor {
        private val x_center = 0f
        private val y_center = 0f
        private val h = 0f
        private val w = 0f
    }

    private fun loadModel(asset: AssetManager?): ByteBuffer {

        val fileDescriptor: AssetFileDescriptor = asset!!.openFd(MODEL_FILE)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel: FileChannel = inputStream.channel
        val startOffset: Long = fileDescriptor.startOffset
        val declaredLength: Long = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun loadLabel(asset: AssetManager?) {
        val inputStream: InputStream = asset!!.open(LABEL_FILE)
        Log.d("CameraXBasic", "=-=-=-=-=- $inputStream")
        val size: Int = inputStream.available()
        Log.d("CameraXBasic", "=-=-=-=-=- $size")
        val buffer = ByteArray(size)
        Log.d("CameraXBasic", "=-=-=-=-=- $buffer")
        inputStream.read(buffer)
        for (item in String(buffer)) {
            Log.d("CameraXBasic", "=-=-=-=-=- $item")
        }
    }

    private fun calculateScale(
        min_scale: Float,
        max_scale: Float,
        stride_index: Int,
        num_strides: Int
    ): Float {
        return min_scale + (max_scale - min_scale) * 1.0f * stride_index / (num_strides - 1.0f)
    }

    fun create(assetManager: AssetManager?): TFLite {
        var model: TFLite = TFLite()

        try {
            model.interpreter = Interpreter(loadModel(assetManager))
            Log.d("CameraXBasic", "=-=-=-=-=- interpreter clear")
//            loadLabel(assetManager)
            Log.d("CameraXBasic", "model has been loaded")
        } catch (e: Exception) {
            Log.d("CameraXBasic", "model cannot loaded")
            throw RuntimeException(e)
        }


        // Pre-allocate buffers.
        model.intValue = intArrayOf(INPUT_SIZE_HEIGHT * INPUT_SIZE_WIDTH)
        model.rgbValues = FloatArray(INPUT_SIZE_HEIGHT * INPUT_SIZE_WIDTH * 3)
        model.inputBuffer =
            ByteBuffer.allocateDirect(INPUT_SIZE_HEIGHT * INPUT_SIZE_WIDTH * 3 * BYTE_SIZE_OF_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
        model.outputBuffer = ByteBuffer.allocateDirect(EMBEDDING_SIZE * BYTE_SIZE_OF_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()

        model.bitmap = Bitmap.createBitmap(
            INPUT_SIZE_WIDTH,
            INPUT_SIZE_HEIGHT,
            Bitmap.Config.ARGB_8888
        )
        return model
    }

    fun close() {
        interpreter.close()
    }
}