@echo off
REM Runs all 7 test cases: safe mode once, unsafe mode 5 times each.
REM Usage (CMD):        run_all.bat
REM Usage (PowerShell): .\run_all.bat | Tee-Object output.txt

javac Shahariar_Thread.java || exit /b 1

for %%c in (1:1000 2:10000 5:10000 10:50000 20:50000 50:50000 100:50000) do (
  for /f "tokens=1,2 delims=:" %%a in ("%%c") do (
    echo =================== N=%%a  K=%%b ===================
    echo --- Experiment A ^(true^) ---
    java Shahariar_Thread %%a %%b true
    for %%r in (1 2 3 4 5) do (
      echo --- Experiment B ^(false^) Run %%r ---
      java Shahariar_Thread %%a %%b false
    )
    echo.
  )
)
