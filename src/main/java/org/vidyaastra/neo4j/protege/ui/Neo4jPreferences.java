package org.vidyaastra.neo4j.protege.ui;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;

/**
 * Helper class to manage Neo4j plugin preferences including Neo4j and LLM configuration.
 */
public class Neo4jPreferences {
    
    private static final String PREFERENCES_ID = "org.neo4j.protege";
    
    // Neo4j Connection preference keys
    private static final String NEO4J_URI_KEY = "neo4j.uri";
    private static final String NEO4J_USERNAME_KEY = "neo4j.username";
    private static final String NEO4J_PASSWORD_KEY = "neo4j.password";
    private static final String NEO4J_DATABASE_KEY = "neo4j.database";
    
    // LLM Integration preference keys
    private static final String LLM_BASE_URL_KEY = "llm.baseUrl";
    private static final String LLM_API_KEY_KEY = "llm.apiKey";
    private static final String LLM_MODEL_KEY = "llm.model";
    
    // Default values for Neo4j
    private static final String DEFAULT_NEO4J_URI = "neo4j+s://yourserver.databases.neo4j.io";
    private static final String DEFAULT_NEO4J_USERNAME = "neo4j";
    private static final String DEFAULT_NEO4J_PASSWORD = "yourrealpassword";
    private static final String DEFAULT_NEO4J_DATABASE = "neo4j";
    
    // Default values for LLM
    private static final String DEFAULT_LLM_BASE_URL = "https://api.openai.com/v1";
    private static final String DEFAULT_LLM_MODEL = "gpt-4o-mini";
    
    /**
     * Gets the Neo4j preferences instance.
     */
    private static Preferences getPreferences() {
        PreferencesManager prefMan = PreferencesManager.getInstance();
        return prefMan.getPreferencesForSet(PREFERENCES_ID, PREFERENCES_ID);
    }
    
    // ========== Neo4j Connection Methods ==========
    
    public static String getNeo4jUri() {
        return getPreferences().getString(NEO4J_URI_KEY, DEFAULT_NEO4J_URI);
    }
    
    public static void setNeo4jUri(String uri) {
        getPreferences().putString(NEO4J_URI_KEY, uri);
    }
    
    public static String getNeo4jUsername() {
        return getPreferences().getString(NEO4J_USERNAME_KEY, DEFAULT_NEO4J_USERNAME);
    }
    
    public static void setNeo4jUsername(String username) {
        getPreferences().putString(NEO4J_USERNAME_KEY, username);
    }
    
    public static String getNeo4jPassword() {
        return getPreferences().getString(NEO4J_PASSWORD_KEY, DEFAULT_NEO4J_PASSWORD);
    }
    
    public static void setNeo4jPassword(String password) {
        getPreferences().putString(NEO4J_PASSWORD_KEY, password);
    }
    
    public static String getNeo4jDatabase() {
        return getPreferences().getString(NEO4J_DATABASE_KEY, DEFAULT_NEO4J_DATABASE);
    }
    
    public static void setNeo4jDatabase(String database) {
        getPreferences().putString(NEO4J_DATABASE_KEY, database);
    }
    
    public static boolean isNeo4jConfigured() {
        String uri = getNeo4jUri();
        String username = getNeo4jUsername();
        String password = getNeo4jPassword();
        return uri != null && !uri.trim().isEmpty() &&
               username != null && !username.trim().isEmpty() &&
               password != null && !password.trim().isEmpty();
    }
    
    // ========== LLM Integration Methods ==========
    
    public static String getLlmBaseUrl() {
        return getPreferences().getString(LLM_BASE_URL_KEY, DEFAULT_LLM_BASE_URL);
    }
    
    public static void setLlmBaseUrl(String baseUrl) {
        getPreferences().putString(LLM_BASE_URL_KEY, baseUrl);
    }
    
    public static String getLlmApiKey() {
        return getPreferences().getString(LLM_API_KEY_KEY, "");
    }
    
    public static void setLlmApiKey(String apiKey) {
        getPreferences().putString(LLM_API_KEY_KEY, apiKey);
    }
    
    public static String getLlmModel() {
        return getPreferences().getString(LLM_MODEL_KEY, DEFAULT_LLM_MODEL);
    }
    
    public static void setLlmModel(String model) {
        getPreferences().putString(LLM_MODEL_KEY, model);
    }
    
    public static boolean isLlmConfigured() {
        String apiKey = getLlmApiKey();
        return apiKey != null && !apiKey.trim().isEmpty();
    }
}
