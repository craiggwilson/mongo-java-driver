package org.mongodb.codecs.configuration;

import org.mongodb.codecs.ClassModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ClassModelBuilder<T> {
    private final Class<T> theClass;
    private final List<FieldModelBuilder> fields = new ArrayList<FieldModelBuilder>();

    public ClassModelBuilder(final Class<T> theClass) {
        this.theClass = theClass;
    }

    public void addField(final FieldModelBuilder field) {
        fields.add(field);
    }

    public void addField(final int index, final FieldModelBuilder field) {
        fields.add(index, field);
    }

    public ClassModel<T> build() {
        return new ClassModel<T>(this);
    }

    public void clearFields() {
        fields.clear();
    }

    public FieldModelBuilder getField(final String fieldName) {
        for (FieldModelBuilder field : fields) {
            if (field.getName().get().equals(fieldName)) {
                return field;
            }
        }

        return null;
    }

    public Collection<FieldModelBuilder> getFields() {
        return fields;
    }

    public Class<T> getModelClass() {
        return theClass;
    }

    public void removeField(final String fieldName) {
        for (FieldModelBuilder field : fields) {
            if (field.getName().get().equals(fieldName)) {
                fields.remove(field);
                return;
            }
        }
    }
}