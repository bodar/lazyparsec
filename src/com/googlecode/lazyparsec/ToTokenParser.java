package com.googlecode.lazyparsec;

/**
 * Converts the current return value as a {@link Token} with starting index and length.
 *
 * @author Ben Yu
 */
final class ToTokenParser extends Parser<Token> {
    private final Parser<?> parser;

    ToTokenParser(Parser<?> parser) {
        this.parser = parser;
    }

    @Override
    boolean apply(ParseContext context) throws Exception {
        int begin = context.getIndex();
        if (!parser.apply(context)) {
            return false;
        }
        int len = context.getIndex() - begin;
        Token token = new Token(begin, len, context.result);
        context.result = token;
        return true;
    }

    @Override
    public String toString() {
        return parser.toString();
    }
}
