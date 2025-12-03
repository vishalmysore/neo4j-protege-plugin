package org.vidyaastra.neo4j.protege.core;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.SessionConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Service class for managing Neo4j database connections and executing queries.
 */
public class Neo4jService implements AutoCloseable {
    
    private Driver driver;
    private String uri;
    private String username;
    private String password;
    private String database;
    private boolean connected;
    
    public Neo4jService(String uri, String username, String password, String database) {
        this.uri = uri;
        this.username = username;
        this.password = password;
        this.database = database;
        this.connected = false;
    }
    
    /**
     * Establishes connection to the Neo4j database.
     * 
     * @throws RuntimeException if connection fails
     */
    public void connect() {
        if (connected && driver != null) {
            return; // Already connected
        }
        
        try {
            driver = GraphDatabase.driver(uri, AuthTokens.basic(username, password));
            // Verify connectivity
            driver.verifyConnectivity();
            connected = true;
            System.out.println("Successfully connected to Neo4j at: " + uri);
        } catch (Exception e) {
            connected = false;
            throw new RuntimeException("Failed to connect to Neo4j: " + e.getMessage(), e);
        }
    }
    
    /**
     * Disconnects from the Neo4j database.
     */
    public void disconnect() {
        if (driver != null) {
            driver.close();
            driver = null;
            connected = false;
            System.out.println("Disconnected from Neo4j");
        }
    }
    
    /**
     * Checks if currently connected to Neo4j.
     */
    public boolean isConnected() {
        return connected && driver != null;
    }
    
    /**
     * Executes a Cypher query and returns the results.
     * 
     * @param cypherQuery The Cypher query to execute
     * @return List of records as maps
     * @throws Exception if query execution fails
     */
    public List<Map<String, Object>> executeQuery(String cypherQuery) throws Exception {
        return executeQuery(cypherQuery, Map.of());
    }
    
    /**
     * Executes a Cypher query with parameters and returns the results.
     * 
     * @param cypherQuery The Cypher query to execute
     * @param parameters Query parameters
     * @return List of records as maps
     * @throws Exception if query execution fails
     */
    public List<Map<String, Object>> executeQuery(String cypherQuery, Map<String, Object> parameters) 
            throws Exception {
        
        if (!isConnected()) {
            throw new IllegalStateException("Not connected to Neo4j. Call connect() first.");
        }
        
        List<Map<String, Object>> results = new ArrayList<>();
        
        try (Session session = getSession()) {
            Result result = session.run(cypherQuery, parameters);
            
            while (result.hasNext()) {
                Record record = result.next();
                results.add(record.asMap());
            }
            
            System.out.println("Query executed successfully. Returned " + results.size() + " records.");
            
        } catch (Exception e) {
            System.err.println("Error executing query: " + e.getMessage());
            throw new Exception("Query execution failed: " + e.getMessage(), e);
        }
        
        return results;
    }
    
    /**
     * Executes a Cypher write query (CREATE, UPDATE, DELETE, etc.) and returns the summary.
     * 
     * @param cypherQuery The Cypher query to execute
     * @return Summary string describing what was modified
     * @throws Exception if query execution fails
     */
    public String executeWriteQuery(String cypherQuery) throws Exception {
        return executeWriteQuery(cypherQuery, Map.of());
    }
    
    /**
     * Executes a Cypher write query with parameters and returns the summary.
     * 
     * @param cypherQuery The Cypher query to execute
     * @param parameters Query parameters
     * @return Summary string describing what was modified
     * @throws Exception if query execution fails
     */
    public String executeWriteQuery(String cypherQuery, Map<String, Object> parameters) 
            throws Exception {
        
        if (!isConnected()) {
            throw new IllegalStateException("Not connected to Neo4j. Call connect() first.");
        }
        
        try (Session session = getSession()) {
            var result = session.run(cypherQuery, parameters);
            var summary = result.consume();
            
            StringBuilder sb = new StringBuilder();
            sb.append("Query executed successfully.\n");
            sb.append("Nodes created: ").append(summary.counters().nodesCreated()).append("\n");
            sb.append("Nodes deleted: ").append(summary.counters().nodesDeleted()).append("\n");
            sb.append("Relationships created: ").append(summary.counters().relationshipsCreated()).append("\n");
            sb.append("Relationships deleted: ").append(summary.counters().relationshipsDeleted()).append("\n");
            sb.append("Properties set: ").append(summary.counters().propertiesSet()).append("\n");
            
            String summaryText = sb.toString();
            System.out.println(summaryText);
            return summaryText;
            
        } catch (Exception e) {
            System.err.println("Error executing write query: " + e.getMessage());
            throw new Exception("Write query execution failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Gets a Neo4j session configured for the specified database.
     */
    private Session getSession() {
        if (database != null && !database.trim().isEmpty() && !database.equalsIgnoreCase("neo4j")) {
            return driver.session(SessionConfig.forDatabase(database));
        }
        return driver.session();
    }
    
    /**
     * Tests the connection to Neo4j.
     * 
     * @return true if connection is successful, false otherwise
     */
    public boolean testConnection() {
        try {
            connect();
            // Execute a simple query to verify
            executeQuery("RETURN 1 AS test");
            return true;
        } catch (Exception e) {
            System.err.println("Connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public void close() {
        disconnect();
    }
    
    // Getters for connection info
    public String getUri() {
        return uri;
    }
    
    public String getDatabase() {
        return database;
    }
}
