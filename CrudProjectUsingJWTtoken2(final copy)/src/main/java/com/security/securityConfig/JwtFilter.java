package com.security.securityConfig;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.security.model.Usertable;
import com.security.repo.RepoClass;
import com.security.service.JwtHelper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter
{
	private Logger logger = LoggerFactory.getLogger(OncePerRequestFilter.class);

	@Autowired
	private RepoClass repo;
	
	@Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private UserDetailsService userDetailsService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException 
	{
		// Authorization = Bearer ...token...
        String requestHeader = request.getHeader("Authorization");		//now we got the token present in the authoriztion header, by requesting to this authorization header.			
        //Bearer 2352345235sdfrsfgsdfsdf
        logger.info(" Header :  {}", requestHeader);
        String username = null;					//store the username extracted from the token here in username string.
        String token = null;					//store the token after cutting bearer part, here in the token.
        if (requestHeader != null && requestHeader.startsWith("Bearer")) 
        {
            token = requestHeader.substring(7);
            try 
            {
                username = this.jwtHelper.getUsernameFromToken(token);
            } 
            catch (IllegalArgumentException e) 
            {
                logger.info("Illegal Argument while fetching the username !!");
                e.printStackTrace();
            } 
            catch (ExpiredJwtException e) 
            {
                logger.info("Given jwt token is expired !!");
                
                // Extract the username from the expired token to generate a new token
                username = e.getClaims().getSubject();
                
                // Generate new token after extracting the username
                String newToken = jwtHelper.generateToken(userDetailsService.loadUserByUsername(username));
                
                // Update the token in the database
                Usertable user = repo.findByUsername(username);  // Fetch the user from the database using the username

                if (user != null) 
                {
                    user.setToken(newToken);  // Assuming you have a 'token' field in the Usertable model
                    repo.save(user);  // Save the updated user to the database
                } 
                else 
                {
                    logger.error("User not found in the database.");
                }
                
                // Set the new token in the response header
                response.setHeader("Authorization", "Bearer " +newToken);
                
                // Return the response with a status and message
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Token expired, new token generated! \n\"New Token\": \"" + newToken + "\"}");
                return;
            }
            catch (MalformedJwtException e) 
            {
                logger.info("Some changes has done in token !! Invalid Token");
                e.printStackTrace();
            }
            catch (Exception e) 
            {
                e.printStackTrace();
            }

        } 
        else 
        {
            logger.info("Invalid Header Value !! ");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) 
        {
            //fetch user detail from username
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            Boolean validateToken = this.jwtHelper.validateToken(token, userDetails);
            if (validateToken)
            {
                //set the authentication
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());        
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            else 
            {
                logger.info("Validation fails !!");
            }
        }

        filterChain.doFilter(request, response);
	}
}












//@Component
//public class JwtFilter extends OncePerRequestFilter
//{
//	
//	@Autowired
//	private JwtService service;
//	
//	@Autowired
//	ApplicationContext context;
//
//	@Override
//	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//			throws ServletException, IOException 
//	{
//		String authHeader = request.getHeader("Authorization");
//		String token = null;
//		String username = null;
//		
//		if(authHeader != null && authHeader.startsWith("Bearer "))
//		{
//			token = authHeader.substring(7);
//			username = service.extractUserName(token);
//		}
//		
//		if(username != null && SecurityContextHolder.getContext().getAuthentication() == null)
//		{
//			
//			UserDetails userDetails = context.getBean(MyUserDetailsService.class).loadUserByUsername(username);
//			if(service.validateToken(token, userDetails))
//			{
//				UsernamePasswordAuthenticationToken authToken = 
//						new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//				
//				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//				SecurityContextHolder.getContext().setAuthentication(authToken);
//			}
//		}
//		filterChain.doFilter(request, response);
//	}	
//}












//Purpose: Intercepts HTTP requests to check for valid JWT tokens.

