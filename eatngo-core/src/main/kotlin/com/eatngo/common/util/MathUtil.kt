package com.eatngo.common.util

import java.math.BigDecimal
import java.math.RoundingMode

fun BigDecimal.customNormalizeFloor(unit: BigDecimal): BigDecimal =
    if (this.remainder(unit).compareTo(BigDecimal.ZERO) == 0) {
        this
    } else {
        this.divide(unit, 0, RoundingMode.FLOOR).multiply(unit)
    }

fun BigDecimal.customNormalizeCeil(unit: BigDecimal): BigDecimal =
    if (this.remainder(unit).compareTo(BigDecimal.ZERO) == 0) {
        this
    } else {
        this.divide(unit, 0, RoundingMode.CEILING).multiply(unit)
    }
