package craftinginterpreters.lox.entity.scanner;

import craftinginterpreters.lox.Lox;
import craftinginterpreters.lox.entity.token.Token;
import craftinginterpreters.lox.entity.token.TokenType;

import static craftinginterpreters.lox.entity.token.TokenType.*;

import java.util.ArrayList;
import java.util.List;

import static craftinginterpreters.lox.entity.token.TokenType.EOF;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<Token>();
    private int start;
    private int current;
    private int line = 1;

    public Scanner(String source){
        this.source = source;
    }

    List<Token> scanTokens(){
        while(!endFound()){

        }
        tokens.add(new Token(EOF, "",null, line));

        return tokens;
    }

    private void scanToken(){
        char c = advance();
        switch (c){
            // single character tokens
            case '(': addToken(LEFT_PAREN);break;
            case ')': addToken(RIGHT_PAREN);break;
            case '[': addToken(LEFT_BRACE);break;
            case ']': addToken(RIGHT_BRACE);break;
            case ',': addToken(COMMA);break;
            case '.': addToken(DOT);break;
            case '-': addToken(MINUS);break;
            case '+': addToken(PLUS);break;
            case ';': addToken(SEMICOLON);break;
            case '*': addToken(STAR);break;

            // one or two length tokens
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG );
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case '/':
                if (match('/'))
                    //comment detected, it goes until the EOL
                    while (peek() != '\n' && !endFound()){ advance(); }
                else{ addToken(SLASH); }
                break;
// TODO start adding additional tokens here. whitespace handling, string literals, number literals, keywords, etc.
            default :
                Lox.error(line, "Unexpected character.");
                break;
        }
    }

    private char advance(){
        return source.charAt(current++);
    }

    private char peek(){
        if (endFound()) return '\0';
        return source.charAt(current);
    }

    private boolean match(char expected){
        if (endFound()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;

    }

    private void addToken(TokenType type){
        addToken(type, null);
    }
    private void addToken(TokenType type, Object literal){
        String text = source.substring(start, current);
        tokens.add(new Token(type,text, literal, line));
    }

    boolean endFound(){
        return current >= source.length();
    }
}
