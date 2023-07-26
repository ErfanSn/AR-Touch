/*
 * Copyright (C) 2023 ErfanSn
 *
 * AR Touch is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * AR Touch is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AR Touch.  If not, see <https://www.gnu.org/licenses/>.
 */

package ir.erfansn.artouch.ui.touch

import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.GrantPermissionRule
import ir.erfansn.artouch.R
import ir.erfansn.artouch.di.FakeArTouchPeripheralDevice
import ir.erfansn.artouch.di.appModule
import ir.erfansn.artouch.di.testModule
import ir.erfansn.artouch.dispatcher.ble.peripheral.BleHidConnectionState
import ir.erfansn.artouch.ui.configuration.ConfigurationFragment
import kotlinx.coroutines.flow.update
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTestRule
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
class TouchFragmentTest {

    private val fakeArTouchPeripheralDevice = FakeArTouchPeripheralDevice()

    @get:Rule(order = 0)
    val koinTestRule = KoinTestRule.create {
        modules(appModule, testModule(fakeArTouchPeripheralDevice))
    }

    @get:Rule(order = 1)
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        ConfigurationFragment.CAMERA_PERMISSION,
        *ConfigurationFragment.BLUETOOTH_PERMISSIONS,
    )

    @Before
    fun launchFragment() {
        launchFragmentInContainer<TouchFragment>(
            fragmentArgs = bundleOf(TouchFragment.DEBUG_MODE_KEY to false),
            themeResId = R.style.Theme_ARTouch,
        )
    }

    @Test
    fun notifiesAboutDisconnected_whenBleHidConnectionStateIsDisconnected() {
        fakeArTouchPeripheralDevice.connectionState.update { BleHidConnectionState.Disconnected }

        onView(withId(R.id.connecting_indicator)).check(matches(not(isDisplayed())))
        onView(withId(R.id.user_message)).check(matches(withText(R.string.device_is_disconnected)))
        onView(withId(R.id.reconnect)).check(matches(isDisplayed()))
        onView(withId(R.id.utility_button))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.select_another_device)))
    }

    @Test
    fun notifiesAboutConnecting_whenBleHidConnectionStateIsConnecting() {
        fakeArTouchPeripheralDevice.connectionState.update { BleHidConnectionState.Connecting }

        onView(withId(R.id.connecting_indicator)).check(matches(isDisplayed()))
        onView(withId(R.id.user_message)).check(matches(withText(R.string.connecting)))
        onView(withId(R.id.reconnect)).check(matches(not(isDisplayed())))
        onView(withId(R.id.utility_button))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.cancel)))
    }

    @Test
    fun notifiesAboutErrorInConnecting_whenBleHidConnectionStateIsFailedToConnect() {
        fakeArTouchPeripheralDevice.connectionState.update { BleHidConnectionState.FailedToConnect }

        onView(withId(R.id.connecting_indicator)).check(matches(not(isDisplayed())))
        onView(withId(R.id.user_message)).check(matches(withText(R.string.error_when_connecting)))
        onView(withId(R.id.reconnect)).check(matches(not(isDisplayed())))
        onView(withId(R.id.utility_button))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.select_another_device)))
    }

    @Config(shadows = [ShadowProcessCameraProvider::class])
    @Test
    fun showsCameraPreview_whenBleHidConnectionStateIsConnected() {
        fakeArTouchPeripheralDevice.connectionState.update { BleHidConnectionState.Connected }

        onView(withId(R.id.connection_state)).check(matches(not(isDisplayed())))
    }
}
