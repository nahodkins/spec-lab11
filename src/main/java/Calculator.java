import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculator {

    private static final String BAD_FORMAT_EXPRESSION_ERROR = "The given expression '%s' is bad format";
    private static final String NULL_DIVISION_ERROR_MSG = "Null division error";
    private static final String BAD_NUMBER_ERROR_MSG = "The '%s' is bad number";

    private static final String EXPRESSION_REGEX = "\\d+\\s?[*/+-]\\s?\\d+";
    private static final String NUMBER_REGEX = "\\d+";
    private static final String SIGN_REGEX = "[*/+-]";

    private Integer[] extractNumbers(String expression) throws CalculatorException {
        Pattern pattern = Pattern.compile(NUMBER_REGEX);
        Matcher matcher = pattern.matcher(expression);
        List<Integer> integers = new ArrayList<>();

        while (matcher.find()) {
            try {
                integers.add(Integer.parseInt(matcher.group()));
            } catch (NumberFormatException e) {
                throw new CalculatorException(String.format(BAD_NUMBER_ERROR_MSG, matcher.group()));
            }
        }
        return integers.toArray(Integer[]::new);
    }

    private String extractSign(String expression) {
        Pattern pattern = Pattern.compile(SIGN_REGEX);
        Matcher matcher = pattern.matcher(expression);

        if (matcher.find()) {
            return matcher.group();
        } else {
            return "";
        }
    }

    private double doCalculation(Integer[] integers, String sign) throws CalculatorException {
        int first = integers[0];
        int second = integers[1];

        if (second == 0 && sign.equals("/")) {
            throw new CalculatorException(NULL_DIVISION_ERROR_MSG);
        }
        return switch (sign) {
            case "+" -> first + second;
            case "-" -> first - second;
            case "*" -> first * second;
            case "/" -> (double) first / (double) second;
            default -> 0;
        };
    }

    public double calculate(String expression) throws CalculatorException {
        if (!expression.matches(EXPRESSION_REGEX)) {
            throw new CalculatorException(String.format(BAD_FORMAT_EXPRESSION_ERROR, expression));
        }
        Integer[] integers = extractNumbers(expression);
        String sign = extractSign(expression);
        return doCalculation(integers, sign);
    }
}
