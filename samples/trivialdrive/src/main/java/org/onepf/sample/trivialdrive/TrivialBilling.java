/*
 * Copyright 2012-2015 One Platform Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.onepf.sample.trivialdrive;

import android.content.Context;
import android.content.SharedPreferences;

import org.onepf.opfiab.amazon.AmazonBillingProvider;
import org.onepf.opfiab.billing.BillingProvider;
import org.onepf.opfiab.google.GoogleBillingProvider;
import org.onepf.opfiab.google.GoogleMapSkuResolver;
import org.onepf.opfiab.google.SimpleGooglePurchaseVerifier;
import org.onepf.opfiab.model.Configuration;
import org.onepf.opfiab.model.billing.SkuType;
import org.onepf.opfiab.sku.MapSkuResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class TrivialBilling {

    private static final String NAME = "billing";

    private static final String KEY_FIRST_LAUNCH = "first_launch";
    private static final String KEY_HELPER = "helper";
    private static final String KEY_PROVIDERS = "providers";
    private static final String KEY_AUTO_RECOVER = "auto_recover";
    private static final String KEY_SKIP_UNAUTHORIZED = "skip_unauthorized";

    public static final String SKU_GAS = "sku_gas";
    public static final String SKU_PREMIUM = "sku_premium";
    public static final String SKU_SUBSCRIPTION = "sku_subscription";

    public static final String AMAZON_SKU_GAS = "org.onepf.sample.trivialdrive.sku_gas";
    public static final String AMAZON_SKU_PREMIUM = "";
    public static final String AMAZON_SKU_SUBSCRIPTION = "";

    public static final String GOOGLE_SKU_GAS = "android.test.purchased";
    public static final String GOOGLE_SKU_PREMIUM = "";
    public static final String GOOGLE_SKU_SUBSCRIPTION = "";

    @SuppressWarnings("SpellCheckingInspection")
    public static final String GOOGLE_PLAY_KEY
            = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA5F8fASyrDFdaXrkoW8kNtwH5JIkLnNuTD5uE1a37TbI5LDZR" +
            "VgvMIYAtZ9CAHAfLnJ6OEZt0lvLLJSKVuS47VqYVhGZciOkX8TEihONBRwis6i9A3JnKfyqm0iiT+P0CEktOLuFLROIo13" +
            "utCIO++6h7A7/WLfxNV+Jnxfs9OEHyyPS+MdHxa0wtZGeAGiaN65BymsBQo7J/ABt2DFyMJP1R/nJM45F8yu4D6wSkUNKz" +
            "s/QbPfvHJQzq56/B/hbx59EkzkInqC567hrlUlX4bU5IvOTF/B1G+UMuKg80m3I1IcQk4FD2D9oJ3E+8IXG/1UdejrOsmq" +
            "DAzE7LkMl8xwIDAQAB";


    private static Context context;
    private static SharedPreferences preferences;

    private static BillingProvider newProvider(final Provider provider) {
        switch (provider) {
            case AMAZON:
                return newAmazonProvider();
            case GOOGLE:
                return newGoogleProvider();
            default:
                throw new IllegalStateException();
        }
    }

    private static BillingProvider newGoogleProvider() {
        final GoogleMapSkuResolver skuResolver = new GoogleMapSkuResolver();
        skuResolver.add(SKU_GAS, GOOGLE_SKU_GAS, SkuType.CONSUMABLE);
        skuResolver.add(SKU_PREMIUM, GOOGLE_SKU_PREMIUM, SkuType.ENTITLEMENT);
        skuResolver.add(SKU_SUBSCRIPTION, GOOGLE_SKU_SUBSCRIPTION, SkuType.SUBSCRIPTION);

        return new GoogleBillingProvider.Builder(context)
                //TODO
//                .setPurchaseVerifier(new SimpleGooglePurchaseVerifier(GOOGLE_PLAY_KEY))
                .setSkuResolver(skuResolver)
                .build();
    }

    private static BillingProvider newAmazonProvider() {
        final MapSkuResolver skuResolver = new MapSkuResolver();
        skuResolver.add(SKU_GAS, AMAZON_SKU_GAS);
        skuResolver.add(SKU_PREMIUM, AMAZON_SKU_PREMIUM);
        skuResolver.add(SKU_SUBSCRIPTION, AMAZON_SKU_SUBSCRIPTION);

        return new AmazonBillingProvider.Builder(context)
                .setSkuResolver(skuResolver)
                .build();
    }


    private TrivialBilling() {
        throw new UnsupportedOperationException();
    }

    public static void init(final Context context) {
        TrivialBilling.context = context.getApplicationContext();
        preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);

        if (preferences.getBoolean(KEY_FIRST_LAUNCH, true)) {
            preferences.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply();
            setHelper(Helper.ACTIVITY);
            setProviders(Arrays.asList(Provider.values()));
            setAutoRecover(true);
            setSkipUnauthorized(false);
        }
    }

    public static Configuration getRelevantConfiguration() {
        final Configuration.Builder builder = new Configuration.Builder();
        builder.setBillingListener(new TrivialBillingListener());
        for (final Provider provider : getProviders()) {
            final BillingProvider billingProvider;
            switch (provider) {
                case AMAZON:
                    billingProvider = newAmazonProvider();
                    break;
                case GOOGLE:
                    billingProvider = newGoogleProvider();
                    break;
                default:
                    throw new IllegalStateException();
            }
            builder.addBillingProvider(billingProvider);
        }
        builder.setAutoRecover(preferences.getBoolean(KEY_AUTO_RECOVER, false));
        builder.setSkipUnauthorised(preferences.getBoolean(KEY_SKIP_UNAUTHORIZED, false));
        return builder.build();
    }

    public static Helper getHelper() {
        return Helper.valueOf(preferences.getString(KEY_HELPER, null));
    }

    public static void setHelper(final Helper helper) {
        preferences.edit().putString(KEY_HELPER, helper.name()).apply();
    }

    public static Collection<Provider> getProviders() {
        final Collection<String> providersNames = preferences.getStringSet(KEY_PROVIDERS, null);
        //noinspection ConstantConditions
        final Collection<Provider> providers = new ArrayList<>(providersNames.size());
        for (final String providerName : providersNames) {
            providers.add(Provider.valueOf(providerName));
        }
        return providers;
    }

    public static void setProviders(final Collection<Provider> providers) {
        final Set<String> providersNames = new HashSet<>();
        for (final Provider provider : providers) {
            providersNames.add(provider.name());
        }
        preferences.edit().putStringSet(KEY_PROVIDERS, providersNames).apply();
    }

    public static boolean isAutoRecover() {
        if (!preferences.contains(KEY_AUTO_RECOVER)) {
            throw new IllegalStateException();
        }
        return preferences.getBoolean(KEY_AUTO_RECOVER, false);
    }

    public static void setAutoRecover(final boolean autoRecover) {
        preferences.edit().putBoolean(KEY_AUTO_RECOVER, autoRecover).apply();
    }

    public static boolean isSkipUnauthorized() {
        if (!preferences.contains(KEY_SKIP_UNAUTHORIZED)) {
            throw new IllegalStateException();
        }
        return preferences.getBoolean(KEY_SKIP_UNAUTHORIZED, false);
    }

    public static void setSkipUnauthorized(final boolean skipUnauthorized) {
        preferences.edit().putBoolean(KEY_SKIP_UNAUTHORIZED, skipUnauthorized).apply();
    }
}