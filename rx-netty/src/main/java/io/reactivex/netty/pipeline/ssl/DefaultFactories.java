package io.reactivex.netty.pipeline.ssl;

import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.util.SelfSignedCertificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

/**
 * @author Tomasz Bak
 */
public final class DefaultFactories {

    public static SSLEngineFactory SELF_SIGNED = new SelfSignedSSLEngineFactory();

    public static SSLEngineFactory TRUST_ALL = new TrustAllSSLEngineFactory();

    private DefaultFactories() {
    }

    public static SSLEngineFactory fromSSLContext(SSLContext sslContext) {
        return new SSLContextBasedFactory(sslContext);
    }

    public static class SSLContextBasedFactory implements SSLEngineFactory {

        private final SSLContext sslContext;

        public SSLContextBasedFactory(SSLContext sslContext) {
            this.sslContext = sslContext;
        }

        @Override
        public SSLEngine createSSLEngine(ByteBufAllocator allocator) {
            return sslContext.createSSLEngine();
        }
    }

    private static class TrustAllSSLEngineFactory implements SSLEngineFactory {

        private final SslContext sslCtx;

        private TrustAllSSLEngineFactory() {
            try {
                sslCtx = SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);
            } catch (SSLException e) {
                throw new IllegalStateException("Failed to create Netty's Ssl context with InsecureTrustManagerFactory", e);
            }
        }

        @Override
        public SSLEngine createSSLEngine(ByteBufAllocator allocator) {
            return sslCtx.newEngine(allocator);
        }
    }

    private static class SelfSignedSSLEngineFactory implements SSLEngineFactory {

        private final SslContext sslCtx;

        private SelfSignedSSLEngineFactory() {
            SelfSignedCertificate ssc;
            try {
                ssc = new SelfSignedCertificate();
            } catch (CertificateException e) {
                throw new IllegalStateException("Self signed certificate creation error", e);
            }
            try {
                sslCtx = SslContext.newServerContext(ssc.certificate(), ssc.privateKey());
            } catch (SSLException e) {
                throw new IllegalStateException("Failed to create Netty's Ssl context with self signed certificate", e);
            }
        }

        @Override
        public SSLEngine createSSLEngine(ByteBufAllocator allocator) {
            return sslCtx.newEngine(allocator);
        }
    }
}
