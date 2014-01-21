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

import org.mongodb.codecs.FieldModel;
import org.mongodb.codecs.configuration.ClassModelBuilder;
import org.mongodb.codecs.configuration.CodecSourceContext;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class DeclaredFieldFinderConvention extends VisitingModelConvention {

    @Override
    @SuppressWarnings("unchecked")
    protected void visitClass(final ClassModelBuilder<?> builder, final CodecSourceContext<?> context) {
        Class<?> theClass = builder.getModelClass();
        for (final Field field : theClass.getDeclaredFields()) {
            if (Modifier.isTransient(field.getModifiers())) {
                // user has indicated not to serialize this field
                continue;
            }

            if (Modifier.isStatic(field.getModifiers())) {
                // static fields aren't persistable
                continue;
            }

            if (!FieldModel.isValidFieldName(field.getName())) {
                // we are going to skip this field even though
                // a later convention may change its name before
                // committing.  However, since we don't know this
                // will happen, it is up to someone else to bring
                // in these initially invalid fields.
                continue;
            }

            // TODO: what else should we ignore?  final?

            builder.map(field);
        }
    }
}