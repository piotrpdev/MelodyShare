package dev.piotrp.melodyshare.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.view.doOnPreDraw
import com.github.ajalt.timberkt.d
import com.google.android.material.R
import com.google.android.material.color.MaterialColors
import dev.piotrp.melodyshare.models.MelodyModel

// Good examples:
// https://medium.com/android-news/android-canvas-for-drawing-and-custom-views-e1a3e90d468b
// https://medium.com/@huseyinozkoc/android-canvas-and-create-custom-view-c6ed11fcc42f

// Taken from https://stackoverflow.com/q/61344444/19020549

/**
 * View used to visualize [MelodyModel] melodies on a canvas.
 */
class MelodyView
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0,
    ) : View(context, attrs, defStyle) {
        // ? Can only fit so much on screen
        private val pitchMax = 72
        private val pitchMin = 48

        // TODO: Adjust this depending on BPM?
        // ? eight quarter notes
        private val tickMax = 480 * 8

        private val minX = 2f
        private val minY = 2f

        private var noteHeightUnit: Float = 0f
        private var noteWidthUnit: Float = 0f

        private var viewHeight: Float = 0f
        private var viewWidth: Float = 0f

        private val outlinePaint =
            Paint().apply {
                isAntiAlias = true
                // TODO: Does calling this method cause a slowdown?
                // https://stackoverflow.com/a/64509627/19020549
                color = MaterialColors.getColor(context, R.attr.colorOutline, Color.BLACK)
                style = Paint.Style.STROKE
                strokeWidth = 1f
            }

        private val textPaint =
            Paint().apply {
                isAntiAlias = true
                // TODO: Does calling this method cause a slowdown?
                // https://stackoverflow.com/a/64509627/19020549
                color = MaterialColors.getColor(context, R.attr.colorPrimary, Color.BLACK)
                style = Paint.Style.FILL
                textAlign = Paint.Align.CENTER
                textSize = 30f
            }

        private val notePaint =
            Paint().apply {
                isAntiAlias = true
                // TODO: Does calling this method cause a slowdown?
                // https://stackoverflow.com/a/64509627/19020549
                color = MaterialColors.getColor(context, R.attr.colorPrimary, Color.BLACK)
                style = Paint.Style.FILL
            }

        private var melodyModel: MelodyModel? = null

        init {
            // https://stackoverflow.com/a/28136027/19020549
            this.doOnPreDraw {
                viewHeight = height.toFloat()
                viewWidth = width.toFloat()
                // Extra minY for outline strokeWidth
                noteHeightUnit = (viewHeight - (minY * 3)) / (pitchMax - pitchMin)
                noteWidthUnit = (viewWidth - (minX * 3)) / tickMax

//            d { "noteHeightUnit: $noteHeightUnit" }
//            d { "noteWidthUnit: $noteWidthUnit" }
            }
            setLayerType(LAYER_TYPE_SOFTWARE, null)
        }

        fun setMelody(melodyModel: MelodyModel) {
            d { "setMelody() called with melody: ${melodyModel.title}" }
            this.melodyModel = melodyModel
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            // TODO: Add note grid?
//        d { "Drawing melody RoundRect at (x1: ${minX}, y1: ${minY}, x2: ${viewWidth - minX}, y2: ${viewHeight - minY})" }
            canvas.drawRoundRect(minX, minY, viewWidth - minX, viewHeight - minY, 10f, 10f, outlinePaint)

            if (melodyModel == null) {
                // https://stackoverflow.com/a/11121873/19020549
                canvas.drawText(
                    "melodyModel is null",
                    viewWidth / 2,
                    ((viewHeight / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)),
                    textPaint,
                )
                return
            }

            melodyModel!!.notes.forEach {
                if (it.pitch !in pitchMin..pitchMax) return@forEach
                if (it.tick >= tickMax) return@forEach

                // TODO: Maybe scale timeline depending on bpm?
                val noteY = viewHeight - (((it.pitch - pitchMin) * noteHeightUnit) + minY)
                val noteX = (it.tick * noteWidthUnit) + minX

//            d { "Drawing:  [pitch: ${it.pitch}, tick: ${it.tick}] at (x1: ${noteX}, y1: ${noteY}, x2: ${noteX + noteWidthUnit}, y2: ${noteY + noteHeightUnit})" }

                // TODO: Change color for notes on same x co-ord?
                canvas.drawRect(noteX, noteY, noteX + (it.duration * noteWidthUnit), noteY - noteHeightUnit, notePaint)
            }
        }
    }
