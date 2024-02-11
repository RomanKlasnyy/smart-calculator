import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmartCalculator {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        HashMap<String, Integer> variables = new HashMap<>();

        while (true) {
            String inp = scanner.nextLine().trim();
            if (inp.isEmpty()) {
                continue;
            } else if (inp.equals("/exit")) {
                System.out.println("Bye!");
                break;
            } else if (inp.equals("/help")) {
                System.out.println("Smart calculator. Separate numbers and operators with spaces, angle brackets should not be separated: (2 + 2).");
                System.out.println("It will automatically detect errors. Supported operators: (+) (-) (*) (/)");
            } else if (inp.startsWith("/")) {
                System.out.println("Unknown command");
            } else if (inp.contains("=")) {
                String[] parts = inp.split("=");
                if (parts.length != 2) {
                    System.out.println("Invalid assignment");
                } else {
                    String varName = parts[0].trim();
                    String varValue = parts[1].trim();
                    if (varName.matches("[a-zA-Z]+")) {
                        if (varValue.matches("[a-zA-Z]+")) {
                            try {
                                variables.put(varName, variables.get(varValue));
                            } catch (NullPointerException e) {
                                System.out.println("Unknown variable");
                            }
                        } else {
                            try {
                                variables.put(varName, Integer.parseInt(varValue));
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid identifier");
                            }
                        }
                    } else {
                        System.out.println("Invalid identifier");
                    }
                }
            } else if (inp.split("\\s+").length == 1) {
                if (inp.matches("[a-zA-Z]+")) {
                    try {
                        System.out.println(variables.get(inp));
                    } catch (NullPointerException e) {
                        System.out.println("Unknown variable");
                    }
                } else {
                    try {
                        System.out.println(Integer.parseInt(inp));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid identifier");
                    }
                }
            } else if (inp.split("\\s+").length % 2 == 0) {
                System.out.println("Invalid expression");
            } else if (countOccurrences(inp, '(') != countOccurrences(inp, ')')) {
                System.out.println("Invalid expression");
            } else {
                try {
                    int numSum = 0;
                    char operator = ' ';

                    for (String key : variables.keySet()) {
                        if (inp.contains(key)) {
                            inp = inp.replaceAll(key, String.valueOf(variables.get(key)));
                        }
                    }

                    while (inp.contains("(")) {
                        int start = inp.indexOf("(");
                        int finish = inp.indexOf(")");
                        inp = inp.substring(0, start) + evalExpression(inp.substring(start + 1, finish)) + inp.substring(finish + 1);
                    }

                    while (inp.contains(" * ") || inp.contains(" / ")) {
                        Pattern pattern = Pattern.compile("[a-zA-Z0-9]+ (\\*|/) [a-zA-Z0-9]+");
                        Matcher matcher = pattern.matcher(inp);
                        while (matcher.find()) {
                            String match = matcher.group();
                            inp = inp.replace(match, String.valueOf(evalExpression(match)));
                        }
                    }

                    String[] nums = inp.split("\\s+");
                    for (String num : nums) {
                        if (num.matches("-?\\d+")) {
                            if (operator == ' ') {
                                numSum = Integer.parseInt(num);
                            } else if (operator == '+') {
                                numSum += Integer.parseInt(num);
                            } else if (operator == '-') {
                                numSum -= Integer.parseInt(num);
                            }
                        } else if (num.matches("[a-zA-Z]+")) {
                            try {
                                if (operator == ' ') {
                                    numSum = variables.get(num);
                                } else if (operator == '+') {
                                    numSum += variables.get(num);
                                } else if (operator == '-') {
                                    numSum -= variables.get(num);
                                }
                            } catch (NullPointerException e) {
                                System.out.println("Unknown variable");
                            }
                        } else {
                            operator = num.charAt(0);
                        }
                    }
                    System.out.println(numSum);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid expression");
                }
            }
        }
    }

    private static int countOccurrences(String str, char c) {
        return str.length() - str.replace(String.valueOf(c), "").length();
    }

    private static int evalExpression(String expr) {
        String[] parts = expr.split("\\s+");
        int a = Integer.parseInt(parts[0]);
        int b = Integer.parseInt(parts[2]);
        return switch (parts[1]) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> a / b;
            default -> 0;
        };
    }
}
