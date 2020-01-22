package xyz.world.currency.rate.converter

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.hamcrest.Matchers
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class RecyclerViewInstrumentedTest {

    @get:Rule
    var activityRule: ActivityTestRule<EntryConfigurations> = ActivityTestRule(EntryConfigurations::class.java)

    @Before
    fun launch_MainActivity() {
        activityRule.activity
    }

    @Test
    fun scrollOnRecyclerViewItem_ItemView() {
        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        uiDevice.wait(Until.hasObject(By.scrollable(true)), 5000)
        uiDevice.performActionAndWait({
            Espresso.onView(ViewMatchers.withId(R.id.loadView))
                .inRoot(RootMatchers.withDecorView(Matchers.`is`(activityRule.activity.window.decorView)))
                .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(20))
        }, Until.scrollFinished(Direction.DOWN), 1000)
    }

    @Test
    fun clickOnRecyclerViewItem_ItemView() {
        val uiDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        uiDevice.wait(Until.hasObject(By.scrollable(true)), 5000)
        uiDevice.performActionAndWait({
            Espresso.onView(ViewMatchers.withId(R.id.loadView))
                .inRoot(RootMatchers.withDecorView(Matchers.`is`(activityRule.activity.window.decorView)))
                .perform(
                    RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(5,
                        ViewActions.click()
                    ))
        }, Until.scrollFinished(Direction.DOWN), 2000)
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("xyz.world.currency.rate.converter", appContext.packageName)
    }
}
