package com.eatngo.common.util

import java.math.BigDecimal
import java.math.RoundingMode

fun BigDecimal.normalizeFloor(unit: BigDecimal): BigDecimal =
    this.divide(unit, 0, RoundingMode.FLOOR).multiply(unit).setScale(unit.scale(), RoundingMode.HALF_UP)

fun BigDecimal.normalizeCeil(unit: BigDecimal): BigDecimal =
    this.divide(unit, 0, RoundingMode.CEILING).multiply(unit).setScale(unit.scale(), RoundingMode.HALF_UP)
