package xyz.world.currency.rate.converter.utils.extensions

/**
 * Return String Of Given Double Number with Three Digit After Point
 * And round up third digit.
 *
 * I used double to have better control over offset rate calculation.
 */
fun Any.formatToThreeDigitAfterPoint()  = String.format("%.3f", this)