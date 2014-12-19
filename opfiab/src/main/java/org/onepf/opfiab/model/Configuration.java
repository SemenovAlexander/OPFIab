/*
 * Copyright 2012-2014 One Platform Foundation
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

package org.onepf.opfiab.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.onepf.opfiab.BillingProvider;
import org.onepf.opfiab.listener.BillingListener;

import java.util.Collection;
import java.util.LinkedHashSet;

public final class Configuration {

    @NonNull
    private final Collection<BillingProvider> providers;

    @Nullable
    private final BillingListener billingListener;

    protected Configuration(@NonNull final Collection<BillingProvider> providers,
                            @Nullable final BillingListener billingListener) {
        this.providers = providers;
        this.billingListener = billingListener;
    }

    @NonNull
    public Collection<BillingProvider> getProviders() {
        return providers;
    }

    @Nullable
    public BillingListener getBillingListener() {
        return billingListener;
    }

    public static class Builder {

        @NonNull
        private final Collection<BillingProvider> providers = new LinkedHashSet<>();

        @Nullable
        private BillingListener billingListener;

        public Builder addBillingProvider(@NonNull final BillingProvider provider) {
            providers.add(provider);
            return this;
        }

        public Builder setBillingListener(@Nullable final BillingListener billingListener) {
            this.billingListener = billingListener;
            return this;
        }

        public Configuration build() {
            return new Configuration(providers, billingListener);
        }
    }
}
