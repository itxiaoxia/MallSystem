$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $PSScriptRoot
$runtimeRoot = Join-Path $projectRoot 'runtime'
$javaHomeCandidates = @()

if (-not [string]::IsNullOrWhiteSpace($env:JAVA_HOME)) {
    $javaHomeCandidates += $env:JAVA_HOME
}
$machineJavaHome = [Environment]::GetEnvironmentVariable('JAVA_HOME', 'Machine')
if (-not [string]::IsNullOrWhiteSpace($machineJavaHome)) {
    $javaHomeCandidates += $machineJavaHome
}

$javaHome = $javaHomeCandidates |
    Select-Object -Unique |
    Where-Object { Test-Path (Join-Path $_ 'bin\java.exe') } |
    Select-Object -First 1
if ([string]::IsNullOrWhiteSpace($javaHome)) {
    throw '未找到 JDK 17，请先配置 JAVA_HOME。'
}

$env:JAVA_HOME = $javaHome
$env:Path = "$(Join-Path $javaHome 'bin');$env:Path"
$javaVersion = (& java -version 2>&1 | Out-String)
if ($javaVersion -notmatch 'version "17(\.|")') {
    throw "当前 Java 不是 17：$javaVersion"
}

$mysqlService = Get-Service -Name 'MySQL84' -ErrorAction SilentlyContinue
if ($null -eq $mysqlService) {
    throw '未找到 MySQL84 服务。'
}
if ($mysqlService.Status -ne 'Running') {
    Start-Service -Name 'MySQL84'
    $mysqlService.WaitForStatus('Running', [TimeSpan]::FromSeconds(15))
}
if (-not (Test-NetConnection -ComputerName '127.0.0.1' -Port 3306 -InformationLevel Quiet -WarningAction SilentlyContinue)) {
    throw 'MySQL84 未监听 127.0.0.1:3306。'
}

Push-Location $projectRoot
try {
    & .\mvnw.cmd -DskipTests package
    if ($LASTEXITCODE -ne 0) {
        throw 'Maven 构建失败。'
    }

    $jar = Get-ChildItem -LiteralPath (Join-Path $projectRoot 'target') -Filter '*.jar' |
        Where-Object { $_.Name -notlike '*.original' } |
        Sort-Object LastWriteTime -Descending |
        Select-Object -First 1
    if ($null -eq $jar) {
        throw '未找到可启动的 JAR 文件。'
    }

    New-Item -ItemType Directory -Path $runtimeRoot -Force | Out-Null
    $stdoutLog = Join-Path $runtimeRoot 'mall-system.out.log'
    $stderrLog = Join-Path $runtimeRoot 'mall-system.err.log'
    $pidFile = Join-Path $runtimeRoot 'mall-system.pid'
    $java = Join-Path $javaHome 'bin\java.exe'
    $process = Start-Process -FilePath $java -ArgumentList @('-jar', $jar.FullName) `
        -WorkingDirectory $projectRoot -RedirectStandardOutput $stdoutLog `
        -RedirectStandardError $stderrLog -WindowStyle Hidden -PassThru
    Set-Content -LiteralPath $pidFile -Value $process.Id -Encoding ascii

    $healthUrl = 'http://127.0.0.1:8080/actuator/health'
    $ready = $false
    for ($attempt = 0; $attempt -lt 30; $attempt++) {
        if ($process.HasExited) {
            throw "应用启动失败，请查看 $stderrLog"
        }
        try {
            $health = Invoke-WebRequest -Uri $healthUrl -UseBasicParsing -TimeoutSec 2
            if ($health.StatusCode -eq 200) {
                $ready = $true
                break
            }
        } catch {
            Start-Sleep -Seconds 1
        }
    }
    if (-not $ready) {
        throw "应用未在预期时间内就绪，请查看 $stdoutLog 和 $stderrLog"
    }

    Write-Output "MallSystem 已启动，PID=$($process.Id)"
    Write-Output "健康检查：$healthUrl"
    Write-Output "日志：$stdoutLog"
} finally {
    Pop-Location
}
