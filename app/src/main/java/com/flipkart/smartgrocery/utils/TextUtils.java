package com.flipkart.smartgrocery.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class TextUtils {

    public static final String RS = "\u20B9%s";
    public static final String RS_NEGATIVE = "-\u20B9%s";

    private static String getRupeeFormat(boolean negative) {
        if (negative) {
            return RS_NEGATIVE;
        }
        return RS;
    }

    public static String getRupeeText(Double value) {
        return getRupeeText(value, true);
    }

    public static String getRupeeText(Double value, boolean commaSeparated) {
        if (value == null) {
            return null;
        }
        boolean isNegative = false;
        if (value < 0d) {
            isNegative = true;
            value = Math.abs(value);
        }
        if (commaSeparated)
            return String.format(getRupeeFormat(isNegative), getCommaSeparatedValue(value));
        return String.format(getRupeeFormat(isNegative), Double.toString(value));
    }

    public static String getRupeeText(Long value) {
        return getRupeeText(value, true);
    }

    public static String getRupeeText(Long value, boolean commaSeparated) {
        if (value == null) {
            return null;
        }
        boolean isNegative = false;
        if (value < 0l) {
            isNegative = true;
            value = Math.abs(value);
        }
        if (commaSeparated)
            return String.format(getRupeeFormat(isNegative), getCommaSeparatedValue(value));
        return String.format(getRupeeFormat(isNegative), Long.toString(value));
    }

    public static String getRupeeText(Integer value) {
        return getRupeeText(value, true);
    }

    public static String getRupeeText(Integer value, boolean commaSeparated) {
        if (value == null) {
            return null;
        }
        boolean isNegative = false;
        if (value < 0) {
            isNegative = true;
            value = Math.abs(value);
        }
        if (commaSeparated)
            return String.format(getRupeeFormat(isNegative), getCommaSeparatedValue(value));
        return String.format(getRupeeFormat(isNegative), Integer.toString(value));
    }

    public static String getRupeeText(String value) {
        return getRupeeText(value, true);
    }

    public static String getRupeeText(String value, boolean commaSeparated) {
        if (value == null) {
            return null;
        }
        if (commaSeparated)
            return String.format(RS, getCommaSeparatedValue(value));
        return String.format(RS, value);
    }

    public static String getCommaSeparatedValue(Double value) {
        if (value == null)
            return null;
        try {
            DecimalFormat formatter = new DecimalFormat();
            formatter.setMaximumFractionDigits(2);
            formatter.setDecimalSeparatorAlwaysShown(false);
            formatter.applyPattern("#,##,##0.##");
            return formatter.format(value);
        } catch (Exception e) {
        }
        //if everything fails just return the number
        return Double.toString(value);
    }

    public static String getCommaSeparatedValue(Long value) {
        if (value == null)
            return null;
        try {
            return NumberFormat.getIntegerInstance().format(value);
        } catch (Exception e) {
            try {
                DecimalFormat formatter = new DecimalFormat();
                formatter.applyPattern("#,##,##0");
                return formatter.format(value);
            } catch (Exception e1) {
            }
        }
        //if everything fails just return the number
        return Long.toString(value);
    }

    // this works just the way expected
    public static String getCommaSeparatedValue(Integer value) {
        if (value == null)
            return null;
        try {
            return NumberFormat.getIntegerInstance().format(value);
        } catch (Exception e) {
            try {
                DecimalFormat formatter = new DecimalFormat();
                formatter.applyPattern("#,##,##0");
                return formatter.format(value);
            } catch (Exception e1) {
            }
        }
        //if everything fails just return the number
        return Integer.toString(value);
    }

    public static String getCommaSeparatedValue(String value) {
        if (value == null)
            return null;
        try {
            Double doubleValue = Double.parseDouble(value);
            try {
                DecimalFormat formatter = new DecimalFormat();
                formatter.setMaximumFractionDigits(2);
                formatter.applyPattern("#,##,##0.##");
                return formatter.format(doubleValue);
            } catch (Exception e1) {
            }
        } catch (NumberFormatException e) {
        }
        //if everything fails just return the number
        return value;
    }
}
