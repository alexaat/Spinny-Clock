package com.alexaat.spinnyclock.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.alexaat.spinnyclock.R
import kotlinx.android.synthetic.main.fragment_title.*

class TitleFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_title, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startButton.setOnClickListener {
            val action =
                TitleFragmentDirections.actionTitleFragmentToGameFragment()
            it.findNavController().navigate(action)
        }
    }
}