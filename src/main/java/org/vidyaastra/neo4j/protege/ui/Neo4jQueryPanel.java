package org.vidyaastra.neo4j.protege.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.vidyaastra.neo4j.protege.core.Neo4jService;
import org.vidyaastra.neo4j.protege.core.NlpQueryService;
import org.vidyaastra.neo4j.protege.core.OwlExportService;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;

/**
 * Panel for Neo4j query input and execution, following VidyaAstra pattern.
 */
public class Neo4jQueryPanel extends JPanel {
    
    private static final long serialVersionUID = 1L;
    
    private final OWLEditorKit editorKit;
    private final Neo4jDialogManager dialogManager;
    
    // Connection settings
    private JTextField neo4jUriField;
    private JTextField neo4jUsernameField;
    private JPasswordField neo4jPasswordField;
    private JTextField neo4jDatabaseField;
    private JTextField llmBaseUrlField;
    private JPasswordField llmApiKeyField;
    private JTextField llmModelField;
    private JButton saveSettingsButton;
    
    private JComboBox<Neo4jOperationType> operationTypeComboBox;
    private JTextArea queryInputArea;
    private JButton executeButton;
    private JButton connectButton;
    private JButton clearButton;
    private JLabel statusLabel;
    
    private Neo4jResultsPanel resultsPanel;
    private Neo4jService neo4jService;
    
    public Neo4jQueryPanel(OWLEditorKit editorKit) {
        this.editorKit = editorKit;
        this.dialogManager = new Neo4jDialogManager();
        
        initializeComponents();
        layoutComponents();
        setupListeners();
        loadSettings();
    }
    
    private void initializeComponents() {
        // Connection settings fields
        neo4jUriField = new JTextField(30);
        neo4jUsernameField = new JTextField(20);
        neo4jPasswordField = new JPasswordField(20);
        neo4jDatabaseField = new JTextField(20);
        llmBaseUrlField = new JTextField(30);
        llmApiKeyField = new JPasswordField(30);
        llmModelField = new JTextField(20);
        saveSettingsButton = new JButton("Save Settings");
        
        // Operation type selector
        operationTypeComboBox = new JComboBox<>(Neo4jOperationType.values());
        operationTypeComboBox.setSelectedItem(Neo4jOperationType.NATURAL_LANGUAGE_QUERY);
        
        // Query input area
        queryInputArea = new JTextArea(10, 50);
        queryInputArea.setLineWrap(true);
        queryInputArea.setWrapStyleWord(true);
        queryInputArea.setBorder(BorderFactory.createLineBorder(java.awt.Color.GRAY));
        
        // Buttons
        connectButton = new JButton("Connect to Neo4j");
        executeButton = new JButton("Execute Query");
        executeButton.setEnabled(false); // Disabled until connected
        clearButton = new JButton("Clear");
        
        // Status label
        statusLabel = new JLabel("Not connected");
        statusLabel.setForeground(java.awt.Color.RED);
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel - Settings section
        JPanel settingsPanel = createSettingsPanel();
        add(settingsPanel, BorderLayout.NORTH);
        
        // Center panel - query input
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        
        // Query mode and connection status
        JPanel queryHeaderPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        queryHeaderPanel.add(new JLabel("Query Mode:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        queryHeaderPanel.add(operationTypeComboBox, gbc);
        
        gbc.gridx = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        queryHeaderPanel.add(connectButton, gbc);
        
        gbc.gridx = 3;
        queryHeaderPanel.add(statusLabel, gbc);
        
        centerPanel.add(queryHeaderPanel, BorderLayout.NORTH);
        
        JPanel queryInputPanel = new JPanel(new BorderLayout(5, 5));
        queryInputPanel.add(new JLabel("Enter your query:"), BorderLayout.NORTH);
        queryInputPanel.add(new JScrollPane(queryInputArea), BorderLayout.CENTER);
        centerPanel.add(queryInputPanel, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
        
        // Bottom panel - action buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(clearButton);
        bottomPanel.add(executeButton);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createSettingsPanel() {
        JPanel settingsPanel = new JPanel(new GridBagLayout());
        settingsPanel.setBorder(BorderFactory.createTitledBorder("Connection Settings"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Neo4j URI
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        settingsPanel.add(new JLabel("Neo4j URI:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        settingsPanel.add(neo4jUriField, gbc);
        
        // Username
        gbc.gridx = 2; gbc.weightx = 0.0;
        settingsPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.5;
        settingsPanel.add(neo4jUsernameField, gbc);
        
        row++;
        
        // Password
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        settingsPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        settingsPanel.add(neo4jPasswordField, gbc);
        
        // Database
        gbc.gridx = 2; gbc.weightx = 0.0;
        settingsPanel.add(new JLabel("Database:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.5;
        settingsPanel.add(neo4jDatabaseField, gbc);
        
        row++;
        
        // LLM Base URL
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        settingsPanel.add(new JLabel("LLM Base URL:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        settingsPanel.add(llmBaseUrlField, gbc);
        
        // LLM Model
        gbc.gridx = 2; gbc.weightx = 0.0;
        settingsPanel.add(new JLabel("Model:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.5;
        settingsPanel.add(llmModelField, gbc);
        
        row++;
        
        // LLM API Key
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.0;
        settingsPanel.add(new JLabel("LLM API Key:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.weightx = 1.0;
        settingsPanel.add(llmApiKeyField, gbc);
        
        row++;
        
        // Save button
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        settingsPanel.add(saveSettingsButton, gbc);
        
        return settingsPanel;
    }
    
    private void loadSettings() {
        neo4jUriField.setText(Neo4jPreferences.getNeo4jUri());
        neo4jUsernameField.setText(Neo4jPreferences.getNeo4jUsername());
        neo4jPasswordField.setText(Neo4jPreferences.getNeo4jPassword());
        neo4jDatabaseField.setText(Neo4jPreferences.getNeo4jDatabase());
        llmBaseUrlField.setText(Neo4jPreferences.getLlmBaseUrl());
        llmApiKeyField.setText(Neo4jPreferences.getLlmApiKey());
        llmModelField.setText(Neo4jPreferences.getLlmModel());
    }
    
    private void saveSettings() {
        Neo4jPreferences.setNeo4jUri(neo4jUriField.getText().trim());
        Neo4jPreferences.setNeo4jUsername(neo4jUsernameField.getText().trim());
        Neo4jPreferences.setNeo4jPassword(new String(neo4jPasswordField.getPassword()));
        Neo4jPreferences.setNeo4jDatabase(neo4jDatabaseField.getText().trim());
        Neo4jPreferences.setLlmBaseUrl(llmBaseUrlField.getText().trim());
        Neo4jPreferences.setLlmApiKey(new String(llmApiKeyField.getPassword()));
        Neo4jPreferences.setLlmModel(llmModelField.getText().trim());
        
        dialogManager.showMessageDialog(this, "Settings saved successfully!");
    }
    
    private void setupListeners() {
        // Save settings button
        saveSettingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveSettings();
            }
        });
        
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleConnect();
            }
        });
        
        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleExecuteQuery();
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                queryInputArea.setText("");
            }
        });
        
        operationTypeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateQueryPlaceholder();
            }
        });
    }
    
    private void updateQueryPlaceholder() {
        Neo4jOperationType type = (Neo4jOperationType) operationTypeComboBox.getSelectedItem();
        if (type != null && queryInputArea.getText().trim().isEmpty()) {
            switch (type) {
                case NATURAL_LANGUAGE_QUERY:
                    queryInputArea.setText("// Example: Find all nodes related to fraud detection");
                    break;
                case DIRECT_CYPHER_QUERY:
                    queryInputArea.setText("// Example: MATCH (n) RETURN n LIMIT 10");
                    break;
                case EXPORT_TO_NEO4J:
                    queryInputArea.setText("// Click Execute to export current ontology to Neo4j");
                    break;
            }
        }
    }
    
    private void handleConnect() {
        // Check if Neo4j is configured
        if (!Neo4jPreferences.isNeo4jConfigured()) {
            dialogManager.showErrorMessageDialog(this, 
                "Neo4j connection not configured.\n\n" +
                "Please configure your Neo4j connection in:\n" +
                "Preferences → Neo4j Integration");
            return;
        }
        
        try {
            // Create Neo4j service
            String uri = Neo4jPreferences.getNeo4jUri();
            String username = Neo4jPreferences.getNeo4jUsername();
            String password = Neo4jPreferences.getNeo4jPassword();
            String database = Neo4jPreferences.getNeo4jDatabase();
            
            neo4jService = new Neo4jService(uri, username, password, database);
            
            // Test connection
            statusLabel.setText("Connecting...");
            statusLabel.setForeground(java.awt.Color.ORANGE);
            
            SwingUtilities.invokeLater(() -> {
                try {
                    if (neo4jService.testConnection()) {
                        statusLabel.setText("Connected to " + uri);
                        statusLabel.setForeground(java.awt.Color.GREEN);
                        executeButton.setEnabled(true);
                        connectButton.setText("Disconnect");
                        
                        dialogManager.showMessageDialog(this, "Successfully connected to Neo4j!");
                    } else {
                        throw new Exception("Connection test failed");
                    }
                } catch (Exception ex) {
                    statusLabel.setText("Connection failed");
                    statusLabel.setForeground(java.awt.Color.RED);
                    dialogManager.showErrorMessageDialog(this, 
                        "Failed to connect to Neo4j:\n" + ex.getMessage());
                }
            });
            
        } catch (Exception ex) {
            statusLabel.setText("Connection failed");
            statusLabel.setForeground(java.awt.Color.RED);
            dialogManager.showErrorMessageDialog(this, 
                "Failed to initialize Neo4j connection:\n" + ex.getMessage());
        }
    }
    
    private void handleExecuteQuery() {
        if (neo4jService == null || !neo4jService.isConnected()) {
            dialogManager.showErrorMessageDialog(this, "Not connected to Neo4j. Please connect first.");
            return;
        }
        
        Neo4jOperationType operationType = (Neo4jOperationType) operationTypeComboBox.getSelectedItem();
        
        // Handle Export to Neo4j mode
        if (operationType == Neo4jOperationType.EXPORT_TO_NEO4J) {
            handleExportToNeo4j();
            return;
        }
        
        String query = queryInputArea.getText().trim();
        if (query.isEmpty() || query.startsWith("//")) {
            dialogManager.showErrorMessageDialog(this, "Please enter a query.");
            return;
        }
        
        // Execute in background thread
        new Thread(() -> {
            try {
                String cypherQuery = query;
                
                // Translate natural language to Cypher if needed
                if (operationType == Neo4jOperationType.NATURAL_LANGUAGE_QUERY) {
                    if (!Neo4jPreferences.isLlmConfigured()) {
                        SwingUtilities.invokeLater(() -> {
                            dialogManager.showErrorMessageDialog(Neo4jQueryPanel.this,
                                "LLM not configured.\n\n" +
                                "Please configure your LLM API in:\n" +
                                "Preferences → Neo4j Integration");
                        });
                        return;
                    }
                    
                    // Get graph schema
                    String schema = NlpQueryService.getGraphSchema(neo4jService);
                    
                    // Translate to Cypher
                    NlpQueryService nlpService = new NlpQueryService(
                        Neo4jPreferences.getLlmApiKey(),
                        Neo4jPreferences.getLlmModel(),
                        Neo4jPreferences.getLlmBaseUrl()
                    );
                    
                    cypherQuery = nlpService.translateToCypher(query, schema);
                    
                    // Show translated query
                    final String translatedQuery = cypherQuery;
                    SwingUtilities.invokeLater(() -> {
                        int confirm = dialogManager.showConfirmDialog(Neo4jQueryPanel.this,
                            "Confirm Query",
                            "Translated Cypher query:\n\n" + translatedQuery + "\n\nExecute this query?");
                        
                        if (confirm == javax.swing.JOptionPane.YES_OPTION) {
                            executeQueryInternal(translatedQuery);
                        }
                    });
                    return;
                }
                
                executeQueryInternal(cypherQuery);
                
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    dialogManager.showErrorMessageDialog(Neo4jQueryPanel.this,
                        "Query execution failed:\n" + ex.getMessage());
                });
            }
        }).start();
    }
    
    private void handleExportToNeo4j() {
        // Confirm export
        int confirm = dialogManager.showConfirmDialog(this,
            "Confirm Export",
            "This will export the current ontology to Neo4j.\n\n" +
            "OWL Classes → Neo4j nodes (label: OWLClass)\n" +
            "OWL Individuals → Neo4j nodes (label: OWLIndividual)\n" +
            "Object Properties → Neo4j relationships\n" +
            "Subclass relationships → SUBCLASS_OF relationships\n" +
            "Instance relationships → INSTANCE_OF relationships\n\n" +
            "Continue?");
        
        if (confirm != javax.swing.JOptionPane.YES_OPTION) {
            return;
        }
        
        // Execute export in background thread
        new Thread(() -> {
            try {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Exporting ontology...");
                    statusLabel.setForeground(java.awt.Color.ORANGE);
                });
                
                OWLOntology ontology = editorKit.getOWLModelManager().getActiveOntology();
                OwlExportService exportService = new OwlExportService(neo4jService);
                
                // Export with optional reasoner
                OwlExportService.ExportSummary summary = exportService.exportOntology(ontology, null);
                
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Export completed");
                    statusLabel.setForeground(java.awt.Color.GREEN);
                    
                    dialogManager.showMessageDialog(this,
                        "Ontology exported successfully!\n\n" + summary.toString());
                });
                
            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Export failed");
                    statusLabel.setForeground(java.awt.Color.RED);
                    dialogManager.showErrorMessageDialog(Neo4jQueryPanel.this,
                        "Export failed:\n" + ex.getMessage());
                });
            }
        }).start();
    }
    
    private void executeQueryInternal(String cypherQuery) {
        try {
            var results = neo4jService.executeQuery(cypherQuery);
            
            // Convert results to OWL and add to ontology
            addResultsToOntology(results);
            
            SwingUtilities.invokeLater(() -> {
                if (resultsPanel != null) {
                    resultsPanel.displayResults(results, cypherQuery);
                }
                dialogManager.showMessageDialog(this, 
                    "Query executed successfully. " + results.size() + " records imported to ontology.");
            });
            
        } catch (Exception ex) {
            SwingUtilities.invokeLater(() -> {
                dialogManager.showErrorMessageDialog(Neo4jQueryPanel.this,
                    "Query execution failed:\n" + ex.getMessage());
            });
        }
    }
    
    private void addResultsToOntology(java.util.List<java.util.Map<String, Object>> results) {
        try {
            OWLOntology ontology = editorKit.getOWLModelManager().getActiveOntology();
            OWLDataFactory dataFactory = editorKit.getOWLModelManager().getOWLDataFactory();
            
            // Get base IRI from ontology
            String baseIRI = "http://www.semanticweb.org/ontology";
            if (ontology.getOntologyID().getOntologyIRI().isPresent()) {
                baseIRI = ontology.getOntologyID().getOntologyIRI().get().toString();
            }
            
            for (java.util.Map<String, Object> record : results) {
            for (java.util.Map.Entry<String, Object> entry : record.entrySet()) {
                Object value = entry.getValue();                    // Handle Neo4j nodes
                    if (value instanceof org.neo4j.driver.types.Node) {
                        org.neo4j.driver.types.Node node = (org.neo4j.driver.types.Node) value;
                        String nodeId = String.valueOf(node.id());
                        
                        // Get node labels (used as OWL classes)
                        for (String label : node.labels()) {
                            // Create OWL Class for the label if it doesn't exist
                            OWLClass owlClass = dataFactory.getOWLClass(IRI.create(baseIRI + "#" + label));
                            editorKit.getOWLModelManager().applyChange(
                                new org.semanticweb.owlapi.model.AddAxiom(ontology,
                                    dataFactory.getOWLDeclarationAxiom(owlClass)));
                            
                            // Create individual for this node
                            String individualName = label + "_" + nodeId;
                            OWLNamedIndividual individual = dataFactory.getOWLNamedIndividual(
                                IRI.create(baseIRI + "#" + individualName));
                            
                            // Add class assertion
                            editorKit.getOWLModelManager().applyChange(
                                new org.semanticweb.owlapi.model.AddAxiom(ontology,
                                    dataFactory.getOWLClassAssertionAxiom(owlClass, individual)));
                            
                            // Add properties from node
                            for (String propKey : node.keys()) {
                                Object propValue = node.get(propKey).asObject();
                                if (propValue != null) {
                                    OWLDataProperty dataProp = dataFactory.getOWLDataProperty(
                                        IRI.create(baseIRI + "#" + propKey));
                                    String literalValue = propValue.toString();
                                    if (literalValue != null) {
                                        editorKit.getOWLModelManager().applyChange(
                                            new org.semanticweb.owlapi.model.AddAxiom(ontology,
                                                dataFactory.getOWLDataPropertyAssertionAxiom(
                                                    dataProp, individual, 
                                                    dataFactory.getOWLLiteral(literalValue))));
                                    }
                                }
                            }
                        }
                    }
                    // Handle Neo4j relationships
                    else if (value instanceof org.neo4j.driver.types.Relationship) {
                        org.neo4j.driver.types.Relationship rel = (org.neo4j.driver.types.Relationship) value;
                        String relType = rel.type();
                        
                        // Create object property for relationship
                        OWLObjectProperty objProp = dataFactory.getOWLObjectProperty(
                            IRI.create(baseIRI + "#" + relType));
                        editorKit.getOWLModelManager().applyChange(
                            new org.semanticweb.owlapi.model.AddAxiom(ontology,
                                dataFactory.getOWLDeclarationAxiom(objProp)));
                    }
                }
            }
            
            System.out.println("Successfully imported " + results.size() + " records to ontology");
            
        } catch (Exception e) {
            System.err.println("Error adding results to ontology: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void setResultsPanel(Neo4jResultsPanel resultsPanel) {
        this.resultsPanel = resultsPanel;
    }
    
    public void dispose() {
        if (neo4jService != null) {
            neo4jService.close();
        }
    }
}
