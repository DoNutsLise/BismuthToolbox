package com.donuts.bismuth.bismuthtoolbox.Models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constants {
    public static final String SERVER_ADDRESS = "192.168.0.1";

    // firebase push notifications
    public static final String CHANNEL_ID = "my_channel_01";
    public static final String CHANNEL_NAME = "Bismuth Toolbox Notification";
    public static final String CHANNEL_DESCRIPTION = "Notification from Bismuth Toolbox";

    // BIS servers API endpoints
    public static final String BIS_HN_BASIC_URL = "https://hypernodes.bismuth.live/status.json"; // only (ip:active) pair
    public static final String BIS_HN_VERBOSE_URL = "https://hypernodes.bismuth.live/hypernodes.php"; // full details
    public static final String BIS_HN_VERBOSE1_URL = "https://hypernodes.bismuth.live/status_ex.json"; // yet another hypernodes json
    // python3 hn_client.py --action=status --ip=1.2.3.4 - to query a single node:
    public static final String EGGPOOL_MINER_STATS_URL = "https://eggpool.net/index.php?action=api&type=detail&miner="; // detailed stats about a miner on eggpool; add wallet addredd at the end of the string
    public static final String EGGPOOL_BIS_STATS_URL = "https://eggpool.net/api/currencies"; // some BIS network and eggpool  stats (hashrate, blocks found, etc) from eggpool
    public static final String BIS_PRICE_COINGECKO_URL = "https://api.coingecko.com/api/v3/simple/price?ids=bismuth&vs_currencies=usd,btc"; // BIS price from coingecko
    public static final String BIS_API_URL = "https://bismuth.online/api/"; // BIS api requests http://bismuth.online/api/parameter1/parameter2; for wallet balance: parameter1 = node; parameter2 = balancegetjson:939250d1ce3e543a2f0c3106a12a56649a2199d7ef59b7078ede127f
    public static final String BIS_LAST_BLOCK_URL  = "https://bismuth.online/api/node/blocklastjson"; // POW last block stats

    // Urls which are needed to update a particular screen: Screen <-> Url relationship, needed for Room database and api queries
    public static final List<String> MAIN_SCREEN_URLS = new ArrayList<>(Arrays.asList(BIS_PRICE_COINGECKO_URL, BIS_HN_BASIC_URL));

    // Preference screen: keys for preferences categories, which are empty by default in preferences.xml and are inflated
    // programmatically (these names correspond to android:key="hypernodeIP" in preferences.xml).
    // These are used in the SettingsFragment and in all Activities to create urls for asynctask queries (check them before modifying this List)
    public static final List<String> PREFERENCES_CATEGORIES_KEYS = new ArrayList<>(Arrays.asList("hypernodeIP", "miningWalletAddress", "bisWalletAddress"));
}
