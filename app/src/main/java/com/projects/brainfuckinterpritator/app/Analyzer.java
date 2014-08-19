package com.projects.brainfuckinterpritator.app;

import java.util.LinkedList;
import java.util.Queue;

public class Analyzer {
    private String inputData;
    private Queue<Token> tokens = new LinkedList<Token>();

    public Analyzer(String inputData){
        this.inputData = inputData;
    }

    public Queue<Token> getTokens(){
        while(inputData != null && inputData.length()>0){
            addToken(getNextSymbol());
        }
        return tokens;
    }

    private char getNextSymbol(){
        char peek = inputData.charAt(0);
        if(inputData.length()>1)
            inputData = inputData.substring(1);
        else
            inputData = null;
        return peek;
    }

    private void addToken(char tk){
        switch (tk) {
            case ' ': break;
            case '+': tokens.offer(Token.PLUS); break;
            case '-': tokens.offer(Token.MINUS); break;
            case '>': tokens.offer(Token.UP); break;
            case '<': tokens.offer(Token.DOWN); break;
            case '.': tokens.offer(Token.PRINT); break;
            case ',': tokens.offer(Token.ENTER); break;
            case '[': tokens.offer(Token.LEFT_BRACKET); break;
            case ']': tokens.offer(Token.RIGHT_BRACKET); break;
            default: /*COMPILE_ERROR*/ break;
        }
    }
}
