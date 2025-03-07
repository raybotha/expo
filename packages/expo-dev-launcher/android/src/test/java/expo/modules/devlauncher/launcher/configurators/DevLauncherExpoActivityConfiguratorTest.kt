package expo.modules.devlauncher.launcher.configurators

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.launchActivity
import com.facebook.react.ReactActivity
import com.google.common.truth.Truth
import expo.modules.devlauncher.launcher.DevLauncherActivity
import expo.modules.devlauncher.launcher.manifest.DevLauncherManifest
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
internal class DevLauncherExpoActivityConfiguratorTest {

  private val context: Context = ApplicationProvider.getApplicationContext()

  @Config(sdk = [28])
  @Test
  fun `sets task description from manifest`() {
    val manifest = DevLauncherManifest.fromJson("{\"name\":\"test-app-name\",\"primaryColor\":\"#cccccc\",\"slug\":\"test-app-slug\",\"version\":\"1.0.0\",\"sdkVersion\":\"42.0.0\",\"bundleUrl\":\"https://d1wp6m56sqw74a.cloudfront.net/%40esamelson%2Fsdk42updates%2F1.0.0%2F67b67696b1cab67319035d39a7379786-42.0.0-ios.js\",\"hostUri\":\"exp.host/@esamelson/sdk42updates\"}".reader())
    val configurator = DevLauncherExpoActivityConfigurator(manifest, context)
    val mockActivity = mockk<Activity>(relaxed = true)
    val slot = slot<ActivityManager.TaskDescription>()

    configurator.applyTaskDescription(mockActivity)
    verify { mockActivity.setTaskDescription(capture(slot)) }
    confirmVerified(mockActivity) // no other calls were made

    Truth.assertThat(slot.captured.label).isEqualTo("test-app-name")
    Truth.assertThat(slot.captured.primaryColor).isEqualTo(Color.parseColor("#cccccc"))
  }

  @Test
  fun `does not set task description if manifest primaryColor is invalid`() {
    val manifest = DevLauncherManifest.fromJson("{\"name\":\"test-app-name\",\"primaryColor\":\"invalid\",\"slug\":\"test-app-slug\",\"version\":\"1.0.0\",\"sdkVersion\":\"42.0.0\",\"bundleUrl\":\"https://d1wp6m56sqw74a.cloudfront.net/%40esamelson%2Fsdk42updates%2F1.0.0%2F67b67696b1cab67319035d39a7379786-42.0.0-ios.js\",\"hostUri\":\"exp.host/@esamelson/sdk42updates\"}".reader())
    val configurator = DevLauncherExpoActivityConfigurator(manifest, context)
    val mockActivity = mockk<Activity>(relaxed = true)

    configurator.applyTaskDescription(mockActivity)
    confirmVerified(mockActivity) // no calls were made to mockActivity
  }

  @Test
  fun `sets orientation from manifest`() {
    verifyOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, "{\"orientation\":\"portrait\",\"name\":\"sdk42updates\",\"slug\":\"sdk42updates\",\"version\":\"1.0.0\",\"sdkVersion\":\"42.0.0\",\"bundleUrl\":\"https://d1wp6m56sqw74a.cloudfront.net/%40esamelson%2Fsdk42updates%2F1.0.0%2F67b67696b1cab67319035d39a7379786-42.0.0-ios.js\",\"hostUri\":\"exp.host/@esamelson/sdk42updates\"}")
    verifyOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, "{\"orientation\":\"landscape\",\"name\":\"sdk42updates\",\"slug\":\"sdk42updates\",\"version\":\"1.0.0\",\"sdkVersion\":\"42.0.0\",\"bundleUrl\":\"https://d1wp6m56sqw74a.cloudfront.net/%40esamelson%2Fsdk42updates%2F1.0.0%2F67b67696b1cab67319035d39a7379786-42.0.0-ios.js\",\"hostUri\":\"exp.host/@esamelson/sdk42updates\"}")
    verifyOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED, "{\"orientation\":\"default\",\"name\":\"sdk42updates\",\"slug\":\"sdk42updates\",\"version\":\"1.0.0\",\"sdkVersion\":\"42.0.0\",\"bundleUrl\":\"https://d1wp6m56sqw74a.cloudfront.net/%40esamelson%2Fsdk42updates%2F1.0.0%2F67b67696b1cab67319035d39a7379786-42.0.0-ios.js\",\"hostUri\":\"exp.host/@esamelson/sdk42updates\"}")
    // orientation key missing
    verifyOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED, "{\"name\":\"sdk42updates\",\"slug\":\"sdk42updates\",\"version\":\"1.0.0\",\"platforms\":[\"ios\",\"android\",\"web\"],\"sdkVersion\":\"42.0.0\",\"bundleUrl\":\"https://d1wp6m56sqw74a.cloudfront.net/%40esamelson%2Fsdk42updates%2F1.0.0%2F67b67696b1cab67319035d39a7379786-42.0.0-ios.js\",\"hostUri\":\"exp.host/@esamelson/sdk42updates\"}")
  }

  private fun verifyOrientation(expectedOrientation: Int, manifestString: String) {
    val manifest = DevLauncherManifest.fromJson(manifestString.reader())
    val configurator = DevLauncherExpoActivityConfigurator(manifest, context)
    val mockActivity = mockk<ReactActivity>(relaxed = true)

    configurator.applyOrientation(mockActivity)
    verify { mockActivity.requestedOrientation = expectedOrientation }
    confirmVerified(mockActivity) // no other calls were made
  }
}
