package com.kamaboko.imageavoid.customView

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.math.MathUtils
import com.google.android.material.math.MathUtils.dist
import kotlin.math.roundToInt

/**
 * TODO: document your custom view class.
 */
class TestView2 @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    val DECAY: Float = 0.05F

    val DD: Float = 0.01F

    var p: Array<PaintData?> = arrayOfNulls(30000)

    // コンストラクター
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        bitmap: Bitmap,
        wAdjust: Int,
        hAdjust: Int
    ) : this(context, attrs, defStyleAttr) {
        Log.d("test", bitmap.height.toString())
        Log.d("test", bitmap.width.toString())
        p = arrayOfNulls(bitmap.height * bitmap.width)
        for (i in 0 until bitmap.height step 10) {
            for (j in 0 until bitmap.width step 10) {
                var col = bitmap.getPixel(j, i)
                col = Color.argb(
                    (Color.alpha(col) * 0.7).toInt(),
                    Color.red(col),
                    Color.green(col),
                    Color.blue(col)
                )
                p[i*bitmap.width+j] = PaintData(j+wAdjust.toFloat(), i+hAdjust.toFloat(), col)
            }
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)


    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        p.forEach {
            if (it != null) {
                paint.color = it.col
                canvas?.drawRect(
                    it.setX, it.setY,
                    it.setX + 15, it.setY + 15, paint
                )
            }
        }
    }

    fun touchMove(x: Float, y: Float) {
        p.forEach {
            if (it != null) {
                val dst = dist(x, y, it.setX.toFloat(), it.setY.toFloat())
                if (dst < 130) {
                    val tmpX = (it.setX - x).toInt()
                    val tmpY = (it.setY - y).toInt()
                    val add = 300 / dst * 10
                    if (tmpX != 0) {
                        it.accelerationX = (tmpX * add).toInt()
                    }
                    if (tmpY != 0) {
                        it.accelerationY = (tmpY * add).toInt()
                    }
                    it.dst += 50
                }
            }
        }
    }

    //　元の場所に戻る
    fun originMove() {

        p.forEach {
            var origin = 0;



            if (it != null) {

                val dst = dist(
                    it.oriX,
                    it.oriY,
                    it.setX,
                    it.setY
                )

                // 加速度
                if (it.accelerationX > 700 || it.accelerationX < -700) {
                    val addX = (it.accelerationX * DD).toInt()
                    // 一定距離離れたら移動完了
                    if (dst < it.dst) {
                        it.setX += addX
                    }
                    it.accelerationX -= addX
                } else {
                    origin++
                }
                if (it.accelerationY > 700 || it.accelerationY < -700) {
                    val addY = (it.accelerationY * DD).toInt()
                    // 一定距離離れたら移動終了
                    if (dst < it.dst) {
                        it.setY += addY
                    }
                    it.accelerationY -= (addY * 2.5).roundToInt()
                } else {
                    origin++
                }

                // 加速後が十分になければ戻る
                if (origin == 2) {
                    // 戻る力
                    if (dst > 1) {
                        it.setX += (((it.oriX - it.setX)).toDouble() * DECAY).toFloat()
                        it.setY += (((it.oriY - it.setY)).toDouble() * DECAY).toFloat()
                    } else {
                        it.setX = it.oriX
                        it.setY = it.oriY
                        it.dst = it.oriDst
                    }
                }
            }
        }


    }

}