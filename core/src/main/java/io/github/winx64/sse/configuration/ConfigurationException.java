package io.github.winx64.sse.configuration;

final class ConfigurationException extends RuntimeException {

    private static final long serialVersionUID = -793672320756754240L;

    ConfigurationException(String message) {
        super(message);
    }

    ConfigurationException(String format, Object... args) {
        super(String.format(format, args));
    }
}
