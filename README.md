# Main
A repository for code with a (command line) user interface

All commands and all dependencies are packaged with a single jar file, call `JAR_FILE` in this documentation. 

This package provides the following commands:

## TEI2XMI
Converts TEI files (from textgrid repository) to UIMA XMI. They are not directly made for viewing, but can be inspected if necessary.

```bash
$ java -cp JAR_FILE de.unistuttgart.ims.drama.Main.TEI2XMI --inputDirectory INPUT --outputDirectory OUTPUT
```

