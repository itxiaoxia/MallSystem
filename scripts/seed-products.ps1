$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $PSScriptRoot
$sqlPath = Join-Path $projectRoot 'database\seed-products.sql'
if (-not (Test-Path -LiteralPath $sqlPath)) {
    throw "找不到 SQLite 商品种子 SQL：$sqlPath"
}

$databasePath = if ([string]::IsNullOrWhiteSpace($env:MALLSYSTEM_DB_PATH)) {
    Join-Path ([Environment]::GetFolderPath('LocalApplicationData')) 'MallAgent\MallAgent\config.db'
} else {
    [Environment]::ExpandEnvironmentVariables($env:MALLSYSTEM_DB_PATH)
}
$sqliteCommand = Get-Command sqlite3.exe -ErrorAction SilentlyContinue
if ($null -eq $sqliteCommand) {
    throw '未找到 sqlite3.exe；MallSystem 正常启动时会自动创建表并写入内置商品，不需要手工执行此脚本。'
}
if (-not (Test-Path -LiteralPath $databasePath)) {
    throw "SQLite 数据库不存在：$databasePath。请先启动 MallSystem，让 JPA 完成建表和内置种子初始化。"
}

$sourcePath = $sqlPath.Replace('\', '/')
& $sqliteCommand.Source $databasePath ".read $sourcePath"
if ($LASTEXITCODE -ne 0) {
    throw 'SQLite 商品种子执行失败，请确认应用已经启动过并完成建表。'
}

$count = & $sqliteCommand.Source $databasePath "SELECT COUNT(*) FROM products WHERE product_code IN ('COCA-330ML','ORANGE-500ML','WATER-550ML','CHIPS-ORIGINAL','CHOCO-CLASSIC');"
if ($LASTEXITCODE -ne 0 -or $count.Trim() -ne '5') {
    throw '商品种子校验失败，预期存在5个商品。'
}
Write-Output '商品种子已幂等写入5个商品。'
