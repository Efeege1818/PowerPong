package de.hhn.it.devtools.apis.turnbasedbattle;

/**
 * exception to be thrown when a ScreenManager has not the ability to switch screens due to unknown
 * source- or target-screen.
 */
public class UnknownTransitionException extends RuntimeException {
  private static final org.slf4j.Logger logger =
      org.slf4j.LoggerFactory.getLogger(UnknownTransitionException.class);


  private String from;
  private String to;

  /**
   * Creates an UnknownTransitionException.
   *
   * @param message message of the exception
   * @param from name of the screen where the transition should start
   * @param to name of the screen where the transition should end
   */
  public UnknownTransitionException(final String message, final String from, final String to) {
    super(message);
  }

  public String getFrom() {
    return from;
  }

  public String getTo() {
    return to;
  }
}
