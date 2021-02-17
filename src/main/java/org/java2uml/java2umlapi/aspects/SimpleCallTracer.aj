package org.java2uml.java2umlapi.aspects;

import org.aspectj.lang.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * <p>
 *     This aspect is used for call tracing in org.java2uml.* package. All of the traced calls are logged using org.slf4j.Logger in TRACE level.
 *     can be used for debugging purposes.
 *      <br><br>
 *     FORMAT:<br>
 *          => Call from [TypeName] line [line number] <br>
 *          => On [target.toString()] <br>
 *          => With args [args.toString()] <br>
 *          => to [MethodSignature] <br>
 * </p>
 *
 * @author kawaiifox
 */
public aspect SimpleCallTracer {
    public static final Logger logger = LoggerFactory.getLogger(SimpleCallTracer.class);

    /*
     * Pointcut expression which identifies joinPoints in java2uml code.
     * ".." is multipart wildcard.
     * "*" is wild card.
     */
    public pointcut simpleTracing():
            /*
             * captures all the method calls which can be public, private or protected and can have any return type
             * and Are present in org.java2uml.* package and are not within org.java2uml.java2umlapi.aspects.SimpleCallTracer
             */
            call(* org.java2uml..*(..)) && !within(org.java2uml.java2umlapi.aspects.SimpleCallTracer);

    /*
     * Before advice that does the actual logging using logger, before method is executed.
     * Flow of control.
     *      |
     *      |
     *      -> before() runs. (all logging happens here.)
     *      joinPoint -> method() gets executed.
     *      |
     *      |
     */
    before(): simpleTracing() {
        Signature signature = thisJoinPointStaticPart.getSignature();
        //line number where method was called.
        var line = thisJoinPointStaticPart.getSourceLocation().getLine();
        //class name of calling method.
        var className = thisJoinPointStaticPart
                .getSourceLocation()
                .getWithinType()
                .getCanonicalName();

        //instance on which method was called.
        var target = thisJoinPoint.getTarget();

        //input parameters of the called method.
        var args = Arrays.asList(thisJoinPoint.getArgs());

        logger.trace("\n => Call From " + className + " line " + line
                + "\n => On " + target
                + "\n => With Args " + args
                + "\n => to " + signature.getDeclaringTypeName() + "." + signature.getName());
    }
}
