package io.github.karino2.kasokudomove

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.abs

class KasokudoMoveView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    var posX = 0.0F
    var posY = 0.0F

    var touchX = 0.0F
    var touchY = 0.0F

    // 速度
    var vX = 0.0F
    var vY = 0.0F

    // 加速度
    var aX = 0.0F
    var aY = 0.0F

    // 加速度の大きさ
    val aCoeff = 0.1F

    // 抵抗の大きさ
    val fCoeff = 0.5F

    var animating = false

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE)
        {
            touchX = event.x
            touchY = event.y
            if (!animating)
                tsugiNoSyori()
            return true
        }
        return super.onTouchEvent(event)
    }

    // Runnableの生成のコストを減らすために同じものを使い回す
    val nextFrameRun = Runnable { tsugiNoSyori() }

    fun tsugiNoSyori() {
        val diffX = touchX - posX
        val diffY = touchY - posY

        // 近すぎたらもう何もしない
        if (abs(diffX)+abs(diffY) < 0.1F) {
            aX = 0.0F
            aY = 0.0F
            vX = 0.0F
            vY = 0.0F
            animating = false
            return
        }

        // 力は離れた距離に比例して、aCoeffを掛けた値で加速度になるとする
        // 抵抗はvXとvYに比例して逆方向
        aX = aCoeff*diffX - fCoeff*vX
        aY =  aCoeff*diffY - fCoeff*vY

        // 速度の更新
        vX += aX
        vY += aY

        // 位置の更新
        posX += vX
        posY += vY

        animating = true
        invalidate()
        handler.postDelayed( nextFrameRun, 100)
    }

    // 初期化のさぼりにapplyを使わせてもらう
    val paint = Paint().apply { color= Color.RED; style=Paint.Style.FILL  }

    val rectWidth = 200.0F
    val rectHeight = 200.0F
    val rect = RectF(0.0F, 0.0F, rectWidth, rectHeight )


    override fun onDraw(canvas: Canvas) {
        rect.set(posX, posY, posX+rectWidth, posY+rectHeight)
        canvas.drawRect(rect, paint)
    }

}