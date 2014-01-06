package org.mongodb.codecs;

import org.bson.BSONReader;
import org.bson.BSONWriter;
import org.mongodb.CollectibleCodec;

public class ClassModelCodec<T> implements CollectibleCodec<T> {

    private final ClassModel<T> classModel;
    private final ClassModelDecoder<T> decoder;
    private final ClassModelEncoder<T> encoder;

    public ClassModelCodec(final ClassModel<T> classModel) {
        this.classModel = classModel;
        decoder = new ClassModelDecoder<T>(classModel);
        encoder = new ClassModelEncoder<T>(classModel);
    }

    @Override
    public <E> T decode(final BSONReader reader) {
        return decoder.decode(reader);
    }

    @Override
    public void encode(final BSONWriter bsonWriter, final T value) {
        encoder.encode(bsonWriter, value);
    }

    @Override
    public Class<T> getEncoderClass() {
        return classModel.getModelClass();
    }

    @Override
    public Object getId(final T document) {
        return null;
    }
}