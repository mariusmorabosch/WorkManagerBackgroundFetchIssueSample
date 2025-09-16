Repro steps:
0. Use an Android 15 emulator or device (IMPORTANT!)
1. Open the app
2. Close the app by tapping on the home buttom (leave app in background)
3. Wait ~30 seconds for the WorkManager job to run 

Expected:
The api request succeeds as normal.

Actual: 
Error fetching from background: java.net.UnknownHostException: Unable to resolve host "dog.ceo": No address associated with hostname
