# Start the local mock functions server for VoxMate
Set-Location -Path "$PSScriptRoot\functions"
if (!(Test-Path node_modules)) {
    Write-Output "Installing node modules..."
    npm install
}
Write-Output "Starting mock server on http://localhost:5000"
npm start
