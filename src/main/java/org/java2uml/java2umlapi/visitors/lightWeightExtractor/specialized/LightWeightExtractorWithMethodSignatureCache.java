package org.java2uml.java2umlapi.visitors.lightWeightExtractor.specialized;

import org.java2uml.java2umlapi.lightWeight.LightWeight;
import org.java2uml.java2umlapi.lightWeight.repository.MethodRepository;
import org.java2uml.java2umlapi.parsedComponent.ParsedMethodComponent;
import org.java2uml.java2umlapi.visitors.lightWeightExtractor.LightWeightExtractor;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * This is a specialized lightWeight extractor which stores a mapping of method Signature to its id.
 * <p/>
 *
 * @author kawaiifox
 */

public class LightWeightExtractorWithMethodSignatureCache extends LightWeightExtractor {
    private final Map<String, Long> signatureToIdMap;
    private final MethodRepository repository;

    public LightWeightExtractorWithMethodSignatureCache(MethodRepository repository) {
        this.repository = repository;
        this.signatureToIdMap = new HashMap<>();
    }

    /**
     * Visits passed component and extracts lightWeight from it.
     *
     * @param parsedMethodComponent parsedMethodComponent representing Method.
     * @return Method which has been persisted in the database.
     */
    @Override
    public LightWeight visit(ParsedMethodComponent parsedMethodComponent) {
        var method = repository.save(super.visit(parsedMethodComponent).asMethod()
                .orElseThrow(
                        () -> new RuntimeException("extracting light weight from parsed method" +
                                " component should generate Method."
                        )
                ));

        signatureToIdMap.put(method.getSignature(), method.getId());
        return method;
    }

    /**
     * @return a map which contains mapping between method signature and id.
     * Empty map is returned if this method is called without passing this visitor to source component.
     */
    public Map<String, Long> getSignatureToIdMap() {
        return signatureToIdMap;
    }
}
