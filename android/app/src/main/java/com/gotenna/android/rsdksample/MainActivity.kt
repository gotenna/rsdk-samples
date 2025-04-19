package com.gotenna.android.rsdksample

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.gotenna.android.rsdksample.databinding.ActivityMainBinding
import com.gotenna.android.rsdksample.ui.radiomanagement.RadioManagementFragment
import com.gotenna.android.rsdksample.utils.Global
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupObservers()

        // Load initial Fragment
        supportFragmentManager.beginTransaction()
            .replace(binding.mainContainer.id, RadioManagementFragment.newInstance(), RadioManagementFragment.NAV_TAG)
            .commit()

        // Check permissions
        startActivity(Intent(this, PermissionActivity::class.java))
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Handles app-wide loading overlay
                Global.loadingState.collect { loading ->
                    binding.groupLoading.visibility = when {
                        loading -> View.VISIBLE
                        else -> View.GONE
                    }
                }
            }
        }
    }
}