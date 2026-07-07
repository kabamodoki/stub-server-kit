@echo off
REM load-env.bat
REM .local.env の内容を現在の cmd セッションに一時的に設定します。
REM セッションを閉じると設定は消えます。
REM
REM 使い方:
REM   CALL env\load-env.bat
REM   CALL env\load-env.bat path\.local.env
REM
REM ※ 必ず CALL を付けてください。CALL なしだと現在のセッションに反映されません。
REM ※ # 始まりのコメント行は無視されます（空白行も同様）。

SET _ENV_FILE=%~1
IF "%_ENV_FILE%"=="" SET _ENV_FILE=.local.env

IF NOT EXIST "%_ENV_FILE%" (
    echo Warning: env file not found: %_ENV_FILE%
    EXIT /B 1
)

FOR /F "usebackq eol=# tokens=1,* delims==" %%A IN ("%_ENV_FILE%") DO (
    IF NOT "%%A"=="" (
        SET %%A=%%B
        echo   SET %%A
    )
)

echo Loaded env vars from %_ENV_FILE%
SET _ENV_FILE=
