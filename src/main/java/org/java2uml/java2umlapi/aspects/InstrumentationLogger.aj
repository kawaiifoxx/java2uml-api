package org.java2uml.java2umlapi.aspects;

import org.java2uml.java2umlapi.parsedComponent.SourceComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;

/**
 * <p>
 * This aspect is used for instrumentation purposes, it logs various instrumentation details
 * such as time taken by a particular method.
 * </p>
 *
 * @author kawaiifox
 */
public aspect InstrumentationLogger {

    Logger logger = LoggerFactory.getLogger(InstrumentationLogger.class);

    /*
     * pointcut expression for capturing all the calls to Parser.parse with its args.
     */
    public pointcut ParserParse(Path path):
            call(* org.java2uml.java2umlapi.parser.Parser.parse(java.nio.file.Path)) && args(path);

    /*
     * pointcut expression for capturing all the calls to Unzipper.unzipDir with its args.
     */
    public pointcut unzipDir(Path srcZipPath, Path destDirPath):
            call(* org.java2uml.java2umlapi.util.unzipper.Unzipper.unzipDir(Path, Path))
            && args(srcZipPath, destDirPath);

    /*
     * Advice to perform instrumentation for Parser.parse
     * !!!WARNING!!! Please do not touch this unless absolutely necessary, as changing code in this can break,
     * core functionality of application. !!!WARNING!!!
     */
    SourceComponent around(Path path): ParserParse(path) {
        long startTime = System.currentTimeMillis();
        var retVal = proceed(path);
        double totalTime = (System.currentTimeMillis() - startTime) / 1000d;
        if (logger.isInfoEnabled()) {

            logger.info("{}() took {} secs to parse source code at " + path,
                    thisJoinPoint.getSignature().getDeclaringTypeName() + "." +
                            thisJoinPoint.getSignature().getName(), totalTime);
        }

        return retVal;
    }


    /*
     * Advice to perform instrumentation for Unzipper.unzipDir
     * !!!WARNING!!! Please do not touch this unless absolutely necessary, as changing code in this can break,
     * core functionality of application. !!!WARNING!!!
     */
    File around(Path srcZipPath, Path destDirPath): unzipDir(srcZipPath,destDirPath) {
        long startTime = System.currentTimeMillis();
        var retVal = proceed(srcZipPath, destDirPath);
        double totalTime = (System.currentTimeMillis() - startTime)/1000d;

        if (logger.isInfoEnabled()) {
            Object[] loggingParams = {thisJoinPoint.getSignature().getDeclaringTypeName(),
                    thisJoinPoint.getSignature().getName(), totalTime};
            logger.info("{}.{}() took {} secs to unzip source code.", loggingParams);
        }

        return retVal;
    }
}
