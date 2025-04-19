package com.gotenna.android.rsdksample.ui.radiomanagement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.gotenna.android.rsdksample.ui.radioactions.RadioActionsFragment
import com.gotenna.android.rsdksample.utils.Screen
import com.gotenna.android.rsdksample.utils.navigateTo
import kotlinx.coroutines.launch

class RadioManagementFragment : Fragment() {

    private val viewModel: RadioManagementViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = Screen {
        RadioManagementScreen(viewModel.toState())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Navigate to the radio actions screen
                viewModel.eventNavToActions.collect {
                    navigateTo(
                        fragment = RadioActionsFragment.newInstance(),
                        tag = RadioActionsFragment.NAV_TAG,
                    )
                }
            }
        }
    }

    companion object {
        const val NAV_TAG = "RadioManagementFragment"
        fun newInstance() = RadioManagementFragment()
    }
}