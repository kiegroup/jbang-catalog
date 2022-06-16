///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 11+
//DEPS org.kie:kie-dmn-xls2dmn-cli:7.71.0.Final
//DEPS info.picocli:picocli:4.2.0
//DEPS com.fasterxml.jackson.core:jackson-databind:2.10.5.1
//DEPS org.slf4j:slf4j-simple:1.7.30

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "xls2dmn",
    description = {"JBang script proxy of the Converter for Excel (.xls/.xlsx) file containing DMN decision tables (Experimental)",
                "This can be an helpful utility to convert spreadsheet file into DMN decision table automatically."},
    footer = "See also: https://drools.org/learn/dmn.html \nSee also:\nhttps://github.com/kiegroup/drools/tree/main/kie-dmn/kie-dmn-xls2dmn-cli#readme",
    mixinStandardHelpOptions = true)
public class xls2dmn extends org.kie.dmn.xls2dmn.cli.App {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new xls2dmn()).execute(args);
        System.exit(exitCode);
    }
}
