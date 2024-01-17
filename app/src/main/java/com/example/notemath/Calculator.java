package com.example.notemath;

import java.math.RoundingMode;
import java.util.Objects;
import java.util.Scanner;
import java.math.BigDecimal;

public class Calculator {
    static final String numberRegex = "-?[0-9]+(\\.[0-9]+)?";
    public String doMath(String expression) {
        if (expression.isEmpty()) {
            return new Exception("Error: Not a valid expression.").getMessage();
        }
        expression = getCleanExpression(expression);
        int currentPrio = 3;
        while (currentPrio >= 0) {
            System.out.println(expression);
            if (currentPrio != 3) {
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
            } else {
                String match = findParentheses(expression);
                if (match.isEmpty()) {
                    currentPrio--;
                } else {
                    expression = expression.replace(match, doMath(match.substring(1, match.length() - 1)));
                }
            }
        }
        expression = roundExpression(expression);
        if (isValidRes(expression)){
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
        String operator = sc.findInLine("[\\+\\-\\*\\/\\^]");
        BigDecimal secondNum = new BigDecimal(sc.findInLine(numberRegex));
        sc.close();
        switch(operator) {
            case "+":
                return firstNum.add(secondNum).toString();
            case "-":
                return firstNum.subtract(secondNum).toString();
            case "*":
                return firstNum.multiply(secondNum).toString();
            case "/":
                return firstNum.divide(secondNum, 5, RoundingMode.HALF_UP).toString();
            case "^":
                return firstNum.pow(secondNum.intValue()).toString();
            default:
                return "";
        }
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
        Scanner sc = new Scanner(expression);
        String match = sc.findInLine("\\.[0-9]+");
        sc.close();
        if (match != null && match.equals(".0")) {
            expression = expression.substring(0, expression.length() - 2);
        }
        return expression;
    }

    private String getCleanExpression(String expression) {
        expression = expression.replaceAll("^\\d\\.\\s", "");
        System.out.println(expression);
        expression = expression.replaceAll("\\s+", "");
        System.out.println(expression);
        Scanner sc = new Scanner(expression);
        expression = sc.findInLine("[\\d\\(\\)\\+\\-\\*\\/\\^\\.]*$");
        sc.close();
        if (expression == null) {
            expression = "";
        }
        return expression;
    }
}