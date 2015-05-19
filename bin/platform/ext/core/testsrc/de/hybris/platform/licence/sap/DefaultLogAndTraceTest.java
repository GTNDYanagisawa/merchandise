package de.hybris.platform.licence.sap;

import org.apache.log4j.Level;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;


public class DefaultLogAndTraceTest
{
    // Setting log and trace to work on DEBUG level for testing purposes
    private final DefaultLogAndTrace logAndTrace = new DefaultLogAndTrace(Level.DEBUG, Level.DEBUG);


	@Test
	public void shouldReturnTraceMessagesAndClearContent() throws Exception
	{
        // given
        final String message1 = "Foo Bar ";
        final String message2 = "Baz Boom";

        // when
        logAndTrace.writeTrace(message1);
        logAndTrace.writeTrace(message2);

        // then
        assertThat(logAndTrace.getAndClearTraceMessages()).isEqualTo("Foo Bar Baz Boom");
        assertThat(logAndTrace.getAndClearTraceMessages()).isNull();
	}

	@Test
	public void shouldReturnErrorMessagesAndClearContent() throws Exception
	{
        // given
        final String message1 = "Foo Bar ";
        final String message2 = "Baz Boom";

        // when
        logAndTrace.writeError(message1);
        logAndTrace.writeError(message2);

        // then
        assertThat(logAndTrace.getAndClearErrorMessages()).isEqualTo("Foo Bar Baz Boom");
        assertThat(logAndTrace.getAndClearErrorMessages()).isNull();
	}
}
