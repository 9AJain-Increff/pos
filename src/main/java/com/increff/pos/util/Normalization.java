package com.increff.pos.util;

import java.util.Formatter;

public class Normalization {

    public static String normalize(String input) {
        return input.trim().toLowerCase();
    }

    public static Float normalize(Float input) {
        Formatter formatter = new Formatter();
        formatter.format("%.2f", input);
        return input;
    }

}
