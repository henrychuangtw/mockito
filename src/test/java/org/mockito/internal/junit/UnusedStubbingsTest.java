package org.mockito.internal.junit;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.mockito.internal.invocation.InvocationBuilder;
import org.mockito.internal.stubbing.StubbedInvocationMatcher;
import org.mockito.internal.stubbing.answers.DoesNothing;
import org.mockito.internal.util.SimpleMockitoLogger;
import org.mockitoutil.TestBase;

import java.util.Collections;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class UnusedStubbingsTest extends TestBase {

    SimpleMockitoLogger logger = new SimpleMockitoLogger();

    @Test
    public void no_unused_stubbings() throws Exception {
        //given
        UnusedStubbings stubbings = new UnusedStubbings(Collections.emptyList());

        //when
        stubbings.format("MyTest.myTestMethod", logger);

        //then
        assertEquals("", logger.getLoggedInfo());
    }

    @Test
    public void unused_stubbings() throws Exception {
        //given
        UnusedStubbings stubbings = new UnusedStubbings(asList(
            new StubbedInvocationMatcher(new InvocationBuilder().toInvocationMatcher(), new DoesNothing()),
            new StubbedInvocationMatcher(new InvocationBuilder().toInvocationMatcher(), new DoesNothing())
        ));


        //when
        stubbings.format("MyTest.myTestMethod", logger);

        //then
        String[] message = filterLineNo(logger.getLoggedInfo()).split("\n");
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(message[0]).isEqualTo("[MockitoHint] MyTest.myTestMethod (see javadoc for MockitoHint):");
            softly.assertThat(message[1]).matches("\\[MockitoHint\\] 1\\. Unused \\-\\> at [[\\w\\.]+/]*[\\w\\.]+\\.reflect\\.NativeMethodAccessorImpl\\.invoke0\\(.*Native Method\\)");
            softly.assertThat(message[2]).matches("\\[MockitoHint\\] 2\\. Unused \\-\\> at [[\\w\\.]+/]*[\\w\\.]+\\.reflect\\.NativeMethodAccessorImpl\\.invoke0\\(.*Native Method\\)");
        });
    }
}
