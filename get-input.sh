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

mkdir -p "./src/main/resources/$YEAR"
curl --cookie "session=$TOKEN" -s "https://adventofcode.com/$YEAR/day/$DAY/input" > "./src/main/resources/${YEAR}/day${DAY}.txt"
cat "./src/main/resources/${YEAR}/day${DAY}.txt"

