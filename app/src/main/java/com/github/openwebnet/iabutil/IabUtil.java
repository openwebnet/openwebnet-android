package com.github.openwebnet.iabutil;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.annimon.stream.Stream;
import com.github.openwebnet.BuildConfig;
import com.github.openwebnet.iabutil.IabHelper.IabAsyncInProgressException;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author niqdev
 */
public class IabUtil {

    private static final Logger log = LoggerFactory.getLogger(IabUtil.class);

    private static IabUtil mIabUtil;
    private Activity mActivity;
    private IabHelper mHelper;

    private IabUtil() {
        // fake instance if base64EncodedPublicKey is missing
    }

    private IabUtil(Activity activity) {
        this.mActivity = activity;
    }

    /* base64EncodedPublicKey should be YOUR APPLICATION'S PUBLIC KEY
     * (that you got from the Google Play developer console). This is not your
     * developer public key, it's the *app-specific* public key.
     *
     * Instead of just storing the entire literal string here embedded in the
     * program,  construct the key at runtime from pieces or
     * use bit manipulation (for example, XOR with some other string) to hide
     * the actual key.  The key itself is not secret information, but we don't
     * want to make it easy for an attacker to replace the public key with one
     * of their own and then fake messages from the server.
     */
    private static final String base64EncodedPublicKey = BuildConfig.IAB_KEY;

    // TODO
    // (arbitrary) request code for the purchase flow
    private static final int RC_REQUEST = 10001;

    private static final String SKU_COFFEE = "coffee";
    private static final String SKU_BEER = "beer";
    private static final String SKU_PIZZA = "pizza";
    // TODO
    private final List<String> skus = ImmutableList.of(
        SKU_TEST_PURCHASED, SKU_TEST_CANCELED, SKU_TEST_REFUNDED, SKU_TEST_ITEM_UNAVAILABLE,
        SKU_COFFEE, SKU_BEER, SKU_PIZZA);
    private final Map<String, DonationEntry> donations = new HashMap<>();

    // RUN ~/Android/Sdk/platform-tools/adb shell pm clear com.android.vending
    private static final String SKU_TEST_PURCHASED = "android.test.purchased";
    private static final String SKU_TEST_CANCELED = "android.test.canceled";
    private static final String SKU_TEST_REFUNDED = "android.test.refunded";
    private static final String SKU_TEST_ITEM_UNAVAILABLE = "android.test.item_unavailable";

    // TODO release
    private static final boolean DEBUG_IAB = true;

    /**
     *
     */
    public static IabUtil newInstance(Activity activity) {
        if (mIabUtil != null) {
            return mIabUtil;
        }

        if (TextUtils.isEmpty(Strings.emptyToNull(base64EncodedPublicKey))) {
            log.warn("missing IAB_KEY: fake instance");
            mIabUtil = new IabUtil();
        } else {
            log.debug("found IAB_KEY: new instance");
            mIabUtil = new IabUtil(activity);
        }

        return mIabUtil;
    }

    /**
     *
     */
    public static IabUtil getInstance() {
        Preconditions.checkNotNull(mIabUtil, "iab is not instantiated");
        return mIabUtil;
    }

    /**
     *
     */
    public void init() {
        if (TextUtils.isEmpty(Strings.emptyToNull(base64EncodedPublicKey))) {
            log.warn("missing IAB_KEY: do nothing");
            return;
        }

        // Create the helper, passing it our context and the public key to verify signatures with
        log.debug("Creating IAB helper.");
        mIabUtil.mHelper = new IabHelper(mActivity, base64EncodedPublicKey);

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(DEBUG_IAB);

        // Start setup. This is asynchronous and the specified listener will be called once setup completes.
        log.debug("Starting setup.");
        mHelper.startSetup(result -> {
            log.debug("Setup finished.");

            if (!result.isSuccess()) {
                // Oh noes, there was a problem.
                log.error("Problem setting up in-app billing: " + result);
                return;
            }

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // IAB is fully set up. Now, let's get an inventory of stuff we own.
            log.debug("Setup successful. Querying inventory.");
            try {
                mHelper.queryInventoryAsync(true, skus, null, mGotInventoryListener);
            } catch (IabAsyncInProgressException e) {
                log.error("Error querying inventory. Another async operation in progress.");
            }
        });
    }

    // Listener that's called when we finish querying the items and subscriptions we own
    private IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            log.debug("Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                log.error("Failed to query inventory: " + result);
                return;
            }

            log.debug("Query inventory was successful.");

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See verifyDeveloperPayload().
             */

            if (inventory != null) {
                Stream.of(skus).forEach(sku -> donations.put(sku, consumableDonation(inventory, sku)));
            }

            log.debug("Initial inventory query finished.");
        }
    };

    private DonationEntry consumableDonation(Inventory inventory, String sku) {
        Preconditions.checkArgument(inventory.hasDetails(sku), "missing sku details");
        SkuDetails details = inventory.getSkuDetails(sku);

        boolean isPurchased = false;
        if (inventory.hasPurchase(sku)) {
            Purchase purchase = inventory.getPurchase(sku);
            isPurchased = (purchase != null && verifyDeveloperPayload(purchase));
            log.debug("User has purchased {}: {}", sku, isPurchased);
            consumeItem(purchase);
        }

        return new DonationEntry.Builder(details.getSku())
            .name(details.getTitle())
            .description(details.getDescription())
            .price(details.getPrice())
            .currencyCode(details.getPriceCurrencyCode())
            .purchased(isPurchased)
            .build();
    }

    /** Verifies the developer payload of a purchase. */
    private boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        /*
         * TODO:
         * verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true;
    }

    /**
     * TODO
     */
    public void purchase() {
        if (TextUtils.isEmpty(Strings.emptyToNull(base64EncodedPublicKey))) {
            log.warn("missing IAB_KEY: do nothing");
            return;
        }

        log.debug("Launching purchase flow for donation.");

        /* TODO:
         * for security, generate your payload here for verification. See the comments on
         * verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         * an empty string, but on a production app you should carefully generate this.
         */
        String payload = "";

        try {
            mHelper.launchPurchaseFlow(mActivity, SKU_TEST_PURCHASED, RC_REQUEST,  mPurchaseFinishedListener, payload);
        } catch (IabAsyncInProgressException e) {
            log.error("Error launching purchase flow. Another async operation in progress.");
        }
    }

    // Callback for when a purchase is finished
    private IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            log.debug("Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                log.error("Error purchasing: " + result);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                log.error("Error purchasing. Authenticity verification failed.");
                return;
            }

            log.debug("Purchase successful: {}", purchase.getSku());

            consumeItem(purchase);
        }
    };

    private void consumeItem(Purchase purchase) {
        log.debug("Starting consumption.");
        try {
            mHelper.consumeAsync(purchase, mConsumeFinishedListener);
        } catch (IabAsyncInProgressException e) {
            log.error("Error consuming donation. Another async operation in progress.");
            return;
        }
    }

    // Called when consumption is complete
    private IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            log.debug("Consumption finished. Purchase: " + purchase + ", result: " + result);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isSuccess()) {
                log.debug("Consumption successful. Ready to donate again.");
                donations.get(purchase.getSku()).setPurchased(false);
            } else {
                log.error("Error while consuming: " + result);
            }
            log.debug("End consumption flow.");
        }
    };

    /**
     *
     */
    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        log.debug("onActivityResult({}, {}, {})", requestCode, requestCode, data);
        boolean isIabResult = mHelper.handleActivityResult(requestCode, resultCode, data);
        log.debug("onActivityResult handled by IABUtil: {}", isIabResult);
        return isIabResult;
    }

    /**
     *
     */
    public void destroy() {
        if (TextUtils.isEmpty(Strings.emptyToNull(base64EncodedPublicKey))) {
            log.warn("missing IAB_KEY: do nothing");
            return;
        }

        // very important:
        log.debug("Destroying helper.");
        if (mHelper != null) {
            mHelper.disposeWhenFinished();
            mHelper = null;
        }
    }
    
}
