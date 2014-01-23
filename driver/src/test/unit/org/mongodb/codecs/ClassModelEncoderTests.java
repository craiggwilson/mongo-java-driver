package org.mongodb.codecs;

import org.bson.BSONBinaryReader;
import org.bson.BSONBinaryWriter;
import org.bson.BSONBinaryWriterSettings;
import org.bson.BSONReaderSettings;
import org.bson.BSONWriter;
import org.bson.BSONWriterSettings;
import org.bson.ByteBufNIO;
import org.bson.io.BasicInputBuffer;
import org.bson.io.BasicOutputBuffer;
import org.bson.io.InputBuffer;
import org.bson.io.OutputBuffer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mongodb.Codec;
import org.mongodb.CodecRegistry;
import org.mongodb.Document;
import org.mongodb.codecs.configuration.ClassModelBuilder;
import org.mongodb.codecs.pojo.Address;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

public class ClassModelEncoderTests {

    private BasicOutputBuffer buffer;
    private DocumentCodec documentCodec;
    private BSONWriter writer;

    @Before
    public void setUp() throws Exception {
        buffer = new BasicOutputBuffer();
        writer = new BSONBinaryWriter(buffer, true);
        documentCodec = new DocumentCodec(PrimitiveCodecs.createDefault());
    }

    @After
    public void tearDown() {
        writer.close();
    }

    @Test
    public void shouldEncodeFullClass() throws IOException {
        ClassModelBuilder<Address> builder = new ClassModelBuilder<Address>(Address.class);
        builder.map("address1").name("a1").codec(new StringCodec());
        builder.map("address2").name("a2").codec(new StringCodec());

        Address value = new Address();

        Document actual = encode(builder, value);

        Document expected = new Document();
        expected.append("a1", value.getAddress1());
        expected.append("a2", value.getAddress2());

        assertEquals(expected, actual);
    }

    @Test
    public void shouldSkipFieldsSetToIgnoreDefaultValues() throws IOException {
        ClassModelBuilder<Address> builder = new ClassModelBuilder<Address>(Address.class);
        builder.map("address1").name("a1").codec(new StringCodec()).defaultValue("Flat 1").ignoreIfDefault();
        builder.map("address2").name("a2").codec(new StringCodec());

        Address value = new Address();

        Document actual = encode(builder, value);

        Document expected = new Document();
        expected.append("a2", value.getAddress2());

        assertEquals(expected, actual);
    }

    private <T> Document encode(final ClassModelBuilder<T> builder, T value) throws IOException {
        ClassModel<T> model = new ClassModel<T>(builder);
        Codec<T> codec = new ClassModelCodec<T>(model);

        codec.encode(writer, value);

        return readDocumentFromOutputBuffer();
    }

    private Document readDocumentFromOutputBuffer() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        buffer.pipe(baos);
        InputBuffer inputBuffer = new BasicInputBuffer(new ByteBufNIO(ByteBuffer.wrap(baos.toByteArray())));
        return documentCodec.decode(new BSONBinaryReader(new BSONReaderSettings(), inputBuffer, false));
    }
}