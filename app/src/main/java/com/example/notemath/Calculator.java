package com.example.notemath;

import java.math.RoundingMode;
import java.util.Objects;
import java.util.Scanner;
import java.math.BigDecimal;

public class Calculator {
    static final String numberRegex = "-?[0-9]+(\\.[0-9]+)?";
    private int roundPrecision;
    //private int maxLength = 10;

    public Calculator(int roundPrecision) {
        this.roundPrecision = roundPrecision;
    }

    public String doMath(String expression) {
        expression = expression.replaceAll("\\s", "");
        int currentPrio = 5;
        while (currentPrio >= 0) {
            //System.out.println(expression);
            if (currentPrio != 5) {
                if (currentPrio < 3) {
                    expression = cleanSubtraction(expression);
                    String opRegex = operatorRegex(currentPrio);
                    Scanner sc = new Scanner(expression);
                    String match = sc.findInLine(numberRegex + opRegex + numberRegex);
                    sc.close();
                    if (match == null) {
                        currentPrio--;
                    } else {
                        expression = expression.replace(match, simpleExpression(match));
                    }
                }
                else {
                    expression = cleanSubtraction(expression);
                    String opRegex = operatorRegex(currentPrio);
                    Scanner sc = new Scanner(expression);
                    String match = sc.findInLine(numberRegex + opRegex);
                    sc.close();
                    if (match == null) {
                        currentPrio--;
                    } else {
                        expression = expression.replace(match, simpleExpression(match));
                    }
                }
            } else {
                String match = findParentheses(expression);
                if (match.isEmpty()) {
                    currentPrio--;
                } else {
                    expression = expression.replace(match, doMath(match.substring(1, match.length() - 1)));
                }
            }
        }
        if (isValidRes(expression)){
            expression = roundExpression(expression);
            //expression = shortExpression(expression);
            return expression;
        }
        return new Exception("Error: Not a valid expression.").getMessage();
    }

    private Boolean isValidRes(String expression) {
        Scanner sc = new Scanner(expression);
        String match = sc.findInLine("^" + numberRegex + "$");
        sc.close();
        return match != null;
    }

    private String simpleExpression(String expression) {
        Scanner sc = new Scanner(expression);
        BigDecimal firstNum = new BigDecimal(sc.findInLine(numberRegex));
        String operator = sc.findInLine("[\\+\\-\\*\\/\\^\\!\\%]");
        String secondNumString = sc.findInLine(numberRegex);
        BigDecimal secondNum = new BigDecimal(0);
        if (secondNumString != null) {
            secondNum = new BigDecimal(secondNumString);
        }
        sc.close();
        switch(operator) {
            case "+":
                return firstNum.add(secondNum).toString();
            case "-":
                return firstNum.subtract(secondNum).toString();
            case "*":
                return firstNum.multiply(secondNum).toString();
            case "/":
                return firstNum.divide(secondNum, 15, RoundingMode.HALF_UP).toString();
            case "^":
                return firstNum.pow(secondNum.intValue()).toString();
            case "%":
                return firstNum.divide(new BigDecimal(100), 15, RoundingMode.HALF_UP).toString();
            case "!":
                return calculateFactorial(firstNum.intValue()).toString();
            default:
                return "";
        }
    }

    private BigDecimal calculateFactorial (int number) {
        BigDecimal factorial = BigDecimal.ONE;
        for (int i = 1; i <= number; i++) {
            factorial = factorial.multiply(new BigDecimal(i));
        }
        return factorial;
    }

    private String findParentheses(String expression) {
        int count = 0;
        String res = "";
        int i = 0;
        while ((res.isEmpty() || count > 0) && i < expression.length()) {
            char current = expression.charAt(i);
            if (current == '(') {
                count += 1;
            }
            if (count > 0) {
                res += current;
            }
            if (current == ')') {
                count -= 1;
            }
            i++;
        }
        return res;
    }

    private String cleanSubtraction(String expression) {
        Scanner sc = new Scanner(expression);
        String match = sc.findInLine("--");
        while (match != null) {
            expression = expression.replace(match, "");
            match = sc.findInLine("--");
        }
        sc.close();
        return expression;
    }

    private String operatorRegex(int i) {
        switch (i) {
            case 4:
                return "\\%";
            case 3:
                return "\\!";
            case 2:
                return "\\^";
            case 1:
                return "[\\*\\/]";
            case 0:
                return "[\\+\\-]";
            default:
                return "";
        }
    }

    private String roundExpression(String expression) {
        expression = new BigDecimal(expression).setScale(roundPrecision, RoundingMode.HALF_UP).toString();
        Scanner sc = new Scanner(expression);
        String match = sc.findInLine("(?<=\\..{0," + roundPrecision + "})0+$");
        sc.close();
        //System.out.println(expression);
        if (match != null) {
            expression = expression.replaceAll(match + "$", "");
        }
        if (expression.charAt(expression.length() - 1) == '.') {
            expression = expression.substring(0, expression.length() - 1);
        }
        //System.out.println(expression);
        return expression;
    }
    /*
    private String shortExpression(String expression) {
        if (expression.length() > maxLength) {
            String first = expression.substring(0, 1) + "." + expression.substring(1, maxLength);
            String second = expression.substring(maxLength, expression.length());
            first += "E+" + (second.length() + maxLength);
            expression = first;
        }
        return expression;
    }
    */
}