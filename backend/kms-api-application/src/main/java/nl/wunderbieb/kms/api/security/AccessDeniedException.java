package nl.wunderbieb.kms.api.security;

public final class AccessDeniedException extends RuntimeException {

  private final String requiredCapability;

  public AccessDeniedException(String requiredCapability) {
    super("Je hebt geen rechten om deze actie uit te voeren.");
    this.requiredCapability = requiredCapability;
  }

  public String requiredCapability() {
    return requiredCapability;
  }
}
