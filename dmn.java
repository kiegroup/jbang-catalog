///usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 11+
//DEPS org.kie:kie-dmn-core:RELEASE
//DEPS info.picocli:picocli:4.2.0
//DEPS com.fasterxml.jackson.core:jackson-databind:2.10.5.1
//DEPS org.slf4j:slf4j-simple:1.7.30

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Callable;

import com.fasterxml.jackson.databind.json.JsonMapper;

import org.drools.core.io.impl.FileSystemResource;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.kie.dmn.core.internal.utils.DynamicDMNContextBuilder;
import org.kie.dmn.core.internal.utils.MarshallingStubUtils;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "dmn", mixinStandardHelpOptions = true)
public class dmn implements Callable<Integer> {

    @Parameters(
        index = "0",
        description = "The DMN model file to evaluate.",
        arity = "1")
    private File dmnModel;

    @Parameters(
        index = "1",
        description = "The DMN Context as JSON, for evaluation (InputData variables). If left empty, will read from STDIN.",
        arity = "0..1"
    )
    private String context;

    public static void main(String[] args) throws Exception {
        int exitCode = new CommandLine(new dmn()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        try {
            if (context == null) {
                var scanner = new Scanner(System.in).useDelimiter("\\A");
                context = "{}";
                if (scanner.hasNext()) {
                    context = scanner.next();
                }
            }
            DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults()
                .buildConfiguration()
                .fromResources(List.of(new FileSystemResource(dmnModel)))
                .getOrElseThrow(RuntimeException::new);
            @SuppressWarnings("unchecked")
            Map<String, Object> readValue = JsonMapper.builder().build().readValue(readInputContext(), Map.class);
            DMNContext dmnContext = new DynamicDMNContextBuilder(dmnRuntime.newContext(), dmnRuntime.getModels().get(0))
                .populateContextWith(readValue);
            DMNResult dmnResult = dmnRuntime.evaluateAll(dmnRuntime.getModels().get(0), dmnContext);
            final Object serialized = MarshallingStubUtils.stubDMNResult(dmnResult.getContext().getAll(), Object::toString);
            System.out.println(JsonMapper.builder().build().writerWithDefaultPrettyPrinter().writeValueAsString(serialized));
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
        return 0;
    }

    private String readInputContext() {

        String wholeContext;

        if (context != null) {
            wholeContext = String.join(" ", context);
        } else {
            var scanner = new Scanner(System.in).useDelimiter("\\A");
            wholeContext = "";
            if (scanner.hasNext()) {
                wholeContext = scanner.next();
            }
        }

        return wholeContext.isEmpty() ? " {} ": wholeContext;
    }
}
