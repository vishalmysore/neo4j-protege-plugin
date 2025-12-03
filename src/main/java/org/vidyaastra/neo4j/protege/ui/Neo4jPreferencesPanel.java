package org.vidyaastra.neo4j.protege.ui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.protege.editor.core.ui.preferences.PreferencesLayoutPanel;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;

/**
 * Preferences panel for Neo4j plugin settings, including Neo4j connection and LLM configuration.
 */
public class Neo4jPreferencesPanel extends OWLPreferencesPanel {
    
    private static final long serialVersionUID = 1L;
    
    // Neo4j connection fields
    private JTextField neo4jUriField;
    private JTextField neo4jUsernameField;
    private JPasswordField neo4jPasswordField;
    private JTextField neo4jDatabaseField;
    
    // LLM integration fields
    private JTextField llmBaseUrlField;
    private JPasswordField llmApiKeyField;
    private JTextField llmModelField;
    
    @Override
    public void initialise() throws Exception {
        setLayout(new BorderLayout());
        
        PreferencesLayoutPanel panel = new PreferencesLayoutPanel();
        add(panel, BorderLayout.NORTH);
        
        // Neo4j Connection Settings Section
        JPanel neo4jPanel = createNeo4jConfigPanel();
        panel.addGroup("Neo4j Connection");
        panel.addGroupComponent(neo4jPanel);
        
        // LLM Integration Settings Section
        JPanel llmPanel = createLlmConfigPanel();
        panel.addGroup("LLM Integration (Natural Language Queries)");
        panel.addGroupComponent(llmPanel);
    }
    
    private JPanel createNeo4jConfigPanel() {
        JPanel neo4jPanel = new JPanel(new GridBagLayout());
        neo4jPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Neo4j Database Configuration"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // URI
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        neo4jPanel.add(new JLabel("URI:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        neo4jUriField = new JTextField(40);
        neo4jUriField.setText(Neo4jPreferences.getNeo4jUri());
        neo4jPanel.add(neo4jUriField, gbc);
        
        // Username
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        neo4jPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        neo4jUsernameField = new JTextField(40);
        neo4jUsernameField.setText(Neo4jPreferences.getNeo4jUsername());
        neo4jPanel.add(neo4jUsernameField, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        neo4jPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        neo4jPasswordField = new JPasswordField(40);
        neo4jPasswordField.setText(Neo4jPreferences.getNeo4jPassword());
        neo4jPanel.add(neo4jPasswordField, gbc);
        
        // Database
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        neo4jPanel.add(new JLabel("Database:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        neo4jDatabaseField = new JTextField(40);
        neo4jDatabaseField.setText(Neo4jPreferences.getNeo4jDatabase());
        neo4jPanel.add(neo4jDatabaseField, gbc);
        
        // Help text
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 10, 5, 10);
        JLabel helpLabel = new JLabel("<html><i>Configure your Neo4j database connection.<br>" +
                "Example URI: bolt://localhost:7687 or neo4j://localhost:7687</i></html>");
        neo4jPanel.add(helpLabel, gbc);
        
        return neo4jPanel;
    }
    
    private JPanel createLlmConfigPanel() {
        JPanel llmPanel = new JPanel(new GridBagLayout());
        llmPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "LLM API Configuration"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Base URL
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        llmPanel.add(new JLabel("Base URL:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        llmBaseUrlField = new JTextField(40);
        llmBaseUrlField.setText(Neo4jPreferences.getLlmBaseUrl());
        llmPanel.add(llmBaseUrlField, gbc);
        
        // API Key
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        llmPanel.add(new JLabel("API Key:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        llmApiKeyField = new JPasswordField(40);
        llmApiKeyField.setText(Neo4jPreferences.getLlmApiKey());
        llmPanel.add(llmApiKeyField, gbc);
        
        // Model
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        llmPanel.add(new JLabel("Model:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        llmModelField = new JTextField(40);
        llmModelField.setText(Neo4jPreferences.getLlmModel());
        llmPanel.add(llmModelField, gbc);
        
        // Help text
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 10, 5, 10);
        JLabel helpLabel = new JLabel("<html><i>Configure your LLM API credentials for natural language query translation.<br>" +
                "For OpenAI: Use https://api.openai.com/v1<br>" +
                "For Azure OpenAI: Use your Azure endpoint URL<br>" +
                "Common models: gpt-4o-mini, gpt-4o, gpt-4-turbo, gpt-3.5-turbo</i></html>");
        llmPanel.add(helpLabel, gbc);
        
        return llmPanel;
    }
    
    @Override
    public void dispose() throws Exception {
        // Cleanup if needed
    }
    
    @Override
    public void applyChanges() {
        // Save Neo4j preferences
        Neo4jPreferences.setNeo4jUri(neo4jUriField.getText().trim());
        Neo4jPreferences.setNeo4jUsername(neo4jUsernameField.getText().trim());
        Neo4jPreferences.setNeo4jPassword(new String(neo4jPasswordField.getPassword()));
        Neo4jPreferences.setNeo4jDatabase(neo4jDatabaseField.getText().trim());
        
        // Save LLM preferences
        Neo4jPreferences.setLlmBaseUrl(llmBaseUrlField.getText().trim());
        Neo4jPreferences.setLlmApiKey(new String(llmApiKeyField.getPassword()));
        Neo4jPreferences.setLlmModel(llmModelField.getText().trim());
    }
}
