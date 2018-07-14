package il.co.apeal.app.customaziation

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.util.AttributeSet
import android.widget.EditText


class PinEntryEditText : EditText {

    var mSpace = 24f //24 dp by default
    var mCharSize = 0f
    var mNumChars = 6f
    var mLineSpacing = 8f //8dp by default

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet,
                defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet,
                defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        setBackgroundResource(0)
        val multi = context.resources.displayMetrics.density
        mSpace *= multi //convert to pixels for our density
        mLineSpacing *= multi; //convert to pixels
        val mMaxLength = attrs.getAttributeIntValue(
                "editText", "maxLength", 6)
        mNumChars = mMaxLength.toFloat()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        //super.onDraw(canvas)
        val availableWidth = width - paddingRight - paddingLeft
        mCharSize = if (mSpace < 0) {
            availableWidth / (mNumChars * 2 - 1)
        } else {
            (availableWidth - mSpace * (mNumChars - 1)) / mNumChars
        }

        var startX = paddingLeft.toFloat()
        val bottom = height - paddingBottom.toFloat()

        //Text Width
        val text = text
        val textLength = text.length
        val textWidths = FloatArray(textLength)
        paint.getTextWidths(getText(), 0, textLength, textWidths)
        paint.color = Color.WHITE

        for (i in 0 until mNumChars.toInt()) {
            canvas.drawLine(
                    startX, bottom, startX + mCharSize, bottom, paint)

            if (text.length > i) {
                val middle = startX + mCharSize / 2
                canvas.drawText(text,
                        i,
                        i + 1,
                        middle - textWidths[0] / 2,
                        bottom - mLineSpacing,
                        paint)
            }

            startX += if (mSpace < 0) {
                mCharSize * 2
            } else {
                mCharSize + mSpace
            }
        }
    }
}