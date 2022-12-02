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
    output_error "Usage: $BASH_SOURCE [--year <YEAR> --day <DAY> --token <TOKEN>] --part <1|2> <answer>"
    output_error "--year: Year"
    output_error "--day: Day"
    output_error "--token: Token"
    output_error "--part: 1 or 2"
    exit 1
}

YEAR="$(date +"%Y")"
DAY="$(date +"%-d")"
TOKEN="$AOC_SESSION_TOKEN"
PART=""
ANSWER=""

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
        --part)
            shift
            PART="$1"
        ;;
        --token)
			shift
			TOKEN="$1"
		;;
        *)
            ANSWER="$1"    
            break
        ;;
    esac
    shift # past argument or value
done

if [ -z "$PART" ]; then 
    usage
fi

SCRIPT_DIR=$(real_base_name "$0")

cd "$SCRIPT_DIR"

curl -X POST --cookie "session=$TOKEN" --header "Cache-Control: no-cache" --header "Content-Type: application/x-www-form-urlencoded" -d "level=${PART}&answer=${ANSWER}" -s "https://adventofcode.com/$YEAR/day/$DAY/answer" | grep "<article>"
