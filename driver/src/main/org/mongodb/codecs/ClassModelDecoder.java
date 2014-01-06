package org.mongodb.codecs;

import org.bson.BSONReader;
import org.bson.BSONType;
import org.mongodb.Decoder;

public class ClassModelDecoder<T> implements Decoder<T> {

    private final ClassModel<T> classModel;

    public ClassModelDecoder(final ClassModel<T> classModel) {
        this.classModel = classModel;
    }

    @Override
    public <E> T decode(final BSONReader reader) {
        reader.readStartDocument();

        T instance;
        try {
            instance = classModel.createInstanceOfClass();
        } catch (IllegalAccessException e) {
            throw new DecodingException("oops 1");
        } catch (InstantiationException e) {
            throw new DecodingException("oops 2");
        }

        while (reader.readBSONType() != BSONType.END_OF_DOCUMENT) {

            try {
                FieldModel field = getFieldForNextElement(reader);
                Object decodedValue = field.getCodec().decode(reader);
                field.setValue(instance, decodedValue);
            } catch (IllegalAccessException e) {
                throw new DecodingException("oops 3");
            } catch (NoSuchFieldException e) {
                // we have a field in the document that hasn't been mapped
                // for now, we'll ignore these fields and lose data
            }
        }

        reader.readEndDocument();

        return instance;
    }

    private FieldModel getFieldForNextElement(final BSONReader reader) throws NoSuchFieldException {
        String name = reader.readName();
        return classModel.getDeclaredField(name);
    }
}