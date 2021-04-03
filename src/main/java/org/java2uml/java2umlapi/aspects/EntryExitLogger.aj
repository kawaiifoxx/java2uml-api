package org.java2uml.java2umlapi.aspects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * <p>
 * This aspect is used for logging entry into a method and exit from the method. This can be used for debugging purposes.
 * </p>
 *
 * @author kawaiifox
 */
public aspect EntryExitLogger {
    private static int depth = 0;
    private static final Logger logger = LoggerFactory.getLogger(EntryExitLogger.class);

    public pointcut everyMethodInJava2umlapi():
            call(* org.java2uml..*+.*(..)) &&
                    !within(org.java2uml.java2umlapi.aspects);

    /*
     * Logs entry into a method.
     */
    before(): everyMethodInJava2umlapi() {
        if (logger.isTraceEnabled()) {
            var fileName = thisJoinPointStaticPart.getSourceLocation().getFileName();
            var line = thisJoinPointStaticPart.getSourceLocation().getLine();
            var signature = thisJoinPointStaticPart.getSignature().getDeclaringTypeName() + "."
                    + thisJoinPointStaticPart.getSignature().getName() + "(..)";

            Object[] loggingList = {signature, depth, fileName, line};
            logger.trace("Entering {} at depth {} at {}:{}", loggingList);
        }
        depth++;
    }

    /*
     * Logs exit after successful return.
     */
    after() returning(Object retValue): everyMethodInJava2umlapi() {
        if (logger.isTraceEnabled()) {
            var fileName = thisJoinPointStaticPart.getSourceLocation().getFileName();
            var line = thisJoinPointStaticPart.getSourceLocation().getLine();
            var signature = thisJoinPointStaticPart.getSignature().getDeclaringTypeName() + "."
                    + thisJoinPointStaticPart.getSignature().getName() + "(..)";

            Object[] loggingList = {signature, depth, fileName, line, retValue};
            logger.trace("Exiting {} at depth {} at {}:{}, returned {}", loggingList);
        }
        depth--;
    }

    /*
     * Logs exit after a exception is thrown.
     */
    after() throwing(Throwable throwable): everyMethodInJava2umlapi() {
        if (logger.isTraceEnabled()) {
            var fileName = thisJoinPointStaticPart.getSourceLocation().getFileName();
            var line = thisJoinPointStaticPart.getSourceLocation().getLine();
            var signature = thisJoinPointStaticPart.getSignature().getDeclaringTypeName() + "."
                    + thisJoinPointStaticPart.getSignature().getName() + "(..)";

            Object[] loggingList = {signature, depth, fileName, line};
            logger.trace("Exiting {} at depth {} at {}:{}, threw "
                            + throwable.getMessage() + "\n Stack Trace: \n"
                            + Arrays.toString(throwable.getStackTrace())
                    , loggingList);
        }
        depth--;
    }

}
