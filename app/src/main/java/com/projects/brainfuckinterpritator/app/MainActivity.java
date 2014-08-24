package com.projects.brainfuckinterpritator.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.*;
import java.util.*;

public class MainActivity extends Activity{
    private EditText enterText;
    private TextView outText;
    private Button btnCompile;
    private Analyzer analyzer;
    private Queue<Token> tokens;
    private List<Queue<Token>> leftBracket;
    private Map memory;
    private int index = 0;
    private List<Integer> answers;
    private static final String FILE_NAME = "Code.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        enterText = (EditText) findViewById(R.id.enterEditText);
        outText = (TextView) findViewById(R.id.outTextView);
        btnCompile = (Button) findViewById(R.id.btn_compile);

        final String code = readFile(FILE_NAME);
        enterText.setText(code);

        btnCompile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                memory = new HashMap<Integer, Integer>();
                index = 0;
                leftBracket = new LinkedList<Queue<Token>>();
                answers = new LinkedList<Integer>();
                analyzer = new Analyzer(enterText.getText().toString());
                tokens = analyzer.getTokens();
                process();
            }
        });

    }

    @Override
    public void onBackPressed() {
        final String code = enterText.getText().toString();
        writeFile(FILE_NAME, code);
        super.onBackPressed();
    }

    private String readFile(String fileName) {
        String savedCode = "";

        //ошибка intellij idea при поптыки использовать try-with-resources
        //исправить
        try {
            InputStream inStream = openFileInput(fileName);
            try {
                InputStreamReader sr = new InputStreamReader(inStream);
                BufferedReader reader = new BufferedReader(sr);
                StringBuffer buffer = new StringBuffer();
                String str = "";

                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                }
                savedCode = buffer.toString();

                if(savedCode == null)
                    return "";
            } finally {
                inStream.close();
            }

        } catch (IOException e) {
            Log.e("FILE READ ERROR", e.toString());
        }

        return savedCode;
    }

    private void writeFile(String fileName, String inputCode){
        //ошибка intellij idea при поптыки использовать try-with-resources
        //исправить
                try{
                    OutputStreamWriter outStream = new OutputStreamWriter(openFileOutput(fileName, 0));
                    try {
                        outStream.write(inputCode);
                    } finally {
                        outStream.close();
                    }
                }catch (IOException e){
                    Log.e("WRITE ERROR", e.toString());
                }
    }

    private void process(){
        while(!tokens.isEmpty()) {
            Token tk = tokens.remove();

            tokenPlus(tk == Token.PLUS);
            tokenMinus(tk == Token.MINUS);
            tokenUP(tk == Token.UP);
            tokenDown(tk == Token.DOWN);
            tokenPrint(tk == Token.PRINT);
            tokenEnter(tk == Token.ENTER);
            tokenLeftBracket(tk==Token.LEFT_BRACKET);
            tokenRightBracket(tk == Token.RIGHT_BRACKET);
        }
        printAnswers();
    }

    private void tokenPlus(boolean flag){
        if(flag){
            if(memory.get(index) == null)
                memory.put(index, 1);
            else
                memory.put(index, ((Integer)memory.get(index)+1));
        }
    }

    private void tokenMinus(boolean flag){
        if(flag){
            if(memory.get(index) == null)
                memory.put(index, -1);
            else
                memory.put(index, ((Integer)memory.get(index)-1));
        }
    }
    private void tokenUP(boolean flag){
        if(flag){
            if(memory.get(index+1) == null) {
                memory.put(index + 1, 0);
                index++;
            }
            else
                index++;
        }
    }
    private void tokenDown(boolean flag){
        if(flag){
            if(memory.get(index-1) == null) {
                memory.put(index - 1, 0);
                index--;
            }
            else
                index--;
        }
    }
    private void tokenPrint(boolean flag){
        if(flag){
            final Queue<Token> tokens = new LinkedList<Token>(this.tokens);
            if(memory.get(index) == null) {
                answers.add(0);
            }
            else {
                answers.add((Integer) memory.get(index));
            }
        }
    }

    private void tokenEnter(boolean flag) {
        if (flag) {
            final EditText newVariable = new EditText(this);
            final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            final Queue<Token> tokens = new LinkedList<Token>(this.tokens);

            newVariable.setInputType(InputType.TYPE_CLASS_NUMBER);
            this.tokens.clear();

            DialogInterface.OnClickListener cancelButtonListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            };

            DialogInterface.OnClickListener okButtonListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    memory.put(index, Integer.parseInt(newVariable.getText().toString()));
                    repairTokens(tokens);
                    process();
                }
            };

            alertBuilder
                    .setTitle("Ввод")
                    .setMessage("Введите значение переменной")
                    .setView(newVariable)
                    .setPositiveButton("Да", okButtonListener)
                    .setNegativeButton("Остановить программу", cancelButtonListener)
                    .show();

        }
    }

    private void tokenLeftBracket(boolean flag){
        if(flag) {
            Queue tempTokens = new LinkedList<Token>(tokens);
            leftBracket.add(tempTokens);
        }
    }

    private void tokenRightBracket(boolean flag){
        if(flag){
            if(!leftBracket.isEmpty()){
                if((Integer)memory.get(index)!=0) {
                    tokens = new LinkedList<Token>(leftBracket.get(leftBracket.size() - 1));
                } else {
                    leftBracket.remove(leftBracket.size()-1);
                }
            } else {
                tokens.clear();
                Toast.makeText(getApplicationContext(), "Input Error", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void repairTokens(Queue<Token> tokens){
        this.tokens = tokens;
    }

    private void printAnswers(){
        String ans = "";
        for(int i = 0; i < answers.size(); i++) {
            if(i!=answers.size()-1)
                ans += answers.get(i) + ",";
            else
                ans += answers.get(i);
        }
        outText.setText(ans);
    }
}
