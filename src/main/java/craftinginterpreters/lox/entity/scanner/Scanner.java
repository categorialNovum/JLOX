package craftinginterpreters.lox.entity.scanner;

import craftinginterpreters.lox.Lox;
import craftinginterpreters.lox.entity.token.Token;
import craftinginterpreters.lox.entity.token.TokenType;

import static craftinginterpreters.lox.entity.token.TokenType.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static craftinginterpreters.lox.entity.token.TokenType.EOF;

public class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<Token>();
    private int start;
    private int current;
    private int line = 1;
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<String, TokenType>();
        keywords.put("and", AND);
        keywords.put("class", CLASS);
        keywords.put("else", ELSE);
        keywords.put("false", FALSE);
        keywords.put("for", FOR);
        keywords.put("fun", FUN);
        keywords.put("if", IF);
        keywords.put("nil", NIL);
        keywords.put("or", OR);
        keywords.put("print", PRINT);
        keywords.put("return", RETURN);
        keywords.put("super", SUPER);
        keywords.put("this", THIS);
        keywords.put("true", TRUE);
        keywords.put("var", VAR);
        keywords.put("while", WHILE);
    }

    public Scanner(String source){
        this.source = source;
    }

    public List<Token> scanTokens(){
        while(!endFound()){
            start = current;
            scanToken();
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
            // discard whitesapce / tabs
            case ' ':
            case '\r':
            case '\t':
            // new line, increment line count
            case '\n':
                line++;
                break;
            // opening quote for string
            case '"':
                string();
                break;
            default :
                if (isDigit(c)) {
                    number();
                }else if (isAlpha(c)){
                    identifier();
                }else{
                    Lox.error(line, "Unexpected character.");
                }
                break;
        }
    }

    private char advance(){
        return source.charAt(current++);
    }
    // look one ahead
    private char peek(){
        if (endFound()) return '\0';
        return source.charAt(current);
    }

    // look two ahead
    private char peekNext(){
        if (current + 1 >= source.length()){
            return '\0';
        }
        return source.charAt(current + 1);
    }

    private boolean isAlpha(char c){
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'A') ||
                c == '_';
    }
    private boolean isAlphaNumeric (char c){
        return isDigit(c) || isAlpha(c);
    }

    private void identifier(){
        while (isAlphaNumeric(peek())) advance();
        String text = source.substring(start, current);
        // check if the identifier is on the reserved words list
        TokenType type = keywords.get(text);
        if (type == null){
            type = IDENTIFIER;
        }
        addToken(type);
    }

    private void string(){
        while (peek() != '"'  && !endFound()){
            if (peek() == '\n') line++;
            advance();
        }
        if (endFound()){
            Lox.error(line, "Unterminated String.");
            return;
        }
        advance(); // go past close quote
        String value = source.substring(start+1, current-1);
        addToken(STRING, value);
    }

    // allow for a single decimal in a number string
    private void number(){
        while (isDigit(peek())) advance();
        if (peek() == '.' && isDigit(peekNext())){
            advance();
            while (isDigit(peek())) advance();
        }
        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private boolean isDigit(char c){
        return  c >= '0' && c <= '9';
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
