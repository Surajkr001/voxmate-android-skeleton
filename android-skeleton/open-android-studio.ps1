# Try to open Android Studio with the project folder; fallback to VS Code
$projectPath = "$PSScriptRoot"
$studioPaths = @(
    "C:\\Program Files\\Android\\Android Studio\\bin\\studio64.exe",
    "C:\\Program Files\\Android\\Android Studio\\bin\\studio.exe"
)
$opened = $false
foreach ($p in $studioPaths) {
    if (Test-Path $p) {
        Start-Process -FilePath $p -ArgumentList $projectPath
        $opened = $true
        break
    }
}
if (-not $opened) {
    if (Get-Command code -ErrorAction SilentlyContinue) {
        code $projectPath
    } else {
        Write-Output "Neither Android Studio nor VS Code found in PATH. Open the folder in Android Studio manually: $projectPath"
    }
}
