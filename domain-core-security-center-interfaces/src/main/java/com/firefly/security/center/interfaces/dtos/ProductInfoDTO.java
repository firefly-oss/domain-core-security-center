/*
 * Copyright 2025 Firefly Software Solutions Inc
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

package com.firefly.security.center.interfaces.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Product information from product management service
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductInfoDTO {

    @JsonProperty("productId")
    private UUID productId;

    @JsonProperty("productCatalogId")
    private UUID productCatalogId;

    @JsonProperty("productSubtypeId")
    private UUID productSubtypeId;

    @JsonProperty("productName")
    private String productName;

    @JsonProperty("productCode")
    private String productCode;

    @JsonProperty("productDescription")
    private String productDescription;

    @JsonProperty("productType")
    private String productType;

    @JsonProperty("productStatus")
    private String productStatus;

    @JsonProperty("launchDate")
    private LocalDate launchDate;

    @JsonProperty("endDate")
    private LocalDate endDate;

    @JsonProperty("dateCreated")
    private LocalDateTime dateCreated;

    @JsonProperty("dateUpdated")
    private LocalDateTime dateUpdated;
}
