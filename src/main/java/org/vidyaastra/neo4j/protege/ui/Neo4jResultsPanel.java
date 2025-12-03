package org.vidyaastra.neo4j.protege.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Panel for displaying Neo4j query results, following VidyaAstra pattern.
 */
public class Neo4jResultsPanel extends JPanel {
    
    private static final long serialVersionUID = 1L;
    
    private JTextArea resultsArea;
    private JLabel queryLabel;
    
    public Neo4jResultsPanel() {
        initializeComponents();
        layoutComponents();
    }
    
    private void initializeComponents() {
        queryLabel = new JLabel("No query executed yet");
        queryLabel.setFont(new Font("Monospaced", Font.BOLD, 12));
        
        resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        resultsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultsArea.setText("Results will appear here after executing a query.");
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel with query info
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("Executed Query:"), BorderLayout.NORTH);
        topPanel.add(queryLabel, BorderLayout.CENTER);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel with results
        JScrollPane scrollPane = new JScrollPane(resultsArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Results"));
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    /**
     * Displays query results in the panel.
     * 
     * @param results List of result records as maps
     * @param query The executed query
     */
    public void displayResults(List<Map<String, Object>> results, String query) {
        queryLabel.setText(query);
        
        if (results == null || results.isEmpty()) {
            resultsArea.setText("Query executed successfully but returned no results.");
            return;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Total Records: ").append(results.size()).append("\n");
        sb.append("=".repeat(80)).append("\n\n");
        
        for (int i = 0; i < results.size(); i++) {
            Map<String, Object> record = results.get(i);
            sb.append("Record ").append(i + 1).append(":\n");
            sb.append("-".repeat(40)).append("\n");
            
            for (Map.Entry<String, Object> entry : record.entrySet()) {
                sb.append(String.format("  %-20s : %s\n", entry.getKey(), formatValue(entry.getValue())));
            }
            
            sb.append("\n");
        }
        
        resultsArea.setText(sb.toString());
        resultsArea.setCaretPosition(0); // Scroll to top
    }
    
    /**
     * Formats a value for display.
     */
    private String formatValue(Object value) {
        if (value == null) {
            return "null";
        }
        
        if (value instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) value;
            StringBuilder sb = new StringBuilder("{");
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first) sb.append(", ");
                sb.append(entry.getKey()).append(": ").append(entry.getValue());
                first = false;
            }
            sb.append("}");
            return sb.toString();
        }
        
        if (value instanceof List) {
            List<?> list = (List<?>) value;
            return list.toString();
        }
        
        return value.toString();
    }
    
    /**
     * Clears the results display.
     */
    public void clearResults() {
        queryLabel.setText("No query executed yet");
        resultsArea.setText("Results will appear here after executing a query.");
    }
    
    public void dispose() {
        // Cleanup if needed
    }
}
