/*
 * Copyright (c) 2008 - 2013 10gen, Inc. <http://10gen.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mongodb.codecs.configuration.conventions;

import org.mongodb.codecs.configuration.ClassModelBuilder;
import org.mongodb.codecs.configuration.CodecSourceContext;
import org.mongodb.codecs.configuration.FieldModelBuilder;

public abstract class VisitingModelConvention implements ModelConvention {

    @Override
    public void apply(final ClassModelBuilder<?> builder, final CodecSourceContext<?> context) {
        visitClass(builder, context);
    }

    protected void visitClass(final ClassModelBuilder<?> builder, final CodecSourceContext<?> context) {
        for (FieldModelBuilder field : builder.getMappedFields()) {
            visitField(field, context);
        }
    }

    protected void visitField(final FieldModelBuilder builder, final CodecSourceContext<?> context) {
        // do nothing
    }
}