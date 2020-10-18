import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyntacticAnalyser {

    public static ParseTree parse(List<Token> tokens) throws SyntaxException {
        //Turn the List of Tokens into a ParseTree.
        
        ParseTree pTree = new ParseTree();
        //Deque<Symbol> stack = new ArrayDeque<Symbol>(); // this one can contain TokenType and Label
        Deque<Pair<Symbol,TreeNode>> stack = new ArrayDeque<>();
        stack.add(new Pair(TreeNode.Label.prog, null));
        int i = 0;

        while (i < tokens.size()) {
            if (stack.peek().fst().isVariable()) {
                if (stack.peek().fst() == TreeNode.Label.prog && tokens.get(i).getType() == Token.TokenType.PUBLIC) {
                    // 1.0 <<prog>> -> public class <<ID>> { public static void main ( String[] args ) { <<los>> } }
                	TreeNode current = new TreeNode(TreeNode.Label.prog, null);
                	pTree.setRoot(current);
                    stack.pop();
                    stack.add(new Pair(Token.TokenType.RBRACE, current));
                    stack.add(new Pair(Token.TokenType.RBRACE, current));
                    stack.add(new Pair(TreeNode.Label.los, current));
                    stack.add(new Pair(Token.TokenType.LBRACE, current));
                    stack.add(new Pair(Token.TokenType.RPAREN, current));
                    stack.add(new Pair(Token.TokenType.ARGS, current));
                    stack.add(new Pair(Token.TokenType.STRINGARR, current));
                    stack.add(new Pair(Token.TokenType.LPAREN, current));
                    stack.add(new Pair(Token.TokenType.MAIN, current));
                    stack.add(new Pair(Token.TokenType.VOID, current));
                    stack.add(new Pair(Token.TokenType.STATIC, current));
                    stack.add(new Pair(Token.TokenType.PUBLIC, current));
                    stack.add(new Pair(Token.TokenType.LBRACE, current));
                    stack.add(new Pair(Token.TokenType.ID, current));
                    stack.add(new Pair(Token.TokenType.CLASS, current));
                    stack.add(new Pair(Token.TokenType.PUBLIC, current));
                } else if (stack.peek().fst() == TreeNode.Label.los &&
                        (tokens.get(i).getType() == Token.TokenType.SEMICOLON ||
                                tokens.get(i).getType() == Token.TokenType.TYPE ||
                                tokens.get(i).getType() == Token.TokenType.PRINT ||
                                tokens.get(i).getType() == Token.TokenType.WHILE ||
                                tokens.get(i).getType() == Token.TokenType.FOR ||
                                tokens.get(i).getType() == Token.TokenType.IF ||
                                tokens.get(i).getType() == Token.TokenType.ID
                        )) {
                    // 2.0 <<los>> -> <<stat>> <<los>>
                	TreeNode current = new TreeNode(TreeNode.Label.los, stack.peek().snd());
                	stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(TreeNode.Label.los, current));
                    stack.add(new Pair(TreeNode.Label.stat, current));
                } else if (stack.peek().fst() == TreeNode.Label.los && tokens.get(i).getType() == Token.TokenType.RBRACE) {
                    // 2.1 <<los>> -> e
                	TreeNode current = new TreeNode(TreeNode.Label.los, stack.peek().snd());
                	stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(TreeNode.Label.epsilon, current));
                } else if (stack.peek().fst() == TreeNode.Label.stat && tokens.get(i).getType() == Token.TokenType.WHILE) {
                    // 3.0 <<stat>> => <<while>>
                	TreeNode current = new TreeNode(TreeNode.Label.stat, stack.peek().snd());
                	stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(TreeNode.Label.whilestat, current));
                } else if (stack.peek().fst() == TreeNode.Label.stat && tokens.get(i).getType() == Token.TokenType.FOR) {
                    // 3.1 <<stat>> => <<for>>
                	TreeNode current = new TreeNode(TreeNode.Label.stat, stack.peek().snd());
                	stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(TreeNode.Label.forstat, current));
                } else if (stack.peek().fst() == TreeNode.Label.stat && tokens.get(i).getType() == Token.TokenType.IF) {
                    // 3.2 <<stat>> => <<if>>
                	TreeNode current = new TreeNode(TreeNode.Label.stat, stack.peek().snd());
                	stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(TreeNode.Label.ifstat, current));
                } else if (stack.peek().fst() == TreeNode.Label.stat && tokens.get(i).getType() == Token.TokenType.ID) {
                    // 3.3 <<stat>> => <<assign>> ;
                	TreeNode current = new TreeNode(TreeNode.Label.stat, stack.peek().snd());
                	stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(Token.TokenType.SEMICOLON, current));
                    stack.add(new Pair(TreeNode.Label.assign, current));
                } else if (stack.peek().fst() == TreeNode.Label.stat && tokens.get(i).getType() == Token.TokenType.TYPE) {
                    // 3.4 <<stat>> => <<decl>> ;
                	TreeNode current = new TreeNode(TreeNode.Label.stat, stack.peek().snd());
                	stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(Token.TokenType.SEMICOLON, current));
                    stack.add(new Pair(TreeNode.Label.decl, current));
                } else if (stack.peek().fst() == TreeNode.Label.stat && tokens.get(i).getType() == Token.TokenType.PRINT) {
                    // 3.5 <<stat>> => <<print>> ;
                	TreeNode current = new TreeNode(TreeNode.Label.stat, stack.peek().snd());
                	stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(Token.TokenType.SEMICOLON, current));
                    stack.add(new Pair(TreeNode.Label.print, current));
                } else if (stack.peek().fst() == TreeNode.Label.stat && tokens.get(i).getType() == Token.TokenType.SEMICOLON) {
                    // 3.6 <<stat>> => ;
                	TreeNode current = new TreeNode(TreeNode.Label.stat, stack.peek().snd());
                	stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(Token.TokenType.SEMICOLON, current));
                } else if (stack.peek().fst() == TreeNode.Label.whilestat && tokens.get(i).getType() == Token.TokenType.WHILE) {
                    // 4.0 <<while>> -> while ( <<rel expr>> <<bool expr>> ) { <<los>> }
                	TreeNode current = new TreeNode(TreeNode.Label.whilestat, stack.peek().snd());
                	stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(Token.TokenType.RBRACE, current));
                    stack.add(new Pair(TreeNode.Label.los, current));
                    stack.add(new Pair(Token.TokenType.LBRACE, current));
                    stack.add(new Pair(Token.TokenType.RPAREN, current));
                    stack.add(new Pair(TreeNode.Label.boolexpr, current));
                    stack.add(new Pair(TreeNode.Label.relexpr, current));
                    stack.add(new Pair(Token.TokenType.LBRACE, current));
                    stack.add(new Pair(Token.TokenType.WHILE, current));


                } else if (stack.peek().fst() == TreeNode.Label.forstat && tokens.get(i).getType() == Token.TokenType.FOR) {
                    // 5.0 <<for>> -> for ( <<for start>> ; <<rel expr>> <<bool expr>> ; <<for arith>> ) { <<los>> }
                	TreeNode current = new TreeNode(TreeNode.Label.forstat, stack.peek().snd());
                	stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(Token.TokenType.RBRACE,current));
                    stack.add(new Pair(TreeNode.Label.los, current));
                    stack.add(new Pair(Token.TokenType.LBRACE,current));
                    stack.add(new Pair(Token.TokenType.RPAREN,current));
                    stack.add(new Pair(TreeNode.Label.forarith,current));
                    stack.add(new Pair(Token.TokenType.SEMICOLON, current));
                    stack.add(new Pair(TreeNode.Label.boolexpr, current));
                    stack.add(new Pair(TreeNode.Label.relexpr, current));
                    stack.add(new Pair(Token.TokenType.SEMICOLON, current));
                    stack.add(new Pair(TreeNode.Label.forstart, current));
                    stack.add(new Pair(Token.TokenType.LPAREN, current));
                    stack.add(new Pair(Token.TokenType.FOR, current));

                } else if (stack.peek().fst() == TreeNode.Label.forstart && tokens.get(i).getType() == Token.TokenType.TYPE) {
                    //6.0 <<for start>> → <<decl>>
                	TreeNode current = new TreeNode(TreeNode.Label.forstart, stack.peek().snd());
                	stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(TreeNode.Label.decl, current));
                } else if (stack.peek().fst() == TreeNode.Label.forstart && tokens.get(i).getType() == Token.TokenType.ID) {
                    //6.1 <<for start>> → <<assign>>
                	TreeNode current = new TreeNode(TreeNode.Label.forstart, stack.peek().snd());
                	stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(TreeNode.Label.assign, current));
                } else if (stack.peek().fst() == TreeNode.Label.forstart && tokens.get(i).getType() == Token.TokenType.SEMICOLON) {
                    //6.2 <<for start>> → e
                	TreeNode current = new TreeNode(TreeNode.Label.forstart, stack.peek().snd());
                	stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(TreeNode.Label.epsilon, current));
                } else if (stack.peek().fst() == TreeNode.Label.forarith && (tokens.get(i).getType() == Token.TokenType.LPAREN
                        || tokens.get(i).getType() == Token.TokenType.ID || tokens.get(i).getType() == Token.TokenType.NUM)) {
                    //7.0 <<for arith>> → <<arith expr>>
                	TreeNode current = new TreeNode(TreeNode.Label.forarith, stack.peek().snd());
                	stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(TreeNode.Label.arithexpr, current));
                } else if (stack.peek().fst() == TreeNode.Label.forarith && tokens.get(i).getType() == Token.TokenType.RPAREN) {
                    //7.1 <<for arith>> -> e
                	TreeNode current = new TreeNode(TreeNode.Label.forarith, stack.peek().snd());
                	stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(TreeNode.Label.epsilon, current));
                } else if (stack.peek().fst() == TreeNode.Label.ifstat && tokens.get(i).getType() == Token.TokenType.IF) {
                    //8.0 <<if>> → if ( <<rel expr>> <<bool expr>> ) { <<los>> } <<else if>>
                	TreeNode current = new TreeNode(TreeNode.Label.ifstat, stack.peek().snd());
                	stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(TreeNode.Label.elseifstat, current));
                    stack.add(new Pair(Token.TokenType.RBRACE, current));
                    stack.add(new Pair(TreeNode.Label.los, current));
                    stack.add(new Pair(Token.TokenType.LBRACE, current));
                    stack.add(new Pair(Token.TokenType.RPAREN, current));
                    stack.add(new Pair(TreeNode.Label.boolexpr, current));
                    stack.add(new Pair(TreeNode.Label.relexpr, current));
                    stack.add(new Pair(Token.TokenType.LPAREN, current));
                    stack.add(new Pair(Token.TokenType.IF, current));
                } else if (stack.peek().fst() == TreeNode.Label.elseifstat && tokens.get(i).getType() == Token.TokenType.ELSE) {
                    //9.0 <<else if>> → <<else?if>> { <<los>> } <<else if>>
                	TreeNode current = new TreeNode(TreeNode.Label.elseifstat, stack.peek().snd());
                	stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(TreeNode.Label.elseifstat, current));
                    stack.add(new Pair(Token.TokenType.RBRACE, current));
                    stack.add(new Pair(TreeNode.Label.los, current));
                    stack.add(new Pair(Token.TokenType.LBRACE, current));
                    stack.add(new Pair(TreeNode.Label.elseorelseif, current));
                } else if (stack.peek().fst() == TreeNode.Label.elseifstat && (tokens.get(i).getType() == Token.TokenType.RBRACE
                        || tokens.get(i).getType() == Token.TokenType.SEMICOLON || tokens.get(i).getType() == Token.TokenType.TYPE
                        || tokens.get(i).getType() == Token.TokenType.PRINT || tokens.get(i).getType() == Token.TokenType.WHILE
                        || tokens.get(i).getType() == Token.TokenType.FOR || tokens.get(i).getType() == Token.TokenType.IF
                        || tokens.get(i).getType() == Token.TokenType.ID)) {
                    //9.1 <<else if>> -> e
                	TreeNode current = new TreeNode(TreeNode.Label.elseifstat, stack.peek().snd());
                	stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(TreeNode.Label.epsilon, current));
                } else if (stack.peek().fst() == TreeNode.Label.elseorelseif && tokens.get(i).getType() == Token.TokenType.ELSE) {
                    //10.0 <<else?if>> → else <<poss if>>
                	TreeNode current = new TreeNode(TreeNode.Label.elseorelseif, stack.peek().snd());
                	stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(TreeNode.Label.possif, current));
                    stack.add(new Pair(Token.TokenType.ELSE, current));
                } else if (stack.peek().fst() == TreeNode.Label.possif && tokens.get(i).getType() == Token.TokenType.IF) {
                    //11.0 <<poss if>> → if ( <<rel expr>> <<bool expr>> )
                	TreeNode current = new TreeNode(TreeNode.Label.possif, stack.peek().snd());
                	stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(Token.TokenType.RPAREN, current));
                    stack.add(new Pair(TreeNode.Label.boolexpr, current));
                    stack.add(new Pair(TreeNode.Label.relexpr, current));
                    stack.add(new Pair(Token.TokenType.LPAREN, current));
                    stack.add(new Pair(Token.TokenType.IF, current));
                } else if (stack.peek().fst() == TreeNode.Label.possif && tokens.get(i).getType() == Token.TokenType.LBRACE) {
                    //11.1 <<poss if>> -> e
                	TreeNode current = new TreeNode(TreeNode.Label.possif, stack.peek().snd());
                	stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(TreeNode.Label.epsilon, current));
                } else if (stack.peek().fst() == TreeNode.Label.assign && tokens.get(i).getType() == Token.TokenType.ID) {
                    //12.0 <<assign>> → <<ID>> = <<expr>>
                	TreeNode current = new TreeNode(TreeNode.Label.assign, stack.peek().snd());
                	stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(TreeNode.Label.expr, current));
                    stack.add(new Pair(Token.TokenType.ASSIGN, current));
                    stack.add(new Pair(Token.TokenType.ID, current));
                } else if (stack.peek().fst() == TreeNode.Label.decl && tokens.get(i).getType() == Token.TokenType.TYPE) {
                    //13.0 <<decl>> → <<type>> <<ID>> <<poss assign>>
                	TreeNode current = new TreeNode(TreeNode.Label.decl, stack.peek().snd());
                	stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(TreeNode.Label.possassign, current));
                    stack.add(new Pair(Token.TokenType.ID, current));
                    stack.add(new Pair(TreeNode.Label.type, current));
                } else if (stack.peek().fst() == TreeNode.Label.possassign && tokens.get(i).getType() == Token.TokenType.ASSIGN) {
                    //14.0 <<poss assign>> → = <<expr>>
                	TreeNode current = new TreeNode(TreeNode.Label.possassign, stack.peek().snd());
                	stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(TreeNode.Label.expr, current));
                    stack.add(new Pair(Token.TokenType.ASSIGN, current));
                } else if (stack.peek().fst() == TreeNode.Label.possassign && tokens.get(i).getType() == Token.TokenType.SEMICOLON) {
                    //14.1 <<poss assign>> → e
                	TreeNode current = new TreeNode(TreeNode.Label.possassign, stack.peek().snd());
                	stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(TreeNode.Label.epsilon, current));
                } else if (stack.peek().fst() == TreeNode.Label.print && tokens.get(i).getType() == Token.TokenType.PRINT) {
                    //15.0 <<print>> → System.out.println ( <<print expr>> )
                	TreeNode current = new TreeNode(TreeNode.Label.print, stack.peek().snd());
                	stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(Token.TokenType.RPAREN, current));
                    stack.add(new Pair(TreeNode.Label.printexpr, current));
                    stack.add(new Pair(Token.TokenType.LPAREN, current));
                    stack.add(new Pair(Token.TokenType.PRINT, current));
                } else if (stack.peek().fst() == TreeNode.Label.type && tokens.get(i).getValue().get().equals("int")) {
                    //16.0 <<type>> → int
                    TreeNode current = new TreeNode(TreeNode.Label.type, stack.peek().snd());
                    stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(Token.TokenType.TYPE, current));
                } else if (stack.peek().fst() == TreeNode.Label.type && tokens.get(i).getValue().get().equals("boolean")) {
                    //16.1 <<type>> → boolean
                    TreeNode current = new TreeNode(TreeNode.Label.type, stack.peek().snd());
                    stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(Token.TokenType.TYPE, current));
                } else if (stack.peek().fst() == TreeNode.Label.type && tokens.get(i).getValue().get().equals("char")) {
                    //16.2 <<type>> → char
                    TreeNode current = new TreeNode(TreeNode.Label.type, stack.peek().snd());
                    stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(Token.TokenType.TYPE, current));
                } else if (stack.peek().fst() == TreeNode.Label.expr && (tokens.get(i).getType() == Token.TokenType.TRUE
                        || tokens.get(i).getType() == Token.TokenType.LPAREN
                        || tokens.get(i).getType() == Token.TokenType.FALSE
                        || tokens.get(i).getType() == Token.TokenType.ID
                        || tokens.get(i).getType() == Token.TokenType.NUM)) {
                    //17 <<expr>> → <<rel expr>> <<bool expr>>
                    TreeNode current = new TreeNode(TreeNode.Label.expr, stack.peek().snd());
                    stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(TreeNode.Label.boolexpr, current));
                    stack.add(new Pair(TreeNode.Label.relexpr, current));
                } else if (stack.peek().fst() == TreeNode.Label.expr && tokens.get(i).getType() == Token.TokenType.SQUOTE) {
                    //17.1 <<expr>> → <<char expr>>
                    TreeNode current = new TreeNode(TreeNode.Label.expr, stack.peek().snd());
                    stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(TreeNode.Label.charexpr, current));
                } else if (stack.peek().fst() == TreeNode.Label.charexpr && tokens.get(i).getType() == Token.TokenType.SQUOTE) {
                    //18 <<char expr>> → ' <<char>> '
                    TreeNode current = new TreeNode(TreeNode.Label.charexpr, stack.peek().snd());
                    stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(Token.TokenType.SQUOTE, current));
                    stack.add(new Pair(Token.TokenType.CHARLIT, current));
                    stack.add(new Pair(Token.TokenType.SQUOTE, current);
                } else if (stack.peek().fst() == TreeNode.Label.boolexpr && (
                        tokens.get(i).getType() == Token.TokenType.EQUAL
                                || tokens.get(i).getType() == Token.TokenType.NEQUAL
                                || tokens.get(i).getType() == Token.TokenType.AND
                                || tokens.get(i).getType() == Token.TokenType.OR)) {
                    //19 <<bool expr>> → <<bool op>> <<rel expr>> <<bool expr>>
                    TreeNode current = new TreeNode(TreeNode.Label.boolexpr, stack.peek().snd());
                    stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(TreeNode.Label.boolexpr, current));
                    stack.add(new Pair(TreeNode.Label.relexpr, current));
                    stack.add(new Pair(TreeNode.Label.boolop, current));
                } else if (stack.peek().fst() == TreeNode.Label.boolexpr && (tokens.get(i).getType() == Token.TokenType.RPAREN
                        || tokens.get(i).getType() == Token.TokenType.SEMICOLON)) {
                    TreeNode current = new TreeNode(TreeNode.Label.boolexpr, stack.peek().snd());
                    stack.peek().snd().addChild(current);
                    //19.1 <<bool expr>> → ε
                    stack.pop();
                    stack.add(new Pair(TreeNode.Label.epsilon, current));
                } else if (stack.peek().fst() == TreeNode.Label.boolop && (tokens.get(i).getType() == Token.TokenType.EQUAL
                        || tokens.get(i).getType() == Token.TokenType.NEQUAL)) {
                    //20.0 <<bool op>> → <<bool eq>>
                    TreeNode current = new TreeNode(TreeNode.Label.boolop, stack.peek().snd());
                    stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(TreeNode.Label.booleq, current));
                } else if (stack.peek().fst() == TreeNode.Label.boolop && (tokens.get(i).getType() == Token.TokenType.AND
                        || tokens.get(i).getType() == Token.TokenType.OR)) {
                    //20.1 <<bool op>> →  <<bool log>>
                    TreeNode current = new TreeNode(TreeNode.Label.boolop, stack.peek().snd());
                    stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(TreeNode.Label.boollog, current));
                } else if (stack.peek().fst() == TreeNode.Label.booleq && tokens.get(i).getType() == Token.TokenType.EQUAL) {
                    //21.0 <<bool eq>> → ==
                    TreeNode current = new TreeNode(TreeNode.Label.booleq, stack.peek().snd());
                    stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(Token.TokenType.EQUAL, current));
                } else if (stack.peek().fst() == TreeNode.Label.booleq && tokens.get(i).getType() == Token.TokenType.NEQUAL) {
                    //21.1 <<bool eq>> → !=
                    TreeNode current = new TreeNode(TreeNode.Label.booleq, stack.peek().snd());
                    stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(Token.TokenType.NEQUAL,current));
                } else if (stack.peek().fst() == TreeNode.Label.boollog && tokens.get(i).getType() == Token.TokenType.AND) {
                    //22.0 <<bool log>> → &&
                    TreeNode current = new TreeNode(TreeNode.Label.boollog, stack.peek().snd());
                    stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(Token.TokenType.AND, current));
                } else if (stack.peek().fst() == TreeNode.Label.boollog && tokens.get(i).getType() == Token.TokenType.OR) {
                    //22.1 <<bool log>> → ||
                    TreeNode current = new TreeNode(TreeNode.Label.boollog, stack.peek().snd());
                    stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(Token.TokenType.OR, current));
                } else if (stack.peek().fst() == TreeNode.Label.relexpr && (tokens.get(i).getType() == Token.TokenType.LPAREN
                        || tokens.get(i).getType() == Token.TokenType.ID
                        || tokens.get(i).getType() == Token.TokenType.NUM)) {
                    //23.0 <<rel expr>> → <<arith expr>> <<rel expr'>>
                    TreeNode current = new TreeNode(TreeNode.Label.relexpr, stack.peek().snd());
                    stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(TreeNode.Label.relexprprime, current));
                    stack.add(new Pair(TreeNode.Label.arithexpr, current));
                } else if (stack.peek().fst() == TreeNode.Label.relexpr && tokens.get(i).getType() == Token.TokenType.TRUE) {
                    //23.1 <<rel expr>> → true
                    TreeNode current = new TreeNode(TreeNode.Label.relexpr, stack.peek().snd());
                    stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(Token.TokenType.TRUE, current));
                } else if (stack.peek().fst() == TreeNode.Label.relexpr && tokens.get(i).getType() == Token.TokenType.FALSE) {
                    //23.2 <<rel expr>> → false
                    TreeNode current = new TreeNode(TreeNode.Label.relexpr, stack.peek().snd());
                    stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(Token.TokenType.FALSE, current));
                } else if (stack.peek().fst() == TreeNode.Label.relexprprime && (tokens.get(i).getType() == Token.TokenType.LT
                        || tokens.get(i).getType() == Token.TokenType.GT
                        || tokens.get(i).getType() == Token.TokenType.LE
                        || tokens.get(i).getType() == Token.TokenType.GE)) {
                    //24.0 <<rel expr'>> → <<rel op>> <<arith expr>>
                    TreeNode current = new TreeNode(TreeNode.Label.relexprprime, stack.peek().snd());
                    stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(TreeNode.Label.arithexpr, current));
                    stack.add(new Pair(TreeNode.Label.relop, current));
                } else if (stack.peek().fst() == TreeNode.Label.relexprprime && (tokens.get(i).getType() == Token.TokenType.EQUAL
                        || tokens.get(i).getType() == Token.TokenType.NEQUAL
                        || tokens.get(i).getType() == Token.TokenType.RPAREN
                        || tokens.get(i).getType() == Token.TokenType.AND
                        || tokens.get(i).getType() == Token.TokenType.OR
                        || tokens.get(i).getType() == Token.TokenType.SEMICOLON)) {
                    //24.1 <<rel expr'>> → ε
                    TreeNode current = new TreeNode(TreeNode.Label.relexprprime, stack.peek().snd());
                    stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(TreeNode.Label.epsilon, current));
                } else if (stack.peek().fst() == TreeNode.Label.relop && tokens.get(i).getType() == Token.TokenType.LT) {
                    //25.0 <<rel op>> → <
                    TreeNode current = new TreeNode(TreeNode.Label.relop, stack.peek().snd());
                    stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(Token.TokenType.LT, current));
                } else if (stack.peek().fst() == TreeNode.Label.relop && tokens.get(i).getType() == Token.TokenType.LE) {
                    //25.1 <<rel op>> →  <=
                    TreeNode current = new TreeNode(TreeNode.Label.relop, stack.peek().snd());
                    stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(Token.TokenType.LE, current);
                } else if (stack.peek().fst() == TreeNode.Label.relop && tokens.get(i).getType() == Token.TokenType.GT) {
                    //25.2 <<rel op>> →  >
                    TreeNode current = new TreeNode(TreeNode.Label.relop, stack.peek().snd());
                    stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(Token.TokenType.GT, current));
                } else if (stack.peek().fst() == TreeNode.Label.relop && tokens.get(i).getType() == Token.TokenType.GE) {
                    //25.3 <<rel op>> → >=
                    TreeNode current = new TreeNode(TreeNode.Label.relop, stack.peek().snd());
                    stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(Token.TokenType.GE, current));
                } else if (stack.peek().fst() == TreeNode.Label.arithexpr && (tokens.get(i).getType() == Token.TokenType.LPAREN
                        || tokens.get(i).getType() == Token.TokenType.NUM
                        || tokens.get(i).getType() == Token.TokenType.ID)) {
                    //26 <<arith expr>> → <<term>> <<arith expr'>>
                    TreeNode current = new TreeNode(TreeNode.Label.arithexpr, stack.peek().snd());
                    stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(TreeNode.Label.arithexprprime, current));
                    stack.add(new Pair(TreeNode.Label.term, current));
                } else if (stack.peek().fst() == TreeNode.Label.arithexprprime && tokens.get(i).getType() == Token.TokenType.PLUS) {
                    //27.0 <<arith expr'>> → + <<term>> <<arith expr'>>
                    TreeNode current = new TreeNode(TreeNode.Label.arithexprprime, stack.peek().snd());
                    stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(TreeNode.Label.arithexprprime, current));
                    stack.add(new Pair(TreeNode.Label.term, current));
                    stack.add(new Pair)(Token.TokenType.PLUS, current);
                } else if (stack.peek().fst() == TreeNode.Label.arithexprprime && tokens.get(i).getType() == Token.TokenType.MINUS) {
                    //27.1 <<arith expr'>> → - <<term>> <<arith expr'>>
                    TreeNode current = new TreeNode(TreeNode.Label.arithexprprime, stack.peek().snd());
                    stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(TreeNode.Label.arithexprprime, current));
                    stack.add(new Pair(TreeNode.Label.term, current));
                    stack.add(new Pair(Token.TokenType.MINUS, current));
                } else if (stack.peek().fst() == TreeNode.Label.arithexprprime && (tokens.get(i).getType() == Token.TokenType.EQUAL
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
                    TreeNode current = new TreeNode(TreeNode.Label.arithexprprime, stack.peek().snd());
                    stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(TreeNode.Label.epsilon, current));
                } else if (stack.peek().fst() == TreeNode.Label.term && (tokens.get(i).getType() == Token.TokenType.LPAREN
                        || tokens.get(i).getType() == Token.TokenType.ID
                        || tokens.get(i).getType() == Token.TokenType.NUM)) {
                    //28.0 <<term>> → <<factor>> <<term'>>
                    TreeNode current = new TreeNode(TreeNode.Label.term, stack.peek().snd());
                    stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(TreeNode.Label.termprime, current));
                    stack.add(new Pair(TreeNode.Label.factor, current));
                } else if (stack.peek().fst() == TreeNode.Label.termprime && tokens.get(i).getType() == Token.TokenType.TIMES) {
                    //29.0 <<term'>> → * <<factor>> <<term'>>
                    TreeNode current = new TreeNode(TreeNode.Label.termprime, stack.peek().snd());
                    stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(TreeNode.Label.termprime, current));
                    stack.add(new Pair(TreeNode.Label.factor, current));
                    stack.add(new Pair(Token.TokenType.TIMES,current));
                } else if (stack.peek().fst() == TreeNode.Label.termprime && tokens.get(i).getType() == Token.TokenType.DIVIDE) {
                    //29.1 <<term'>> → / <<factor>> <<term'>>
                    TreeNode current = new TreeNode(TreeNode.Label.termprime, stack.peek().snd());
                    stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(TreeNode.Label.termprime, current));
                    stack.add(new Pair(TreeNode.Label.factor, current));
                    stack.add(new Pair(Token.TokenType.DIVIDE, current));
                } else if (stack.peek().fst() == TreeNode.Label.termprime && tokens.get(i).getType() == Token.TokenType.MOD) {
                    //29.2 <<term'>> → % <<factor>> <<term'>>
                    TreeNode current = new TreeNode(TreeNode.Label.termprime, stack.peek().snd());
                    stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(TreeNode.Label.termprime, current));
                    stack.add(new Pair(TreeNode.Label.factor, current));
                    stack.add(new Pair(Token.TokenType.MOD, current));
                } else if (stack.peek().fst() == TreeNode.Label.termprime && (
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
                    TreeNode current = new TreeNode(TreeNode.Label.termprime, stack.peek().snd());
                    stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(TreeNode.Label.epsilon, current));
                } else if (stack.peek().fst() == TreeNode.Label.factor && tokens.get(i).getType() == Token.TokenType.LPAREN) {
                    //30.0 <<factor>> → ( <<arith expr>> )
                    TreeNode current = new TreeNode(TreeNode.Label.factor, stack.peek().snd());
                    stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(Token.TokenType.RPAREN, current));
                    stack.add(new Pair(TreeNode.Label.arithexpr, current));
                    stack.add(new Pair(Token.TokenType.LPAREN, current));
                } else if (stack.peek().fst() == TreeNode.Label.factor && tokens.get(i).getType() == Token.TokenType.ID) {
                    //30.1 <<factor>> → <<ID>>
                    TreeNode current = new TreeNode(TreeNode.Label.factor, stack.peek().snd());
                    stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(Token.TokenType.ID, current));
                } else if (stack.peek().fst() == TreeNode.Label.factor && tokens.get(i).getType() == Token.TokenType.NUM) {
                    //30.2 <<factor>> → <<num>>
                    TreeNode current = new TreeNode(TreeNode.Label.factor, stack.peek().snd());
                    stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(Token.TokenType.NUM, current));
                } else if (stack.peek().fst() == TreeNode.Label.printexpr &&
                        (tokens.get(i).getType() == Token.TokenType.LPAREN
                                || tokens.get(i).getType() == Token.TokenType.TRUE
                                || tokens.get(i).getType() == Token.TokenType.FALSE
                                || tokens.get(i).getType() == Token.TokenType.ID
                                || tokens.get(i).getType() == Token.TokenType.NUM)) {
                    //31.0 <<print expr>> → <<rel expr>> <<bool expr>>
                    TreeNode current = new TreeNode(TreeNode.Label.printexpr, stack.peek().snd());
                    stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(TreeNode.Label.boolexpr, current));
                    stack.add(new Pair(TreeNode.Label.relexpr, current));
                } else if (stack.peek().fst() == TreeNode.Label.printexpr && tokens.get(i).getType() == Token.TokenType.DQUOTE) {
                    //31.1 <<print expr>> →"<<string lit>> "
                    TreeNode current = new TreeNode(TreeNode.Label.printexpr, stack.peek().snd());
                    stack.peek().snd().addChild(current);
                    stack.pop();
                    stack.add(new Pair(Token.TokenType.DQUOTE, current));
                    stack.add(new Pair(Token.TokenType.STRINGLIT, current));
                    stack.add(new Pair(Token.TokenType.DQUOTE, current));
                }else{
                    throw new SyntaxException("No such rule");
                }
            } else if(stack.peek().fst() == TreeNode.Label.terminal){
                i++;
                stack.pop();
                // Create
            }
        }


        return pTree;
    }
}