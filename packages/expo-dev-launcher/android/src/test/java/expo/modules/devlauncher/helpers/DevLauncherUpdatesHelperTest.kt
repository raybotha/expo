package expo.modules.devlauncher.helpers

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.facebook.react.ReactNativeHost
import com.google.common.truth.Truth
import expo.modules.devlauncher.helpers.loadUpdate
import expo.modules.devlauncher.koin.DevLauncherKoinContext
import expo.modules.devlauncher.launcher.DevLauncherControllerInterface
import expo.modules.devlauncher.launcher.manifest.DevLauncherManifest
import expo.modules.devlauncher.launcher.manifest.DevLauncherManifestParser
import expo.modules.updatesinterface.UpdatesInterface
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.robolectric.RobolectricTestRunner
import java.lang.Exception

@RunWith(RobolectricTestRunner::class)
internal class DevLauncherUpdatesHelperTest {

  private val context: Context = ApplicationProvider.getApplicationContext()
  private val configuration = createUpdatesConfigurationWithUrl(Uri.parse("https://exp.host/@esamelson/sdk42updates"))
  private val mockManifest = JSONObject("{\"icon\":\"./assets/icon.png\",\"name\":\"sdk42updates\",\"slug\":\"sdk42updates\",\"splash\":{\"image\":\"./assets/splash.png\",\"imageUrl\":\"https://d1wp6m56sqw74a.cloudfront.net/~assets/201a91bd1740bb1d6a1dbad049310724\",\"resizeMode\":\"contain\",\"backgroundColor\":\"#ffffff\"},\"iconUrl\":\"https://d1wp6m56sqw74a.cloudfront.net/~assets/4e3f888fc8475f69fd5fa32f1ad5216a\",\"version\":\"1.0.0\",\"sdkVersion\":\"42.0.0\",\"orientation\":\"portrait\",\"currentFullName\":\"@esamelson/sdk42updates\",\"originalFullName\":\"@esamelson/sdk42updates\",\"id\":\"@esamelson/sdk42updates\",\"projectId\":\"04e1e9f2-b297-44da-a11c-90a6a27859bc\",\"scopeKey\":\"@esamelson/sdk42updates\",\"releaseId\":\"a2b0a544-40f0-4fd6-9972-19f094380681\",\"publishedTime\":\"2021-07-13T22:29:24.170Z\",\"commitTime\":\"2021-07-13T22:29:24.209Z\",\"bundleUrl\":\"https://d1wp6m56sqw74a.cloudfront.net/%40esamelson%2Fsdk42updates%2F1.0.0%2F8c3e605420e77ba9fe35a0a7ef8459a9-42.0.0-ios.js\",\"bundleKey\":\"8c3e605420e77ba9fe35a0a7ef8459a9\",\"releaseChannel\":\"default\",\"hostUri\":\"exp.host/@esamelson/sdk42updates\"}")
  private val mockUpdate = object : UpdatesInterface.Update {
    override fun getManifest(): JSONObject = mockManifest
    override fun getLaunchAssetPath(): String = ""
  }

  @Test
  fun `passes true shouldContinue value through to UpdatesInterface`() = runBlocking<Unit> {
    val updatesInterface = mockUpdatesInterface {
      Truth.assertThat(it).isTrue()
    }
    val loadedUpdate = updatesInterface.loadUpdate(configuration, context) { true }
    Truth.assertThat(loadedUpdate).isEqualTo(mockUpdate)
  }

  @Test
  fun `passes false shouldContinue value through to UpdatesInterface`() = runBlocking<Unit> {
    val updatesInterface = mockUpdatesInterface {
      Truth.assertThat(it).isFalse()
    }
    updatesInterface.loadUpdate(configuration, context) { false }
  }

  private fun mockUpdatesInterface(verifyOnManifestLoadedCallback: (Boolean) -> Unit): UpdatesInterface {
    val updatesInterface = mockk<UpdatesInterface>()
    val slot = slot<UpdatesInterface.UpdateCallback>()
    every {
      updatesInterface.fetchUpdateWithConfiguration(any(), any(), capture(slot))
    } answers {
      val callback = slot.captured
      val onManifestLoadedCallbackValue = callback.onManifestLoaded(mockManifest)
      verifyOnManifestLoadedCallback(onManifestLoadedCallbackValue)

      // mock the behavior of the UpdatesDevLauncherController
      if (onManifestLoadedCallbackValue) {
        callback.onSuccess(mockUpdate)
      } else {
        callback.onSuccess(null)
      }
    }
    return updatesInterface
  }
}
