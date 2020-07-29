package com.alexaat.spinnyclock.viewmodels


import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*


class GameFragmentViewModel: ViewModel(){

    private lateinit var countDownTimer:CountDownTimer
    private var gameTime = 10L
    private var hoursNeeded = 0
    private var minutesNeeded = 0

    private var _hours = 0
        val hours:Int
        get() = _hours

    private var _minutes = 0
        val minutes:Int
            get() = _minutes

    private val _score = MutableLiveData(0)
    val score: LiveData<Int>
    get() = _score

    private val _playCoinSound = MutableLiveData(false)
    val playCoinSound: LiveData<Boolean>
        get() = _playCoinSound

    private val _time = MutableLiveData<String>("00:00")
    val time: LiveData<String>
        get() = _time

    private val _timer = MutableLiveData<String>("00:09")
    val timer: LiveData<String>
        get() = _timer

    private val _level =  MutableLiveData<Level>(Level.level_1)
    val level:LiveData<Level>
        get() = _level

    private val _navigateToGameOverEvent = MutableLiveData<Int>()
    val navigateToGameOverEvent: LiveData<Int>
        get() = _navigateToGameOverEvent


    init{
        setRandomTime()
    }

    private fun setRandomTime(){
        hoursNeeded = (0..11).random()
        minutesNeeded = (0..59).random()

        var hoursString = hoursNeeded.toString()
        if(hoursNeeded<10){
            hoursString = "0$hoursString"
        }
        var minutesString = minutesNeeded.toString()
        if(minutesNeeded<10){
            minutesString = "0$minutesString"
        }
        _time.value = "$hoursString:$minutesString"


    }

    private fun startTimer() {
       countDownTimer = object: CountDownTimer(gameTime*1000, 1000){
            override fun onFinish() {
                _navigateToGameOverEvent.value = score.value
                _navigateToGameOverEvent.value = null
            }
            override fun onTick(p0: Long) {
                gameTime-=1
                var currentTimeString = gameTime.toString()
                if(gameTime<10) currentTimeString = "0$gameTime"
                _timer.value = "00:$currentTimeString"
            }
        }
        countDownTimer.start()
    }

    fun setSelectedTime(calendar: Calendar){
        _hours = calendar.get(Calendar.HOUR_OF_DAY)
        _minutes = calendar.get(Calendar.MINUTE)

        if(hoursNeeded==_hours && minutesNeeded==_minutes){
             _score.value =  _score.value?.plus(1)
            _playCoinSound.value = true
            _playCoinSound.value = false
             setRandomTime()
             countDownTimer.cancel()
             gameTime = 10L
             startTimer()
            _level.value =
                when(_score.value){
                in 0..4  -> Level.level_1
                in 5..10 -> Level.level_2
                in 11..15 -> Level.level_3
                in 16..20 -> Level.level_4
                in 21..25 -> Level.level_5
                in 26..30 -> Level.level_6
                in 31..35 -> Level.level_7
                else -> Level.level_last
            }
        }
    }

    fun cancelTimer(){
        countDownTimer.cancel()
    }
    fun resumeTimer(){
       startTimer()
    }
}

enum class Level{
    level_1,
    level_2,
    level_3,
    level_4,
    level_5,
    level_6,
    level_7,
    level_last
}