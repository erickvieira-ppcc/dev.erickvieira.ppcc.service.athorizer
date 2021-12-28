package dev.erickvieira.ppcc.service.banking.domain.extension

import java.math.BigDecimal

fun BigDecimal.invert() = this * BigDecimal(-1)

fun BigDecimal.modulus() = if (this < BigDecimal.ZERO) this.invert() else this

fun BigDecimal.isBetween(start: BigDecimal, end: BigDecimal) = this > start && this < end