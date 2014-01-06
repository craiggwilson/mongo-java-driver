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
            bsonWriter.writeName(fieldModel.getName());
            fieldModel.getCodec().encode(bsonWriter, fieldValue);
        } catch (IllegalAccessException e) {
            throw new EncodingException("Could not encode field '" + fieldModel.getName() + "' from " + value, e);
        }
    }
}