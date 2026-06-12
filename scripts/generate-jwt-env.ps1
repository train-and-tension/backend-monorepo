param(
    [string]$EnvPath = ".env"
)

$ErrorActionPreference = "Stop"

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
$envFile = Join-Path $repoRoot $EnvPath
$exampleFile = Join-Path $repoRoot ".env.example"
$jwtDir = Join-Path $repoRoot "infra/jwt"

if (-not (Test-Path $envFile)) {
    Copy-Item -Path $exampleFile -Destination $envFile
}

New-Item -ItemType Directory -Force -Path $jwtDir | Out-Null

$privatePem = Join-Path $jwtDir "private.pem"
$privateDer = Join-Path $jwtDir "private.der"
$publicDer = Join-Path $jwtDir "public.der"

openssl ecparam -name prime256v1 -genkey -noout -out $privatePem | Out-Null
openssl pkcs8 -topk8 -nocrypt -in $privatePem -outform DER -out $privateDer | Out-Null
openssl ec -in $privatePem -pubout -outform DER -out $publicDer 2>$null

$privateKey = [Convert]::ToBase64String([IO.File]::ReadAllBytes($privateDer))
$publicKey = [Convert]::ToBase64String([IO.File]::ReadAllBytes($publicDer))

function Set-EnvValue {
    param(
        [string]$Path,
        [string]$Name,
        [string]$Value
    )

    $lines = Get-Content -Path $Path
    $updated = $false
    $next = foreach ($line in $lines) {
        if ($line -match "^$([Regex]::Escape($Name))=") {
            "$Name=$Value"
            $updated = $true
        } else {
            $line
        }
    }

    if (-not $updated) {
        $next += "$Name=$Value"
    }

    Set-Content -Path $Path -Value $next -Encoding UTF8
}

Set-EnvValue -Path $envFile -Name "JWT_PRIVATE_KEY" -Value $privateKey
Set-EnvValue -Path $envFile -Name "JWT_PUBLIC_KEY" -Value $publicKey

Write-Host "Updated $envFile with a fresh ES256 JWT key pair."
