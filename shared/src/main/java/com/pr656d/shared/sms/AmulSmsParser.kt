package com.pr656d.shared.sms

import com.pr656d.model.Milk
import com.pr656d.shared.utils.TimeUtils
import org.threeten.bp.ZonedDateTime

/**
 * Sms parser for BGAMAMCS sender AKA Amul to convert it into [Milk].
 *
 *  BGAMAMCS sample message :
 *      Code-(264) 1047
 *      Date-16/01/2020 06:41
 *      Shift-M
 *      Milk-C
 *      Qty-36.5
 *      Fat-3.8
 *      Amt-1057.09
 *      TQty-391.7
 *      TAmt-11282.60
 *      https://goo.gl/UY1HAC
 */
object AmulSmsParser : SmsParser {

    override val SENDER_ID = "BGAMAMCS"

    override fun getMilkingData(message: String): Milk {
        // Divide message by lines.
        val parts = message.lines()

        val (dairyCode, customerId) = getDairyCodeAndCustomerId(parts[0])
        val timeStamp = getZonedDateTime(parts[1])
        val shift = getShift(parts[2])
        val milkOf = getMilkOf(parts[3])
        val qty = getQuantity(parts[4])
        val fat = getFat(parts[5])
        val amt = getAmount(parts[6])
        val tQty = getTotalQuantity(parts[7])
        val tAmt = getTotalAmount(parts[8])
        val link = getLink(parts[9])

        return Milk(
            dairyCode, customerId, timeStamp, shift,
            milkOf, qty, fat, amt, tQty, tAmt, link
        )
    }

    /**
     * Parses `Code-(DAIRY_CODE) CUSTOMER_ID` and returns pair of dairyCode and customerId.
     * Make it open for testing.
     */
    fun getDairyCodeAndCustomerId(str: String): Pair<Int, Int> {
        return CODE_REGEX.find(str)!!
            .destructured
            .let { (dairyCode, customerId) ->
                dairyCode.toInt() to customerId.toInt()
            }
    }

    /**
     * Parses `Date-dd/mm/yyyy hour:minute` and returns epoch millis of it.
     * Make it open for testing.
     */
    fun getZonedDateTime(str: String): ZonedDateTime {
        return DATE_TIME_REGEX.find(str)!!
            .destructured
            .let { (dd, mm, yyyy, hour, minute) ->
                TimeUtils.toZonedDateTime(
                    dd.toInt(), mm.toInt(), yyyy.toInt(),
                    hour.toInt(), minute.toInt()
                )
            }
    }

    /**
     * Parses `Shift-M or E` returns char.
     * Make it open for testing.
     */
    fun getShift(str: String): Char {
        return SHIFT_REGEX.find(str)!!
            .destructured
            .let { (value) -> value.toCharArray().first() }
    }

    /**
     * Parses `Milk-C or B` returns char.
     * Make it open for testing.
     */
    fun getMilkOf(str: String): Char {
        return MILK_REGEX.find(str)!!
            .destructured
            .let { (value) -> value.toCharArray().first() }
    }

    /**
     * Parses `Qty-123.12` returns float value.
     * Make it open for testing.
     */
    fun getQuantity(str: String): Float {
        return QTY_REGEX.find(str)!!
            .destructured
            .let { (value) -> value.toFloat() }
    }

    /**
     * Parses `Fat-3.4` returns float value.
     * Make it open for testing.
     */
    fun getFat(str: String): Float {
        return FAT_REGEX.find(str)!!
            .destructured
            .let { (value) -> value.toFloat() }
    }

    /**
     * Parses `Amt-1250.23` returns float.
     * Make it open for testing.
     */
    fun getAmount(str: String): Float {
        return AMT_REGEX.find(str)!!
            .destructured
            .let { (value) -> value.toFloat() }
    }

    /**
     * Parses `TQty-42.32` returns float.
     * Make it open for testing.
     */
    fun getTotalQuantity(str: String): Float {
        return TQTY_REGEX.find(str)!!
            .destructured
            .let { (value) -> value.toFloat() }
    }

    /**
     * Parses `TAmt-2311.12` returns float.
     * Make it open for testing.
     */
    fun getTotalAmount(str: String): Float {
        return TAMT_REGEX.find(str)!!
            .destructured
            .let { (value) -> value.toFloat() }
    }

    /**
     * Parses
     * Make it open for testing.
     */
    fun getLink(str: String): String {
        return LINK_REGEX.find(str)!!
            .destructured
            .let { (value) -> value }
    }

    private val CODE_REGEX = "Code-\\((\\d+)\\) (\\d+)".toRegex()
    private val DATE_TIME_REGEX = "Date-(\\d{1,2})/(\\d{1,2})/(\\d{4}) (\\d{1,2}):(\\d{1,2})".toRegex()
    private val SHIFT_REGEX = "Shift-([ME])".toRegex()
    private val MILK_REGEX = "Milk-(\\S)".toRegex()
    private val QTY_REGEX = "Qty-(\\d+(\\.\\d{1,2})?)".toRegex()
    private val FAT_REGEX = "Fat-(\\d+(\\.\\d{1,2})?)".toRegex()
    private val AMT_REGEX = "Amt-(\\d+(\\.\\d{1,2})?)".toRegex()
    private val TQTY_REGEX = "TQty-(\\d+(\\.\\d{1,2})?)".toRegex()
    private val TAMT_REGEX = "TAmt-(\\d+(\\.\\d{1,2})?)".toRegex()
    private val LINK_REGEX = "(\\S+)".toRegex()
}