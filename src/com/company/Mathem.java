package com.company;

import java.util.ArrayList;
import java.util.List;

public class Mathem extends Context{
    private static String Mathvalue;
    public static String evaluate(String value) {
        Mathvalue=value;
        if (value.contains("[")){
            value = Assignment.evaluate(value);
        }
        Mathvalue = MathCount.count(value);
        return Mathvalue;
    }


     class MathCount {
        public static String count(String expressionText) {
            List<Lexeme> lexemes = lexAnalyze(expressionText);
            LexemeBuffer lexemeBuffer = new LexemeBuffer(lexemes);
            return (Double.toString(expr(lexemeBuffer)));

        }

        public enum LexemeType {
            LEFT_BRACKET, RIGHT_BRACKET,
            OP_PLUS, OP_MINUS, OP_MUL, OP_DIV,
            NUMBER,
            EOF;
        }

        public static class Lexeme {
            LexemeType type;
            String value;

            public Lexeme(LexemeType type, String value) {
                this.type = type;
                this.value = value;
            }

            public Lexeme(LexemeType type, Character value) {
                this.type = type;
                this.value = value.toString();
            }

            @Override
            public String toString() {
                return "Lexeme{" +
                        "type=" + type +
                        ", value='" + value + '\'' +
                        '}';
            }
        }

        public static class LexemeBuffer {
            private int pos;

            public List<Lexeme> lexemes;

            public LexemeBuffer(List<Lexeme> lexemes) {
                this.lexemes = lexemes;
            }

            public Lexeme next() {
                return lexemes.get(pos++);
            }

            public void back() {
                pos--;
            }

            public int getPos() {
                return pos;
            }
        }

        public static List<Lexeme> lexAnalyze(String expText) {
            ArrayList<Lexeme> lexemes = new ArrayList<>();
            int pos = 0;
            while (pos < expText.length()) {
                char c = expText.charAt(pos);
                switch (c) {
                    case '(':
                        lexemes.add(new Lexeme(LexemeType.LEFT_BRACKET, c));
                        pos++;
                        continue;
                    case ')':
                        lexemes.add(new Lexeme(LexemeType.RIGHT_BRACKET, c));
                        pos++;
                        continue;
                    case '+':
                        lexemes.add(new Lexeme(LexemeType.OP_PLUS, c));
                        pos++;
                        continue;
                    case '-':
                        lexemes.add(new Lexeme(LexemeType.OP_MINUS, c));
                        pos++;
                        continue;
                    case '*':
                        lexemes.add(new Lexeme(LexemeType.OP_MUL, c));
                        pos++;
                        continue;
                    case '/':
                        lexemes.add(new Lexeme(LexemeType.OP_DIV, c));
                        pos++;
                        continue;
                    default:
                        if (c <= '9' && c >= '0' || c == '.') {
                            StringBuilder sb = new StringBuilder();
                            do {
                                sb.append(c);
                                pos++;
                                if (pos >= expText.length()) {
                                    break;
                                }
                                c = expText.charAt(pos);
                            } while (c <= '9' && c >= '0' || c == '.');
                            lexemes.add(new Lexeme(LexemeType.NUMBER, sb.toString()));
                        } else {
                            if (c != ' ') {
                               Context.evaluate("#3");
                            }
                            pos++;
                        }
                }
            }
            lexemes.add(new Lexeme(LexemeType.EOF, ""));
            return lexemes;
        }

        public static double expr(LexemeBuffer lexemes) {
            Lexeme lexeme = lexemes.next();
            if (lexeme.type == LexemeType.EOF) {
                return 0;
            } else {
                lexemes.back();
                return plusminus(lexemes);
            }
        }

        public static double plusminus(LexemeBuffer lexemes) {
            double value = multdiv(lexemes);
            while (true) {
                Lexeme lexeme = lexemes.next();
                switch (lexeme.type) {
                    case OP_PLUS:
                        value += multdiv(lexemes);
                        break;
                    case OP_MINUS:
                        value -= multdiv(lexemes);
                        break;
                    case EOF:
                    case RIGHT_BRACKET:
                        lexemes.back();
                        return value;
                    default:
                        throw new RuntimeException("Unexpected token: " + lexeme.value
                                + " at position: " + lexemes.getPos());
                }
            }
        }

        public static double multdiv(LexemeBuffer lexemes) {
            double value = factor(lexemes);
            while (true) {
                Lexeme lexeme = lexemes.next();
                switch (lexeme.type) {
                    case OP_MUL:
                        value *= factor(lexemes);
                        break;
                    case OP_DIV:
//                        if(factor(lexemes)==0){
//                            Context.evaluate("#2");
                        value /= factor(lexemes);
//                        } else value /= factor(lexemes);
                        break;
                    case EOF:
                    case RIGHT_BRACKET:
                    case OP_PLUS:
                    case OP_MINUS:
                        lexemes.back();
                        return value;
                    default:
                        Context.evaluate("#2");
                }
            }
        }

         public static double factor(LexemeBuffer lexemes) {
             Lexeme lexeme = lexemes.next();
             switch (lexeme.type) {
                 case NUMBER:
                     return Double.parseDouble(lexeme.value);
                 case LEFT_BRACKET:
                     double value = plusminus(lexemes);
                     lexeme = lexemes.next();
                     if (lexeme.type != LexemeType.RIGHT_BRACKET) {
                         Context.evaluate("#3");
                     }
                     return value;
                 default:
                     throw new RuntimeException("Unexpected token: " + lexeme.value
                             + " at position: " + lexemes.getPos());
             }
         }
    }


}
