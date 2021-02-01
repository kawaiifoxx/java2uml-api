package org.java2uml.java2umlapi.umlComponenets;

import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;

/**
 * <p>
 * This Component interface declares common methods for ParsedSourceComponent (which is a composite component),
 * ParsedClassComponent (which is a composite component), ParsedMethodComponent (which is a simple component)
 * and ParsedFieldComponent (which is a simple component).
 * </p>
 * <p>
 * For more information on composite design pattern, go to below link:<br>
 * https://refactoring.guru/design-patterns/composite
 * </p>
 *
 * @author kawaiifox
 */
public interface ParsedComponent {
    /**
     * returns the wrapped resolved reference type declaration.
     *
     * @return returns wrapped ResolvedReferenceTypeDeclaration.
     */
    ResolvedReferenceTypeDeclaration getResolvedReferenceTypeDeclaration();

}
