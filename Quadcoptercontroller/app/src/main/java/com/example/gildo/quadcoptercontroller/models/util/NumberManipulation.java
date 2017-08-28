package com.example.gildo.quadcoptercontroller.models.util;

import java.math.RoundingMode;
import java.security.InvalidParameterException;
import java.text.DecimalFormat;

/**
 * Created by gildo on 26/09/16.
 */
public abstract class NumberManipulation {
    public static float unitBaseNormalize(float value, float maxValue, float minValue) {
        //Reference: https://en.wikipedia.org/wiki/Normalization_(statistics)
        float percentage;
        percentage = (value - minValue) / (maxValue - minValue);

        return percentage;
    }

    public static float roundFloatToNDecimalsPoints(float number, int decimalsQuantity) {
        DecimalFormat decimalFormat;
        StringBuilder stringBuilder;
        float roundedNumber;

        if (decimalsQuantity >= 1) {
            stringBuilder = new StringBuilder("#.");
            for (int iterator = 0; iterator < decimalsQuantity; iterator++) {
                stringBuilder.append("#");
            }

            decimalFormat = new DecimalFormat(stringBuilder.toString());
            decimalFormat.setRoundingMode(RoundingMode.HALF_UP);

            roundedNumber = Float.parseFloat(decimalFormat.format(number));
        } else {
            roundedNumber = number;
        }

        return roundedNumber;
    }

    public static float removeCompleteSpinsDegrees(float degrees) {
        float reducedArc = degrees % 360.0f;

        return reducedArc;
    }

    public static float transformNegativeAngleToPositiveDegrees(float degrees) {
        if (degrees < 0.0f) {
            //transforms the negative angle to positive
            degrees = degrees + 360.0f;
        }

        return degrees;
    }

    public static boolean isAlmostEqual(float numberA, float numberB, float thresholdPercentage) {
        boolean almostEqual = false;
        float acceptableDifference;
        float numberApositiveNeighboor;
        float numberAnegativeNeighboor;

        if (thresholdPercentage > 0 && thresholdPercentage < 1) {
            acceptableDifference = numberA * thresholdPercentage;
            numberApositiveNeighboor = numberA + acceptableDifference;
            numberAnegativeNeighboor = numberA - acceptableDifference;

            if (((numberB > numberA) && (numberB < numberApositiveNeighboor))
                    || ((numberB < numberA) && (numberB > numberAnegativeNeighboor))) {
                almostEqual = true;
            }

            return almostEqual;
        } else {
            throw new InvalidParameterException("The variable thresholdPercentage needs to be ]0,1[.");
        }
    }

    public static int roundToNearestMultiplier(double numberToBeRounded, int multiplier) {
        return (int) Math.round(numberToBeRounded / multiplier) * multiplier;
    }
}
