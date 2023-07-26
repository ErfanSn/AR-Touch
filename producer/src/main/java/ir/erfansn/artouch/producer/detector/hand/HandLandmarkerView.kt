/*
 * Copyright (C) 2023 ErfanSn
 *
 * AR Touch is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AR Touch is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AR Touch.  If not, see <https://www.gnu.org/licenses/>.
 */

package ir.erfansn.artouch.producer.detector.hand

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import kotlin.math.max

class HandLandmarkerView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var pointPaint = Paint()
    private var linePaint = Paint()

    private var scaleFactor: Float = 1f
    private var imageWidth: Int = 1
    private var imageHeight: Int = 1

    init {
        pointPaint.color = Color.YELLOW
        pointPaint.strokeWidth = 24f
        pointPaint.style = Paint.Style.FILL

        linePaint.color = Color.LTGRAY
        linePaint.strokeWidth = 12f
        linePaint.style = Paint.Style.STROKE
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        result?.let { result ->
            for (connection in HandLandmarker.HAND_CONNECTIONS) {
                if (result.landmarks.isEmpty()) break

                val startPreviewOptimizedLandmark = result.landmarks[connection.start()].previewOptimized
                val endPreviewOptimizedLandmark = result.landmarks[connection.end()].previewOptimized
                canvas.drawLine(
                    startPreviewOptimizedLandmark.x(),
                    startPreviewOptimizedLandmark.y(),
                    endPreviewOptimizedLandmark.x(),
                    endPreviewOptimizedLandmark.y(),
                    linePaint
                )
            }
            for (normalizedLandmark in result.landmarks) {
                val previewOptimizedLandmark = normalizedLandmark.previewOptimized
                canvas.drawPoint(
                    previewOptimizedLandmark.x(),
                    previewOptimizedLandmark.y(),
                    pointPaint
                )
            }
        }
    }

    private val NormalizedLandmark.previewOptimized: NormalizedLandmark
        get() {
            val widthSizeDeltaHalf = (imageWidth * scaleFactor - width).toInt() / 2
            val heightSizeDeltaHalf = (imageHeight * scaleFactor - height).toInt() / 2
            return NormalizedLandmark.create(
                x() * imageWidth * scaleFactor - widthSizeDeltaHalf,
                y() * imageHeight * scaleFactor - heightSizeDeltaHalf,
                z()
            )
        }

    var result: HandDetectionResult? = null
        set(value) {
            field = value
            value ?: return

            imageWidth = value.inputImageSize.width
            imageHeight = value.inputImageSize.height

            scaleFactor = max(width / imageWidth.toFloat(), height / imageHeight.toFloat())

            invalidate()
        }
}
