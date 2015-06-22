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

package org.onepf.opfiab.model.billing;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;
import org.onepf.opfiab.billing.BillingProvider;
import org.onepf.opfiab.model.JsonCompatible;
import org.onepf.opfiab.util.OPFIabUtils;
import org.onepf.opfutils.OPFLog;

import java.io.Serializable;

import static org.json.JSONObject.NULL;

/**
 * Parent class for all billing models.
 */
public abstract class BillingModel implements JsonCompatible, Serializable {

    private static final String NAME_SKU = "sku";
    private static final String NAME_TYPE = "type";
    private static final String NAME_PROVIDER_NAME = "provider_name";
    private static final String NAME_ORIGINAL_JSON = "original_json";


    @NonNull
    private final String sku;
    @NonNull
    private final SkuType type;
    @Nullable
    private final String providerName;
    @Nullable
    private final String originalJson;

    protected BillingModel(@NonNull final String sku,
                           @Nullable final SkuType type,
                           @Nullable final String providerName,
                           @Nullable final String originalJson) {
        this.sku = sku;
        this.type = type == null ? SkuType.UNKNOWN : type;
        this.providerName = providerName;
        this.originalJson = originalJson;
    }

    /**
     * Makes copy of this BillingModel with different SKU.
     *
     * @param sku SKU to use for new copy.
     *
     * @return BillingModel identical to this one, except for SKU value.
     *
     * @see #getSku()
     */
    @NonNull
    public abstract BillingModel copyWithSku(@NonNull final String sku);

    /**
     * Gets Store Keeping Unit (SKU) associated with this billing model.
     *
     * @return SKU associated with this billing model.
     */
    @NonNull
    public String getSku() {
        return sku;
    }

    /**
     * Gets type of this billing model.
     *
     * @return Type of this billing model.
     */
    @NonNull
    public SkuType getType() {
        return type;
    }

    @Nullable
    public String getProviderName() {
        return providerName;
    }

    /**
     * Gets JSON representation of data from which this billing model was constructed.
     *
     * @return JSON representation of data originally returned by {@link BillingProvider}.
     */
    @Nullable
    public String getOriginalJson() {
        return originalJson;
    }

    @NonNull
    @Override
    public JSONObject toJson() {
        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(NAME_SKU, sku);
            jsonObject.put(NAME_TYPE, type);
            jsonObject.put(NAME_PROVIDER_NAME, providerName == null ? NULL : providerName);
            jsonObject.put(NAME_ORIGINAL_JSON,
                           originalJson == null ? NULL : new JSONObject(originalJson));
        } catch (JSONException exception) {
            OPFLog.e("", exception);
        }
        return jsonObject;
    }

    //CHECKSTYLE:OFF
    @SuppressWarnings({"PMD", "RedundantIfStatement", "SimplifiableIfStatement"})
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof BillingModel)) return false;

        final BillingModel that = (BillingModel) o;

        if (!getSku().equals(that.getSku())) return false;
        if (getType() != that.getType()) return false;
        if (getProviderName() != null ? !getProviderName().equals(
                that.getProviderName()) : that.getProviderName() != null) return false;
        return !(getOriginalJson() != null ? !getOriginalJson().equals(
                that.getOriginalJson()) : that.getOriginalJson() != null);

    }

    @Override
    public int hashCode() {
        int result = getSku().hashCode();
        result = 31 * result + getType().hashCode();
        result = 31 * result + (getProviderName() != null ? getProviderName().hashCode() : 0);
        result = 31 * result + (getOriginalJson() != null ? getOriginalJson().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return OPFIabUtils.toString(this);
    }
    //CHECKSTYLE:ON

    /**
     * Parent class for all billing models builders.
     */
    @SuppressWarnings("unchecked")
    abstract static class Builder<B extends Builder, M extends BillingModel> {

        @NonNull
        protected final String sku;
        @Nullable
        protected SkuType type;
        @Nullable
        protected String providerName;
        @Nullable
        protected String originalJson;

        protected Builder(@NonNull final String sku) {
            this.sku = sku;
        }

        /**
         * Sets existing billing model to use as base for a new billing model.
         *
         * @param billingModel BillingModel object to copy data from.
         * @return this object.
         */
        public B setBase(@NonNull final M billingModel) {
            setType(billingModel.getType());
            setOriginalJson(billingModel.getOriginalJson());
            setProviderName(billingModel.getProviderName());
            return (B) this;
        }

        /**
         * Sets type for a new billing model.
         *
         * @param type Type to set.
         * @return this object.
         */
        public B setType(@Nullable final SkuType type) {
            this.type = type;
            return (B) this;
        }

        /**
         * Sets JSON representation of original data for a new billing model.
         *
         * @param originalJson JSON data to set.
         * @return this object.
         */
        public B setOriginalJson(@Nullable final String originalJson) {
            this.originalJson = originalJson;
            return (B) this;
        }

        /**
         * Sets billing provider name for a new billing model.
         *
         * @param providerName BillingProvider name to set.
         * @return this object.
         *
         * @see BillingProvider#getName()
         */
        public B setProviderName(@Nullable final String providerName) {
            this.providerName = providerName;
            return (B) this;
        }

        /**
         * Constructs a new billing model object.
         *
         * @return new BillingModer from supplied data.
         */
        public abstract M build();
    }
}
