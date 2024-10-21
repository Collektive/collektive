$buildFolderPath = "build"

function Get-LockingProcess { param ($filePath)
    $handleOutput = &handle.exe $filePath 2>&1
    return if ($handleOutput -match "pid: (\d+)\s+") { [int]$matches[1] } else { $null }
}

function Terminate-LockingProcess { param ($processId)
    try { Stop-Process -Id $processId -Force; Write-Host "Terminated process with ID $processId." }
    catch { Write-Host "Unable to terminate process with ID $processId." }
}

Get-ChildItem -Path $buildFolderPath -File | ForEach-Object {
    $lockingProcessId = Get-LockingProcess $_.FullName
    if ($lockingProcessId) {
        Terminate-LockingProcess $lockingProcessId
    } else {
        Write-Host "$($_.FullName) is not locked."
    }
}

Write-Host "Completed checking for locked files in $buildFolderPath."
