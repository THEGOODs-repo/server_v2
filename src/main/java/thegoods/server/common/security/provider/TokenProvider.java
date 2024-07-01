package thegoods.server.common.security.provider;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import thegoods.server.common.exception.handler.JwtHandler;
import thegoods.server.common.exception.status.ErrorStatus;
import com.umc.TheGoods.web.dto.member.MemberDetail;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;


@Component
@Slf4j
public class TokenProvider implements InitializingBean {

    private final Logger LOGGER = LoggerFactory.getLogger(TokenProvider.class);

    private static final String AUTHORITIES_KEY = "auth";
    public static final String AUTHORIZATION_HEADER = "Authorization";


    public enum TokenType {
        ACCESS, REFRESH
    }

    private final String secret;
    private Key key;

    private final long accessTokenValidityInMilliseconds;


    public TokenProvider(@Value("${jwt.token.secret}") String secretKey) {
        this.secret = secretKey;
        this.accessTokenValidityInMilliseconds = 1000 * 60 * 60 * 6L;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }


    public String createAccessToken(Long memberId, String memberRole, String email, Collection<? extends GrantedAuthority> authorities) {
        //30분

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.accessTokenValidityInMilliseconds);


        //토큰 생성
        return Jwts.builder()
                .setSubject(String.valueOf(memberId))
                .claim(AUTHORITIES_KEY, authorities)
                .claim("memberRole", memberRole)
                .claim("email", email)
                .claim("type", TokenType.ACCESS)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();


    }


    public String createRefreshToken(Collection<? extends GrantedAuthority> authorities) {
        //2주
        long tokenValidTime = 60 * 60 * 24 * 30 * 1000L;
        long now = (new Date()).getTime();
        Date validity = new Date(now + tokenValidTime);

        return Jwts.builder()
                .claim(AUTHORITIES_KEY, authorities)
                .claim("type", TokenType.REFRESH)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();


    }

    // JWT 토큰 인증 정보 조회
    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String token, TokenType type) throws JwtHandler {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            throw new JwtHandler(ErrorStatus.JWT_BAD_REQUEST);
        } catch (ExpiredJwtException e) {
            if (type == TokenType.ACCESS) throw new JwtHandler(ErrorStatus.JWT_ACCESS_TOKEN_EXPIRED);
            else throw new JwtHandler(ErrorStatus.JWT_REFRESH_TOKEN_EXPIRED);
        } catch (UnsupportedJwtException e) {
            throw new JwtHandler(ErrorStatus.JWT_UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new JwtHandler(ErrorStatus.JWT_BAD_REQUEST);
        }
    }

    public Long validateAndReturnSubject(String token) throws JwtHandler {
        try {
            Claims body = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return Long.valueOf(body.getSubject());
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            throw new JwtHandler(ErrorStatus.JWT_BAD_REQUEST);


        } catch (ExpiredJwtException e) {
            throw new JwtHandler(ErrorStatus.JWT_ACCESS_TOKEN_EXPIRED);

        } catch (UnsupportedJwtException e) {
            throw new JwtHandler(ErrorStatus.JWT_UNSUPPORTED_TOKEN);

        } catch (IllegalArgumentException e) {
            throw new JwtHandler(ErrorStatus.JWT_BAD_REQUEST);

        }
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
