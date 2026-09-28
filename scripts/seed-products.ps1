$ErrorActionPreference = 'Stop'

$projectRoot = Split-Path -Parent $PSScriptRoot
$sqlPath = Join-Path $projectRoot 'database\seed-products.sql'
if (-not (Test-Path -LiteralPath $sqlPath)) {
    throw "找不到商品种子 SQL：$sqlPath"
}

$mysqlService = Get-Service -Name 'MySQL84' -ErrorAction SilentlyContinue
if ($null -eq $mysqlService -or $mysqlService.Status -ne 'Running') {
    throw 'MySQL84 未运行，请先启动 MySQL84 服务。'
}
if (-not (Test-NetConnection -ComputerName '127.0.0.1' -Port 3306 -InformationLevel Quiet -WarningAction SilentlyContinue)) {
    throw 'MySQL84 未监听 127.0.0.1:3306。'
}

$mysqlCommand = Get-Command mysql.exe -ErrorAction SilentlyContinue
$mysqlPath = if ($null -ne $mysqlCommand) {
    $mysqlCommand.Source
} else {
    $serviceInfo = Get-CimInstance Win32_Service -Filter "Name='MySQL84'"
    if ($null -ne $serviceInfo) {
        $serverPath = [regex]::Match($serviceInfo.PathName, '^"([^"]+mysqld\.exe)"').Groups[1].Value
        if (-not [string]::IsNullOrWhiteSpace($serverPath)) {
            Join-Path (Split-Path $serverPath) 'mysql.exe'
        }
    }
}
if ([string]::IsNullOrWhiteSpace($mysqlPath) -or -not (Test-Path -LiteralPath $mysqlPath)) {
    throw '未找到 mysql.exe，请把 MySQL 8.4 的 bin 目录加入 PATH。'
}

$env:MYSQL_PWD = 'root'
try {
    $sourcePath = $sqlPath.Replace('\', '/')
    $sourceCommand = "source $sourcePath"
    & $mysqlPath --protocol=TCP --host=127.0.0.1 --port=3306 --user=root `
        --default-character-set=utf8mb4 --execute=$sourceCommand
    if ($LASTEXITCODE -ne 0) {
        throw '商品种子 SQL 执行失败，请确认应用已经启动过并完成建表。'
    }

    $count = & $mysqlPath --protocol=TCP --host=127.0.0.1 --port=3306 --user=root `
        --default-character-set=utf8mb4 --database=mall_system --batch --skip-column-names `
        --execute="SELECT COUNT(*) FROM products WHERE product_code IN ('COCA-330ML','ORANGE-500ML','WATER-550ML','CHIPS-ORIGINAL','CHOCO-CLASSIC');"
    if ($LASTEXITCODE -ne 0 -or $count.Trim() -ne '5') {
        throw '商品种子校验失败，预期存在5个商品。'
    }
    Write-Output '商品种子已幂等写入5个商品。'
} finally {
    Remove-Item Env:MYSQL_PWD -ErrorAction SilentlyContinue
}
