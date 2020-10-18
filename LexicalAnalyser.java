import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Collections;

public class LexicalAnalyser {

    public static List<Token> analyse(String sourceCode) throws LexicalException {
        //Turn the input String into a list of Tokens!
        if(sourceCode.equals("")){return null;}
        List<Token> collection = new ArrayList<Token>();
        //String line = "public class foo { public static void main(String[] args){ int i = 0; if (i == 2) { i = i + 1; System.out.println(\"Hi\"); } else { i = i * 2; } } }";

        String[] splt = sourceCode.split("((?<=(\\{|\\}|\\|\\||&&|<|>|!|=|\\+|\\*|-|%|/|\\)|\\(|;|\\s|\"|'))|(?=(\\{|\\}|\\|\\||&&|<|>|=|!|\\+|\\*|-|%|/|\\)|\\(|;|\\s|\"|')))");

        for(int i = 0; i < splt.length; i++){
            if(splt[i].equals(" ")) {
                // if the current element is a whitespace
                //i += 1;
            }else if(splt[i].equals("=") && i != splt.length - 1 && splt[i+1].equals("=")){
                // if the current element is an =, and follow by an = (i.e ==)
                i += 1;
                collection.add(tokenFromString("==").get());
            }else if(i != splt.length - 1 && i != 0 && splt[i+1].equals("'") && splt[i-1].equals("'")){
                collection.add(new Token(Token.TokenType.CHARLIT, splt[i]));
            }else if(i != splt.length - 1 && i != 0 && splt[i+1].equals("\"") && splt[i-1].equals("\"")){
                collection.add(new Token(Token.TokenType.STRINGLIT, splt[i]));
            }
            else{
                // other input
                Optional<Token> s = tokenFromString(splt[i]);
                if(s.isPresent()){
                    collection.add(s.get());
                }else{
                    throw new LexicalException("Invalid input: " + splt[i]);
                }
            }

        }
        return collection;
    }

    private static Optional<Token> tokenFromString(String t) {
        Optional<Token.TokenType> type = tokenTypeOf(t);
        if (type.isPresent())
            return Optional.of(new Token(type.get(), t));
        return Optional.empty();
    }

    private static Optional<Token.TokenType> tokenTypeOf(String t) {
        switch (t) {
            case "public":
                return Optional.of(Token.TokenType.PUBLIC);
            case "class":
                return Optional.of(Token.TokenType.CLASS);
            case "static":
                return Optional.of(Token.TokenType.STATIC);
            case "main":
                return Optional.of(Token.TokenType.MAIN);
            case "{":
                return Optional.of(Token.TokenType.LBRACE);
            case "void":
                return Optional.of(Token.TokenType.VOID);
            case "(":
                return Optional.of(Token.TokenType.LPAREN);
            case "String[]":
                return Optional.of(Token.TokenType.STRINGARR);
            case "args":
                return Optional.of(Token.TokenType.ARGS);
            case ")":
                return Optional.of(Token.TokenType.RPAREN);
            case "int":
            case "char":
            case "boolean":
                return Optional.of(Token.TokenType.TYPE);
            case "=":
                return Optional.of(Token.TokenType.ASSIGN);
            case ";":
                return Optional.of(Token.TokenType.SEMICOLON);
            case "if":
                return Optional.of(Token.TokenType.IF);
            case "for":
                return Optional.of(Token.TokenType.FOR);
            case "while":
                return Optional.of(Token.TokenType.WHILE);
            case "==":
                return Optional.of(Token.TokenType.EQUAL);
            case "+":
                return Optional.of(Token.TokenType.PLUS);
            case "-":
                return Optional.of(Token.TokenType.MINUS);
            case "*":
                return Optional.of(Token.TokenType.TIMES);
            case "/":
                return Optional.of(Token.TokenType.DIVIDE);
            case "%":
                return Optional.of(Token.TokenType.MOD);
            case "}":
                return Optional.of(Token.TokenType.RBRACE);
            case "else":
                return Optional.of(Token.TokenType.ELSE);
            case "System.out.println":
                return Optional.of(Token.TokenType.PRINT);
            case "||":
                return Optional.of(Token.TokenType.OR);
            case "&&":
                return Optional.of(Token.TokenType.AND);
            case "true":
                return Optional.of(Token.TokenType.TRUE);
            case "false":
                return Optional.of(Token.TokenType.FALSE);
            case "!=":
                return Optional.of(Token.TokenType.NEQUAL);
            case "<":
                return Optional.of(Token.TokenType.LT);
            case ">":
                return Optional.of(Token.TokenType.GT);
            case "<=":
                return Optional.of(Token.TokenType.LE);
            case ">=":
                return Optional.of(Token.TokenType.GE);
            case "\"":
                return Optional.of(Token.TokenType.DQUOTE);
            case "'":
                return Optional.of(Token.TokenType.SQUOTE);

        }

        if (t.matches("\\d+"))
            return Optional.of(Token.TokenType.NUM);
        if (Character.isAlphabetic(t.charAt(0)) && t.matches("[\\d|\\w]+")) {
            return Optional.of(Token.TokenType.ID);
        }
        return Optional.empty();
    }

}
