height = 80
width = 7
grid = [['.' for j in range(width)] for i in range(height)]
highest_rock = -1
 
 
shape1 = ((0, 0), (0, 1), (0, 2), (0, 3))
shape2 = ((0, 1), (1, 0), (1, 1), (1, 2), (2, 1))
shape3 = ((0, 0), (0, 1), (0, 2), (1, 2), (2, 2))
shape4 = ((0, 0), (1, 0), (2, 0), (3, 0))
shape5 = ((0, 0), (0, 1), (1, 0), (1, 1))
 
 
shapes = (shape1, shape2, shape3, shape4, shape5)
 
 
with open('input', 'r') as f:
    moves = f.readline().strip()
 
shape_it = 0
move_it = 0
 
resting_rocks = 0
while resting_rocks < 22:
    # if resting_rocks > 0 and shape_it == 0 and move_it == 18:
        # for row in grid[::-1]:
        #     print(''.join(row))
        # print("resting rocks:", resting_rocks, "highest rock:", highest_rock)
        # break
    # spawn rock
    for highest_point in range(len(grid)-1, -1, -1):
        if '#' in grid[highest_point]:
            break
    else:
        highest_point = -1
    spawn_point = (highest_point + 4, 2)
    shape = tuple((spawn_point[0] + i, spawn_point[1] + j) for (i, j) in shapes[shape_it])
    shape_it = (shape_it+1) % len(shapes)
    done = False
    while not done:
        # gas movement
        move = moves[move_it]
        move_it = (move_it+1) % len(moves)
        dj = 1 if move == '>' else -1
        new_shape = tuple((i, j+dj) for (i, j) in shape)
        for point in new_shape:
            if point[1] < 0 or point[1] >= 7 or grid[point[0]][point[1]] == '#':
                break
        else:
            shape = new_shape
 
        # downwards movement
        new_shape = tuple((i-1, j) for (i, j) in shape)
        for point in new_shape:
            if point[0] < 0 or grid[point[0]][point[1]] == '#':
                done = True
                resting_rocks += 1
                highest_point_on_shape = max(i for (i, _) in shape)
                height_increase = highest_point_on_shape - highest_point
                if height_increase > 0:
                    highest_rock += height_increase
                for p in shape:
                    grid[p[0]][p[1]] = '#'
                midpoint = height//2
                if highest_point_on_shape > midpoint:
                    for _ in range(highest_point_on_shape-midpoint):
                        del grid[0]
                        grid.append(['.' for j in range(width)])
                break
        else:
            shape = new_shape
 
for row in grid[::-1]:
    print(''.join(row))
print("part 1:", highest_rock+1)
 
# manual stuff for part 2:
 
rocks = 1750
rock_height = 2776
num_rocks_increase = 1745
rock_height_increase = 2752
 
target = 1000000000000
intervals = target // 1745 -1
 
final_rocks = rocks + intervals*num_rocks_increase
final_height = rock_height + intervals*rock_height_increase
 
print(final_rocks, final_height)
gap = target - final_rocks
print(gap)
 
print(final_height + (4362 - 2776) + 1)





