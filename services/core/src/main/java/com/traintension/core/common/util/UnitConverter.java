package com.traintension.core.common.util;

import com.traintension.core.generated.enums.UnitSystem;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class UnitConverter {

    private static final BigDecimal INCH_TO_CM = new BigDecimal("2.54");
    private static final BigDecimal LBS_TO_KG = new BigDecimal("0.453592");

    public static BigDecimal convert(BigDecimal value, BigDecimal factor, boolean multiply) {
        return multiply
                ? value.multiply(factor).setScale(2, RoundingMode.HALF_UP)
                : value.divide(factor, 2, RoundingMode.HALF_UP);
    }

    // imperial → metric (DB'ye yazarken)
    public static BigDecimal inchToCm(BigDecimal inch) {
        return convert(inch, INCH_TO_CM, true);
    }

    public static BigDecimal lbsToKg(BigDecimal lbs) {
        return convert(lbs, LBS_TO_KG, true);
    }

    // metric → imperial (response'a çevirirken)
    public static BigDecimal cmToInch(BigDecimal cm) {
        return convert(cm, INCH_TO_CM, false);
    }

    public static BigDecimal kgToLbs(BigDecimal kg) {
        return convert(kg, LBS_TO_KG, false);
    }

    public static BigDecimal toStoredHeight(BigDecimal height, UnitSystem unit) {
        return unit == UnitSystem.IMPERIAL ? inchToCm(height) : height;
    }

    public static BigDecimal toStoredWeight(BigDecimal weight, UnitSystem unit) {
        return unit == UnitSystem.IMPERIAL ? lbsToKg(weight) : weight;
    }

    public static BigDecimal toResponseHeight(BigDecimal heightCm, UnitSystem unit) {
        return unit == UnitSystem.IMPERIAL ? cmToInch(heightCm) : heightCm;
    }

    public static BigDecimal toResponseWeight(BigDecimal weightKg, UnitSystem unit) {
        return unit == UnitSystem.IMPERIAL ? kgToLbs(weightKg) : weightKg;
    }
}
