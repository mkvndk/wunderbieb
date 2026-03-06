package nl.wunderbieb.kms.api.security;

public final class UnauthorizedException extends RuntimeException {

  public UnauthorizedException() {
    super("Authenticatiegegevens ontbreken.");
  }
}
