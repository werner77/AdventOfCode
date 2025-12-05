#!/bin/bash

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
    output_error "Usage: $BASH_SOURCE [--year <YEAR> --day <DAY>]"
    output_error "--year: Year, defaults to current"
    output_error "--day: Day, defaults to current"
    exit 1
}

YEAR="$(date +"%Y")"
DAY="$(date +"%-d")"
TOKEN="$AOC_SESSION_TOKEN"

while [[ $# -gt 0 ]]
    do
    key="$1"
    case $key in
    --help)
      usage
    ;;
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

./create-source.sh --day "$DAY" --year "$YEAR" || fail "Could not create source file"
./get-input.sh --day "$DAY" --year "$YEAR" --token "$TOKEN" || fail "Could not get input"
./get-sample.sh --day "$DAY" --year "$YEAR" --token "$TOKEN" || fail "Could not get input"
