/*
 * Copyright (C) 2015 Jorge Castillo Pérez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.anbillon.nougatboot.svg

import android.graphics.Path
import android.graphics.PointF
import java.text.ParseException

/**
 * Entity to parse Svg paths to {@link Path} items understandable by the Android SDK. Obtained
 * from romainnurik Muzei implementation to avoid rewriting .
 *
 * @author romainnurik
 */
class SvgPathParser {
    private val TOKEN_ABSOLUTE_COMMAND = 1
    private val TOKEN_RELATIVE_COMMAND = 2
    private val TOKEN_VALUE = 3
    private val TOKEN_EOF = 4

    private var mCurrentToken: Int = 0
    private val mCurrentPoint = PointF()
    private var mLength: Int = 0
    private var mIndex: Int = 0
    private var mPathString: String? = null

    protected fun transformX(x: Float): Float {
        return x
    }

    protected fun transformY(y: Float): Float {
        return y
    }

    @Throws(ParseException::class)
    fun parsePath(s: String): Path {
        mCurrentPoint.set(java.lang.Float.NaN, java.lang.Float.NaN)
        mPathString = s
        mIndex = 0
        mLength = mPathString!!.length

        val tempPoint1 = PointF()
        val tempPoint2 = PointF()
        val tempPoint3 = PointF()

        val p = Path()
        p.setFillType(Path.FillType.WINDING)

        var firstMove = true
        while (mIndex < mLength) {
            val command = consumeCommand()
            val relative = mCurrentToken == TOKEN_RELATIVE_COMMAND
            when (command) {
                'M', 'm' -> {
                    // move command
                    var firstPoint = true
                    while (advanceToNextToken() == TOKEN_VALUE) {
                        consumeAndTransformPoint(tempPoint1, relative && mCurrentPoint.x !== java.lang.Float.NaN)
                        if (firstPoint) {
                            p.moveTo(tempPoint1.x, tempPoint1.y)
                            firstPoint = false
                            if (firstMove) {
                                mCurrentPoint.set(tempPoint1)
                                firstMove = false
                            }
                        } else {
                            p.lineTo(tempPoint1.x, tempPoint1.y)
                        }
                    }
                    mCurrentPoint.set(tempPoint1)
                }

                'C', 'c' -> {
                    // curve command
                    if (mCurrentPoint.x === java.lang.Float.NaN) {
                        throw ParseException("Relative commands require current point", mIndex)
                    }

                    while (advanceToNextToken() == TOKEN_VALUE) {
                        consumeAndTransformPoint(tempPoint1, relative)
                        consumeAndTransformPoint(tempPoint2, relative)
                        consumeAndTransformPoint(tempPoint3, relative)
                        p.cubicTo(tempPoint1.x, tempPoint1.y, tempPoint2.x, tempPoint2.y, tempPoint3.x,
                                tempPoint3.y)
                    }
                    mCurrentPoint.set(tempPoint3)
                }

                'L', 'l' -> {
                    // line command
                    if (mCurrentPoint.x === java.lang.Float.NaN) {
                        throw ParseException("Relative commands require current point", mIndex)
                    }

                    while (advanceToNextToken() == TOKEN_VALUE) {
                        consumeAndTransformPoint(tempPoint1, relative)
                        p.lineTo(tempPoint1.x, tempPoint1.y)
                    }
                    mCurrentPoint.set(tempPoint1)
                }

                'H', 'h' -> {
                    // horizontal line command
                    if (mCurrentPoint.x === java.lang.Float.NaN) {
                        throw ParseException("Relative commands require current point", mIndex)
                    }

                    while (advanceToNextToken() == TOKEN_VALUE) {
                        var x = transformX(consumeValue())
                        if (relative) {
                            x += mCurrentPoint.x
                        }
                        p.lineTo(x, mCurrentPoint.y)
                    }
                    mCurrentPoint.set(tempPoint1)
                }

                'V', 'v' -> {
                    // vertical line command
                    if (mCurrentPoint.x === java.lang.Float.NaN) {
                        throw ParseException("Relative commands require current point", mIndex)
                    }

                    while (advanceToNextToken() == TOKEN_VALUE) {
                        var y = transformY(consumeValue())
                        if (relative) {
                            y += mCurrentPoint.y
                        }
                        p.lineTo(mCurrentPoint.x, y)
                    }
                    mCurrentPoint.set(tempPoint1)
                }

                'A', 'a' ->{
                    if (mCurrentPoint.x === java.lang.Float.NaN) {
                        throw ParseException("Relative commands require current point", mIndex)
                    }
                    var orX = mCurrentPoint.x
                    var orY = mCurrentPoint.y
                    while (advanceToNextToken() == TOKEN_VALUE) {
//                        consumeAndTransformPoint(tempPoint1, relative)
//                        consumeAndTransformPoint(tempPoint2, relative)
//                        consumeAndTransformPoint(tempPoint3, relative)
                        var rx = consumeValue()
                        var ry = consumeValue()
                        //弧度
                        var arc = consumeValue()
                        //1 是大弧度 0是小弧度
                        var bigEnd = consumeValue()
                        //1 是顺时针 0是逆时针
                        var clockorder = consumeValue()
                        //终点
                        consumeAndTransformPoint(tempPoint1, relative)


//                        p.arcTo(RectF(),getAngel())
                    }
                    mCurrentPoint.set(tempPoint1)
                }

                'Z', 'z' -> {
                    // close command
                    p.close()
                }
            }
        }

        return p
    }

    private fun advanceToNextToken(): Int {
        while (mIndex < mLength) {
            val c = mPathString!![mIndex]
            if ('a' <= c && c <= 'z') {
                mCurrentToken = TOKEN_RELATIVE_COMMAND
                return mCurrentToken
            } else if ('A' <= c && c <= 'Z') {
                mCurrentToken = TOKEN_ABSOLUTE_COMMAND
                return mCurrentToken

            } else if ('0' <= c && c <= '9' || c == '.' || c == '-') {
                mCurrentToken = TOKEN_VALUE
                return mCurrentToken

            }
            ++mIndex
        }

        mCurrentToken = TOKEN_EOF
        return mCurrentToken

    }

    private fun getAngel(x:Float,y:Float):Float{
        return (Math.atan2(y.toDouble(),x.toDouble()).toFloat()/(2*Math.PI)*360).toFloat()
    }

    @Throws(ParseException::class)
    private fun consumeCommand(): Char {
        advanceToNextToken()
        if (mCurrentToken != TOKEN_RELATIVE_COMMAND && mCurrentToken != TOKEN_ABSOLUTE_COMMAND) {
            throw ParseException("Expected command", mIndex)
        }

        return mPathString!![mIndex++]
    }

    @Throws(ParseException::class)
    private fun consumeAndTransformPoint(out: PointF, relative: Boolean) {
        out.x = transformX(consumeValue())
        out.y = transformY(consumeValue())
        if (relative) {
            out.x += mCurrentPoint.x
            out.y += mCurrentPoint.y
        }
    }

    @Throws(ParseException::class)
    private fun consumeValue(): Float {
        advanceToNextToken()
        if (mCurrentToken != TOKEN_VALUE) {
            throw ParseException("Expected value", mIndex)
        }

        var start = true
        var seenDot = false
        var index = mIndex
        while (index < mLength) {
            val c = mPathString!![index]
            if (!('0' <= c && c <= '9') && (c != '.' || seenDot) && (c != '-' || !start)) {
                // end of value
                break
            }
            if (c == '.') {
                seenDot = true
            }
            start = false
            ++index
        }

        if (index == mIndex) {
            throw ParseException("Expected value", mIndex)
        }

        val str = mPathString!!.substring(mIndex, index)
        try {
            val value = java.lang.Float.parseFloat(str)
            mIndex = index
            return value
        } catch (e: NumberFormatException) {
            throw ParseException("Invalid float value '$str'.", mIndex)
        }

    }
}