# BismuthToolbox

All-in-one app for monitoring Bismuth network vitals.
 Work in progress. Currently available:
* Monitor the status of your hypernodes and receive push notifications when any of your hypernodes lag of become inactive
* Monitor the status of your GPU miners (currently only for Eggpool) and receive push notifications when any of your miners become inactive
* Add your bismuth wallets to track their balance in $BIS and USD
* Track network status ($BIS price, etc)
* More to come soon...

<p align="center">
    <img src="https://user-images.githubusercontent.com/49869348/83574008-6e877c00-a524-11ea-8e3a-29fc79377d85.png" width="250" alt="accessibility text">
    <img src="https://user-images.githubusercontent.com/49869348/83572052-ce7c2380-a520-11ea-9aaf-e6eccf97189e.png" width="250" alt="accessibility text">
    <img src="https://user-images.githubusercontent.com/49869348/83572017-be644400-a520-11ea-84de-0569c39e868c.png" width="250" title="hover text">
</p>

Note: Push notifications are disabled in settings by default. When you enable this option you will be asked if you accept sharing your hypernode IPs and mining wallets addresses. The wallet addresses and hypernode IPs you enter in the settings screen will be sent to Google's Firebase together with your device's unique ID. This data is totally anonymous and is used ONLY to serve targeted push notifications (e.g. about problems with your hypernodes and/or miners). If you are not happy about it - just click "decline" (you will receive no push notifications though).

HOW TO INSTALL:
1. Build from source - google it somewhere else how to do it.
2. Downlod apk from "releases" page. Connect your mobile device with a USB cable. Run in terminal: "adb devices" - that should output something like "List of devices attached 60573ea4	device"  if connection is OK. Then run "adb install BismuthToolbox_v2.apk" (provided BismuthToolbox_v2.apk is the name of the downloaded file). Note: developer options should be enabled in your mobile device and possibly "install via USB" option, too.

