package org.mongodb.codecs

import org.mongodb.Codec
import org.mongodb.util.Factory;
import org.mongodb.util.Lazy;
import spock.lang.Specification
import spock.lang.Subject


class LazyCodecSpecification extends Specification {
    private final Codec<String> actualCodec = Mock();
    private final Factory<Codec<String>> lazyFactory = Mock();

    @Subject
    private final LazyCodec<String> subject = new LazyCodec<>(new Lazy<Codec<String>>(lazyFactory));

    def setup() {
        lazyFactory.get() >> actualCodec;
    }

    def "should delegate encode to underlying codec"() {
        given:
        String s = "funny";

        when:
        subject.encode(null, s);

        then:
        1 * actualCodec.encode(null, s);
    }

    def "should delegate decode to underlying codec"() {
        when:
        subject.decode(null);

        then:
        1 * actualCodec.decode(null);
    }

    def "should delegate getEncoderClass to underlying codec"() {
        when:
        subject.getEncoderClass();

        then:
        1 * actualCodec.getEncoderClass();
    }
}