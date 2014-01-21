package org.mongodb.codecs.configuration;

public class CodecConfigurationException extends RuntimeException {

    public CodecConfigurationException(final String msg) {
        super(msg);
    }

    public CodecConfigurationException(final String msg, final Throwable t) {
        super(msg, t);
    }
}
