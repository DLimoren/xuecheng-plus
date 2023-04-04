package com.xuecheng.content.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @projectName: xuecheng-plus
 * @package: com.xuecheng.content.api
 * @className: FreemarkerTest
 * @author: Caixi
 * @description: TODO
 * @date: 2023/4/4 22:53
 * @version: 1.0
 */

@Controller
public class FreemarkerTestController {

    @GetMapping("/testfreemarker")
    public ModelAndView test(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name" , "小明");
        modelAndView.setViewName("test");
        return modelAndView;
    }
}
