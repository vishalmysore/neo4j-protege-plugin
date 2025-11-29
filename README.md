# Neo4j Protégé Plugin

A Protégé desktop plugin for integrating with Neo4j graph database and processing natural language queries for fraud detection ontologies.

## Overview

This plugin extends the Protégé ontology editor to enable seamless interaction with Neo4j graph databases. It allows users to:
- Query Neo4j databases directly from within Protégé
- Use natural language to generate Cypher queries
- Visualize fraud detection patterns stored in Neo4j
- Map ontology concepts to graph database nodes and relationships

## Prerequisites

- **Java 11** or higher
- **Maven 3.6+**
- **Protégé 5.5.0** or higher
- **Neo4j 4.4+** database instance

## Project Structure

```
neo4j-protege-plugin/
├── src/
│   ├── main/
│   │   ├── java/com/fraud/protege/
│   │   │   ├── Neo4jConnectorPlugin.java    # Main plugin class
│   │   │   ├── QueryPanel.java              # UI panel for queries
│   │   │   └── ResultsPanel.java            # UI panel for results
│   │   └── resources/
│   │       ├── plugin.xml                   # Plugin metadata
│   │       ├── plugin.properties            # Configuration
│   │       └── ontology/
│   │           └── fraud-detection-ontology.owl
│   └── test/
├── docker/
│   └── docker-compose.yml                   # Neo4j setup
├── scripts/
│   ├── load-sample-data.cypher             # Sample fraud data
│   └── setup-neo4j.sh                      # Neo4j initialization
└── pom.xml                                  # Maven configuration
```

## Building the Plugin

### 1. Clone the repository
```bash
git clone <repository-url>
cd neo4j-protege-nlp-integration
```

### 2. Configure Neo4j connection
Edit `src/main/resources/plugin.properties`:
```properties
neo4j.uri=bolt://localhost:7687
neo4j.username=neo4j
neo4j.password=your_password
```

### 3. Build the plugin
```bash
mvn clean package
```

This creates a JAR file at `target/neo4j-protege-plugin-1.0-SNAPSHOT.jar`

## Installing the Plugin

### Method 1: Manual Installation

1. Build the plugin (see above)
2. Copy the JAR file to Protégé's plugins directory:
   - **Windows**: `C:\Program Files\Protege-5.5.0\plugins\`
   - **macOS**: `/Applications/Protege.app/Contents/Java/plugins/`
   - **Linux**: `/opt/protege/plugins/`
3. Restart Protégé

### Method 2: Protégé Plugin Manager

1. Open Protégé
2. Go to `File > Check for plugins...`
3. Click `Install` and select the built JAR file
4. Restart Protégé

## Setting Up Neo4j

### Using Docker (Recommended)

```bash
cd docker
docker-compose up -d
```

This starts Neo4j on:
- **Browser UI**: http://localhost:7474
- **Bolt Protocol**: bolt://localhost:7687
- **Credentials**: neo4j/password

### Load Sample Data

1. Access Neo4j Browser at http://localhost:7474
2. Open and run `scripts/load-sample-data.cypher`

Or use the script:
```bash
bash scripts/setup-neo4j.sh
```

## Using the Plugin

### 1. Open Protégé
Launch Protégé desktop application with the plugin installed.

### 2. Load Ontology
Open the fraud detection ontology: `src/main/resources/ontology/fraud-detection-ontology.owl`

### 3. Access Neo4j Panel
- Navigate to `Window > Views > Neo4j Query Panel`
- The plugin panel will appear in the Protégé interface

### 4. Connect to Neo4j
- Click "Connect" in the Neo4j panel
- Verify connection status

### 5. Run Queries
- **Natural Language**: Type "Find all suspicious transactions" 
- **Cypher**: Enter raw Cypher queries
- View results in the Results Panel

## Development

### Running Tests
```bash
mvn test
```

### Debug Mode
To debug the plugin in Protégé:
```bash
# Set JAVA_OPTS before launching Protégé
export JAVA_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"
./Protege
```

Then attach your IDE debugger to port 5005.

## Configuration

Edit `src/main/resources/plugin.properties`:

```properties
# Neo4j Connection
neo4j.uri=bolt://localhost:7687
neo4j.username=neo4j
neo4j.password=password
neo4j.database=neo4j

# NLP/LLM Integration (optional)
llm.api.key=your_openai_key
llm.model=gpt-4
llm.endpoint=https://api.openai.com/v1/chat/completions
```

## Troubleshooting

### Plugin doesn't appear in Protégé
- Verify JAR is in correct plugins directory
- Check Protégé logs: `logs/protege.log`
- Ensure Java version compatibility (Java 11+)

### Cannot connect to Neo4j
- Verify Neo4j is running: `docker ps` or check Neo4j Browser
- Check connection credentials in `plugin.properties`
- Ensure port 7687 is not blocked by firewall

### Build fails
- Verify Maven version: `mvn --version`
- Ensure Java 11+ is installed: `java -version`
- Clean and rebuild: `mvn clean install`

## Technologies Used

- **Protégé API 5.5.0** - Ontology editor framework
- **Neo4j Java Driver 4.4.9** - Graph database connectivity
- **Apache Felix (OSGi)** - Plugin bundle management
- **SLF4J** - Logging

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit changes: `git commit -am 'Add new feature'`
4. Push to branch: `git push origin feature/my-feature`
5. Submit a Pull Request

## License

This project is licensed under the MIT License.

## Support

For issues and questions:
- Open an issue on GitHub
- Email: support@frauddetection.com
