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

package org.onepf.opfiab;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import org.onepf.opfiab.model.billing.SkuDetails;
import org.onepf.opfiab.model.event.FragmentLifecycleEvent;
import org.onepf.opfiab.model.event.SupportFragmentLifecycleEvent;

import static org.onepf.opfiab.model.event.LifecycleEvent.Type;

public class ActivityIabHelper extends SelfManagedIabHelper {

    @NonNull
    private final Object eventHandler = new Object() {

        public void onEvent(@NonNull final FragmentLifecycleEvent event) {
            if (opfFragment == event.getFragment()) {
                handleLifecycle(event.getType());
            }
        }

        public void onEvent(@NonNull final SupportFragmentLifecycleEvent event) {
            if (opfFragment == event.getFragment()) {
                handleLifecycle(event.getType());
            }
        }

        private void handleLifecycle(@NonNull final Type type) {
            if (type == Type.ATTACH) {
                managedIabHelper.subscribe();
            } else if (type == Type.DETACH) {
                managedIabHelper.unsubscribe();
            } else if (type == Type.DESTROY) {
                OPFIab.unregister(eventHandler);
            }
        }
    };

    @NonNull
    private final Activity activity;

    @NonNull
    private final ManagedIabHelper managedIabHelper;

    @NonNull
    private Object opfFragment;

    private ActivityIabHelper(@NonNull final ManagedIabHelper managedIabHelper,
                              @Nullable final Activity activity,
                              @Nullable final FragmentActivity fragmentActivity) {
        super(managedIabHelper);
        this.managedIabHelper = managedIabHelper;
        OPFIab.register(eventHandler);
        //noinspection ConstantConditions
        this.activity = fragmentActivity != null ? fragmentActivity : activity;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ActivityIabHelper(@NonNull final ManagedIabHelper managedIabHelper,
                             @NonNull final Activity activity) {
        this(managedIabHelper, activity, null);

        android.app.Fragment fragment;
        final android.app.FragmentManager fragmentManager = activity.getFragmentManager();
        if ((fragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG)) == null) {
            fragment = OPFIabFragment.newInstance();
            fragmentManager.beginTransaction()
                    .add(fragment, FRAGMENT_TAG)
                    .commit();
        }
        //TODO check attach\detach condition. Use sticky?
        this.opfFragment = fragment;
        fragmentManager.executePendingTransactions();
    }

    public ActivityIabHelper(@NonNull final ManagedIabHelper managedIabHelper,
                             @NonNull final FragmentActivity fragmentActivity) {
        this(managedIabHelper, null, fragmentActivity);

        android.support.v4.app.Fragment fragment;
        final android.support.v4.app.FragmentManager fragmentManager =
                fragmentActivity.getSupportFragmentManager();
        if ((fragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG)) == null) {
            fragment = OPFIabSupportFragment.newInstance();
            fragmentManager.beginTransaction()
                    .add(fragment, FRAGMENT_TAG)
                    .commit();
        }
        this.opfFragment = fragment;
        fragmentManager.executePendingTransactions();
    }

    @Override
    public void purchase(@NonNull final String sku) {
        managedIabHelper.purchase(activity, sku);
    }
}
