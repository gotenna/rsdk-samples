package com.gotenna.android.rsdksample.ui.radioactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.gotenna.android.rsdksample.utils.Screen

class RadioActionsFragment : Fragment() {

    private val viewModel: RadioActionsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = Screen {
        RadioActionsScreen(viewModel.toState())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Disconnect radio when navigating back
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.disconnect()
                    parentFragmentManager.popBackStack()
                }
            }
        )
    }

    companion object {
        const val NAV_TAG = "RadioActionsFragment"
        fun newInstance() = RadioActionsFragment()
    }
}