package main

import (
    "bufio"
    "fmt"
    "os"
    "regexp"
    "strconv"
    "strings"
)

func main() {
    variables := make(map[string]int)
    scanner := bufio.NewScanner(os.Stdin)

    for {
        fmt.Print("> ")
        scanner.Scan()
        inp := scanner.Text()

        if len(inp) == 0 {
            continue
        } else if inp == "/exit" {
            fmt.Println("Bye!")
            break
        } else if inp == "/help" {
            fmt.Println("Smart calculator. Separate numbers and operators with spaces, angle brackets should not be separated: (2 + 2).")
            fmt.Println("It will automatically detect errors. Supported operators: (+) (-) (*) (/)")
        } else if strings.HasPrefix(inp, "/") {
            fmt.Println("Unknown command")
        } else if strings.Contains(inp, "=") {
            parts := strings.Split(inp, "=")
            if len(parts) != 2 {
                fmt.Println("Invalid assignment")
            } else {
                varName := strings.TrimSpace(parts[0])
                varValue := strings.TrimSpace(parts[1])
                if _, err := strconv.Atoi(varValue); err != nil {
                    if val, ok := variables[varValue]; ok {
                        variables[varName] = val
                    } else {
                        fmt.Println("Unknown variable")
                    }
                } else {
                    num, err := strconv.Atoi(varValue)
                    if err != nil {
                        fmt.Println("Invalid identifier")
                    } else {
                        variables[varName] = num
                    }
                }
            }
        } else if len(strings.Fields(inp)) == 1 {
            if _, err := strconv.Atoi(inp); err != nil {
                if val, ok := variables[inp]; ok {
                    fmt.Println(val)
                } else {
                    fmt.Println("Unknown variable")
                }
            } else {
                num, err := strconv.Atoi(inp)
                if err != nil {
                    fmt.Println("Invalid identifier")
                } else {
                    fmt.Println(num)
                }
            }
        } else if len(strings.Fields(inp))%2 == 0 {
            fmt.Println("Invalid expression")
        } else if strings.Count(inp, "(") != strings.Count(inp, ")") {
            fmt.Println("Invalid expression")
        } else {
            numSum := 0
            var operator rune

            for key, val := range variables {
                inp = strings.ReplaceAll(inp, key, strconv.Itoa(val))
            }

            for strings.Contains(inp, "(") {
                start := strings.Index(inp, "(")
                finish := strings.Index(inp, ")")
                result := evalExpression(inp[start+1 : finish])
                inp = inp[:start] + strconv.Itoa(result) + inp[finish+1:]
            }

            for strings.Contains(inp, " * ") || strings.Contains(inp, " / ") {
                re := regexp.MustCompile(`[a-zA-Z0-9]+ (\*|/) [a-zA-Z0-9]+`)
                matches := re.FindStringSubmatch(inp)
                match := matches[0]
                result := evalExpression(match)
                inp = strings.ReplaceAll(inp, match, strconv.Itoa(result))
            }

            nums := strings.Fields(inp)
            for _, num := range nums {
                if n, err := strconv.Atoi(num); err == nil {
                    if operator == 0 {
                        numSum = n
                    } else if operator == '+' {
                        numSum += n
                    } else if operator == '-' {
                        numSum -= n
                    }
                } else if val, ok := variables[num]; ok {
                    if operator == 0 {
                        numSum = val
                    } else if operator == '+' {
                        numSum += val
                    } else if operator == '-' {
                        numSum -= val
                    }
                } else {
                    operator = []rune(num)[0]
                }
            }
            fmt.Println(numSum)
        }
    }
}

func evalExpression(expr string) int {
    parts := strings.Fields(expr)
    a, _ := strconv.Atoi(parts[0])
    b, _ := strconv.Atoi(parts[2])
    switch parts[1] {
    case "+":
        return a + b
    case "-":
        return a - b
    case "*":
        return a * b
    case "/":
        return a / b
    }
    return 0
}
