package org.vidyaastra.neo4j.protege.core;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.util.*;

/**
 * Service for exporting OWL ontology entities to Neo4j graph database.
 * Converts OWL classes, individuals, and properties to Neo4j nodes and relationships.
 */
public class OwlExportService {
    
    private final Neo4jService neo4jService;
    
    public OwlExportService(Neo4jService neo4jService) {
        this.neo4jService = neo4jService;
    }
    
    /**
     * Exports the entire ontology to Neo4j.
     * Creates nodes for classes and individuals, and relationships for object/data properties.
     * 
     * @param ontology The OWL ontology to export
     * @param reasoner Optional reasoner for inferred relationships
     * @return Export summary with statistics
     * @throws Exception if export fails
     */
    public ExportSummary exportOntology(OWLOntology ontology, OWLReasoner reasoner) throws Exception {
        System.out.println("\n=== Starting OWL to Neo4j Export ===");
        
        ExportSummary summary = new ExportSummary();
        
        // 1. Export OWL Classes as nodes
        System.out.println("Exporting OWL Classes...");
        for (OWLClass owlClass : ontology.getClassesInSignature()) {
            if (!owlClass.isOWLThing() && !owlClass.isOWLNothing()) {
                exportClass(owlClass, ontology);
                summary.classesExported++;
            }
        }
        
        // 2. Export OWL Individuals as nodes
        System.out.println("Exporting OWL Individuals...");
        for (OWLNamedIndividual individual : ontology.getIndividualsInSignature()) {
            exportIndividual(individual, ontology);
            summary.individualsExported++;
        }
        
        // 3. Export Object Properties as relationships
        System.out.println("Exporting Object Properties...");
        for (OWLObjectProperty objectProperty : ontology.getObjectPropertiesInSignature()) {
            exportObjectPropertyRelationships(objectProperty, ontology);
            summary.objectPropertiesExported++;
        }
        
        // 4. Export subclass relationships
        System.out.println("Exporting Class Hierarchy...");
        for (OWLClass owlClass : ontology.getClassesInSignature()) {
            if (!owlClass.isOWLThing() && !owlClass.isOWLNothing()) {
                exportSubclassRelationships(owlClass, ontology);
            }
        }
        
        // 5. Export class assertions (individual types)
        System.out.println("Exporting Class Assertions...");
        for (OWLNamedIndividual individual : ontology.getIndividualsInSignature()) {
            exportClassAssertions(individual, ontology);
        }
        
        System.out.println("=== OWL to Neo4j Export Completed ===\n");
        System.out.println(summary.toString());
        
        return summary;
    }
    
    /**
     * Exports a single OWL Class as a Neo4j node.
     */
    private void exportClass(OWLClass owlClass, OWLOntology ontology) throws Exception {
        String className = getLocalName(owlClass.getIRI());
        String iri = owlClass.getIRI().toString();
        
        // Get annotations (labels, comments, etc.)
        Map<String, Object> properties = new HashMap<>();
        properties.put("iri", iri);
        properties.put("name", className);
        properties.put("type", "OWLClass");
        
        // Add rdfs:label if available
        for (OWLAnnotationAssertionAxiom axiom : ontology.getAnnotationAssertionAxioms(owlClass.getIRI())) {
            if (axiom.getProperty().isLabel()) {
                if (axiom.getValue() instanceof OWLLiteral) {
                    OWLLiteral literal = (OWLLiteral) axiom.getValue();
                    properties.put("label", literal.getLiteral());
                }
            } else if (axiom.getProperty().isComment()) {
                if (axiom.getValue() instanceof OWLLiteral) {
                    OWLLiteral literal = (OWLLiteral) axiom.getValue();
                    properties.put("comment", literal.getLiteral());
                }
            }
        }
        
        // Create or merge the class node
        String cypher = "MERGE (c:OWLClass {iri: $iri}) " +
                       "SET c.name = $name, c.type = $type";
        
        if (properties.containsKey("label")) {
            cypher += ", c.label = $label";
        }
        if (properties.containsKey("comment")) {
            cypher += ", c.comment = $comment";
        }
        
        neo4jService.executeWriteQuery(cypher, properties);
    }
    
    /**
     * Exports an OWL Individual as a Neo4j node.
     */
    private void exportIndividual(OWLNamedIndividual individual, OWLOntology ontology) throws Exception {
        String individualName = getLocalName(individual.getIRI());
        String iri = individual.getIRI().toString();
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("iri", iri);
        properties.put("name", individualName);
        properties.put("type", "OWLIndividual");
        
        // Add annotations
        for (OWLAnnotationAssertionAxiom axiom : ontology.getAnnotationAssertionAxioms(individual.getIRI())) {
            if (axiom.getProperty().isLabel()) {
                if (axiom.getValue() instanceof OWLLiteral) {
                    OWLLiteral literal = (OWLLiteral) axiom.getValue();
                    properties.put("label", literal.getLiteral());
                }
            } else if (axiom.getProperty().isComment()) {
                if (axiom.getValue() instanceof OWLLiteral) {
                    OWLLiteral literal = (OWLLiteral) axiom.getValue();
                    properties.put("comment", literal.getLiteral());
                }
            }
        }
        
        // Get data property values
        for (OWLDataProperty dataProperty : ontology.getDataPropertiesInSignature()) {
            ontology.getDataPropertyAssertionAxioms(individual).stream()
                .filter(axiom -> axiom.getProperty().equals(dataProperty))
                .forEach(axiom -> {
                    String propName = getLocalName(dataProperty.getIRI());
                    OWLLiteral literal = axiom.getObject();
                    properties.put(propName, literal.getLiteral());
                });
        }
        
        // Build Cypher query dynamically based on properties
        StringBuilder cypher = new StringBuilder("MERGE (i:OWLIndividual {iri: $iri}) SET i.name = $name, i.type = $type");
        
        for (String key : properties.keySet()) {
            if (!key.equals("iri") && !key.equals("name") && !key.equals("type")) {
                cypher.append(", i.").append(sanitizePropertyName(key)).append(" = $").append(key);
            }
        }
        
        neo4jService.executeWriteQuery(cypher.toString(), properties);
    }
    
    /**
     * Exports object property relationships between individuals.
     */
    private void exportObjectPropertyRelationships(OWLObjectProperty objectProperty, OWLOntology ontology) 
            throws Exception {
        
        String propertyName = getLocalName(objectProperty.getIRI());
        String relationshipType = sanitizeRelationshipName(propertyName);
        
        // Get all object property assertions
        for (OWLObjectPropertyAssertionAxiom axiom : ontology.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION)) {
            if (!axiom.getProperty().equals(objectProperty)) {
                continue;
            }
            if (axiom.getSubject() instanceof OWLNamedIndividual && 
                axiom.getObject() instanceof OWLNamedIndividual) {
                
                OWLNamedIndividual subject = (OWLNamedIndividual) axiom.getSubject();
                OWLNamedIndividual object = (OWLNamedIndividual) axiom.getObject();
                
                Map<String, Object> params = new HashMap<>();
                params.put("subjectIri", subject.getIRI().toString());
                params.put("objectIri", object.getIRI().toString());
                params.put("propertyName", propertyName);
                
                String cypher = String.format(
                    "MATCH (subject:OWLIndividual {iri: $subjectIri}), " +
                    "(object:OWLIndividual {iri: $objectIri}) " +
                    "MERGE (subject)-[r:%s]->(object) " +
                    "SET r.propertyName = $propertyName",
                    relationshipType
                );
                
                neo4jService.executeWriteQuery(cypher, params);
            }
        }
    }
    
    /**
     * Exports subclass relationships (rdfs:subClassOf).
     */
    private void exportSubclassRelationships(OWLClass owlClass, OWLOntology ontology) throws Exception {
        if (owlClass == null) return;
        
        for (OWLSubClassOfAxiom axiom : ontology.getSubClassAxiomsForSubClass(owlClass)) {
            OWLClassExpression superClass = axiom.getSuperClass();
            
            if (!superClass.isAnonymous()) {
                OWLClass namedSuperClass = superClass.asOWLClass();
                
                if (!namedSuperClass.isOWLThing()) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("subclassIri", owlClass.getIRI().toString());
                    params.put("superclassIri", namedSuperClass.getIRI().toString());
                    
                    String cypher = 
                        "MATCH (sub:OWLClass {iri: $subclassIri}), " +
                        "(sup:OWLClass {iri: $superclassIri}) " +
                        "MERGE (sub)-[:SUBCLASS_OF]->(sup)";
                    
                    neo4jService.executeWriteQuery(cypher, params);
                }
            }
        }
    }
    
    /**
     * Exports class assertions (rdf:type relationships).
     */
    private void exportClassAssertions(OWLNamedIndividual individual, OWLOntology ontology) throws Exception {
        if (individual == null) return;
        
        for (OWLClassAssertionAxiom axiom : ontology.getClassAssertionAxioms(individual)) {
            OWLClassExpression classExpression = axiom.getClassExpression();
            
            if (!classExpression.isAnonymous()) {
                OWLClass owlClass = classExpression.asOWLClass();
                
                if (!owlClass.isOWLThing()) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("individualIri", individual.getIRI().toString());
                    params.put("classIri", owlClass.getIRI().toString());
                    
                    String cypher = 
                        "MATCH (ind:OWLIndividual {iri: $individualIri}), " +
                        "(cls:OWLClass {iri: $classIri}) " +
                        "MERGE (ind)-[:INSTANCE_OF]->(cls)";
                    
                    neo4jService.executeWriteQuery(cypher, params);
                }
            }
        }
    }
    
    /**
     * Extracts the local name from an IRI.
     */
    private String getLocalName(IRI iri) {
        String iriString = iri.toString();
        int hashIndex = iriString.lastIndexOf('#');
        int slashIndex = iriString.lastIndexOf('/');
        
        int splitIndex = Math.max(hashIndex, slashIndex);
        if (splitIndex >= 0 && splitIndex < iriString.length() - 1) {
            return iriString.substring(splitIndex + 1);
        }
        
        return iriString;
    }
    
    /**
     * Sanitizes property names for Neo4j (removes special characters).
     */
    private String sanitizePropertyName(String name) {
        return name.replaceAll("[^a-zA-Z0-9_]", "_");
    }
    
    /**
     * Sanitizes relationship type names for Neo4j (uppercase, underscores).
     */
    private String sanitizeRelationshipName(String name) {
        return name.replaceAll("[^a-zA-Z0-9]", "_").toUpperCase();
    }
    
    /**
     * Export summary statistics.
     */
    public static class ExportSummary {
        public int classesExported = 0;
        public int individualsExported = 0;
        public int objectPropertiesExported = 0;
        public int dataPropertiesExported = 0;
        
        @Override
        public String toString() {
            return String.format(
                "Export Summary:\n" +
                "  Classes exported: %d\n" +
                "  Individuals exported: %d\n" +
                "  Object properties exported: %d\n" +
                "  Data properties exported: %d",
                classesExported, individualsExported, 
                objectPropertiesExported, dataPropertiesExported
            );
        }
    }
}
