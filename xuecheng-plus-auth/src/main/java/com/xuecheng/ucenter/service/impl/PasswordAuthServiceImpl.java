package com.xuecheng.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.ucenter.feignclient.CheckCodeClient;
import com.xuecheng.ucenter.mapper.XcUserMapper;
import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.model.po.XcUser;
import com.xuecheng.ucenter.service.AuthService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @projectName: xuecheng-plus
 * @package: com.xuecheng.ucenter.service.impl
 * @className: PasswordAuthServiceImpl
 * @author: Caixi
 * @description: TODO
 * @date: 2023/4/7 19:13
 * @version: 1.0
 */

@Service("password_authservice")
public class PasswordAuthServiceImpl implements AuthService {

    @Autowired
    XcUserMapper xcUserMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CheckCodeClient checkCodeClient;

    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {

        //  校验验证码
        String checkcode = authParamsDto.getCheckcode();
        // 验证码对应的key
        String checkcodekey = authParamsDto.getCheckcodekey();


        if(StringUtils.isEmpty(checkcodekey) || StringUtils.isEmpty(checkcode)){
            throw new RuntimeException("请输入验证码");
        }

        // 远程调用验证码服务接口校验验证码
        Boolean verify = checkCodeClient.verify(checkcodekey, checkcode);

        if(null == verify || !verify){
            throw new RuntimeException("验证码输入错误");
        }

        // 验证账号是否存在
        String username = authParamsDto.getUsername();
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username));
        // 如果用户不存在，返回null即可，由spring security抛出用户不存在异常
        if(xcUser == null ){
            throw new RuntimeException("账号不存在");
        }

        // 验证密码是否正确
        // 如果查到用户拿到正确的密码
        String passwordDb = xcUser.getPassword();
        // 拿到用户输入的密码
        String passwordForm = authParamsDto.getPassword();
        // 校验密码
        boolean matches = passwordEncoder.matches(passwordForm, passwordDb);
        if(!matches){
            throw new RuntimeException("账号或密码错误");
        }
        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(xcUser , xcUserExt);

        return xcUserExt;
    }
}
