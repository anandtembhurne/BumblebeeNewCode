/*
 * (C) Copyright 2014 Nuxeo SA (http://nuxeo.com/) and others.
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
 *
 * Contributors:
 *     bdelbosc
 */
package org.nuxeo.elasticsearch.aggregate;

import static org.nuxeo.elasticsearch.ElasticSearchConstants.AGG_TYPE_DATE_HISTOGRAM;
import static org.nuxeo.elasticsearch.ElasticSearchConstants.AGG_TYPE_DATE_RANGE;
import static org.nuxeo.elasticsearch.ElasticSearchConstants.AGG_TYPE_HISTOGRAM;
import static org.nuxeo.elasticsearch.ElasticSearchConstants.AGG_TYPE_RANGE;
import static org.nuxeo.elasticsearch.ElasticSearchConstants.AGG_TYPE_SIGNIFICANT_TERMS;
import static org.nuxeo.elasticsearch.ElasticSearchConstants.AGG_TYPE_TERMS;

import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.query.api.AggregateDefinition;
import org.nuxeo.ecm.platform.query.api.Bucket;

/**
 * @since 6.0
 */
final public class AggregateFactory {

    private AggregateFactory() {
    }

    public static AggregateEsBase<? extends Bucket> create(AggregateDefinition def, DocumentModel searchDocumentModel) {
        switch (def.getType()) {
        case AGG_TYPE_TERMS:
            return new TermAggregate(def, searchDocumentModel);
        case AGG_TYPE_RANGE:
            return new RangeAggregate(def, searchDocumentModel);
        case AGG_TYPE_DATE_HISTOGRAM:
            return new DateHistogramAggregate(def, searchDocumentModel);
        case AGG_TYPE_SIGNIFICANT_TERMS:
            return new SignificantTermAggregate(def, searchDocumentModel);
        case AGG_TYPE_HISTOGRAM:
            return new HistogramAggregate(def, searchDocumentModel);
        case AGG_TYPE_DATE_RANGE:
            return new DateRangeAggregate(def, searchDocumentModel);
        default:
            throw new IllegalArgumentException("Unknown aggregate type: " + def.getType());
        }

    }
}
