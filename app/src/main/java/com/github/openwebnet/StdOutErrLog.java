package com.github.openwebnet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;

/*
 * @see http://stackoverflow.com/a/1370033/4197187
 */
public class StdOutErrLog {

    private static final Logger logger = LoggerFactory.getLogger(StdOutErrLog.class);

    public static void tieSystemOutAndErrToLog() {
        System.setOut(createLoggingProxy(System.out));
        System.setErr(createLoggingProxy(System.err));
    }

    private static PrintStream createLoggingProxy(final PrintStream realPrintStream) {
        return new PrintStream(realPrintStream) {
            public void print(final String string) {
                realPrintStream.print(string);
                logger.info(string);
            }
        };
    }
}