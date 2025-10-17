package org.deegree.featureinfo.templating;

import org.antlr.v4.runtime.*;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class Templating2ParserTestTNG {

	@Test
	public void testLexer() throws IOException {
		String name = "utahdemo.gfi";
		InputStream inputStream = Templating2ParserTestTNG.class.getResourceAsStream(name);
		assertNotNull(inputStream);
		CharStream input = CharStreams.fromStream(inputStream);
		Templating2LexerTNG lexer = new Templating2LexerTNG(input);

		CommonTokenStream cts = new CommonTokenStream(lexer);
		cts.fill();

		Vocabulary vocab = lexer.getVocabulary();

		List<Token> tokens = cts.getTokens(); // includes EOF and hidden-channel tokens
		for (int i = 0; i < tokens.size(); i++) {
			Token t = tokens.get(i);
			int type = t.getType();

			String symbolicName = vocab.getSymbolicName(type);
			if (symbolicName == null)
				symbolicName = vocab.getLiteralName(type);
			if (symbolicName == null)
				symbolicName = Integer.toString(type); // fallback (EOF is -1)

			String text = t.getText();
			if (text != null) {
				text = text.replace("\n", "\\n");
			}

			System.out.printf("%d: %-25s text=%s line=%d col=%d channel=%d%n", i, symbolicName, text, t.getLine(),
					t.getCharPositionInLine(), t.getChannel());
		}
	}

}
