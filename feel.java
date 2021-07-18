///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 11+
//DEPS org.kie:kie-dmn-feel:RELEASE
//DEPS info.picocli:picocli:4.2.0
//DEPS org.slf4j:slf4j-simple:1.7.30

import java.util.List;
import java.util.concurrent.Callable;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEventListener;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.parser.feel11.profiles.KieExtendedFEELProfile;
import org.kie.dmn.feel.runtime.functions.extended.CodeFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "feel",
    description = {"Evaluate a FEEL expression using the Drools DMN Engine.",
                "The result of the evaluation is emitted as a FEEL representation on STDOUT."},
    footer = "See also: https://drools.org/learn/dmn.html",
    mixinStandardHelpOptions = true)
public class feel implements Callable<Integer> {

    @Parameters(
        index = "0",
        description = "The FEEL expression to evaluate.",
        arity = "1")
    private String expression;
    private FEEL feel;

    public feel() {
        feel = FEEL.newInstance(List.of(new KieExtendedFEELProfile()));
        feel.addListener(new CliFeelListener());
    }

    public static void main(String[] args) throws Exception {
        int exitCode = new CommandLine(new feel()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        try {
            var evaluationResult = feel.evaluate(expression);
            var asFEELCode = new CodeFunction().invoke(evaluationResult);
            if (asFEELCode.isRight()) {
                System.out.println(asFEELCode.getOrElseThrow(e->new RuntimeException(e.toString())));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
        return 0;
    }
}

class CliFeelListener implements FEELEventListener {

    private static final Logger LOG = LoggerFactory.getLogger(CliFeelListener.class);

    @Override
    public void onEvent(FEELEvent event) {
        switch(event.getSeverity()) {
            case ERROR:
                LOG.error("{}", event.getMessage());
                break;
            case WARN:
                LOG.warn("{}", event.getMessage());
                break;  
            case INFO:
                LOG.info("{}", event.getMessage());
                break;
            case TRACE:
            default:
                // do nothing.
                break;

        }
    }

}