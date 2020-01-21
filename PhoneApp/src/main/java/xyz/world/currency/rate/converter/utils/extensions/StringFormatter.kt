package xyz.world.currency.rate.converter.utils.extensions

/**
 * Return String Of Given Double Number with Three Digit After Point
 * And round up third digit
 */
fun Any.formatToThreeDigitAfterPoint()  = String.format("%.3f", this)