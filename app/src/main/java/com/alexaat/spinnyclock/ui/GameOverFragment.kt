package com.alexaat.spinnyclock.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.alexaat.spinnyclock.R
import com.alexaat.spinnyclock.databinding.FragmentGameOverBinding

class GameOverFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val binding:FragmentGameOverBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_game_over, container, false)
        val score  = GameOverFragmentArgs.fromBundle(
            requireArguments()
        ).score
        binding.yourScoreText.text = resources.getString(R.string.yourScoreFormat, score)

        binding.playAgainButton.setOnClickListener {
            val action =
                GameOverFragmentDirections.actionGameOverFragmentToGameFragment()
            findNavController().navigate(action)
        }

        return binding.root
    }


}