package com.xuecheng.ucenter.service.impl;

import com.xuecheng.ucenter.model.dto.AuthParamsDto;
import com.xuecheng.ucenter.model.dto.XcUserExt;
import com.xuecheng.ucenter.service.AuthService;
import org.springframework.stereotype.Service;

/**
 * @projectName: xuecheng-plus
 * @package: com.xuecheng.ucenter.service.impl
 * @className: WxAuthServiceImpl
 * @author: Caixi
 * @description: TODO
 * @date: 2023/4/7 19:13
 * @version: 1.0
 */

@Service("wx_authservice")
public class WxAuthServiceImpl implements AuthService {
    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        return null;
    }
}