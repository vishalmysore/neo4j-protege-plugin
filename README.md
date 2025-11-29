# 🚀 Neo4j-Protégé Integration Plugin

<div align="center">

![Neo4j + Protégé](https://img.shields.io/badge/Neo4j-4581C3?style=for-the-badge&logo=neo4j&logoColor=white)
![Protégé](https://img.shields.io/badge/Protégé-5.6.4-orange?style=for-the-badge)
![AI Powered](https://img.shields.io/badge/AI-Powered-blueviolet?style=for-the-badge)
![Java](https://img.shields.io/badge/Java-11-red?style=for-the-badge)

**Seamlessly connect Neo4j cloud databases with Protégé ontologies using natural language queries**

[Features](#-key-features) • [Installation](#-installation) • [Usage](#-usage) • [Screenshots](#-screenshots) • [Full Guide](PLUGIN_GUIDE.md)

</div>

---

## 🌟 Overview

The **Neo4j-Protégé Integration Plugin** revolutionizes how researchers, data scientists, and knowledge engineers work with graph databases and ontologies. This plugin creates a **unified semantic workspace** that bridges Neo4j's powerful graph capabilities with Protégé's ontology reasoning.

### ✨ Key Features

| Feature | Description |
|---------|-------------|
| 🌐 **Cloud-Native** | Direct connection to Neo4j Aura with secure `neo4j+s://` protocol |
| 🤖 **NLP-Powered** | Convert English questions to Cypher queries using LLM (GPT-4, Claude, Ollama) |
| 🔄 **Bidirectional Sync** | Import Neo4j data as OWL entities AND export Protégé ontologies to Neo4j |
| 🎯 **VidyaAstra Compatible** | Works seamlessly with VidyaAstra reasoning and SPARQL queries |
| 📊 **Visual Integration** | Graph results displayed directly in Protégé's ontology view |
| 🔒 **Secure** | Password-masked credentials for Neo4j and LLM API keys |
| 🌍 **Domain-Agnostic** | Works with ANY ontology - medical, legal, manufacturing, research, etc. |

---

## 🎯 Use Cases

This plugin is designed to work with **any domain ontology**, not just specific domains:

- 🏥 **Medical & Healthcare**: Patient records, diagnoses, treatments, drug interactions
- 📚 **Research & Academia**: Publications, citations, author networks
- 🏭 **Manufacturing**: Product components, supply chains, quality control
- ⚖️ **Legal & Compliance**: Case law, regulations, entity relationships
- 🔬 **Bioinformatics**: Gene interactions, protein networks, pathways
- 🏦 **Financial Services**: Transaction networks, fraud detection, risk analysis
- 🌐 **Social Networks**: User relationships, communities, influence patterns

---

## 📋 Prerequisites

- **Java 11** or higher
- **Maven 3.6+** (for building from source)
- **Protégé 5.6.4** or higher ([Download](https://protege.stanford.edu/))
- **Neo4j Aura** account (free tier available) or local Neo4j instance
- **LLM API Key** (optional, for natural language queries):
  - OpenAI API key, or
  - Anthropic Claude API key, or
  - Local Ollama installation

---

## 🚀 Installation

### Quick Install

1. **Download the Plugin**
   ```bash
   # Download the latest release JAR file
   wget https://github.com/vishalmysore/neo4j-protege-plugin/releases/latest/neo4j-protege-plugin-1.0.0.jar
   ```

2. **Install in Protégé**
   
   Copy the JAR to your Protégé plugins directory:
   
   - **Windows**: `C:\Users\<username>\protege\Protege-5.6.7\plugins\`
   - **macOS**: `/Applications/Protege.app/Contents/Java/plugins/`
   - **Linux**: `~/.Protege/plugins/` or `/opt/protege/plugins/`

3. **Restart Protégé**

4. **Access the Plugin**
   - Open Protégé
   - Go to **Window → Tabs → Neo4j Query**
   - The plugin panel will appear!

### Build from Source

```bash
# Clone the repository
git clone https://github.com/vishalmysore/neo4j-protege-plugin.git
cd neo4j-protege-plugin

# Build the plugin
mvn clean package

# Copy to Protégé plugins directory
cp target/neo4j-protege-plugin-1.0.0.jar <PROTEGE_HOME>/plugins/
```

---

## 🎨 Usage

### 1️⃣ Configure Connection Settings

Open the plugin in Protégé and configure your connections:

**Neo4j Connection:**
- **URI**: `neo4j+s://your-instance.databases.neo4j.io` (for Aura) or `bolt://localhost:7687` (local)
- **Username**: `neo4j`
- **Password**: Your Neo4j password
- **Database**: `neo4j` (or your database name)

**LLM Configuration (Optional for NLP queries):**
- **Base URL**: 
  - OpenAI: `https://api.openai.com/v1`
  - Anthropic: `https://api.anthropic.com/v1`
  - Ollama: `http://localhost:11434/v1`
- **Model**: `gpt-4o-mini`, `claude-3-sonnet`, `llama3`, etc.
- **API Key**: Your LLM API key

Click **Save Settings** and then **Connect to Neo4j**.

### 2️⃣ Query Modes

The plugin offers three powerful query modes:

#### 🗣️ Natural Language Query
Ask questions in plain English!

```
Examples:
- "get me all the nodes"
- "find all reactions and symptoms they cause"
- "show me all patients with rare conditions"
```

The LLM translates your question to Cypher, shows you the query for confirmation, then executes it.

#### ⚡ Direct Cypher Query
Execute raw Cypher queries directly:

```cypher
MATCH (r:Reaction)-[:CAUSES]->(s:Symptom)
RETURN r.name AS reaction, s.name AS symptom
LIMIT 10
```

#### 📤 Export to Neo4j
Export your Protégé ontology to Neo4j as a graph database!

- **OWL Classes** → Neo4j nodes (label: `OWLClass`)
- **OWL Individuals** → Neo4j nodes (label: `OWLIndividual`)
- **Object Properties** → Neo4j relationships
- **Subclass relationships** → `SUBCLASS_OF` relationships
- **Instance relationships** → `INSTANCE_OF` relationships

Simply select "Export to Neo4j" mode and click "Execute Query".

---

## 📸 Screenshots

### 1. Natural Language Query - Translation

<img src="docs/images/screenshot1-nlp-translation.png" alt="NLP Query Translation" width="800"/>

**What's happening:**
1. User enters natural language query: "get me all the nodes"
2. Plugin retrieves Neo4j graph schema (node labels, relationships, properties)
3. Schema is sent to LLM along with the question
4. LLM generates valid Cypher: `MATCH (n) RETURN n`
5. User confirms the translated query before execution

**Key Features:**
- ✅ Schema-aware translation (no hallucinated labels)
- ✅ Confirmation dialog shows exact Cypher
- ✅ Full logging of translation process

---

### 2. Query Execution Success

<img src="docs/images/screenshot2-query-success.png" alt="Query Success" width="800"/>

**What's happening:**
1. Cypher query executed against Neo4j Aura
2. Results retrieved: 22 records in this example
3. Neo4j nodes automatically converted to OWL entities
4. Entities imported into Protégé ontology
5. Success message confirms import

**Key Features:**
- ✅ Automatic OWL conversion (nodes → classes/individuals)
- ✅ Relationship mapping (Neo4j edges → OWL object properties)
- ✅ Instant integration with existing ontology

---

### 3. Ontology Integration with VidyaAstra

<img src="docs/images/screenshot3-ontology-view.png" alt="Ontology Visualization" width="800"/>

**What's happening:**
1. Imported Neo4j data now appears in Protégé class hierarchy
2. New classes created: `Reaction`, `Patient`, `Diagnosis`, `Symptom`, etc.
3. VidyaAstra graph shows visual relationships
4. Classes can now be used in SWRL rules and reasoning

**Key Features:**
- ✅ Seamless integration with Protégé's class hierarchy
- ✅ Compatible with VidyaAstra visualization
- ✅ Ready for ontology reasoning and inference
- ✅ Can combine with existing ontology classes

---

### 4. Advanced Natural Language Query

<img src="docs/images/screenshot4-advanced-query.png" alt="Advanced NLP Query" width="800"/>

**What's happening:**
1. Complex query: "find all reactions"
2. LLM generates appropriate Cypher based on schema
3. Schema includes: `Reaction`, `Symptom`, `Treatment`, etc.
4. Query targets specific node type from schema
5. Results show instances of `Reaction` class

**Query History Panel:**
- 📊 Shows query type (Natural Language vs Direct Cypher)
- 📊 Displays target entities discovered
- 📊 Lists all instances found

**Key Features:**
- ✅ Handles complex multi-entity queries
- ✅ Respects graph schema constraints
- ✅ Provides detailed result breakdown

---

## 🔄 Complete Workflow Example

Here's a real-world workflow combining all features:

### Scenario: Medical Research Database

```
1. START: You have a medical ontology in Protégé
   Classes: Patient, Diagnosis, Treatment, Symptom, Medicine

2. EXPORT: Export ontology to Neo4j
   - Select "Export to Neo4j" mode
   - Click Execute
   - Result: 150 classes, 500 individuals exported

3. ENHANCE: Add clinical data to Neo4j
   - Use Neo4j Browser or Cypher scripts
   - Add patient records, test results, etc.

4. QUERY: Ask questions in natural language
   - "find all patients diagnosed with rare conditions"
   - LLM generates Cypher query
   - Results imported back to Protégé

5. REASON: Apply VidyaAstra rules
   - Define SWRL rules for drug interactions
   - Run reasoner on combined data
   - Discover new inferences

6. ANALYZE: Visualize in VidyaAstra Graph
   - See patient-diagnosis-treatment relationships
   - Identify patterns and anomalies
   - Generate research insights
```

---

## 🛠️ Technical Architecture

### Plugin Components

```
org.neo4j.protege
├── core/
│   ├── Neo4jService.java          // Database connection & queries
│   ├── NlpQueryService.java       // LLM integration & translation
│   ├── OwlExportService.java      // OWL → Neo4j export
│   ├── Neo4jPreferences.java      // Settings management
│   └── Neo4jDialogManager.java    // User interactions
└── ui/
    ├── Neo4jQueryPanel.java       // Main UI panel
    ├── Neo4jResultsPanel.java     // Results display
    └── Neo4jQueryViewComponent.java // Protégé integration
```

### Technology Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| **Graph DB** | Neo4j Java Driver | 4.4.13 |
| **Ontology** | OWL API | 4.5.x |
| **Platform** | Protégé | 5.6.4 |
| **NLP/LLM** | OpenAI, Anthropic, Ollama | Latest |
| **Build** | Maven + OSGi | 3.9.x |
| **Language** | Java | 11 |

---

## 📊 Logging & Debugging

The plugin provides comprehensive logging for troubleshooting:

### NLP Translation Logs
```
=== NLP to Cypher Translation Started ===
Natural Language Query: get me all the nodes
Graph Schema Length: 247 characters

--- System Prompt ---
You are an expert Neo4j Cypher query generator...
AVAILABLE GRAPH SCHEMA:
Node Labels: Department, Condition, Symptom...

--- LLM Request ---
Endpoint: http://langchain4j.dev/demo/openai/v1/chat/completions
Model: gpt-4o-mini
Temperature: 0.1

--- LLM Response ---
Response Code: 200
Generated Cypher: MATCH (n) RETURN labels(n) AS labels, n AS node

=== Translation Completed ===
```

### Viewing Logs
- **Console Output**: Protégé console window
- **Log Files**: Check Protégé's `logs/` directory
- **Debug Mode**: Set logging level in `log4j.properties`

---

## 🎯 Best Practices

### 1. Schema Design for Better NLP
✅ Use descriptive node labels: `Diagnosis` not `D`  
✅ Meaningful relationships: `TREATS` not `REL1`  
✅ Rich property names: `patientAge` not `pa`

### 2. Query Optimization
✅ Start specific, then broaden  
✅ Use `LIMIT` for large datasets  
✅ Test Cypher directly before using NLP

### 3. Ontology Integration
✅ Pre-define key classes in ontology  
✅ Use consistent naming conventions  
✅ Leverage VidyaAstra reasoning after import

### 4. Security
✅ Use read-only Neo4j credentials when possible  
✅ Never share API keys (fields are masked!)  
✅ Rotate passwords regularly

---

## 🚨 Troubleshooting

### Plugin Not Appearing
```
✓ Check JAR is in correct plugins/ directory
✓ Restart Protégé completely
✓ Check logs/protege.log for errors
✓ Verify Java 11+ is installed
```

### Connection Failed
```
✓ Verify Neo4j is running (test in Neo4j Browser)
✓ Check URI format: neo4j+s:// for Aura, bolt:// for local
✓ Confirm credentials are correct
✓ Ensure firewall allows port 7687
```

### LLM Translation Errors
```
✓ Verify API key is valid
✓ Check LLM base URL is correct
✓ Test API key with curl/Postman
✓ Review console logs for detailed error messages
```

### Build Failures
```bash
# Clean and rebuild
mvn clean install -U

# Skip tests if needed
mvn clean package -DskipTests

# Verify Maven version
mvn --version  # Should be 3.6+
```

---

## 🤝 Contributing

We welcome contributions! Here's how:

1. **Fork** the repository
2. **Create** a feature branch: `git checkout -b feature/amazing-feature`
3. **Commit** your changes: `git commit -m 'Add amazing feature'`
4. **Push** to branch: `git push origin feature/amazing-feature`
5. **Open** a Pull Request

### Development Setup

```bash
# Clone your fork
git clone https://github.com/YOUR_USERNAME/neo4j-protege-plugin.git
cd neo4j-protege-plugin

# Build
mvn clean package

# Run tests
mvn test

# Debug in Protégé
export MAVEN_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
# Then start Protégé and attach debugger to port 5005
```

---

## 📝 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

---

## 🙏 Acknowledgments

Built with inspiration and support from:

- **[VidyaAstra](https://sites.google.com/view/vidyaastra)** - Advanced ontology reasoning and visualization
- **[Neo4j](https://neo4j.com/)** - Leading graph database platform
- **[Protégé](https://protege.stanford.edu/)** - Premier ontology editor
- **[OpenAI](https://openai.com/)** / **[Anthropic](https://anthropic.com/)** - Cutting-edge language models
- **Knowledge Graph Community** - For continuous support and feedback

---

## 📞 Support & Community

- 🐛 **Issues**: [GitHub Issues](https://github.com/vishalmysore/neo4j-protege-plugin/issues)
- 💬 **Discussions**: [GitHub Discussions](https://github.com/vishalmysore/neo4j-protege-plugin/discussions)
- 📧 **Email**: vishalmysore@gmail.com
- 📖 **Full Guide**: [PLUGIN_GUIDE.md](PLUGIN_GUIDE.md)

---

<div align="center">

## ⭐ Star This Project!

If you find this plugin useful, give it a star on GitHub!

**Made with ❤️ for the Knowledge Graph Community**

![Semantic Web](https://img.shields.io/badge/Semantic%20Web-Ready-brightgreen?style=for-the-badge)
![Open Source](https://img.shields.io/badge/Open%20Source-MIT-blue?style=for-the-badge)

### 🌈 Happy Graph Querying!

*Connecting graphs, ontologies, and intelligence - one query at a time.*

</div>
