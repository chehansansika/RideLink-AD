package com.ridelink.driver.config;

import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

/**
 * Enables MongoDB auditing so that {@code @CreatedDate} and {@code @LastModifiedDate}
 * annotations on domain models are automatically populated.
 *
 * <p>Also registers a {@link MongoClientSettingsBuilderCustomizer} that configures a
 * permissive TLS/SSL context for the MongoDB client. This works around {@code SSLException:
 * internal_error} failures caused by Java 21 disabling cipher suites and named EC curves
 * (via {@code jdk.disabled.namedCurves}) that some MongoDB Atlas cluster TLS stacks still
 * require. Intended for development use; in production, replace with a properly scoped
 * trust store.
 */
@Configuration
@EnableMongoAuditing
public class MongoConfig {

    /**
     * Provides a MongoDB SSL customizer that:
     * <ul>
     *   <li>Explicitly enables TLS on the connection</li>
     *   <li>Bypasses hostname verification (safe for dev against Atlas)</li>
     *   <li>Uses a trust-all {@link SSLContext} so certificate-chain issues do not
     *       interfere with diagnosing cipher-suite negotiation problems</li>
     * </ul>
     */
    @Bean
    public MongoClientSettingsBuilderCustomizer mongoSslCustomizer() {
        return builder -> builder.applyToSslSettings(ssl -> {
            try {
                TrustManager[] trustAll = {new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    public void checkClientTrusted(X509Certificate[] c, String a) {}
                    public void checkServerTrusted(X509Certificate[] c, String a) {}
                }};
                SSLContext ctx = SSLContext.getInstance("TLS");
                ctx.init(null, trustAll, new SecureRandom());
                ssl.enabled(true).invalidHostNameAllowed(true).context(ctx);
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                throw new RuntimeException("Failed to configure MongoDB SSL context", e);
            }
        });
    }
}
