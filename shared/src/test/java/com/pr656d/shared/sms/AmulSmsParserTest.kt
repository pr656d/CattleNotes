package com.pr656d.shared.sms

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.pr656d.model.Milk
import com.pr656d.shared.utils.TimeUtils
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.hamcrest.Matchers.equalTo as isEqualTo

/**
 * Unit tests for [AmulSmsParser].
 */
class AmulSmsParserTest {

    // Executes tasks in the Architecture Components in the same thread
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    val message = """
            |Code-(268) 1047
            |Date-16/01/2020 06:41
            |Shift-M
            |Milk-C
            |Qty-36.5
            |Fat-3.8
            |Amt-1057.09
            |TQty-391.7
            |TAmt-11282.60
            |https://goo.gl/UY1HAC""".trimMargin()

    @Test
    fun getCodeAndCustomerIdTest() {
        val actualDairyCode = 123
        val actualCustomerId = 456

        val str = "Code-($actualDairyCode) $actualCustomerId"

        val (dairyCode, customerId) = AmulSmsParser.getDairyCodeAndCustomerId(str)

        assertThat(actualDairyCode, isEqualTo(dairyCode))
        assertThat(actualCustomerId, isEqualTo(customerId))
    }

    @Test
    fun getZonedDateTimeTest() {
        val actualDateTime = TimeUtils.toZonedDateTime(
            16, 1, 2020, 6, 41
        )

        val str = "Date-16/01/2020 06:41"

        assertThat(actualDateTime, isEqualTo(AmulSmsParser.getZonedDateTime(str)))
    }

    @Test
    fun getShiftTest() {
        val shiftM = 'M'    // Valid
        val shiftE = 'E'    // Valid

        val strM = "Shift-M"
        assertThat(shiftM, isEqualTo(AmulSmsParser.getShift(strM)))

        val strE = "Shift-E"
        assertThat(shiftE, isEqualTo(AmulSmsParser.getShift(strE)))
    }

    @Test
    fun getMilkOfTest() {
        val str = "Milk-C"
        assertThat('C', isEqualTo(AmulSmsParser.getMilkOf(str)))
    }

    @Test
    fun getQuantityTest() {
        val str = "Qty-45.21"
        assertThat(45.21f, isEqualTo(AmulSmsParser.getQuantity(str)))
    }

    @Test
    fun getFatTest() {
        val str = "Fat-3.4"
        assertThat(3.4f, isEqualTo(AmulSmsParser.getFat(str)))
    }

    @Test
    fun getAmountTest() {
        val str = "Amt-1057.09"
        assertThat(1057.09f, isEqualTo(AmulSmsParser.getAmount(str)))
    }

    @Test
    fun getTotalQuantityTest() {
        val str = "TQty-391.7"
        assertThat(391.7f, isEqualTo(AmulSmsParser.getTotalQuantity(str)))
    }

    @Test
    fun getTotalAmountTest() {
        val str = "TAmt-11282.60"
        assertThat(11282.60f, isEqualTo(AmulSmsParser.getTotalAmount(str)))
    }

    @Test
    fun getLinkTest() {
        val str = "https://goo.gl/UY1HAC"
        assertThat("https://goo.gl/UY1HAC", isEqualTo(AmulSmsParser.getLink(str)))
    }

    @Test
    fun getMilkingDataTest() {
        val data = Milk(
            268, 1047,
            TimeUtils.toZonedDateTime(16, 1, 2020, 6, 41),
            'M', 'C', 36.5f, 3.8f, 1057.09f,
            391.7f, 11282.60f, "https://goo.gl/UY1HAC"
        )
        assertThat(data, isEqualTo(AmulSmsParser.getMilkingData(message)))
    }
}