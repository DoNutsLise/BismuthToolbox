## BismuthToolbox

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

## HOW TO INSTALL:

Option 1 (easy way) - install from Google Play (https://play.google.com/store/apps/details?id=com.donuts.bismuth.bismuthtoolbox). I would choose this option because it's easy to get updates.

Option 2 (harder way) - install a compiled apk from "releases" page with adb or sideload:
Here is a link with instructions: https://www.droidviews.com/install-apk-files-using-adb-commands/. Difficulty of installation is about the same as from Google Play, but you have to update manually every time.

Option 3 (hardest way) - build from source:
Here are good instructions on how to build an android apk from source using Android Studio (https://github.com/openaps/AndroidAPSdocs/blob/master/docs/EN/Installing-AndroidAPS/Building-APK.md). It's a quite an involved process and I personally do not recommend it.

NOTE: github  apks are released with a certain delay after a new version of the app was published on Goole Play store. The reason is that I get bug reports through Google Play console (thanks to responsible users) and can act on them  quickly. So, I use Google Play releases as some sort of a testing platform before I release a stable version on github.

PARANOID? (like myself)
apk I put  in release are not obfuscated; there are plenty of tools (including online ones) which allow you to decomplie apks and check the code.

## Requested features:
* details about missing miner/hypernode in push notification
* import/export of settings (including addresses)
* clock height for POW and POS chains
* POW difficulty
* ROI, percentage of coin supply, weekly, monthly. yearly rewards in bis and usd.
* Check for a duplicate entry in setting (warn user when he enters a duplicate IP or wallet address) - done.

