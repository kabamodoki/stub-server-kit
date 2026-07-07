# load-env.ps1
# .local.env の内容を現在の PowerShell セッションに一時的に設定します。
# セッションを閉じると設定は消えます。
#
# 使い方:
#   . .\env\load-env.ps1
#   . .\env\load-env.ps1 path/to/.local.env
#
# ※ 先頭の ". " (ドット＋スペース) が必須です（dot-source）

param(
    [string]$EnvFile = ".local.env"
)

if (-not (Test-Path $EnvFile)) {
    Write-Warning "Env file not found: $EnvFile"
    return
}

$count = 0
Get-Content $EnvFile | ForEach-Object {
    $line = $_.Trim()
    if (-not $line -or $line.StartsWith('#')) { return }
    if ($line -notmatch '^([A-Za-z_][A-Za-z0-9_]*)=(.*)$') { return }

    $key   = $Matches[1]
    $value = $Matches[2]

    # シングル・ダブルクォートを除去
    if ($value -match '^"(.*)"$' -or $value -match "^'(.*)'$") {
        $value = $Matches[1]
    }

    Set-Item -Path "Env:$key" -Value $value
    Write-Host "  SET $key" -ForegroundColor Cyan
    $count++
}

Write-Host "Loaded $count variable(s) from $EnvFile" -ForegroundColor Green
