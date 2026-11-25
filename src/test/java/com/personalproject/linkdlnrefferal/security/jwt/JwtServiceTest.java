package com.personalproject.linkdlnrefferal.security.jwt;

import com.personalproject.linkdlnrefferal.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String SECRET = "cmVmZXJsaW5rLWxvY2FsLXRlc3Qtand0LXNlY3JldC0yMDI2";

    private final JwtService jwtService = new JwtService(SECRET, 900000L, 604800000L);

    @Test
    void accessTokenContainsSubjectAndType() {
        Claims claims = jwtService.parseToken(jwtService.generateAccessToken(42L, "user@example.com"));

        assertThat(claims.getSubject()).isEqualTo("user@example.com");
        assertThat(claims.get("userId").toString()).isEqualTo("42");
        assertThat(claims.get("type", String.class)).isEqualTo("access");
    }

    @Test
    void invalidTokenIsRejected() {
        assertThatThrownBy(() -> jwtService.parseToken("not-a-token"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid or expired token");
    }
}
