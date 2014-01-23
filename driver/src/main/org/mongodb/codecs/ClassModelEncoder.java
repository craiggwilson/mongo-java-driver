package org.mongodb.codecs;

import org.bson.BSONWriter;
import org.mongodb.Encoder;

public class ClassModelEncoder<T> implements Encoder<T> {

    private final ClassModel<T> classModel;

    public ClassModelEncoder(final ClassModel<T> classModel) {
        this.classModel = classModel;
    }

    @Override
    public void encode(final BSONWriter bsonWriter, final T value) {
        bsonWriter.writeStartDocument();
        encodePojo(bsonWriter, value);
        bsonWriter.writeEndDocument();
    }

    @Override
    public Class<T> getEncoderClass() {
        return classModel.getModelClass();
    }

    private void encodePojo(final BSONWriter bsonWriter, final T value) {
        for (FieldModel fieldModel : classModel.getFields()) {
            encodeField(bsonWriter, value, fieldModel);
        }
    }

    private void encodeField(final BSONWriter bsonWriter, final T value, final FieldModel fieldModel) {
        try {
            Object fieldValue = fieldModel.getValue(value);
            if (!shouldEncodeField(fieldModel, fieldValue)) {
                return;
            }

            bsonWriter.writeName(fieldModel.getName());
            fieldModel.getCodec().encode(bsonWriter, fieldValue);
        } catch (IllegalAccessException e) {
            throw new EncodingException("Could not encode field '" + fieldModel.getName() + "' from " + value, e);
        }
    }

    private boolean shouldEncodeField(final FieldModel fieldModel, final Object fieldValue) {
        if (!fieldModel.getIgnoreIfDefault()) {
            return true;
        }

        Object defaultValue = fieldModel.getDefaultValue();
        if (defaultValue == null && fieldValue == null) {
            return false;
        }

        if (defaultValue.equals(fieldValue)) {
            return false;
        }

        return true;
    }
}