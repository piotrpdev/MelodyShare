package dev.piotrp.melodyshare.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.view.doOnPreDraw
import com.github.ajalt.timberkt.d
import com.google.android.material.color.MaterialColors
import com.google.android.material.R
import dev.piotrp.melodyshare.models.MelodyModel

// Good examples:
// https://medium.com/android-news/android-canvas-for-drawing-and-custom-views-e1a3e90d468b
// https://medium.com/@huseyinozkoc/android-canvas-and-create-custom-view-c6ed11fcc42f

// Taken from https://stackoverflow.com/q/61344444/19020549
class MelodyView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : View(context, attrs, defStyle) {
    private var viewHeight: Float = 0f
    private var viewWidth: Float = 0f

    private val outlinePaint = Paint().apply {
        isAntiAlias = true
        // TODO: Does calling this method cause a slowdown?
        // https://stackoverflow.com/a/64509627/19020549
        color = MaterialColors.getColor(context, R.attr.colorOutline, Color.BLACK)
        style = Paint.Style.STROKE
        strokeWidth = 1f
    }

    private var melodyModel: MelodyModel? = null

    init {
        // https://stackoverflow.com/a/28136027/19020549
        this.doOnPreDraw {
            viewHeight = height.toFloat()
            viewWidth = width.toFloat()
        }
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    fun setMelody(melodyModel: MelodyModel) {
        d { "setMelody() called with melody: ${melodyModel.title}" }
        this.melodyModel = melodyModel
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (melodyModel != null) {
            canvas.drawRoundRect(2f, 2f, viewWidth -2f, viewHeight - 2f, 10f, 10f, outlinePaint)
        }
    }
}