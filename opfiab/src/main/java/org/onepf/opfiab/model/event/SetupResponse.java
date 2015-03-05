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

package org.onepf.opfiab.model.event;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;
import org.onepf.opfiab.JsonCompatible;
import org.onepf.opfiab.OPFIabUtils;
import org.onepf.opfiab.billing.BillingProvider;
import org.onepf.opfutils.OPFLog;

import java.util.Arrays;
import java.util.Collection;

import static org.json.JSONObject.NULL;
import static org.onepf.opfiab.model.event.SetupResponse.Status.PROVIDER_CHANGED;
import static org.onepf.opfiab.model.event.SetupResponse.Status.SUCCESS;
import static org.onepf.opfiab.model.event.SetupResponse.Status.UNAUTHORISED;


public class SetupResponse implements JsonCompatible {

    private static final String NAME_STATUS = "status";
    private static final String NAME_PROVIDER_INFO = "provider_info";

    public static enum Status {

        SUCCESS,
        PROVIDER_CHANGED,
        UNAUTHORISED,
        FAILED,
    }

    private static final Collection<Status> SUCCESSFUL =
            Arrays.asList(SUCCESS, PROVIDER_CHANGED, UNAUTHORISED);


    @NonNull
    private final Status status;
    @Nullable
    private final BillingProvider billingProvider;

    public SetupResponse(@NonNull final Status status,
                         @Nullable final BillingProvider billingProvider) {
        this.status = status;
        this.billingProvider = billingProvider;
        if (billingProvider == null && isSuccessful()) {
            throw new IllegalArgumentException();
        }
    }

    @Nullable
    public BillingProvider getBillingProvider() {
        return billingProvider;
    }

    @NonNull
    public Status getStatus() {
        return status;
    }

    public final boolean isSuccessful() {
        return SUCCESSFUL.contains(status);
    }

    @NonNull
    @Override
    public JSONObject toJson() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(NAME_STATUS, status);
            jsonObject.put(NAME_PROVIDER_INFO, billingProvider == null ? NULL : billingProvider);
        } catch (JSONException exception) {
            OPFLog.e("", exception);
        }
        return jsonObject;
    }

    @Override
    public String toString() {
        return OPFIabUtils.toString(this);
    }
}
