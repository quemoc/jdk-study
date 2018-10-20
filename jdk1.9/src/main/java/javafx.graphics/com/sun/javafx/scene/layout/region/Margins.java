/*
 * Copyright (c) 2011, 2015, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package com.sun.javafx.scene.layout.region;

import javafx.scene.text.Font;

import javafx.css.Size;
import javafx.css.SizeUnits;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import sun.util.logging.PlatformLogger;
import sun.util.logging.PlatformLogger.Level;

/**
 * Similar to Insets but with flag denoting values are proportional.
 * If proportional is true, then the values represent fractions or percentages
 * and are in the range 0..1, although this is not enforced.
 */
public class Margins {

    // lazy, thread-safe instantiation
    private static class Holder {
        static Converter CONVERTER_INSTANCE = new Converter();
        static SequenceConverter SEQUENCE_CONVERTER_INSTANCE = new SequenceConverter();
    }

    final double top;
    public final double getTop() { return top; }

    final double right;
    public final double getRight() { return right; }

    final double bottom;
    public final double getBottom() { return bottom; }

    final double left;
    public final double getLeft() { return left; }

    final boolean proportional;
    public final boolean isProportional() { return proportional; }

    public Margins(double top, double right, double bottom, double left, boolean proportional) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
        this.proportional = proportional;
    }

    @Override
    public String toString() {
        return "top: "+top+"\nright: "+right+"\nbottom: "+bottom+"\nleft: "+left;
    }

    /**
     * Convert a sequence of sizes to an Margins
     */
    public static final class Converter extends StyleConverter<ParsedValue[], Margins> {

        public static Converter getInstance() {
            return Holder.CONVERTER_INSTANCE;
        }

        private Converter() {
            super();
        }

        @Override
        public Margins convert(ParsedValue<ParsedValue[], Margins> value, Font font) {
            ParsedValue<?, Size>[] sides = value.getValue();
            Size topSz = (sides.length > 0) ? sides[0].convert(font) : new Size(0.0F, SizeUnits.PX);
            Size rightSz = (sides.length > 1) ? sides[1].convert(font) : topSz;
            Size bottomSz = (sides.length > 2) ? sides[2].convert(font) : topSz;
            Size leftSz = (sides.length > 3) ? sides[3].convert(font) : rightSz;

            // assume proportional if any units are percent
            boolean proportional =
                    (topSz.getUnits() == SizeUnits.PERCENT)    ||
                    (rightSz.getUnits() == SizeUnits.PERCENT)  ||
                    (bottomSz.getUnits() == SizeUnits.PERCENT) ||
                    (leftSz.getUnits() == SizeUnits.PERCENT);

            // if any of the units is percent, then make sure they all are.
            boolean unitsMatch =
                !proportional ||
                ((topSz.getUnits() == SizeUnits.PERCENT)    &&
                 (rightSz.getUnits() == SizeUnits.PERCENT)  &&
                 (bottomSz.getUnits() == SizeUnits.PERCENT) &&
                 (leftSz.getUnits() == SizeUnits.PERCENT));

            // unitsMatch will only be false if proportional is true and
            // not all of the units are percent.
            if (unitsMatch == false) {
                   final PlatformLogger LOGGER = com.sun.javafx.util.Logging.getCSSLogger();
                    if (LOGGER.isLoggable(Level.WARNING)) {
                        final String msg =
                            new StringBuilder("units do no match: ")
                                .append(topSz.toString())
                                .append(" ,").append(rightSz.toString())
                                .append(" ,").append(bottomSz.toString())
                                .append(" ,").append(leftSz.toString())
                            .toString();
                        LOGGER.warning(msg);
                    }
            }

            proportional = proportional && unitsMatch;

            double top = topSz.pixels(font);
            double right = rightSz.pixels(font);
            double bottom = bottomSz.pixels(font);
            double left = leftSz.pixels(font);
            return new Margins(top, right, bottom, left, proportional);
        }

        @Override
        public String toString() {
            return "MarginsConverter";
        }
    }

    /**
     * Convert a sequence of sizes to an Insets
     */
    public static final class SequenceConverter extends StyleConverter<ParsedValue<ParsedValue[], Margins>[], Margins[]> {

        public static SequenceConverter getInstance() {
            return Holder.SEQUENCE_CONVERTER_INSTANCE;
        }

        private SequenceConverter() {
            super();
        }

        @Override
        public Margins[] convert(ParsedValue<ParsedValue<ParsedValue[], Margins>[], Margins[]> value, Font font) {
            ParsedValue<ParsedValue[], Margins>[] layers = value.getValue();
            Margins[] margins = new Margins[layers.length];
            for (int layer = 0; layer < layers.length; layer++) {
                margins[layer] = Converter.getInstance().convert(layers[layer], font);
            }
            return margins;
        }

        @Override
        public String toString() {
            return "MarginsSequenceConverter";
        }
    }

}
