![build status](https://github.com/kiegroup/JBang-catalog/actions/workflows/build.yml/badge.svg)

# a KIE JBang-catalog

An experimental [JBang](https://www.jbang.dev/) catalog to quickly operate some KIE capabilities such as DMN and FEEL evaluation on the Command Line!

For more information on JBang, see:

- [Installation](https://www.jbang.dev/documentation/guide/latest/installation.html)
- [Usage](https://www.jbang.dev/documentation/guide/latest/usage.html)

# DMN

Use this JBang script to evaluate a [DMN model](https://drools.org/learn/dmn.html) using the Drools DMN Engine.

In action:

[![asciicast](https://asciinema.org/a/433150.svg)](https://asciinema.org/a/433150?autoplay=1&speed=2)

(click on the above image to see a recorded demo over at `asciinema` website)

The script takes as input a DMN mode file and a DMN Context expressed as JSON; it produces a JSON of the evaluated DMN result context.

Usage help:

```bash
jbang dmn@kiegroup --help
```

# Converter for Excel (.xls/.xlsx) files containing DMN decision tables

Use this JBang script to convert Excel (.xls/.xlsx) files containing [DMN decision tables](https://drools.org/learn/dmn.html) using the Drools DMN Engine experimental converter.

For the details about the Converter and the conventions to be used in the Excel file, please reference the [Converter documentation](https://github.com/kiegroup/drools/tree/main/kie-dmn/kie-dmn-xls2dmn-cli#readme).

In action:

[![asciicast](https://asciinema.org/a/433167.svg)](https://asciinema.org/a/433167?autoplay=1&speed=2)

(click on the above image to see a recorded demo over at `asciinema` website)

The script takes as input the filename of the Excel (.xls/.xlsx) file to convert.

Usage help:

```bash
jbang xls2dmn@kiegroup --help
```

# FEEL

Use this JBang script to evaluate a [FEEL expression](https://drools.org/learn/dmn.html) using the Drools DMN Engine.

In action:

[![asciicast](https://asciinema.org/a/433154.svg)](https://asciinema.org/a/433154?autoplay=1&speed=2)

(click on the above image to see a recorded demo over at `asciinema` website)

The script takes as input a FEEL expression (as a string) and it produces a FEEL representation of the result of evaluating the expression.

Usage help:

```bash
jbang feel@kiegroup --help
```
