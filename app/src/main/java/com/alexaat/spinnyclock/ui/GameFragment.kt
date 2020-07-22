package com.alexaat.spinnyclock.ui


import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.alexaat.spinnyclock.R
import com.alexaat.spinnyclock.databinding.FragmentGameBinding
import com.alexaat.spinnyclock.viewmodels.GameFragmentViewModel
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


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val binding: FragmentGameBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_game, container, false)

        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this).get(GameFragmentViewModel::class.java)

        viewModel.score.observe(viewLifecycleOwner, Observer {
            it?.let{score->
                binding.score.text = this.resources.getString(R.string.scoreFormat, score)
                if(score>0){
                    playCoin()
                }
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

        binding.clockView.time.observe(viewLifecycleOwner, Observer {
            it?.let { calendar ->
                viewModel.setSelectedTime(calendar)

            }
        })

        binding.clockView.tickSound.observe(viewLifecycleOwner, Observer {
            it?.let{beep->
                if(beep && !playerLocked){
                        playTick()
                }
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
        viewModel.cancelTimer()
        super.onPause()
    }
}