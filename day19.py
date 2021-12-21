from itertools import combinations,product

orientations  = ([(a,b,c,i,j,k) for a,b,c in [(1,1,-1),(1,-1,1),(-1,1,1),(-1,-1,-1)] for i,j,k in [(2,1,0),(1,0,2),(0,2,1)]]
                +[(a,b,c,i,j,k) for a,b,c in [(1,1,1),(1,-1,-1),(-1,1,-1),(-1,-1,1)] for i,j,k in [(0,1,2),(1,2,0),(2,0,1)]])

def find_rotation(scan,rebased0):
    for rot,rot_scan in rotations(scan).items():
        rebased = {p1: {psub(p1,p2) for p2 in rot_scan} for p1 in rot_scan}
        for p1,p2 in product(rebased0,rebased):
            if len(rebased0[p1]&rebased[p2])>11: return p1,p2,rot

def make_absolute(scanners):
    absolute,*task_list = scanners
    scanner_locs = {(0,0,0)}
    while task_list:
        scan = task_list.pop(0)
        rebased0 = {p1: {psub(p1,p2) for p2 in absolute} for p1 in absolute}
        result = find_rotation(scan,rebased0)
        if result is None:
            task_list.append(scan)
        else:
            p1,p2,rot = result
            absolute |= {padd(rotate(s,*rot), psub(p1,p2)) for s in scan}
            scanner_locs.add(padd((0,0,0), psub(p1,p2)))
    return len(absolute), scanner_locs

def psub(p1,p2): return p1[0]-p2[0],p1[1]-p2[1],p1[2]-p2[2]
def padd(p1,p2): return p1[0]+p2[0],p1[1]+p2[1],p1[2]+p2[2]
def rotate(s,a,b,c,i,j,k): return (a*s[i],b*s[j],c*s[k])
def rotations(scan): return {r: {rotate(s,*r) for s in scan} for r in orientations}
def mhd(a,b): return abs(a[0]-b[0])+abs(a[1]-b[1])+abs(a[2]-b[2])

filename = "./src/main/resources/2021/day19.txt"
scanners = [{eval(line) for line in scanner.splitlines() if '--' not in line}
            for scanner in open(filename).read().split('\n\n')]
beacons, scanner_locs = make_absolute(scanners)

print('part1:', beacons)
print('part2:', max(mhd(a,b) for a,b in combinations(scanner_locs,2)))