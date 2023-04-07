package com.xuecheng.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

/**
 * @projectName: xuecheng-plus
 * @package: com.xuecheng.auth.config
 * @className: DaoAuthenticationProviderCustom
 * @author: Caixi
 * @description: TODO
 * @date: 2023/4/7 18:50
 * @version: 1.0
 */

@Component
public class DaoAuthenticationProviderCustom extends DaoAuthenticationProvider {

    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        super.setUserDetailsService(userDetailsService);
    }
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

    }
}
