package org.example.project

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Config
import com.google.ar.core.Session
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException
import org.example.project.helper.CameraPermissionHelper

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    var mSession: Session? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            App(viewModel)
        }
    }

    // requestInstall(Activity, true) will triggers installation of
    // Google Play Services for AR if necessary.
    var mUserRequestedInstall = true

    override fun onResume() {
        super.onResume()

        // Check camera permission.
        // ARCore requires camera permission to operate.
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            CameraPermissionHelper.requestCameraPermission(this)
            return
        }

        // Ensure that Google Play Services for AR and ARCore device profile data are
        // installed and up to date.
        try {
            if (mSession == null) {
                when (ArCoreApk.getInstance().requestInstall(this, mUserRequestedInstall)) {
                    ArCoreApk.InstallStatus.INSTALLED -> {
                        // Success: Safe to create the AR session.
                        mSession = Session(this)
                    }

                    ArCoreApk.InstallStatus.INSTALL_REQUESTED -> {
                        // When this method returns `INSTALL_REQUESTED`:
                        // 1. ARCore pauses this activity.
                        // 2. ARCore prompts the user to install or update Google Play
                        //    Services for AR (market://details?id=com.google.ar.core).
                        // 3. ARCore downloads the latest device profile data.
                        // 4. ARCore resumes this activity. The next invocation of
                        //    requestInstall() will either return `INSTALLED` or throw an
                        //    exception if the installation or update did not succeed.
                        mUserRequestedInstall = false
                        return
                    }
                }
            }
        } catch (e: UnavailableUserDeclinedInstallationException) {
            // Display an appropriate message to the user and return gracefully.
            Toast.makeText(this, "TODO: handle exception " + e, Toast.LENGTH_LONG)
                .show()
            return
        } catch (e: Exception) {
            e.printStackTrace()
            return  // mSession remains null, since session creation has failed.
        }

        checkARSupport()
        checkDepthApi()
    }

    fun checkARSupport() {
        ArCoreApk.getInstance().checkAvailabilityAsync(this) { availability ->
            viewModel.updateArStatus(availability.isSupported)
        }
    }

    fun checkDepthApi() {
        val config: Config = mSession!!.getConfig()

        // Check whether the user's device supports the Depth API.
        val isDepthApiSupported: Boolean = mSession!!.isDepthModeSupported(Config.DepthMode.AUTOMATIC)
        if (isDepthApiSupported) {
            config.setDepthMode(Config.DepthMode.AUTOMATIC)
            viewModel.updateDepthStatus(true)

        }
        mSession!!.configure(config)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        results: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, results)
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            // Use toast instead of snackbar here since the activity will exit.
            Toast.makeText(
                this,
                "Camera permission is needed to run this application",
                Toast.LENGTH_LONG
            )
                .show()
            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                CameraPermissionHelper.launchPermissionSettings(this)
            }
            finish()
        }
    }

    override fun onDestroy() {
        mSession?.close()
        super.onDestroy()
    }
}


@Preview
@Composable
fun AppAndroidPreview() {
    App(viewModel = MainViewModel())
}