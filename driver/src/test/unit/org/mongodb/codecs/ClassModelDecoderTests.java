package org.mongodb.codecs;

import org.bson.BSONBinaryReader;
import org.bson.BSONBinaryWriter;
import org.bson.BSONReaderSettings;
import org.bson.BSONWriter;
import org.bson.ByteBufNIO;
import org.bson.io.BasicInputBuffer;
import org.bson.io.BasicOutputBuffer;
import org.bson.io.InputBuffer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mongodb.Codec;
import org.mongodb.Document;
import org.mongodb.codecs.configuration.ClassModelBuilder;
import org.mongodb.codecs.pojo.Address;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.junit.Assert.assertEquals;

public class ClassModelDecoderTests {

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
    public void shouldDecodeFullClass() throws IOException {
        ClassModelBuilder<Address> builder = new ClassModelBuilder<Address>(Address.class);
        builder.map("address1").name("a1").codec(new StringCodec());
        builder.map("address2").name("a2").codec(new StringCodec());

        Document expected = new Document();
        expected.append("a1", "first");
        expected.append("a2", "second");

        Address actual = decode(builder, expected);

        assertEquals("first", actual.getAddress1());
        assertEquals("second", actual.getAddress2());
    }

    private <T> T decode(final ClassModelBuilder<T> builder, final Document value) throws IOException {
        ClassModel<T> model = new ClassModel<T>(builder);
        Codec<T> codec = new ClassModelCodec<T>(model);

        documentCodec.encode(writer, value);

        return readFromOutputBuffer(codec);
    }

    private <T> T readFromOutputBuffer(final Codec<T> codec) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        buffer.pipe(baos);
        InputBuffer inputBuffer = new BasicInputBuffer(new ByteBufNIO(ByteBuffer.wrap(baos.toByteArray())));
        return codec.decode(new BSONBinaryReader(new BSONReaderSettings(), inputBuffer, false));
    }
}