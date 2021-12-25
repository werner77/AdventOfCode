import sys

def flip(rule):
    return tuple(''.join(reversed(line)) for line in rule)

def rotate(rule):
    result = []
    for x in range(len(rule)):
        line = []
        for y in range(len(rule)):
            line.append(rule[len(rule) - 1 - y][x])
        line = ''.join(tuple(line))
        result.append(line)
    return tuple(result)


def rotate_flip(rule):
    result = set()
    result.add(rule)
    result.add(rotate(rule))
    result.add(rotate(rotate(rule)))
    result.add(rotate(rotate(rotate(rule))))
    for r in tuple(result):
        result.add(flip(r))
    return result

rules = {}
for line in open(sys.argv[1]).readlines():
    line = line.strip()
    i, o = line.split(" => ")
    i = tuple(i.split('/'))
    for ri in rotate_flip(i):
        rules[ri] = tuple(o.split('/'))

start = ('.#.', '..#', '###')

def subgrid(grid, x, y, l):
    result = []
    for yi in range(y * l, y * l + l):
        line = ''
        for xi in range(x * l, x * l + l):
            line += grid[yi][xi]
        result.append(line)
    return tuple(result)

def do(grid, l):
    result = []
    for x in range(len(grid) / l):
        for y in range(len(grid) / l):
            sg = subgrid(grid, x, y, l)
            newsg = rules[sg]
            for n, line in enumerate(newsg):
                if y * len(newsg) + n >= len(result):
                    result.append('')
                result[y * len(newsg) + n] = result[y * len(newsg) + n] + line
    return result

def run(grid):
    if len(grid) % 2 == 0:
        newgrid = do(grid, 2)
    else:
        newgrid = do(grid, 3)
    return newgrid

def on(grid):
    return sum(line.count('#') for line in grid)


grid = start
for n in range(18):
    grid = run(grid)
    print n + 1, len(grid), on(grid)
print on(grid)