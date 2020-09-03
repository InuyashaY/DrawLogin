package yzl.swu.drawlogin

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect

/**
 * 管理没赢过圆点的具体样式
 * 中心 x，y
 * 半径 radius
 * 画笔 paint
 * */
class DotView(val cx:Float, val cy:Float, val radius:Float,val tag: Int) {
    val paint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 2f
        style = Paint.Style.FILL
    }

    //点的矩形范围
    val rect = Rect()
    //选中点内圆半径
    var innerCircleRadius = 0f
    //记录是否被选中
    var isSelected = false
        set(value) {
            field = value
            if (value){
                paint.color = Color.rgb(0,199,255)
            }else{
                paint.color = Color.BLACK
            }
        }



    //初始化代码块
    init {
        rect.left = (cx - radius).toInt()
        rect.top = (cy - radius).toInt()
        rect.right = (cx + radius).toInt()
        rect.bottom = (cy + radius).toInt()

        innerCircleRadius = radius / 3.5f
    }
    fun setColor(color: Int){
        paint.color = color
    }






}