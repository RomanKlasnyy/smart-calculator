import re

variables = {}

while True:
    inp = input()
    if not inp or len(inp) == 0:
        continue
    elif inp == '/exit':
        print('Bye!')
        break
    elif inp == '/help':
        print('Smart calculator. Separate numbers and operators with spaces, angle brackets should not be separated: (2 + 2).')
        print('It will automatically detect errors. Supported operators: (+) (-) (*) (/)')
    elif inp.startswith('/'):
        print('Unknown command')
    elif '=' in inp:
        inp = inp.split('=')
        if len(inp) != 2:
            print('Invalid assignment')
        else:
            inp = [x.strip() for x in inp]
            if inp[0].isalpha():
                if inp[1].isalpha():
                    try:
                        variables[inp[0]] = variables[inp[1]]
                    except KeyError:
                        print('Unknown variable')
                else:
                    try:
                        variables[inp[0]] = int(inp[1])
                    except ValueError:
                        print('Invalid identifier')
            else:
                print('Invalid identifier')
    elif len(inp.split()) == 1:
        if inp.strip().isalpha():
            try:
                print(variables[inp.strip()])
            except KeyError:
                print('Unknown variable')
        else:
            try:
                print(int(inp[0]))
            except ValueError:
                print('Invalid identifier')
    elif len(inp.split()) % 2 == 0:
        print('Invalid expression')
    elif inp.count('(') != inp.count(')'):
        print('Invalid expression')
    else:
        try:
            num_sum = 0
            operator = None

            for key in variables.keys():
                if key in inp:
                    inp = re.sub(key, f'{variables[key]}', inp)

            while '(' in inp:
                start = inp.index('(')
                finish = inp.index(')')
                inp = f'{inp[:start]}{eval(inp[start+1:finish])}{inp[finish+1:]}'

            while ' * ' in inp or ' / ' in inp:
                if '*' in inp:
                    inp = re.sub(r"[a-z0-9]+ \* [a-z0-9]+", lambda x: str(int(eval(x.group(0)))), inp)
                elif '/' in inp:
                    inp = re.sub(r"[a-z0-9]+ / [a-z0-9]+", lambda x: str(int(eval(x.group(0)))), inp)

            if len(inp.split()) == 1:
                try:
                    print(int(float(inp)))
                    continue
                except ValueError:
                    print('Invalid identifier')
                    continue

            for num in inp.split():
                if num.isdigit() or num[1:].isdigit():
                    if not operator:
                        num_sum = int(num)
                    elif operator == '+':
                        num_sum += int(num)
                    elif operator == '-':
                        num_sum -= int(num)
                elif num.isalpha():
                    try:
                        if not operator:
                            num_sum = variables[num]
                        elif operator == '+':
                            num_sum += variables[num]
                        elif operator == '-':
                            num_sum -= variables[num]
                    except KeyError:
                        print('Unknown variable')
                elif '+' in num:
                    operator = '+'
                elif '-' in num:
                    oc = num.count('-')
                    if oc % 2 == 0:
                        operator = '+'
                    else:
                        operator = '-'
                elif '*' in num:
                    if num.count('*') == 1:
                        operator = '*'
                    else:
                        raise ValueError
                elif '/' in num:
                    if num.count('/') == 1:
                        operator = '/'
                    else:
                        raise ValueError
                else:
                    continue
            print(num_sum)
        except ValueError:
            print('Invalid expression')
