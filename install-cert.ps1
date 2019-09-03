   $ver = $host | select version
    $DesktopPath = [System.Environment]::GetFolderPath([System.Environment+SpecialFolder]::Desktop)
    if ($ver.Version.Major -gt 1)  {$Host.Runspace.ThreadOptions = "ReuseThread"}

    # Verify that user running script is an administrator
    $IsAdmin=[Security.Principal.WindowsIdentity]::GetCurrent()
    If ((New-Object Security.Principal.WindowsPrincipal $IsAdmin).IsInRole([Security.Principal.WindowsBuiltinRole]::Administrator) -eq $FALSE)
    {
      "`nERROR: You are NOT a local administrator.  Run this script after logging on with a local administrator account."
        # We are not running "as Administrator" - so relaunch as administrator

        # Create a new process object that starts PowerShell
        $newProcess = new-object System.Diagnostics.ProcessStartInfo "PowerShell";

        # Specify the current script path and name as a parameter
        $newProcess.Arguments = $myInvocation.MyCommand.Definition;

        # Indicate that the process should be elevated
        $newProcess.Verb = "runas";

        # Start the new process
        [System.Diagnostics.Process]::Start($newProcess);

        # Exit from the current, unelevated, process
        exit
   }
$file = (Get-ChildItem -Path  "$DesktopPath\EMPUTI_PROJECT.cer")
$file | Import-Certificate -CertStoreLocation Cert:\CurrentUser\Root
Write-Output "Certificate install successfully."
cmd /c pause | out-null
