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
        Deque<Symbol> stack = new ArrayDeque<Symbol>(); // this one can contain TokenType and Label
        stack.add(TreeNode.Label.prog);
        int i = 0;
        while(i < tokens.size()){
            if(stack.peek().isVariable()) {
                if(stack.peek() == TreeNode.Label.prog && tokens.get(i).getType() == Token.TokenType.PUBLIC){
                    // 1.0 <<prog>> → public class <<ID>> { public static void main ( String[] args ) { <<los>> } }
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
                }else if(stack.peek() == TreeNode.Label.los &&
                        (tokens.get(i).getType() == Token.TokenType.SEMICOLON ||
                                tokens.get(i).getType() == Token.TokenType.TYPE ||
                                tokens.get(i).getType() == Token.TokenType.PRINT ||
                                tokens.get(i).getType() == Token.TokenType.WHILE ||
                                tokens.get(i).getType() == Token.TokenType.FOR ||
                                tokens.get(i).getType() == Token.TokenType.IF ||
                                tokens.get(i).getType() == Token.TokenType.ID
                        )){
                    // 2.0 <<los>> → <<stat>> <<los>>
                    stack.pop();
                    stack.add(TreeNode.Label.los);
                    stack.add(TreeNode.Label.stat);
                }else if(stack.peek() == TreeNode.Label.los && tokens.get(i).getType() == Token.TokenType.RBRACE){
                    // 2.1 <<los>> -> e
                    stack.pop();
                    stack.add(TreeNode.Label.epsilon);
                }else if(stack.peek() == TreeNode.Label.los && tokens.get(i).getType() == Token.TokenType.WHILE){
                    // 3.0
                    stack.pop();
                    stack.add(TreeNode.Label.whilestat);
                }else if(stack.peek() == TreeNode.Label.los && tokens.get(i).getType() == Token.TokenType.FOR){
                    // 3.1
                    stack.pop();
                    stack.add(TreeNode.Label.forstat);
                }else if(stack.peek() == TreeNode.Label.los && tokens.get(i).getType() == Token.TokenType.IF){
                    // 3.2
                    stack.pop();
                    stack.add(TreeNode.Label.ifstat);
                }else if(stack.peek() == TreeNode.Label.los && tokens.get(i).getType() == Token.TokenType.ID){
                    // 3.3
                    stack.pop();
                    stack.add(Token.TokenType.SEMICOLON);
                    stack.add(TreeNode.Label.assign);
                }else if(stack.peek() == TreeNode.Label.los && tokens.get(i).getType() == Token.TokenType.TYPE){
                    // 3.4
                    stack.pop();
                    stack.add(Token.TokenType.SEMICOLON);
                    stack.add(TreeNode.Label.decl);
                }else if(stack.peek() == TreeNode.Label.los && tokens.get(i).getType() == Token.TokenType.PRINT){
                    // 3.5
                    stack.pop();
                    stack.add(Token.TokenType.SEMICOLON);
                    stack.add(TreeNode.Label.print);
                }else if(stack.peek() == TreeNode.Label.los && tokens.get(i).getType() == Token.TokenType.SEMICOLON){
                    // 3.6
                    stack.pop();
                    stack.add(Token.TokenType.SEMICOLON);
                }else if(stack.peek() == TreeNode.Label.los && tokens.get(i).getType() == Token.TokenType.WHILE){
                    // 4.0 <<while>> → while ( <<rel expr>> <<bool expr>> ) { <<los>> }
                    stack.pop();
                    stack.add(Token.TokenType.RBRACE);
                    stack.add(TreeNode.Label.los);
                    stack.add(Token.TokenType.LBRACE);
                    stack.add(Token.TokenType.RPAREN);
                    stack.add(TreeNode.Label.boolexpr);
                    stack.add(TreeNode.Label.relexpr);
                    stack.add(Token.TokenType.LBRACE);
                    stack.add(Token.TokenType.WHILE);


                }else if(stack.peek() == TreeNode.Label.los && tokens.get(i).getType() == Token.TokenType.FOR){
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
                }
            }else{
                stack.pop();
                // then add the leave (terminal) to parse tree
            }
        }


        return pTree;
    }

}