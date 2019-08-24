package io.github.winx64.sse.configuration;

final class ConfigurationException extends Exception {

    private static final long serialVersionUID = -793672320756754240L;

    ConfigurationException(String message) {
        super(message);
    }

    ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }
}
