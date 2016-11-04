package com.anbillon.nougatboot

import android.animation.TypeEvaluator

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
 * Created by Paper on 16/11/4.
 */
class NougatBootEvalutor : TypeEvaluator<FloatArray> {
    override fun evaluate(f: Float, startValue: FloatArray?, endValue: FloatArray?): FloatArray {
        var r: FloatArray = FloatArray(4)
        for (i in 0..3) {
            r[i] = getDis(((f + 0.25 * i) % 1).toFloat())
        }
        return r
    }


    fun getDis(input: Float): Float {
        if (input < 0.75) {
            if (input < 0.25) {
                if (input < 0.17)
                    return 0f
                else
                    return (decelerat(((input-0.17) / 0.08).toFloat()) * 0.1).toFloat()
            } else if (input < 0.5) {
                if (input < 0.33)
                    return 0.1f
                else if (input < 0.41)
                    return (0.1f + decelerat(((input - 0.33) / 0.08).toFloat()) * 0.1).toFloat()
                else return 0.2f
            } else {
                if (input < 0.58)
                    return (0.2f + decelerat(((input - 0.5) / 0.08).toFloat()) * 0.1).toFloat()
                else return 0.3f
            }
        } else return (0.3 + 0.7 * ((input - 0.75) / 0.25)).toFloat()
    }

    fun decelerat(input: Float): Float {
        return (Math.cos((input - 1) * Math.PI) / 2f).toFloat() + 0.5f
    }
}