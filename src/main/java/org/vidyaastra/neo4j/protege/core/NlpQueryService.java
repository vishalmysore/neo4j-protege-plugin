package org.vidyaastra.neo4j.protege.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Service for translating natural language queries to Cypher using LLM.
 * Similar to VidyaAstra's OpenAiCaller but focused on Cypher generation.
 */
public class NlpQueryService {
    
    private final String apiKey;
    private final String model;
    private final String baseUrl;
    
    public NlpQueryService(String apiKey, String model, String baseUrl) {
        this.apiKey = apiKey;
        this.model = model;
        this.baseUrl = baseUrl;
    }
    
    /**
     * Converts a natural language query to a Cypher query.
     * 
     * @param naturalLanguageQuery The natural language query from the user
     * @param graphSchema Optional schema information about the Neo4j graph
     * @return The generated Cypher query
     * @throws Exception if translation fails
     */
    public String translateToCypher(String naturalLanguageQuery, String graphSchema) throws Exception {
        System.out.println("\n=== NLP to Cypher Translation Started ===");
        System.out.println("Natural Language Query: " + naturalLanguageQuery);
        System.out.println("Graph Schema Length: " + (graphSchema != null ? graphSchema.length() : 0) + " characters");
        
        String systemPrompt = buildSystemPrompt(graphSchema);
        String userPrompt = buildUserPrompt(naturalLanguageQuery);
        
        System.out.println("\n--- System Prompt ---");
        System.out.println(systemPrompt);
        System.out.println("\n--- User Prompt ---");
        System.out.println(userPrompt);
        System.out.println("\n--- Sending to LLM (" + model + ") at " + baseUrl + " ---");
        
        String llmResponse = generateCompletion(systemPrompt, userPrompt);
        
        System.out.println("\n--- LLM Response ---");
        System.out.println(llmResponse);
        
        // Extract Cypher query from response
        String cypherQuery = extractCypherQuery(llmResponse);
        
        System.out.println("\n--- Extracted Cypher Query ---");
        System.out.println(cypherQuery);
        System.out.println("=== NLP to Cypher Translation Completed ===\n");
        
        return cypherQuery;
    }
    
    /**
     * Builds the system prompt for Cypher generation.
     */
    private String buildSystemPrompt(String graphSchema) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are an expert Neo4j Cypher query generator. ");
        sb.append("Your task is to convert natural language questions into valid Cypher queries.\n\n");
        sb.append("CRITICAL RULES:\n");
        sb.append("1. Generate ONLY valid Cypher queries\n");
        sb.append("2. Use proper Cypher syntax for Neo4j 4.x or 5.x\n");
        sb.append("3. Return only the Cypher query without explanations or markdown\n");
        sb.append("4. Do NOT wrap the query in markdown code blocks (no ```cypher or ```)\n");
        sb.append("5. Do NOT use any node labels or relationship types that are not in the provided schema\n");
        sb.append("6. Do NOT hallucinate or invent labels - use ONLY what's in the schema\n\n");
        
        sb.append("CYPHER SYNTAX REQUIREMENTS:\n");
        sb.append("- UNION queries MUST have identical column names in all parts\n");
        sb.append("  WRONG: MATCH (d:Disease) RETURN d.name AS disease UNION MATCH (s:Symptom) RETURN s.name AS symptom\n");
        sb.append("  RIGHT: MATCH (d:Disease) RETURN d.name AS name UNION MATCH (s:Symptom) RETURN s.name AS name\n");
        sb.append("- Use labels() function to get node type when combining different labels\n");
        sb.append("- Prefer WHERE with OR over UNION when possible for better performance\n");
        sb.append("- Always use RETURN clause to specify what data to retrieve\n");
        sb.append("- Match node and relationship names exactly as shown in schema (case-sensitive)\n\n");
        
        if (graphSchema != null && !graphSchema.trim().isEmpty()) {
            sb.append("AVAILABLE GRAPH SCHEMA:\n");
            sb.append(graphSchema);
            sb.append("\n\n");
            sb.append("STRICT REQUIREMENT: You MUST use ONLY the node labels, relationship types, and property keys listed above.\n");
            sb.append("Do NOT use any labels like 'Disease', 'Person', 'User', or any other labels not explicitly listed in the schema.\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Builds the user prompt for Cypher generation.
     */
    private String buildUserPrompt(String naturalLanguageQuery) {
        StringBuilder sb = new StringBuilder();
        sb.append("Convert the following natural language question to a Cypher query:\n\n");
        sb.append(naturalLanguageQuery);
        sb.append("\n\nProvide only the Cypher query, nothing else.");
        return sb.toString();
    }
    
    /**
     * Generates a completion using the LLM API.
     */
    private String generateCompletion(String systemPrompt, String userPrompt) throws Exception {
        String endpoint = baseUrl.endsWith("/") ? baseUrl + "chat/completions" : baseUrl + "/chat/completions";
        
        System.out.println("LLM Endpoint: " + endpoint);
        
        URL url = new URL(endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        try {
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setDoOutput(true);
            
            // Build request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", model);
            
            JSONArray messages = new JSONArray();
            messages.put(new JSONObject().put("role", "system").put("content", systemPrompt));
            messages.put(new JSONObject().put("role", "user").put("content", userPrompt));
            
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.1); // Low temperature for more deterministic queries
            
            System.out.println("\n--- LLM Request Body ---");
            System.out.println(requestBody.toString(2)); // Pretty print with indent
            
            // Send request
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            // Read response
            int responseCode = conn.getResponseCode();
            System.out.println("\nLLM Response Code: " + responseCode);
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                }
                
                String responseString = response.toString();
                System.out.println("\n--- Full LLM JSON Response ---");
                System.out.println(responseString);
                
                JSONObject jsonResponse = new JSONObject(responseString);
                JSONArray choices = jsonResponse.getJSONArray("choices");
                
                if (choices.length() > 0) {
                    JSONObject firstChoice = choices.getJSONObject(0);
                    JSONObject message = firstChoice.getJSONObject("message");
                    return message.getString("content");
                } else {
                    throw new Exception("No choices returned from LLM");
                }
                
            } else {
                // Read error response
                System.err.println("\n!!! LLM API Error !!!");
                System.err.println("Response Code: " + responseCode);
                StringBuilder errorResponse = new StringBuilder();
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        errorResponse.append(line);
                    }
                }
                throw new Exception("LLM API error (HTTP " + responseCode + "): " + errorResponse.toString());
            }
            
        } finally {
            conn.disconnect();
        }
    }
    
    /**
     * Extracts Cypher query from LLM response, handling cases where it might be
     * wrapped in markdown code blocks or includes explanatory text.
     */
    private String extractCypherQuery(String llmResponse) throws Exception {
        if (llmResponse == null || llmResponse.trim().isEmpty()) {
            throw new Exception("Empty response from LLM");
        }
        
        String query = llmResponse.trim();
        
        // Remove markdown code block formatting if present
        if (query.startsWith("```cypher") || query.startsWith("```")) {
            int startIndex = query.indexOf('\n');
            int endIndex = query.lastIndexOf("```");
            
            if (startIndex > 0 && endIndex > startIndex) {
                query = query.substring(startIndex + 1, endIndex).trim();
            } else if (startIndex > 0) {
                query = query.substring(startIndex + 1).trim();
            }
        }
        
        // Remove any remaining backticks
        query = query.replace("```", "").trim();
        
        // Validate it looks like a Cypher query
        String upperQuery = query.toUpperCase();
        if (!upperQuery.contains("MATCH") && 
            !upperQuery.contains("CREATE") && 
            !upperQuery.contains("MERGE") &&
            !upperQuery.contains("RETURN") &&
            !upperQuery.contains("DELETE")) {
            throw new Exception("Generated text does not appear to be a valid Cypher query: " + query);
        }
        
        return query;
    }
    
    /**
     * Gets the graph schema from Neo4j to help with query generation.
     * 
     * @param neo4jService The Neo4j service to use
     * @return A string describing the graph schema
     */
    public static String getGraphSchema(Neo4jService neo4jService) {
        try {
            StringBuilder schema = new StringBuilder();
            
            // Get node labels
            var labelsResult = neo4jService.executeQuery("CALL db.labels()");
            schema.append("Node Labels:\n");
            for (var record : labelsResult) {
                schema.append("  - ").append(record.get("label")).append("\n");
            }
            schema.append("\n");
            
            // Get relationship types
            var relTypesResult = neo4jService.executeQuery("CALL db.relationshipTypes()");
            schema.append("Relationship Types:\n");
            for (var record : relTypesResult) {
                schema.append("  - ").append(record.get("relationshipType")).append("\n");
            }
            schema.append("\n");
            
            // Get property keys
            var propKeysResult = neo4jService.executeQuery("CALL db.propertyKeys()");
            schema.append("Property Keys:\n");
            for (var record : propKeysResult) {
                schema.append("  - ").append(record.get("propertyKey")).append("\n");
            }
            
            return schema.toString();
            
        } catch (Exception e) {
            System.err.println("Failed to retrieve graph schema: " + e.getMessage());
            return "";
        }
    }
}
