package com.alexaat.spinnyclock.views


import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alexaat.spinnyclock.R
import com.alexaat.spinnyclock.utils.*
import java.util.*
import kotlin.math.*


class ClockView @JvmOverloads constructor(context:Context, attrs: AttributeSet?= null, defStyleAttr:Int = 0) : View(context,attrs,defStyleAttr){

    private val _time = MutableLiveData<Calendar>()
    val time:LiveData<Calendar>
        get() = _time

    private val _tickSound = MutableLiveData<Boolean>(false)
    val tickSound:LiveData<Boolean>
        get() = _tickSound


    private var hours = 22
    private var minutes = 10
    private val rangeAngle = 30

    private lateinit var centrePoint:PointF
    private lateinit var hourHandPoint:PointF
    private lateinit var minuteHandPoint:PointF
    private lateinit var scaledClockFace:Bitmap
    private lateinit var scaledClockFaceSize:PointF

    private var radius = 0.0f

    private val hourHandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeWidth = 30.0f
        color = Color.BLACK
    }
    private val minuteHandPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeWidth = 20.0f
        color = Color.BLACK
    }
    private val centralCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.BLACK
    }
    private val innerCentralCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.RED
    }
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeWidth = 8.0f
    }

    private var touchInRange = false


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        centrePoint = PointF((width / 2).toFloat(),(height / 2).toFloat())
        getClockFace()
        getHoursHandCoordinates()
        getMinutesHandCoordinates()
        radius = sqrt((minuteHandPoint.x-centrePoint.x)*(minuteHandPoint.x-centrePoint.x) +(minuteHandPoint.y-centrePoint.y)*(minuteHandPoint.y-centrePoint.y))
    }

    private fun getClockFace() {
        val clockFace = BitmapFactory.decodeResource(resources,
            R.drawable.clock_face
        )
        val clockFaceWidth = (min(width, height)/1.3).toInt()
        val clockFaceHeight = (min(width, height)/1.3).toInt()
        scaledClockFace = Bitmap.createScaledBitmap(clockFace,clockFaceWidth,clockFaceHeight,false)
        scaledClockFaceSize = PointF((width/2-clockFaceWidth/2).toFloat(),(height/2-clockFaceHeight/2).toFloat())
    }

    private fun getHoursHandCoordinates() {
        var time = hours+minutes/60.0f
        if(time>12) time-=12
        val hourHandLength = min(width, height)*0.2f
        val a0 = (time*360/12)
        val a = a0* (PI / 180)
        hourHandPoint = PointF(centrePoint.x+hourHandLength*sin(a).toFloat(), centrePoint.y-hourHandLength*cos(a).toFloat())
    }

    private fun getMinutesHandCoordinates() {
        val minuteHandLength = min(width, height)*0.30f
        val a0 = (minutes*360/60.0f)
        val a = a0* (PI / 180)
        minuteHandPoint = PointF(centrePoint.x+minuteHandLength*sin(a).toFloat(),centrePoint.y-minuteHandLength*cos(a).toFloat())
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawBitmap(scaledClockFace,scaledClockFaceSize.x,scaledClockFaceSize.y,paint)
        canvas?.drawLine(centrePoint.x,centrePoint.y, hourHandPoint.x,hourHandPoint.y, hourHandPaint)
        canvas?.drawLine(centrePoint.x,centrePoint.y, minuteHandPoint.x,minuteHandPoint.y, minuteHandPaint)
        canvas?.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), 22.0f, centralCirclePaint)
        canvas?.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), 10.0f, innerCentralCirclePaint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if(event?.action == MotionEvent.ACTION_DOWN){
            event?.let{
                //current angle
                val xc = minuteHandPoint.x
                val yc = minuteHandPoint.y
                val radCurrent = atan2(yc-centrePoint.y,xc-centrePoint.x)
                var angleCurrent = radCurrent*180/ PI
                if(angleCurrent<0.0){
                    angleCurrent+=360.0
                }

                val angleMin = angleCurrent-rangeAngle
                val angleMax = angleCurrent+rangeAngle


                val x1=it.x.toDouble()
                val y1 = it.y.toDouble()

                val rad = atan2(y1-centrePoint.y,x1-centrePoint.x)
                var angle = rad*180/ PI
                if(angle<0.0){
                    angle+=360.0
                }
                if(angleMax>0&&angleMin<0&&angle>(360-2*rangeAngle)){
                    angle-=360
                }
                touchInRange = angle <angleMax && angle>angleMin
            }
        }
        if(event?.action == MotionEvent.ACTION_UP){
            // set clock
            touchInRange = false
            if(hours>11) hours-=12
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY,hours)
            calendar.set(Calendar.MINUTE,minutes)
            _time.value = calendar
        }
        if(event?.action == MotionEvent.ACTION_MOVE){
           if(touchInRange){

               val newPoint = PointF(event.x,event.y)

               val rad1 = atan2(minuteHandPoint.y-centrePoint.y,minuteHandPoint.x-centrePoint.x)
               val angle1 = rad1*180/ PI

               val rad2 = atan2(newPoint.y-centrePoint.y,newPoint.x-centrePoint.x)
               val angle2 = rad2*180/ PI

              var angleInDegrees = angle1-angle2

               if(angleInDegrees.absoluteValue>330 && angle2>0){
                   angleInDegrees = angle1-(angle2-360)
               }
               if(angleInDegrees.absoluteValue>300 && angle2<0){
                   angleInDegrees = angle1-(angle2+360)
               }

               if(angleInDegrees.absoluteValue>6.0){
                   tick(angleInDegrees)
               }
           }
        }
        return true
    }

    private fun tick(angleInDegrees: Double) {
        val angleInDegreesAbsolute = angleInDegrees.toInt().absoluteValue

        for(t in 6..angleInDegreesAbsolute step 6){
            if(angleInDegrees<0){
                val newTime = addMinute(hours,minutes)
                hours = newTime.get(Calendar.HOUR_OF_DAY)
                minutes = newTime.get(Calendar.MINUTE)
            }else{
                val newTime = subtractMinute(hours,minutes)
                hours = newTime.get(Calendar.HOUR_OF_DAY)
                minutes = newTime.get(Calendar.MINUTE)
            }
            getHoursHandCoordinates()
            getMinutesHandCoordinates()

            _tickSound.value = true
            _tickSound.value = false

            invalidate()
        }
    }

}