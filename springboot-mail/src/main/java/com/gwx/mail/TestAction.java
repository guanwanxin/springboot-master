package com.gwx.mail;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: TestAction
 * @Description:
 * @Auther: Thomas
 * @Version: 1.0
 * @create 2021/5/1 22:34
 */
@RestController
public class TestAction {

    @RequestMapping("/test")
    public String test(){
        return "This is a PropertiesUtil";
    }
}
