import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyntacticAnalyser {

    public static ParseTree parse(List<Token> tokens) throws SyntaxException {
        //Turn the List of Tokens into a ParseTree.
        TreeNode root = new TreeNode(TreeNode.Label.prog, null);
        ParseTree pTree = new ParseTree(root);
        Deque<Symbol> stack = new ArrayDeque<Symbol>(); // this one can contain TokenType and Label
        //Deque<Pair<Symbol,TreeNode>> stack = new ArrayDeque<>();
        stack.add(TreeNode.Label.prog);
        int i = 0;

        while (i < tokens.size()) {
            if (stack.peek().isVariable()) {
                if (stack.peek() == TreeNode.Label.prog && tokens.get(i).getType() == Token.TokenType.PUBLIC) {
                    // 1.0 <<prog>> -> public class <<ID>> { public static void main ( String[] args ) { <<los>> } }

                    stack.pop();
                    stack.add(Token.TokenType.RBRACE);
                    stack.add(Token.TokenType.RBRACE);
                    stack.add(TreeNode.Label.los);
                    stack.add(Token.TokenType.LBRACE);
                    stack.add(Token.TokenType.RPAREN);
                    stack.add(Token.TokenType.ARGS);
                    stack.add(Token.TokenType.STRINGARR);
                    stack.add(Token.TokenType.LPAREN);
                    stack.add(Token.TokenType.MAIN);
                    stack.add(Token.TokenType.VOID);
                    stack.add(Token.TokenType.STATIC);
                    stack.add(Token.TokenType.PUBLIC);
                    stack.add(Token.TokenType.LBRACE);
                    stack.add(Token.TokenType.ID);
                    stack.add(Token.TokenType.CLASS);
                    stack.add(Token.TokenType.PUBLIC);
                } else if (stack.peek() == TreeNode.Label.los &&
                        (tokens.get(i).getType() == Token.TokenType.SEMICOLON ||
                                tokens.get(i).getType() == Token.TokenType.TYPE ||
                                tokens.get(i).getType() == Token.TokenType.PRINT ||
                                tokens.get(i).getType() == Token.TokenType.WHILE ||
                                tokens.get(i).getType() == Token.TokenType.FOR ||
                                tokens.get(i).getType() == Token.TokenType.IF ||
                                tokens.get(i).getType() == Token.TokenType.ID
                        )) {
                    // 2.0 <<los>> -> <<stat>> <<los>>
                    stack.pop();
                    stack.add(TreeNode.Label.los);
                    stack.add(TreeNode.Label.stat);
                } else if (stack.peek() == TreeNode.Label.los && tokens.get(i).getType() == Token.TokenType.RBRACE) {
                    // 2.1 <<los>> -> e
                    stack.pop();
                    stack.add(TreeNode.Label.epsilon);
                } else if (stack.peek() == TreeNode.Label.los && tokens.get(i).getType() == Token.TokenType.WHILE) {
                    // 3.0
                    stack.pop();
                    stack.add(TreeNode.Label.whilestat);
                } else if (stack.peek() == TreeNode.Label.los && tokens.get(i).getType() == Token.TokenType.FOR) {
                    // 3.1
                    stack.pop();
                    stack.add(TreeNode.Label.forstat);
                } else if (stack.peek() == TreeNode.Label.los && tokens.get(i).getType() == Token.TokenType.IF) {
                    // 3.2
                    stack.pop();
                    stack.add(TreeNode.Label.ifstat);
                } else if (stack.peek() == TreeNode.Label.los && tokens.get(i).getType() == Token.TokenType.ID) {
                    // 3.3
                    stack.pop();
                    stack.add(Token.TokenType.SEMICOLON);
                    stack.add(TreeNode.Label.assign);
                } else if (stack.peek() == TreeNode.Label.los && tokens.get(i).getType() == Token.TokenType.TYPE) {
                    // 3.4
                    stack.pop();
                    stack.add(Token.TokenType.SEMICOLON);
                    stack.add(TreeNode.Label.decl);
                } else if (stack.peek() == TreeNode.Label.los && tokens.get(i).getType() == Token.TokenType.PRINT) {
                    // 3.5
                    stack.pop();
                    stack.add(Token.TokenType.SEMICOLON);
                    stack.add(TreeNode.Label.print);
                } else if (stack.peek() == TreeNode.Label.los && tokens.get(i).getType() == Token.TokenType.SEMICOLON) {
                    // 3.6
                    stack.pop();
                    stack.add(Token.TokenType.SEMICOLON);
                } else if (stack.peek() == TreeNode.Label.los && tokens.get(i).getType() == Token.TokenType.WHILE) {
                    // 4.0 <<while>> -> while ( <<rel expr>> <<bool expr>> ) { <<los>> }
                    stack.pop();
                    stack.add(Token.TokenType.RBRACE);
                    stack.add(TreeNode.Label.los);
                    stack.add(Token.TokenType.LBRACE);
                    stack.add(Token.TokenType.RPAREN);
                    stack.add(TreeNode.Label.boolexpr);
                    stack.add(TreeNode.Label.relexpr);
                    stack.add(Token.TokenType.LBRACE);
                    stack.add(Token.TokenType.WHILE);


                } else if (stack.peek() == TreeNode.Label.los && tokens.get(i).getType() == Token.TokenType.FOR) {
                    // 5.0 <<for>> -> for ( <<for start>> ; <<rel expr>> <<bool expr>> ; <<for arith>> ) { <<los>> }
                    stack.pop();
                    stack.add(Token.TokenType.RBRACE);
                    stack.add(TreeNode.Label.los);
                    stack.add(Token.TokenType.LBRACE);
                    stack.add(Token.TokenType.RPAREN);
                    stack.add(TreeNode.Label.forarith);
                    stack.add(Token.TokenType.SEMICOLON);
                    stack.add(TreeNode.Label.boolexpr);
                    stack.add(TreeNode.Label.relexpr);
                    stack.add(Token.TokenType.SEMICOLON);
                    stack.add(TreeNode.Label.forstart);
                    stack.add(Token.TokenType.LPAREN);
                    stack.add(Token.TokenType.FOR);

                } else if (stack.peek() == TreeNode.Label.forstart && tokens.get(i).getType() == Token.TokenType.TYPE) {
                    //6.0 <<for start>> → <<decl>>
                    stack.pop();
                    stack.add(TreeNode.Label.decl);
                } else if (stack.peek() == TreeNode.Label.forstart && tokens.get(i).getType() == Token.TokenType.ID) {
                    //6.1 <<for start>> → <<assign>>
                    stack.pop();
                    stack.add(TreeNode.Label.assign);
                } else if (stack.peek() == TreeNode.Label.forstart && tokens.get(i).getType() == Token.TokenType.SEMICOLON) {
                    //6.2 <<for start>> → e
                    stack.pop();
                    stack.add(TreeNode.Label.epsilon);
                } else if (stack.peek() == TreeNode.Label.forarith && (tokens.get(i).getType() == Token.TokenType.LPAREN
                        || tokens.get(i).getType() == Token.TokenType.ID || tokens.get(i).getType() == Token.TokenType.NUM)) {
                    //7.0 <<for arith>> → <<arith expr>>
                    stack.pop();
                    stack.add(TreeNode.Label.arithexpr);
                } else if (stack.peek() == TreeNode.Label.forarith && tokens.get(i).getType() == Token.TokenType.RPAREN) {
                    //7.1 <<for arith>> -> e
                    stack.pop();
                    stack.add(TreeNode.Label.epsilon);
                } else if (stack.peek() == TreeNode.Label.ifstat && tokens.get(i).getType() == Token.TokenType.IF) {
                    //8.0 <<if>> → if ( <<rel expr>> <<bool expr>> ) { <<los>> } <<else if>>
                    stack.pop();
                    stack.add(TreeNode.Label.elseifstat);
                    stack.add(Token.TokenType.RBRACE);
                    stack.add(TreeNode.Label.los);
                    stack.add(Token.TokenType.LBRACE);
                    stack.add(Token.TokenType.RPAREN);
                    stack.add(TreeNode.Label.boolexpr);
                    stack.add(TreeNode.Label.relexpr);
                    stack.add(Token.TokenType.LPAREN);
                    stack.add(Token.TokenType.IF);
                } else if (stack.peek() == TreeNode.Label.elseifstat && tokens.get(i).getType() == Token.TokenType.ELSE) {
                    //9.0 <<else if>> → <<else?if>> { <<los>> } <<else if>>
                    stack.pop();
                    stack.add(TreeNode.Label.elseifstat);
                    stack.add(Token.TokenType.RBRACE);
                    stack.add(TreeNode.Label.los);
                    stack.add(Token.TokenType.LBRACE);
                    stack.add(TreeNode.Label.elseorelseif);
                } else if (stack.peek() == TreeNode.Label.elseifstat && (tokens.get(i).getType() == Token.TokenType.RBRACE
                        || tokens.get(i).getType() == Token.TokenType.SEMICOLON || tokens.get(i).getType() == Token.TokenType.TYPE
                        || tokens.get(i).getType() == Token.TokenType.PRINT || tokens.get(i).getType() == Token.TokenType.WHILE
                        || tokens.get(i).getType() == Token.TokenType.FOR || tokens.get(i).getType() == Token.TokenType.IF
                        || tokens.get(i).getType() == Token.TokenType.ID)) {
                    //9.1 <<else if>> -> e
                    stack.pop();
                    stack.add(TreeNode.Label.epsilon);
                } else if (stack.peek() == TreeNode.Label.elseorelseif && tokens.get(i).getType() == Token.TokenType.ELSE) {
                    //10.0 <<else?if>> → else <<poss if>>
                    stack.pop();
                    stack.add(TreeNode.Label.possif);
                    stack.add(Token.TokenType.ELSE);
                } else if (stack.peek() == TreeNode.Label.possif && tokens.get(i).getType() == Token.TokenType.IF) {
                    //11.0 <<poss if>> → if ( <<rel expr>> <<bool expr>> )
                    stack.pop();
                    stack.add(Token.TokenType.RPAREN);
                    stack.add(TreeNode.Label.boolexpr);
                    stack.add(TreeNode.Label.relexpr);
                    stack.add(Token.TokenType.LPAREN);
                    stack.add(Token.TokenType.IF);
                } else if (stack.peek() == TreeNode.Label.possif && tokens.get(i).getType() == Token.TokenType.LBRACE) {
                    //11.1 <<poss if>> -> e
                    stack.pop();
                    stack.add(TreeNode.Label.epsilon);
                } else if (stack.peek() == TreeNode.Label.assign && tokens.get(i).getType() == Token.TokenType.ID) {
                    //12.0 <<assign>> → <<ID>> = <<expr>>
                    stack.pop();
                    stack.add(TreeNode.Label.expr);
                    stack.add(Token.TokenType.ASSIGN);
                    stack.add(Token.TokenType.ID);
                } else if (stack.peek() == TreeNode.Label.decl && tokens.get(i).getType() == Token.TokenType.TYPE) {
                    //13.0 <<decl>> → <<type>> <<ID>> <<poss assign>>
                    stack.pop();
                    stack.add(TreeNode.Label.possassign);
                    stack.add(Token.TokenType.ID);
                    stack.add(TreeNode.Label.type);
                } else if (stack.peek() == TreeNode.Label.possassign && tokens.get(i).getType() == Token.TokenType.ASSIGN) {
                    //14.0 <<poss assign>> → = <<expr>>
                    stack.pop();
                    stack.add(TreeNode.Label.expr);
                    stack.add(Token.TokenType.ASSIGN);
                } else if (stack.peek() == TreeNode.Label.possassign && tokens.get(i).getType() == Token.TokenType.SEMICOLON) {
                    //14.1 <<poss assign>> → e
                    stack.pop();
                    stack.add(TreeNode.Label.epsilon);
                } else if (stack.peek() == TreeNode.Label.print && tokens.get(i).getType() == Token.TokenType.PRINT) {
                    //15.0 <<print>> → System.out.println ( <<print expr>> )
                    stack.pop();
                    stack.add(Token.TokenType.RPAREN);
                    stack.add(TreeNode.Label.printexpr);
                    stack.add(Token.TokenType.LPAREN);
                    stack.add(Token.TokenType.PRINT);
                } else if (stack.peek() == TreeNode.Label.type && tokens.get(i).getValue().get().equals("int")) {
                    //16.0 <<type>> → int
                    stack.pop();
                    stack.add(Token.TokenType.TYPE);
                } else if (stack.peek() == TreeNode.Label.type && tokens.get(i).getValue().get().equals("boolean")) {
                    //16.1 <<type>> → boolean
                    stack.pop();
                    stack.add(Token.TokenType.TYPE);
                } else if (stack.peek() == TreeNode.Label.type && tokens.get(i).getValue().get().equals("int")) {
                    //16.2 <<type>> → char
                    stack.pop();
                    stack.add(Token.TokenType.TYPE);
                } else if (stack.peek() == TreeNode.Label.expr && (tokens.get(i).getType() == Token.TokenType.TRUE
                        || tokens.get(i).getType() == Token.TokenType.LPAREN
                        || tokens.get(i).getType() == Token.TokenType.FALSE
                        || tokens.get(i).getType() == Token.TokenType.ID
                        || tokens.get(i).getType() == Token.TokenType.NUM)) {
                    //17 <<expr>> → <<rel expr>> <<bool expr>>
                    stack.pop();
                    stack.add(TreeNode.Label.boolexpr);
                    stack.add(TreeNode.Label.relexpr);
                } else if (stack.peek() == TreeNode.Label.expr && tokens.get(i).getType() == Token.TokenType.SQUOTE) {
                    //17.1 <<expr>> → <<char expr>>
                    stack.pop();
                    stack.add(TreeNode.Label.charexpr);
                } else if (stack.peek() == TreeNode.Label.charexpr && tokens.get(i).getType() == Token.TokenType.SQUOTE) {
                    //18 <<char expr>> → ' <<char>> '
                    stack.pop();
                    stack.add(Token.TokenType.SQUOTE);
                    stack.add(Token.TokenType.CHARLIT);
                    stack.add(Token.TokenType.SQUOTE);
                } else if (stack.peek() == TreeNode.Label.boolexpr && (
                        tokens.get(i).getType() == Token.TokenType.EQUAL
                                || tokens.get(i).getType() == Token.TokenType.NEQUAL
                                || tokens.get(i).getType() == Token.TokenType.AND
                                || tokens.get(i).getType() == Token.TokenType.OR
                )) {
                    //19 <<bool expr>> → <<bool op>> <<rel expr>> <<bool expr>>
                    stack.pop();
                    stack.add(TreeNode.Label.boolexpr);
                    stack.add(TreeNode.Label.relexpr);
                    stack.add(TreeNode.Label.boolop);
                } else if (stack.peek() == TreeNode.Label.boolexpr && (tokens.get(i).getType() == Token.TokenType.RPAREN
                        || tokens.get(i).getType() == Token.TokenType.SEMICOLON)) {
                    //19.1 <<bool expr>> → ε
                    stack.pop();
                    stack.add(TreeNode.Label.epsilon);
                } else if (stack.peek() == TreeNode.Label.boolop && (tokens.get(i).getType() == Token.TokenType.EQUAL
                        || tokens.get(i).getType() == Token.TokenType.NEQUAL)) {
                    //20.0 <<bool op>> → <<bool eq>>
                    stack.pop();
                    stack.add(TreeNode.Label.booleq);
                } else if (stack.peek() == TreeNode.Label.boolop && (tokens.get(i).getType() == Token.TokenType.AND
                        || tokens.get(i).getType() == Token.TokenType.OR)) {
                    //20.1 <<bool op>> →  <<bool log>>
                    stack.pop();
                    stack.add(TreeNode.Label.boollog);
                } else if (stack.peek() == TreeNode.Label.booleq && tokens.get(i).getType() == Token.TokenType.EQUAL) {
                    //21.0 <<bool eq>> → ==
                    stack.pop();
                    stack.add(Token.TokenType.EQUAL);
                } else if (stack.peek() == TreeNode.Label.booleq && tokens.get(i).getType() == Token.TokenType.NEQUAL) {
                    //21.1 <<bool eq>> → !=
                    stack.pop();
                    stack.add(Token.TokenType.NEQUAL);
                } else if (stack.peek() == TreeNode.Label.boollog && tokens.get(i).getType() == Token.TokenType.AND) {
                    //22.0 <<bool log>> → &&
                    stack.pop();
                    stack.add(Token.TokenType.AND);
                } else if (stack.peek() == TreeNode.Label.boollog && tokens.get(i).getType() == Token.TokenType.OR) {
                    //22.1 <<bool log>> → ||
                    stack.pop();
                    stack.add(Token.TokenType.OR);
                } else if (stack.peek() == TreeNode.Label.relexpr && (tokens.get(i).getType() == Token.TokenType.LPAREN
                        || tokens.get(i).getType() == Token.TokenType.ID
                        || tokens.get(i).getType() == Token.TokenType.NUM)) {
                    //23.0 <<rel expr>> → <<arith expr>> <<rel expr'>>
                    stack.pop();
                    stack.add(TreeNode.Label.relexprprime);
                    stack.add(TreeNode.Label.arithexpr);
                } else if (stack.peek() == TreeNode.Label.relexpr && tokens.get(i).getType() == Token.TokenType.TRUE) {
                    //23.1 <<rel expr>> → true
                    stack.pop();
                    stack.add(Token.TokenType.TRUE);
                } else if (stack.peek() == TreeNode.Label.relexpr && tokens.get(i).getType() == Token.TokenType.FALSE) {
                    //23.2 <<rel expr>> → false
                    stack.pop();
                    stack.add(Token.TokenType.FALSE);
                } else if (stack.peek() == TreeNode.Label.relexprprime && (tokens.get(i).getType() == Token.TokenType.LT
                        || tokens.get(i).getType() == Token.TokenType.GT
                        || tokens.get(i).getType() == Token.TokenType.LE
                        || tokens.get(i).getType() == Token.TokenType.GE)) {
                    //24.0 <<rel expr'>> → <<rel op>> <<arith expr>>
                    stack.pop();
                    stack.add(TreeNode.Label.arithexpr);
                    stack.add(TreeNode.Label.relop);
                } else if (stack.peek() == TreeNode.Label.relexprprime && (tokens.get(i).getType() == Token.TokenType.EQUAL
                        || tokens.get(i).getType() == Token.TokenType.NEQUAL
                        || tokens.get(i).getType() == Token.TokenType.RPAREN
                        || tokens.get(i).getType() == Token.TokenType.AND
                        || tokens.get(i).getType() == Token.TokenType.OR
                        || tokens.get(i).getType() == Token.TokenType.SEMICOLON)) {
                    //24.1 <<rel expr'>> → ε
                    stack.pop();
                    stack.add(TreeNode.Label.epsilon);
                } else if (stack.peek() == TreeNode.Label.relop && tokens.get(i).getType() == Token.TokenType.LT) {
                    //25.0 <<rel op>> → <
                    stack.pop();
                    stack.add(Token.TokenType.LT);
                } else if (stack.peek() == TreeNode.Label.relop && tokens.get(i).getType() == Token.TokenType.LE) {
                    //25.1 <<rel op>> →  <=
                    stack.pop();
                    stack.add(Token.TokenType.LE);
                } else if (stack.peek() == TreeNode.Label.relop && tokens.get(i).getType() == Token.TokenType.GT) {
                    //25.2 <<rel op>> →  >
                    stack.pop();
                    stack.add(Token.TokenType.GT);
                } else if (stack.peek() == TreeNode.Label.relop && tokens.get(i).getType() == Token.TokenType.GE) {
                    //25.3 <<rel op>> → >=
                    stack.pop();
                    stack.add(Token.TokenType.GE);
                } else if (stack.peek() == TreeNode.Label.arithexpr && (tokens.get(i).getType() == Token.TokenType.LPAREN
                        || tokens.get(i).getType() == Token.TokenType.NUM
                        || tokens.get(i).getType() == Token.TokenType.ID)) {
                    //26 <<arith expr>> → <<term>> <<arith expr'>>
                    stack.pop();
                    stack.add(TreeNode.Label.arithexprprime);
                    stack.add(TreeNode.Label.term);
                } else if (stack.peek() == TreeNode.Label.arithexprprime && tokens.get(i).getType() == Token.TokenType.PLUS) {
                    //27.0 <<arith expr'>> → + <<term>> <<arith expr'>>
                    stack.pop();
                    stack.add(TreeNode.Label.arithexprprime);
                    stack.add(TreeNode.Label.term);
                    stack.add(Token.TokenType.PLUS);
                } else if (stack.peek() == TreeNode.Label.arithexprprime && tokens.get(i).getType() == Token.TokenType.MINUS) {
                    //27.1 <<arith expr'>> → - <<term>> <<arith expr'>> | ε
                    stack.pop();
                    stack.add(TreeNode.Label.arithexprprime);
                    stack.add(TreeNode.Label.term);
                    stack.add(Token.TokenType.MINUS);
                } else if (stack.peek() == TreeNode.Label.arithexprprime && (tokens.get(i).getType() == Token.TokenType.EQUAL
                        || tokens.get(i).getType() == Token.TokenType.NEQUAL
                        || tokens.get(i).getType() == Token.TokenType.LT
                        || tokens.get(i).getType() == Token.TokenType.GT
                        || tokens.get(i).getType() == Token.TokenType.LE
                        || tokens.get(i).getType() == Token.TokenType.GE
                        || tokens.get(i).getType() == Token.TokenType.RPAREN
                        || tokens.get(i).getType() == Token.TokenType.AND
                        || tokens.get(i).getType() == Token.TokenType.OR
                        || tokens.get(i).getType() == Token.TokenType.SEMICOLON)) {
                    //27.2 <<arith expr'>> → ε
                    stack.pop();
                    stack.add(TreeNode.Label.epsilon);
                } else if (stack.peek() == TreeNode.Label.term && (tokens.get(i).getType() == Token.TokenType.LPAREN
                        || tokens.get(i).getType() == Token.TokenType.ID
                        || tokens.get(i).getType() == Token.TokenType.NUM)) {
                    //28.0 <<term>> → <<factor>> <<term'>>
                    stack.pop();
                    stack.add(TreeNode.Label.termprime);
                    stack.add(TreeNode.Label.factor);
                } else if (stack.peek() == TreeNode.Label.termprime && tokens.get(i).getType() == Token.TokenType.TIMES) {
                    //29.0 <<term'>> → * <<factor>> <<term'>>
                    stack.pop();
                    stack.add(TreeNode.Label.termprime);
                    stack.add(TreeNode.Label.factor);
                    stack.add(Token.TokenType.TIMES);
                } else if (stack.peek() == TreeNode.Label.termprime && tokens.get(i).getType() == Token.TokenType.DIVIDE) {
                    //29.1 <<term'>> → / <<factor>> <<term'>>
                    stack.pop();
                    stack.add(TreeNode.Label.termprime);
                    stack.add(TreeNode.Label.factor);
                    stack.add(Token.TokenType.DIVIDE);
                } else if (stack.peek() == TreeNode.Label.termprime && tokens.get(i).getType() == Token.TokenType.MOD) {
                    //29.2 <<term'>> → % <<factor>> <<term'>>
                    stack.pop();
                    stack.add(TreeNode.Label.termprime);
                    stack.add(TreeNode.Label.factor);
                    stack.add(Token.TokenType.MOD);
                } else if (stack.peek() == TreeNode.Label.termprime && (
                        tokens.get(i).getType() == Token.TokenType.PLUS
                        || tokens.get(i).getType() == Token.TokenType.MINUS
                        || tokens.get(i).getType() == Token.TokenType.EQUAL
                        || tokens.get(i).getType() == Token.TokenType.NEQUAL
                        || tokens.get(i).getType() == Token.TokenType.LT
                        || tokens.get(i).getType() == Token.TokenType.GT
                        || tokens.get(i).getType() == Token.TokenType.LE
                        || tokens.get(i).getType() == Token.TokenType.GE
                        || tokens.get(i).getType() == Token.TokenType.RPAREN
                        || tokens.get(i).getType() == Token.TokenType.AND
                        || tokens.get(i).getType() == Token.TokenType.OR
                        || tokens.get(i).getType() == Token.TokenType.SEMICOLON)) {
                    //29.3 <<term'>> → ε
                    stack.pop();
                    stack.add(TreeNode.Label.epsilon);
                } else if (stack.peek() == TreeNode.Label.factor && tokens.get(i).getType() == Token.TokenType.LPAREN) {
                    //30.0 <<factor>> → ( <<arith expr>> )
                    stack.pop();
                    stack.add(Token.TokenType.RPAREN);
                    stack.add(TreeNode.Label.arithexpr);
                    stack.add(Token.TokenType.LPAREN);
                } else if (stack.peek() == TreeNode.Label.factor && tokens.get(i).getType() == Token.TokenType.ID) {
                    //30.1 <<factor>> → <<ID>>
                    stack.pop();
                    stack.add(Token.TokenType.ID);
                } else if (stack.peek() == TreeNode.Label.factor && tokens.get(i).getType() == Token.TokenType.NUM) {
                    //30.2 <<factor>> → <<num>>
                    stack.pop();
                    stack.add(Token.TokenType.NUM);
                } else if (stack.peek() == TreeNode.Label.printexpr &&
                        (tokens.get(i).getType() == Token.TokenType.LPAREN
                        || tokens.get(i).getType() == Token.TokenType.TRUE
                        || tokens.get(i).getType() == Token.TokenType.FALSE
                        || tokens.get(i).getType() == Token.TokenType.ID
                        || tokens.get(i).getType() == Token.TokenType.NUM)) {
                    //31.0 <<print expr>> → <<rel expr>> <<bool expr>>
                    stack.pop();
                    stack.add(TreeNode.Label.boolexpr);
                    stack.add(TreeNode.Label.relexpr);
                } else if (stack.peek() == TreeNode.Label.printexpr && tokens.get(i).getType() == Token.TokenType.DQUOTE) {
                    //31.1 <<print expr>> →"<<string lit>> "
                    stack.pop();
                    stack.add(Token.TokenType.DQUOTE);
                    stack.add(Token.TokenType.STRINGLIT);
                    stack.add(Token.TokenType.DQUOTE);
                }else{
                    throw new SyntaxException("No such rule");
                }
            } else if(stack.peek() == tokens.get(i).getType()){
                i++;
                stack.pop();
                // Create
            }
        }


        return pTree;
    }
}