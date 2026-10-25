# PowerShell script to run the Financial Agent Orchestrator
Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host " Starting Financial Agent Orchestrator (Port 8080)..." -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

Set-Location -Path "$PSScriptRoot/financial-agent-orchestrator"

# Check if mvn is available in PATH
if (Get-Command mvn -ErrorAction SilentlyContinue) {
    mvn spring-boot:run
} else {
    Write-Host "[INFO] 'mvn' not detected in PATH. Checking for Maven installation..." -ForegroundColor Yellow
    $mvnPaths = @(
        "$env:ProgramFiles\apache-maven*\bin\mvn.cmd",
        "$env:LOCALAPPDATA\Programs\apache-maven*\bin\mvn.cmd",
        "C:\Program Files\Maven*\bin\mvn.cmd",
        "C:\maven*\bin\mvn.cmd"
    )
    $foundMvn = Resolve-Path $mvnPaths -ErrorAction SilentlyContinue | Select-Object -First 1

    if ($foundMvn) {
        Write-Host "[INFO] Found Maven at $foundMvn" -ForegroundColor Green
        & $foundMvn spring-boot:run
    } else {
        Write-Host "[ERROR] Apache Maven was not found in PATH or standard directories." -ForegroundColor Red
        Write-Host "Please ensure Maven is installed and added to your system PATH, or run via your IDE's Spring Boot runner." -ForegroundColor Yellow
        Write-Host "Alternative: In VS Code, open the 'Spring Boot Dashboard' extension or right-click 'pom.xml' -> 'Run Maven Goals' -> 'spring-boot:run'." -ForegroundColor White
    }
}
