package sample;

import java.util.Set;

/**
 * An HTTP <a href="http://www.w3.org/TR/cors/">Cross-Origin Resource Sharing</a>.
 */
public interface CorsPolicy {

    /**
     * Returns the Access-Control-Allow-Origin.
     */
    Set<String> getAllowOrigin();

    /**
     * Set the Access-Control-Allow-Origin.
     */
    void setAllowOrigin(Set<String> allowOrigin);

    /**
     * Returns the Access-Control-Allow-Credentials.
     */
    Boolean getAllowCredentials();

    /**
     * Set the Access-Control-Allow-Credentials.
     */
    void setAllowCredentials(Boolean allowCredentials);

    /**
     * Returns the Access-Control-Expose-Headers.
     */
    Set<String> getExposeHeaders();

    /**
     * Set the Access-Control-Expose-Headers.
     */
    void setExposeHeaders(String... exposeHeaders);

    /**
     * Set the Access-Control-Expose-Headers.
     */
    void setExposeHeaders(Set<String> exposeHeaders);

    /**
     * Returns the Access-Control-Max-Age.
     */
    int getMaxAge();

    /**
     * Set the Access-Control-Max-Age.
     */
    void setMaxAge(int maxAge);

    /**
     * Returns the Access-Control-Allow-Methods.
     */
    Set<String> getAllowMethods();

    /**
     * Set the Access-Control-Allow-Methods.
     */
    void setAllowMethods(String... allowMethods);

    /**
     * Set the Access-Control-Allow-Methods.
     */
    void setAllowMethods(Set<String> allowMethods);

    /**
     * Returns the Access-Control-Allow-Headers.
     */
    Set<String> getAllowHeaders();

    /**
     * Set the Access-Control-Allow-Headers.
     */
    void setAllowHeaders(String... allowHeaders);

    /**
     * Set the Access-Control-Allow-Methods.
     */
    void setAllowHeaders(Set<String> allowHeaders);
}
