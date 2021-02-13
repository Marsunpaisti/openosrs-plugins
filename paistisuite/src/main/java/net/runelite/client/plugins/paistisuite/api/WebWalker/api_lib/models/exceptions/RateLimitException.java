package net.runelite.client.plugins.paistisuite.api.WebWalker.api_lib.models.exceptions;

public class RateLimitException extends RuntimeException {
    public RateLimitException(String message) {
        super(message);
    }
}
