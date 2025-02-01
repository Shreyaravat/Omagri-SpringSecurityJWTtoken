package com.security.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtHelper 
{

    public static final long JWT_TOKEN_VALIDITY = 60; // 30 minutes
//  private final SecretKey secretKey;

    private final SecretKey secretKey = Keys.hmacShaKeyFor("afafasfafafasfasfasfafacasdasfasxASFACASDFACASDFASFASFDAFASFASDAADSCSDFADCVSGCFVADXCcadwavfsfarvf".getBytes());       
    

    public String getUsernameFromToken(String token) 
    {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public Date getExpirationDateFromToken(String token)
    {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimResolver)
    {
        final Claims claims = getAllClaimsFromToken(token);
        return claimResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) 
    {
    	return Jwts.parser()
				.verifyWith(secretKey)
				.build()
				.parseSignedClaims(token).getPayload();
    }

    private boolean isTokenExpired(String token) 
    {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(UserDetails userDetails)
    {
        Map<String, Object> claims = new HashMap<>();
        return doGenerateToken(claims, userDetails.getUsername());
    }

    private String doGenerateToken(Map<String, Object> claims, String username) 
    {
    	return Jwts.builder()
				.claims()
				.add(claims)
				.subject(username)
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
				.and()
//				.signWith(secretKey,SignatureAlgorithm.HS256)
				.signWith(secretKey)
				.compact();
    }

    public boolean validateToken(String token, UserDetails userDetails)
    {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}















//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.function.Function;
//
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.SignatureAlgorithm;
//
//@Component
//public class JwtHelper
//{
//	public static final long JWT_TOKEN_VALIDITY = 30 * 60;
//	
//	private String secret = "afafasfafafasfasfasfafacasdasfasxASFACASDFACASDFASFASFDAFASFASDAADSCSDFADCVSGCFVADXCcadwavfsfarvf";
//	
//	public String getUsernameFromToken(String token)
//	{
//		return getClaimFromToken(token, Claims::getSubject);
//	}
//
//	public Date getExpirationDateFromToken(String token)
//	{
//        return getClaimFromToken(token, Claims::getExpiration);
//    }
//	
//	public<T> T getClaimFromToken(String token, Function<Claims, T> claimResolver) 
//	{ 
//		final Claims claims = getAllClaimsFromToken(token);
//		return claimResolver.apply(claims);
//	}
//
//	private Claims getAllClaimsFromToken(String token) 
//	{
//		return Jwts.builder()
//				.verifyWith(secret.getBytes())
//				.build()
//				.parseSignedClaims(token).getPayload();
//	}
//	
//	// checking if the token is expired or not
//	 private Boolean isTokenExpired(String token) 
//	 {
//	        final Date expiration = getExpirationDateFromToken(token);
//	        return expiration.before(new Date());
//	    }
//
//	    //generate token for user
//	    public String generateToken(UserDetails userDetails)
//	    {
//	        Map<String, Object> claims = new HashMap<>();
//	        return doGenerateToken(claims, userDetails.getUsername());
//	    }
//
//	    private String doGenerateToken(Map<String, Object> claims, String username) {
//	        return Jwts.builder()
//	                   .setClaims(claims)
//	                   .setSubject(username)
//	                   .setIssuedAt(new Date(System.currentTimeMillis()))
//	                   .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
//	                   .signWith(SignatureAlgorithm.HS512, secret.getBytes())
//	                   .compact();
//	    }
//
//		
//		 //validate token
//	    public Boolean validateToken(String token, UserDetails userDetails) {
//	        final String username = getUsernameFromToken(token);
//	        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
//	    }
//}



















//import java.security.NoSuchAlgorithmException;
//import java.util.Base64;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.function.Function;
//
//import javax.crypto.KeyGenerator;
//import javax.crypto.SecretKey;
//
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Service;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//
//@Service
//public class JwtService 
//{
//	
//	private String secretKey = "";
//	
//	public JwtService()
//	{
//		try 
//		{
//			KeyGenerator keygen = KeyGenerator.getInstance("HmacSHA256");
//			SecretKey sk = keygen.generateKey();
//			secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
//		}
//		catch (NoSuchAlgorithmException e)
//		{
//			e.printStackTrace();
//		}
//	}
//	
//	public String generateToken(String username) 
//	{
//		Map<String, Object> claims = new HashMap<>();
//		
//		return Jwts.builder()
//				.claims()
//				.add(claims)
//				.subject(username)
//				.issuedAt(new Date(System.currentTimeMillis()))
//				.expiration(new Date(System.currentTimeMillis() + 60*1000*30))
//				.and()
//				.signWith(getKey())
//				.compact();
//		
//	}
//
//	private SecretKey getKey()
//	{
//		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
//		return Keys.hmacShaKeyFor(keyBytes);
//	}
//
//	public String extractUserName(String token)
//	{
//		return extractClaim(token, Claims::getSubject);
//	}
//
//	private <T> T extractClaim(String token, Function<Claims, T> claimResolver)
//	{
//		final Claims claims = extractAllClaims(token);
//		return claimResolver.apply(claims);
//	}
//	
//	private Claims extractAllClaims(String token)
//	{
//		return Jwts.parser()
//				.verifyWith(getKey())
//				.build()
//				.parseSignedClaims(token).getPayload();
//	}
//	
//	public boolean validateToken(String token, UserDetails userDetails) 
//	{
//		final String userName = extractUserName(token);
//		return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
//	}
//	
//	private boolean isTokenExpired(String token)
//	{
//		return extractExpiration(token).before(new Date());	
//	}
//	
//	private Date extractExpiration(String token)
//	{
//		return extractClaim(token, Claims::getExpiration);
//	}
//}







//Purpose: Manages JWT token generation and validation.

