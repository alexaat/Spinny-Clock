package com.alexaat.spinnyclock.ui


import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.alexaat.spinnyclock.R
import com.alexaat.spinnyclock.databinding.FragmentGameBinding
import com.alexaat.spinnyclock.viewmodels.GameFragmentViewModel
import com.alexaat.spinnyclock.viewmodels.Level
import com.alexaat.spinnyclock.views.OnClockTickListener
import com.alexaat.spinnyclock.views.OnTimeChangedListener
import kotlinx.android.synthetic.main.fragment_game.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class GameFragment : Fragment() {

    var job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Default + job)

    private lateinit var viewModel:GameFragmentViewModel

    lateinit var player: MediaPlayer
    private var playerLocked = false

    private lateinit var onClockTickListener: OnClockTickListener
    private lateinit var onTimeChangedListener: OnTimeChangedListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val binding: FragmentGameBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_game, container, false)

        onClockTickListener = OnClockTickListener {
            if(!playerLocked){
                playTick()
            }
        }
        binding.clockView.setOnClockTickListener(onClockTickListener)

        onTimeChangedListener = OnTimeChangedListener{
            viewModel.setSelectedTime(it)
        }
        binding.clockView.setOnTimeChangedListener(onTimeChangedListener)

        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this).get(GameFragmentViewModel::class.java)

        viewModel.score.observe(viewLifecycleOwner, Observer {
            it?.let{score->
                binding.score.text = resources.getString(R.string.scoreFormat,score)
            }
        })

        viewModel.playCoinSound.observe(viewLifecycleOwner, Observer {
            if(it){
                playCoin()
            }
        })

        viewModel.timer.observe(viewLifecycleOwner, Observer {
            it?.let{timer->
                binding.timer.text = timer
            }
        })

        viewModel.time.observe(viewLifecycleOwner, Observer {
            it?.let{time->
                binding.time.text = time
            }
        })

        viewModel.navigateToGameOverEvent.observe(viewLifecycleOwner, Observer {
            it?.let {
                val action =
                    GameFragmentDirections.actionGameFragmentToGameOverFragment(it)
                findNavController().navigate(action)

            }
        })

        val h = viewModel.hours
        val m=viewModel.minutes
        if(h!=0 || m!=0){
            binding.clockView.setClockTime(h,m)
        }

        viewModel.level.observe(viewLifecycleOwner, Observer {
            it?.let{ level ->
                val color = when(level){
                    Level.level_1 -> R.color.colorRed
                    Level.level_2 -> R.color.colorOrange
                    Level.level_3 -> R.color.colorYellow
                    Level.level_4 -> R.color.colorGreen
                    Level.level_5 -> R.color.colorLightBlue
                    Level.level_6 -> R.color.colorBlue
                    Level.level_7 -> R.color.colorViolet
                    else -> R.color.colorBlack
                }
                clockView.hourHandColor = ContextCompat.getColor(requireContext(),color)
                clockView.minuteHandColor = ContextCompat.getColor(requireContext(),color)
                clockView.invalidate()
            }
        })

        return binding.root
    }

    private fun playCoin(){
        uiScope.launch {
            player = MediaPlayer.create(context,R.raw.coin)
            player.start()
            player.setOnCompletionListener {
                it.release()
            }
        }
    }

    private fun playTick(){
        playerLocked = true
        uiScope.launch {
            player = MediaPlayer.create(context,R.raw.tick)
            player.start()
            player.setOnCompletionListener {
                it.release()
                playerLocked = false
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.cancelTimer()
    }

    override fun onResume() {
        super.onResume()
        viewModel.resumeTimer()
    }
}