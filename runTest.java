///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS junit:junit-dep:4.11
//DEPS org.assertj:assertj-core:3.20.2
//SOURCES dmn.java
//SOURCES feel.java
//SOURCES xls2dmn.java

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Request;
import org.junit.runner.Result;

import picocli.CommandLine;

public class runTest {

    private static final InputStream INIT_STDIN = System.in;
    private static final PrintStream INIT_STDOUT = System.out;
    private ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private PrintStream ps = new PrintStream(baos);

    public static void main(final String... args) {
        Result run = new JUnitCore().run(Request.aClass(runTest.class));
        run.getFailures().forEach(System.err::println);
        System.out.println(String.format("Tests run: %s, ignored: %s, failed: %s", run.getRunCount(), run.getIgnoreCount(), run.getFailureCount()));
        System.exit(run.getFailureCount());
    }

    @Test
    public void testDMNparameterNotApproved() {
        wireIO();
        final String JSON = "{\"FICO Score\":700,\"DTI Ratio\":0.1,\"PITI Ratio\":0.1}";
        int exitCode = new CommandLine(new dmn()).execute(List.of("tests/Loan_approvals.dmn", JSON).toArray(new String[]{}));
        resetIO();
        String out = baos.toString().trim();
        Assertions.assertThat(exitCode).isEqualTo(0);
        Assertions.assertThat(out).contains("\"Loan Approval\" : \"Not approved\"");
    }

    @Test
    public void testDMNparameterApproved() {
        wireIO();
        final String JSON = "{\"FICO Score\":765,\"DTI Ratio\":0.1,\"PITI Ratio\":0.1}";
        int exitCode = new CommandLine(new dmn()).execute(List.of("tests/Loan_approvals.dmn", JSON).toArray(new String[]{}));
        resetIO();
        String out = baos.toString().trim();
        Assertions.assertThat(exitCode).isEqualTo(0);
        Assertions.assertThat(out).contains("\"Loan Approval\" : \"Approved\"");
    }

    @Test
    public void testDMNstdinApproved() {
        final String JSON = "{\"FICO Score\":765,\"DTI Ratio\":0.1,\"PITI Ratio\":0.1}";
        wireIO(JSON);
        int exitCode = new CommandLine(new dmn()).execute(List.of("tests/Loan_approvals.dmn", JSON).toArray(new String[]{}));
        resetIO();
        String out = baos.toString().trim();
        Assertions.assertThat(exitCode).isEqualTo(0);
        Assertions.assertThat(out).contains("\"Loan Approval\" : \"Approved\"");
    }

    @Test
    public void testFEELfac4() {
        wireIO();
        final String EXPR = "{ Y: function(f)(function(x) x(x))(function(y) f(function(x) y(y)(x))), fac: Y(function(f) function(n) if n > 1 then n * f(n-1) else 1), fac4: fac(4) }.fac4";
        int exitCode = new CommandLine(new feel()).execute(List.of(EXPR).toArray(new String[]{}));
        resetIO();
        String out = baos.toString().trim();
        Assertions.assertThat(exitCode).isEqualTo(0);
        Assertions.assertThat(out).isEqualTo("24");
    }

    @Test
    public void testFEELfac4eq24() {
        wireIO();
        final String EXPR = "{ Y: function(f)(function(x) x(x))(function(y) f(function(x) y(y)(x))), fac: Y(function(f) function(n) if n > 1 then n * f(n-1) else 1), fac4: fac(4) }.fac4 = 24";
        int exitCode = new CommandLine(new feel()).execute(List.of(EXPR).toArray(new String[]{}));
        resetIO();
        String out = baos.toString().trim();
        Assertions.assertThat(exitCode).isEqualTo(0);
        Assertions.assertThat(out).isEqualTo("true");
    }

    @Test
    public void testXLS2DMN() {
        int exitCode1 = new CommandLine(new xls2dmn()).execute(List.of("tests/Loan_approvals.xlsx").toArray(new String[]{}));
        Assertions.assertThat(exitCode1).isEqualTo(0);
        wireIO();
        final String JSON = "{\"FICO Score\":765,\"DTI Ratio\":0.1,\"PITI Ratio\":0.1}";
        int exitCode2 = new CommandLine(new dmn()).execute(List.of("tests/Loan_approvals.xlsx.dmn", JSON).toArray(new String[]{})); // please notice using converted file.
        resetIO();
        String out = baos.toString().trim();
        Assertions.assertThat(exitCode2).isEqualTo(0);
        Assertions.assertThat(out).contains("\"Loan Approval\" : \"Approved\"");
    }

    private void wireIO() {
        wireIO(null);
    }

    private void wireIO(String in) {
        if (in != null) {
            System.setIn(new ByteArrayInputStream(in.getBytes()));
        }
        System.setOut(ps);
    }

    private void resetIO() {
        System.setIn(INIT_STDIN);
        System.setOut(INIT_STDOUT);
    }
}
