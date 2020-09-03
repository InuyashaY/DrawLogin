package yzl.swu.drawlogin

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import java.lang.StringBuilder

class SlideUnlockView: View{

    constructor(context: Context?):super(context){}
    constructor(context: Context?,attrs: AttributeSet?): super(context,attrs){}
    constructor(context: Context?,attrs: AttributeSet?,style: Int): super(context,attrs,style){}
    //圆的半径
    private var radius = 0f
    //间距
    private var padding = 0f
    //保存所有九个点的对象
    private val dots = mutableListOf<DotView>()
    //保存被选中的点
    private val selectedDots = mutableListOf<DotView>()
    //上一次被点亮的点
    private var lastSelectDot:DotView? = null
    //记录移动线条
    private var endPoint = Point(0,0)

    //记录划线的路径
    private val linePath = Path()
    //线条画笔
    private val linePaint = Paint().apply {
        color = Color.DKGRAY
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }
    //圆内部遮盖的Paint
    private val innerCirclePaint = Paint().apply{
        color = Color.WHITE
    }
    //记录密码
    private val password = StringBuilder()

    public var oPasswordListenner:OnPasswordChangedListenner? = null


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        var a = charArrayOf()
        //初始化
        init()
    }

    //具体绘制内容
    override fun onDraw(canvas: Canvas?) {
        //画线
        canvas?.drawPath(linePath,linePaint)
        //绘制移动的线
        if (!endPoint.equals(0,0)){
            canvas?.drawLine(lastSelectDot!!.cx,lastSelectDot!!.cy,endPoint.x.toFloat(),endPoint.y.toFloat(),linePaint)
        }
        //绘制九个点
        drawNineDots(canvas)

    }

    //绘制九个点
    private fun drawNineDots(canvas: Canvas?){
        for (dot in dots){
            canvas?.drawCircle(dot.cx,dot.cy,dot.radius,dot.paint)
            canvas?.drawCircle(dot.cx,dot.cy,radius-2,innerCirclePaint)
            if (dot.isSelected){
                canvas?.drawCircle(dot.cx,dot.cy,dot.innerCircleRadius,dot.paint)
            }
        }
    }

    //初始化
    private fun init(){
        //第一个点的中心坐标
        var cx = 0f
        var cy = 0f
        //计算半径和间距
        //判断你用户设置当前View的尺寸  确保在正方形区域绘制
        if (measuredWidth >= measuredHeight){
            //半径
            radius = measuredHeight/5/2f
            //间距
            padding = (measuredHeight-3*radius*2) / 4
            //中心点
            cx = (measuredWidth - measuredWidth)/2f + padding
            cy = padding + radius
        }else{
            radius = measuredWidth/5/2f
            padding = (measuredWidth - 3*radius*2) / 4
            cx = padding + radius
            cy = (measuredHeight - measuredWidth)/2f + padding +radius
        }

        //设置九个点组成的Path
        for (row in 0..2){
            for (colum in 0..2){
                DotView(cx+colum*(2*radius+padding),
                    cy+row*(2*radius+padding),
                    radius,row*3+colum+1).also { dots.add(it) }
            }
        }


    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        //获取触摸点坐标
        val x = event?.x
        val y = event?.y
        when(event?.action){
            MotionEvent.ACTION_DOWN->{
                //判断点是否在某个点的矩形区域内
                containsPoint(x,y).also {
                    if (it != null){
                        //在某个圆点
                        selectItem(it)
                        selectedDots.add(it)
                        linePath.moveTo(it.cx,it.cy)
                    }else{
                        //不在某个圆点
                    }
                }
            }
            MotionEvent.ACTION_MOVE->{
                //判断点是否在某个点的矩形区域内
                containsPoint(x,y).also {
                    if (it != null){
                        if (!it.isSelected){
                            //没有被点亮
                            //是不是第一个点
                            if (lastSelectDot == null){
                                //第一个点
                                linePath.moveTo(it.cx,it.cy)
                            }else{
                                //从上一个点画线
                                linePath.lineTo(it.cx,it.cy)
                            }
                            //点亮这个点
                            selectItem(it)
                            selectedDots.add(it)
                        }
                    }else{
                        //触摸点在外部
                        if (lastSelectDot != null){
                            endPoint.set(x!!.toInt(),y!!.toInt())
                            invalidate()
                        }
                    }
                }
            }
            MotionEvent.ACTION_UP->{
                oPasswordListenner?.passwordChanged(password.toString())
                reset()
            }
        }
        return true
    }

    //重设
    private fun reset(){


        //将颜色改为正常颜色
        for (item in selectedDots){
            item.isSelected = false
        }
        invalidate()
        //清空
        selectedDots.clear()
        lastSelectDot = null
        //线条重设
        linePath.reset()
        //设置endPoint为空
        endPoint.set(0,0)

        //清空密码
        Log.v("yyy",password.toString())
        password.delete(0,password.length)

    }

    //选中某个点
    private fun selectItem(item: DotView){
        //设为被选中
        item.isSelected = true
        //刷新
        invalidate()
        //保存点亮点
        selectedDots.add(item)
        //记录点
        lastSelectDot = item
        //设置endPoint为空
        endPoint.set(0,0)
        //记录当前密码
        password.append(item.tag)
    }

    //查找某个矩形区域是否包含某个触摸点
    private fun containsPoint(x: Float?,y: Float?):DotView?{
        for (item in dots){
            if (item.rect.contains(x!!.toInt(),y!!.toInt())){
                return item
            }
        }
        return null
    }

    //回调密码
    fun passwordBlock(pwd:(String) -> Unit){

        pwd(password.toString())
    }


    //接口 回调密码
    interface OnPasswordChangedListenner{
       fun passwordChanged(pwd:String)

    }

}