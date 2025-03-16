package ng.mint.ocrscanner.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

/**
 * Custom view that draws a card scanning overlay
 * Features:
 * - Card outline with rounded corners
 * - Semi-transparent overlay outside card area
 * - Animated scanning line (in real implementation)
 */
class CardOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val overlayPaint = Paint().apply {
        color = Color.parseColor("#80000000") // Semi-transparent black
        style = Paint.Style.FILL
    }

    private val cardOutlinePaint = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 4f
    }

    private val cardPath = Path()
    private val overlayPath = Path()
    private val cardRect = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Calculate card rectangle with aspect ratio 1.586 (standard card ratio)
        val cardWidth = width * 0.85f // 85% of screen width
        val cardHeight = cardWidth / 1.586f
        val left = (width - cardWidth) / 2
        val top = (height - cardHeight) / 2

        cardRect.set(left, top, left + cardWidth, top + cardHeight)

        // Draw overlay
        overlayPath.reset()
        overlayPath.addRect(0f, 0f, width.toFloat(), height.toFloat(), Path.Direction.CW)
        cardPath.reset()
        cardPath.addRoundRect(cardRect, 20f, 20f, Path.Direction.CW)
        overlayPath.op(cardPath, Path.Op.DIFFERENCE)
        canvas.drawPath(overlayPath, overlayPaint)

        // Draw card outline
        canvas.drawRoundRect(cardRect, 20f, 20f, cardOutlinePaint)
    }
}
