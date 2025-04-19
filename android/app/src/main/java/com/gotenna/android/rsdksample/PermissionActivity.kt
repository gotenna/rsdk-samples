package com.gotenna.android.rsdksample

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlin.system.exitProcess

/**
 * Responsible for ensuring that all necessary permissions are requested
 */
class PermissionActivity : AppCompatActivity() {

    private val permissionList = arrayOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.BLUETOOTH,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            onRequestPermissionResult(it)
        }

    override fun onStart() {
        super.onStart()
        requestPermissions()
    }

    private fun hasPermission(permission: String): Boolean =
        checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED

    private fun hasRequiredPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            hasPermission(Manifest.permission.BLUETOOTH_SCAN) &&
                    hasPermission(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun isPermissionPermanentlyDenied(permission: String): Boolean {
        return !shouldShowRequestPermissionRationale(permission) &&
                checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED
    }

    private fun isRequiredPermissionPermanentlyDenied(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            isPermissionPermanentlyDenied(Manifest.permission.BLUETOOTH_SCAN) &&
                    isPermissionPermanentlyDenied(Manifest.permission.BLUETOOTH_CONNECT)
        } else {
            isPermissionPermanentlyDenied(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun requestPermissions() {
        if (hasRequiredPermissions()) {
            finish()
            return
        }

        requestPermissionsLauncher.launch(permissionList)
    }

    private fun onRequestPermissionResult(results: Map<String, Boolean>) {
        if (isRequiredPermissionPermanentlyDenied()) {
            Log.w("SampleApplication","Required permissions is permanently denied")
            AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage(R.string.permission_denied_permanently)
                .setPositiveButton(R.string.open_settings) { _, _ ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", packageName, null)
                    }
                    startActivity(intent)
                }
                .setNegativeButton(R.string.exit_app) { dialog, _ ->
                    dialog.dismiss()
                    finishAffinity()
                    exitProcess(0)
                }
                .show()
        }
        else if (!hasRequiredPermissions()) {
            Log.w("SampleApplication","Required permissions not granted")
            AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage(R.string.permission_denied)
                .setPositiveButton(R.string.exit_app) { dialog, _ ->
                    dialog.dismiss()
                    finishAffinity()
                    exitProcess(0)
                }
                .show()
        }
        else {
            finish()
        }
    }
}