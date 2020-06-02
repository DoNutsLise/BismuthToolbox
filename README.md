# BismuthToolbox

All-in-one app for monitoring Bismuth network vitals.
 Work in progress. Currently available:
* Monitor the status of your hypernodes and receive push notifications when any of your hypernodes lags of becomes inactive
* Monitor the status of your GPU miners (currently only for Eggpool) and receive push notifications when any of your miners becomes inactive
* Add your bismuth wallets to track their balance in $BIS and USD
* Track network status ($BIS price, etc)
* More to come soon...

<p align="center">
    <img src="https://user-images.githubusercontent.com/49869348/83572065-d340d780-a520-11ea-9591-60e28a70229e.png" width="250" alt="accessibility text">
    <img src="https://user-images.githubusercontent.com/49869348/83572052-ce7c2380-a520-11ea-9aaf-e6eccf97189e.png" width="250" alt="accessibility text">
    <img src="https://user-images.githubusercontent.com/49869348/83572017-be644400-a520-11ea-84de-0569c39e868c.png" width="250" title="hover text">
</p>

Note: The wallet addresses and hypernode IPs you enter in settings screen will be sent to Google's Firebase together with your device's unique ID. This data is totally anonymous and is used only to serve targeted push notifications (e.g. about problems with your hypernodes and/or miners). If you are not happy about it - just delete the last 6 lines in SendDataToFirebase class (no push notifications though).

