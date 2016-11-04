package com.anbillon.nougatboot

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.view.animation.LinearInterpolator
import com.anbillon.nougatboot.svg.SvgPathParser
import java.util.*

/**
 *power by
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MM.:  .:'   `:::  .:`MMMMMMMMMMM|`MMM'|MMMMMMMMMMM':  .:'   `:::  .:'.MM
MMMM.     :          `MMMMMMMMMM  :*'  MMMMMMMMMM'        :        .MMMM
MMMMM.    ::    .     `MMMMMMMM'  ::   `MMMMMMMM'   .     ::   .  .MMMMM
MMMMMM. :   :: ::'  :   :: ::'  :   :: ::'      :: ::'  :   :: ::.MMMMMM
MMMMMMM    ;::         ;::         ;::         ;::         ;::   MMMMMMM
MMMMMMM .:'   `:::  .:'   `:::  .:'   `:::  .:'   `:::  .:'   `::MMMMMMM
MMMMMM'     :           :           :           :           :    `MMMMMM
MMMMM'______::____      ::    .     ::    .     ::     ___._::____`MMMMM
MMMMMMMMMMMMMMMMMMM`---._ :: ::'  :   :: ::'  _.--::MMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMM::.         ::  .--MMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMM-.     ;::-MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM. .:' .M:F_P:MMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM.   .MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM\ /MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMVMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM
 * Created by Paper on 16/11/3.
 */
open class NougatBootDrawable : Drawable(), Animatable {


    val mColors: IntArray
    val mPositions: ArrayList<Pair<Float, Float>>
    val mRadius: Float = 20f
    val mCirclePainter: Paint
    val mStartAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, mRadius)
    val mEndAnimator: ValueAnimator = ValueAnimator.ofInt(0, 10)
    val mRealAnimator: ValueAnimator = ValueAnimator.ofObject(NougatBootEvalutor(),FloatArray(4),FloatArray(4))
    val mRealAnim:AnimatorSet = AnimatorSet()
    var p: Path
    var mPm: PathMeasure
    //    var defuatPath = "M20,50 L40,50 L60,50 L80,50 A10 10 180 0 0 60 50 A10 20 180 0 0 40 50 A10,10,180,0,0,20,50z"
    var defuatPath = "M20,50 L40,50 L60,50 L80,50 C80,30 60,30 60,50 C60,30 40,30 40,50 C40,30 20,30 20,50Z"
    var mPathLength: Float
    var mCanvasWid: Int = 0
    var mCanvasHei: Int = 0

    /**
     * start animation time
     */
    val starttime: Long = 5000L

    init {
        var sp = SvgPathParser()
        p = sp.parsePath(defuatPath)
        mPm = PathMeasure(p, false)
        mPathLength = mPm.length
        println("length:"+mPm.length)
        mColors = intArrayOf(Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE)
        mPositions = ArrayList()
        mPositions.add(Pair(0f, 0f))
        mPositions.add(Pair(0f, 0f))
        mPositions.add(Pair(0f, 0f))
        mPositions.add(Pair(0f, 0f))
        mCirclePainter = Paint(Paint.ANTI_ALIAS_FLAG)
        mCirclePainter.style = Paint.Style.FILL
        mStartAnimator.addListener(object : AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {

            }

            override fun onAnimationEnd(animation: Animator?) {
                mRealAnimator.start()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {

            }
        })
        mStartAnimator.addUpdateListener {

            invalidateSelf()
        }


        mRealAnimator.duration = starttime
        mRealAnimator.repeatCount = -1
        mRealAnimator.interpolator = LinearInterpolator()
        mRealAnimator.addUpdateListener {
            var v = it.animatedValue as FloatArray
            for (i in 0..3) {
                mPositions[i] = calculatePosition(v[i])
                if (i==1) println("value:"+v[i])
            }
            invalidateSelf()
        }


    }

    /**
     * calculate Ball position
     */

    fun calculatePosition(t: Float): Pair<Float, Float> {
        val p: FloatArray = FloatArray(2)
        mPm.getPosTan(mPathLength * t , p, null)
        return Pair(p[0]*mCanvasWid/100, p[1]*mCanvasHei/100)
    }


    override fun draw(canvas: Canvas?) {

        var i: Int = 0
        if (canvas != null) {
            mCanvasHei = canvas.height
            mCanvasWid = canvas.width
            mColors.forEach {
                drawCircle(canvas, i)
                i++
            }
        }
    }

    fun drawCircle(canvas: Canvas, posi: Int) {
        mCirclePainter.color = mColors[posi]
        canvas.drawCircle(mPositions[posi].first, mPositions[posi].second, mRadius, mCirclePainter)
    }

    override fun setAlpha(alpha: Int) {

    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSPARENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }


    override fun isRunning(): Boolean {
        return mStartAnimator.isRunning || mRealAnimator.isRunning || mEndAnimator.isRunning
    }

    override fun start() {
        mStartAnimator.start()
    }

    override fun stop() {
        if (mStartAnimator.isRunning)
            mStartAnimator.cancel()
        if (mRealAnimator.isRunning)
            mRealAnimator.cancel()
        mEndAnimator.start()
    }

    @TargetApi(19)
    fun pause(){
        mRealAnimator.pause()
    }
    fun resume(){
        mRealAnimator.resume()
    }


}