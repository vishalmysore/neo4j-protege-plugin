package org.neo4j.protege.ui;

import java.awt.BorderLayout;

import javax.swing.JTabbedPane;

import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;

/**
 * Main view component for Neo4j integration, similar to VidyaAstra's structure.
 */
public class Neo4jQueryViewComponent extends AbstractOWLViewComponent {
    
    private static final long serialVersionUID = 1L;
    
    private Neo4jQueryPanel queryPanel;
    private Neo4jResultsPanel resultsPanel;
    private JTabbedPane tabbedPane;
    
    @Override
    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout());
        
        // Create tabbed pane for different views
        tabbedPane = new JTabbedPane();
        
        // Query panel
        queryPanel = new Neo4jQueryPanel(getOWLEditorKit());
        tabbedPane.addTab("Query", queryPanel);
        
        // Results panel
        resultsPanel = new Neo4jResultsPanel();
        tabbedPane.addTab("Results", resultsPanel);
        
        // Connect query panel to results panel
        queryPanel.setResultsPanel(resultsPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    @Override
    protected void disposeOWLView() {
        if (queryPanel != null) {
            queryPanel.dispose();
        }
        if (resultsPanel != null) {
            resultsPanel.dispose();
        }
    }
}

