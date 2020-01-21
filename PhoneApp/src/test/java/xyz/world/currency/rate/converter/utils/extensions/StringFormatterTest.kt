package xyz.world.currency.rate.converter.utils.extensions

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(sdk = [Build.VERSION_CODES.O_MR1])
@RunWith(AndroidJUnit4::class)
class StringFormatterTest {

    @Test
    fun formatToThreeDigitAfterPoint_TestResult() {
        val testTextNumber = 13.987654321

        println(testTextNumber.formatToThreeDigitAfterPoint())

        Assert.assertEquals(testTextNumber.formatToThreeDigitAfterPoint(), "13.988")
    }
}