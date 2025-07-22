package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class Lox {
    static InputStreamReader input = new InputStreamReader(System.in);
    static BufferedReader reader = new BufferedReader(input);
    static boolean hadError = false;
    public static void main(String[] args) throws IOException {
        System.out.println("Starting JLOX....");
        if (args.length > 1){
            System.out.println("Usage : jlox [script]");
            System.exit(64);
        }else if (args.length == 1){
            runFile(args[0]);
        }else{
            runREPL();
        }
    }

    /** Execute code read from a file path **/
    private static void runFile(String filePath) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        run(new String(bytes, Charset.defaultCharset()));
        if (hadError) System.exit(65);
    }

    /** Start a Read Evaluate Print Loop (REPL) **/
    private static void runREPL() throws IOException {
        for (;;){
            System.out.println("jlox >");
            String line = reader.readLine();
            if (line == null) break;
            run(line);
            hadError = false;
        }

    }

    /** Execute code **/
    private static void run(String source){
        Scanner scanner = new Scanner(source);
        List<String> tokens = scanner.tokens().toList();
        for(String token : tokens){
            System.out.println(token);
        }
    }

    static void error(int line, String msg){
        report(line, "", msg);
    }

    static void report(int line, String where, String msg){
       System.err.println("[line : " + line + "] Error " + where + ": " + msg);
       hadError = true;
    }
}