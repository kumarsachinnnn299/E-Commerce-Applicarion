package com.ecommerce.project.security.jwt;

import com.ecommerce.project.security.services.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.util.Date;
//import java.util.logging.Logger;

@Component
public class JwtUtils {
    private static final Logger logger= (Logger) LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.app.jwtSecret}")
    private String jwtSecret;//will be used for signing the tokens
    @Value("${spring.app.jwtExpirationMs}")
    private long jwtExpirationMs;

    @Value("${spring.app.jwtCookieName}")
    private String jwtCookie;

    /*

    This method is useful when we authenticate without Cookie

    public String getJwtFromHeader(HttpServletRequest request)
    {
        String bearerToken=request.getHeader("Authorization");
        logger.debug("Authorization Header: {}",bearerToken);
        if(bearerToken!=null&&bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);//Remove Bearer Prefix
        }
        return null;
    }

     public String generateTokenFromUserName(UserDetails userDetails)
    {
        String userName=userDetails.getUsername();
        return Jwts.builder()
                .subject(userName)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime()+jwtExpirationMs))
                .signWith(key())
                .compact();
    }

    */

    //This method to get Jwt from cookie
    public String getJwtFromCookie(HttpServletRequest request)
    {
        Cookie cookie= WebUtils.getCookie(request,jwtCookie);
        if(cookie!=null){
            return cookie.getValue();
        }else{
            return null;
        }
    }


    public ResponseCookie generateJwtCookie(UserDetailsImpl userPrincipal){
        String jwt=generateTokenFromUserName(userPrincipal.getUsername());
        ResponseCookie cookie=ResponseCookie.from(jwtCookie,jwt).path("/api").maxAge(24*
                60*60)
                .httpOnly(false)//this will allow client side scripts to access the cookies
                .build();
        return cookie;
    }

    //This method is used for signout user
    public ResponseCookie getCleanJwtCookie(){
        ResponseCookie cookie=ResponseCookie.from(jwtCookie,null)
                .path("/api")
                .build();
        return cookie;
    }

    //This method is change for cookie . The one which we use when we dont implement cokkie, is insde the comment above
    public String generateTokenFromUserName(String userName)
    {

        return Jwts.builder()
                .subject(userName)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime()+jwtExpirationMs))
                .signWith(key())
                .compact();
    }


    public String getUserNameFromJwtToken(String token)
    {
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build().parseSignedClaims(token)
                .getPayload().getSubject();
    }

    private SecretKey key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public boolean validateJwtToken(String authToken)
    {
        try {
            System.out.println("Validate");
            Jwts.parser().verifyWith((SecretKey) key()).build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (MalformedJwtException e)
        {
            logger.error("Invalid JWT token: {}", e.getMessage());
        }
        catch(ExpiredJwtException e)
        {
            logger.error("JWT token is expired: {}", e.getMessage());
        }
        catch (UnsupportedJwtException e)
        {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        }
        catch (IllegalArgumentException e)
        {
            logger.error("JWT claims string is empty: {}", e.getMessage());

        }
        return false;
    }

}
