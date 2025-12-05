#!/bin/bash

# Common functions
function real_base_name () {
  target=$1

  (
  while true; do
    cd "$(dirname "$target")"
    target=$(basename "$target")
    link=$(readlink "$target")
    test "$link" || break
    target=$link
  done

  echo "$(pwd -P)"
  )
}

function output () {
    echo "$@"
}

function output_error () {
    >&2 echo "$@"
}

function fail () {
    >&2 echo "$@"
    exit 1
}

function usage {
    output_error "Usage: $BASH_SOURCE [--year <YEAR> --day <DAY> --token <TOKEN>]"
    output_error "--year: Year"
    output_error "--day: Day"
    output_error "--token: Token"
    exit 1
}

YEAR="$(date +"%Y")"
DAY="$(date +"%-d")"
TOKEN="$AOC_SESSION_TOKEN"

while [[ $# -gt 0 ]]
    do
    key="$1"
    case $key in
    --day)
      shift
      DAY="$1"
      ;;
    --year)
      shift
      YEAR="$1"
      ;;
    --token)
      shift
      TOKEN="$1"
		;;
    *)
      output_error "Unknown option: $key"
      usage
    ;;
    esac
    shift # past argument or value
done

SCRIPT_DIR=$(real_base_name "$0")

cd "$SCRIPT_DIR"

mkdir -p "./src/main/resources/$YEAR" || fail "Failed to create directory"

HTML=$(curl -f --cookie "session=$TOKEN" -s -L -S --compressed -H "User-Agent: aoc-example-fetcher" "https://adventofcode.com/$YEAR/day/$DAY") || fail "Failed to get example"

INDEX="1"
while [[ true ]]; do
  echo "$HTML" | python3 -c '
import sys, re, html

# Which block to pick (1-based index)
n = int(sys.argv[1]) if len(sys.argv) > 1 else 1

data = sys.stdin.read()

# More robust regex:
# - (?i) case-insensitive
# - (?s) dot matches newlines
# - allow attributes on <code>, allow whitespace before </pre>
pattern = re.compile(r"(?is)<pre><code[^>]*>(.*?)</code>\s*</pre>")

blocks = pattern.findall(data)

if not blocks:
    sys.stderr.write("No <pre><code> blocks found in HTML.\n")
    # Optional: uncomment to debug first 500 chars of HTML
    # sys.stderr.write(data[:500] + "\n")
    sys.exit(1)

if not (1 <= n <= len(blocks)):
    sys.exit(1)

block = blocks[n - 1]

# Decode HTML entities (&lt;, &gt;, &amp;, etc.)
block = html.unescape(block)

# Print the block as-is (keeping internal newlines)
sys.stdout.write(block.rstrip("\n") + "\n")
' "$INDEX" > /tmp/sample.txt

  OUTPUT="$(cat /tmp/sample.txt)"
  if [ -z "$OUTPUT" ]; then
    break
  fi
  mv /tmp/sample.txt "./src/main/resources/$YEAR/day${DAY}-sample${INDEX}.txt"

  output "Found sample $INDEX:"
  output "$OUTPUT"
  output ""

  INDEX=$(( $INDEX + 1 ))
done
