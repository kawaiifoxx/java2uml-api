package org.java2uml.java2umlapi.parsedComponent;

import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import org.java2uml.java2umlapi.visitors.Visitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * <p>
 * Leaf Component, representing method declaration, in a parsed java src code.
 * </p>
 *
 * @author kawaiifox
 */
public class ParsedMethodComponent implements ParsedComponent {

    private final ParsedComponent parent;
    private final ResolvedMethodDeclaration resolvedDeclaration;
    private final String qualifiedName;
    private final String returnType;
    private final Logger logger = LoggerFactory.getLogger(ParsedMethodComponent.class);

    /**
     * Initializes ParsedMethodComponent.
     *
     * @param parent              Parent of this component.
     * @param resolvedDeclaration resolvedMethodDeclaration is type solved method declaration
     *                            retrieved from resolvedReferenceTypeDeclaration.
     */
    public ParsedMethodComponent(ParsedComponent parent, ResolvedMethodDeclaration resolvedDeclaration) {
        this.parent = parent;
        this.resolvedDeclaration = resolvedDeclaration;
        this.qualifiedName = getQualifiedSignature();
        this.returnType = getReturnType();
    }

    private String getQualifiedSignature() {
        String signature;
        try {
            signature = resolvedDeclaration.getQualifiedSignature();
        } catch (UnsolvedSymbolException e) {
            var sigBuilder = new StringBuilder();
            sigBuilder.append(resolvedDeclaration.declaringType().getName())
                    .append('.')
                    .append(resolvedDeclaration.getName())
                    .append('(');

            for (int i = 0; i < resolvedDeclaration.getNumberOfParams(); i++) {
                if (i != 0) {
                    sigBuilder.append(", ");
                }
                sigBuilder.append(getTypeName(i));
            }
            return sigBuilder.append(')').toString();
        }

        return signature;
    }

    private String getTypeName(int i) {
        String typeName;
        try {
            typeName = resolvedDeclaration.getParam(i).getType().describe();
        } catch (UnsolvedSymbolException exception) {
            return exception.getName();
        }

        return typeName;
    }

    /**
     * Gets the correct return type from resolvedDeclaration and then returns it in string form.
     *
     * @return returns string of return type.
     */
    private String getReturnType() {
        ResolvedType resolvedType;
        try {
            resolvedType = resolvedDeclaration.getReturnType();
        } catch (UnsolvedSymbolException e) {
            logger.info("Unable to resolve returnType, it is possible that dependencies are not included with the source code.");
            logger.info("Assigning return type {}", e.getName());
            return e.getName();
        }

        if (resolvedType.isVoid()) {
            return "void";
        }

        if (resolvedType.isReferenceType()) {
            var qualifiedReturnType = resolvedType.asReferenceType().getQualifiedName().split("\\.");
            return qualifiedReturnType[qualifiedReturnType.length - 1];
        }

        return resolvedType.describe();
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    @Override
    public boolean isParsedMethodComponent() {
        return true;
    }

    @Override
    public Optional<ParsedMethodComponent> asParsedMethodComponent() {
        return Optional.of(this);
    }

    /**
     * This returns the Optional<ResolvedMethodDeclaration>.
     *
     * <p>
     * <p>Why I did not just override the getResolvedDeclaration() method, overridden by all parsedComponents?</p>
     * <code>
     * public Optional<ResolvedDeclaration> getResolvedDeclaration() {
     * return Optional.of(resolvedDeclaration);
     * }
     * </code>
     * <p>
     * This is useless because of javaParser library, as it doesn't override method named asResolvedMethodDeclaration()
     * <p>
     * please see https://github.com/javaparser/javaparser/blob/master/javaparser-core/src/main/java/com/github/javaparser/resolution/declarations/ResolvedDeclaration.java
     * Line No. 116.
     * <p>
     */
    public Optional<ResolvedMethodDeclaration> getAsResolvedMethodDeclaration() {
        return Optional.of(resolvedDeclaration);
    }

    public ResolvedMethodDeclaration getResolvedMethodDeclaration() {
        return resolvedDeclaration;
    }

    @Override
    public Optional<ParsedComponent> getParent() {
        return Optional.of(parent);
    }

    @Override
    public String getName() {
        return qualifiedName;
    }

    /**
     * @return returns the return type of ParsedMethodComponent.
     */
    public String getReturnTypeName() {
        return returnType;
    }

    /**
     * Accepts a visitor and returns whatever is returned by the visitor.
     *
     * @param v v is the Visitor
     * @return data extracted by visitor.
     */
    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

    @Override
    public String toString() {
        return "ParsedMethodComponent{" +
                "qualifiedName='" + qualifiedName + '\'' +
                '}';
    }
}
