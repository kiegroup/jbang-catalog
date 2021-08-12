![build status](https://github.com/kiegroup/JBang-catalog/actions/workflows/build.yml/badge.svg)

# a KIE JBang-catalog

An experimental [JBang](https://www.jbang.dev/) catalog to quickly operate some KIE capabilities such as DMN and FEEL evaluation on the Command Line!

For more information on JBang, see:

- [Installation](https://www.jbang.dev/documentation/guide/latest/installation.html)
- [Usage](https://www.jbang.dev/documentation/guide/latest/usage.html)

# DMN

Use this JBang script to evaluate a [DMN model](https://drools.org/learn/dmn.html) using the Drools DMN Engine.

In action:

[![asciicast](https://asciinema.org/a/JSK2sBthe3N8Q6zjVGdvlYJKz.svg)](https://asciinema.org/a/JSK2sBthe3N8Q6zjVGdvlYJKz?autoplay=1&speed=2)

(click on the above image to see a recorded demo over at `asciinema` website)

The script take as input a DMN mode file and a DMN Context expressed as JSON; it produces a JSON of the evaluated DMN result context.

Usage help:

```bash
jbang dmn@kiegroup --help
```
