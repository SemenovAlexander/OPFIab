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

package org.onepf.opfiab.openstore.model;

import android.support.annotation.Nullable;

public enum PurchaseState {

    PURCHASED(0),
    CANCELED(1),
    REFUNDED(2);

    /**
     * Gets purchase state from code.
     *
     * @param code Code to convert.
     *
     * @return Purchase state if code is recognized, null otherwise.
     */
    @Nullable
    public static PurchaseState fromCode(final int code) {
        for (final PurchaseState purchaseState : values()) {
            if (purchaseState.code == code) {
                return purchaseState;
            }
        }
        return null;
    }


    private final int code;

    PurchaseState(final int code) {
        this.code = code;
    }
}
