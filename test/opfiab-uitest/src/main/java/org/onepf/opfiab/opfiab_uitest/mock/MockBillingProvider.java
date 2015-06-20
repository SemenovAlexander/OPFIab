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

package org.onepf.opfiab.opfiab_uitest.mock;

import android.support.annotation.NonNull;

import org.onepf.opfiab.billing.BillingProvider;
import org.onepf.opfiab.billing.Compatibility;
import org.onepf.opfutils.OPFLog;

import java.util.Random;

/**
 * @author antonpp
 * @since 14.05.15
 */
public abstract class MockBillingProvider implements BillingProvider {

    public static final long SLEEP_TIME = 50;
    private static final Random RND = new Random();

    protected void sleep() {
        try {
            Thread.sleep((SLEEP_TIME + RND.nextLong() % SLEEP_TIME) / 2);
        } catch (InterruptedException e) {
            OPFLog.e(e.getMessage());
        }
    }

    @NonNull
    @Override
    public Compatibility checkCompatibility() {
        return Compatibility.COMPATIBLE;
    }
}
