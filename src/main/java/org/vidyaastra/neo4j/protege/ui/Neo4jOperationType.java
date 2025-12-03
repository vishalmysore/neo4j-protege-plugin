package org.vidyaastra.neo4j.protege.ui;

/**
 * Enum representing different types of operations that can be performed
 * through the Neo4j integration interface.
 */
public enum Neo4jOperationType {
    /**
     * Natural language query mode - convert natural language to Cypher and execute
     */
    NATURAL_LANGUAGE_QUERY("Natural Language Query", "Ask questions in natural language"),
    
    /**
     * Direct Cypher query mode - execute raw Cypher queries
     */
    DIRECT_CYPHER_QUERY("Direct Cypher Query", "Execute Cypher queries directly"),
    
    /**
     * Export to Neo4j mode - export OWL ontology entities to Neo4j graph database
     */
    EXPORT_TO_NEO4J("Export to Neo4j", "Export ontology classes and individuals to Neo4j");
    
    private final String displayName;
    private final String description;
    
    Neo4jOperationType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
