#!/bin/bash
# Runs all 7 test cases: safe mode once, unsafe mode 5 times each.
# Usage: bash run_all.sh | tee output.txt

javac Shahariar_Thread.java || exit 1

CASES="1:1000 2:10000 5:10000 10:50000 20:50000 50:50000 100:50000"

for c in $CASES; do
  N=${c%%:*}; K=${c##*:}
  echo "=================== N=$N  K=$K ==================="
  echo "--- Experiment A (true) ---"
  java Shahariar_Thread $N $K true
  for r in 1 2 3 4 5; do
    echo "--- Experiment B (false) Run $r ---"
    java Shahariar_Thread $N $K false
  done
  echo
done
