/*
 * The SmartQuark Project Java Edition
 * Copyright (c) Alessio Saltarin, 2026
 * This software is licensed under ISC License
 * See LICENSE
 */

package net.littlelite.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TitleLogger
{
    private static final int WIDTH = 65;
    private static final TitleLogger INSTANCE = new TitleLogger();

    public static TitleLogger getInstance()
    {
        return INSTANCE;
    }

    public void logTitle(Logger logger,
                         List<String> lines)
    {
        logger.info("┌{}┐", "─".repeat(WIDTH));
        var output = new java.util.ArrayList<String>();
        lines.forEach(line -> output.add(this.centerText(line)));
        output.forEach(logger::info);
        logger.info("└{}┘", "─".repeat(WIDTH));
    }

    private String centerText(String text)
    {
        int pad = (WIDTH - text.length()) / 2;
        return " ".repeat(pad) + text + " ".repeat(WIDTH - text.length() - pad) + "  ";
    }
}
