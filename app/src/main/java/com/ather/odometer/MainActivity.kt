package com.ather.odometer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.ather.odometer.odometer.presentation.OdometerScreen
import com.ather.odometer.odometer.presentation.OdometerViewModel
import com.ather.odometer.odometer.utils.DASHBOARD_APP_ACTIVITY
import com.ather.odometer.odometer.utils.DASHBOARD_APP_PACKAGE
import com.ather.odometer.ui.theme.OdometerTheme
import dagger.hilt.android.AndroidEntryPoint

private const val REQUEST_OVERLAY_PERMISSION = 123

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var viewModel: OdometerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        viewModel = ViewModelProvider(this)[OdometerViewModel::class.java]

        setContent {
            OdometerTheme {
                OdometerScreen(
                    openDashboardApp = {
                        try {
                            launchDashboardApp()
                            if (Settings.canDrawOverlays(this)) {
                                showFloatingOverlay(this, viewModel)
                            } else {
                                val intent = Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:$packageName")
                                )
                                startActivityForResult(
                                    intent,
                                    REQUEST_OVERLAY_PERMISSION
                                )
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this, "Could not find the Dashboard", Toast.LENGTH_SHORT)
                                .show()
                        }
                    },
                    viewModel = viewModel
                )
            }
        }
    }

    private fun launchDashboardApp() {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            component = ComponentName(DASHBOARD_APP_PACKAGE, DASHBOARD_APP_ACTIVITY)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_OVERLAY_PERMISSION && Settings.canDrawOverlays(this)) {
            showFloatingOverlay(this, viewModel)
        }
    }

    private fun showFloatingOverlay(
        context: Context,
        viewModel: OdometerViewModel
    ) {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val composeView = ComposeView(context).apply {
            setViewTreeLifecycleOwner(this@MainActivity)
            setViewTreeViewModelStoreOwner(this@MainActivity)
            setViewTreeSavedStateRegistryOwner(this@MainActivity)
            setContent {
                Surface(modifier = Modifier.size(300.dp, 200.dp)) {
                    OdometerScreen(viewModel.uiState.collectAsState().value, {})
                }
            }
        }

        val params = WindowManager.LayoutParams(
            500, 300,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 300
        }

        windowManager.addView(composeView, params)
    }
}
