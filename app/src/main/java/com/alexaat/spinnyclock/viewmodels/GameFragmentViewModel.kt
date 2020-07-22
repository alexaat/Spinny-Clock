package com.alexaat.spinnyclock.viewmodels


import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*


class GameFragmentViewModel: ViewModel(){

    private lateinit var countDownTimer:CountDownTimer
    private var gameTime = 10
    private var hoursNeeded = 0
    private var minutesNeeded = 0

    private val _score = MutableLiveData(0)
    val score: LiveData<Int>
    get() = _score

    private val _time = MutableLiveData<String>("00:00")
    val time: LiveData<String>
        get() = _time

    private val _timer = MutableLiveData<String>("00:09")
    val timer: LiveData<String>
        get() = _timer


    private val _navigateToGameOverEvent = MutableLiveData<Int>()
    val navigateToGameOverEvent: LiveData<Int>
        get() = _navigateToGameOverEvent


    init{
        setRandomTime()
        startTimer()
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
        gameTime = 10
        countDownTimer = object: CountDownTimer(10000, 1000){
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

        val hours = calendar.get(Calendar.HOUR_OF_DAY)
        val minutes = calendar.get(Calendar.MINUTE)

        if(hoursNeeded==hours && minutesNeeded==minutes){
             _score.value =  _score.value?.plus(1)
             setRandomTime()
             countDownTimer.cancel()
             startTimer()
        }
    }

    fun cancelTimer(){
        countDownTimer.cancel()
    }

}