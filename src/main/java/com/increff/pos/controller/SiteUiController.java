package com.increff.pos.controller;

import com.increff.pos.model.data.InfoData;
import com.increff.pos.util.ValidationUtil;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SiteUiController extends AbstractUiController {

    @Autowired
    private InfoData infoData;
    // WEBSITE PAGES
    @RequestMapping(value = "")
    public ModelAndView index() {
        return mav("redirect:/site/login");
    }

    private Boolean isValidate(){

        Boolean p = !ValidationUtil.isBlank((infoData.getEmail()));
        return p;
    }
    @RequestMapping(value = "/site/login")
    public ModelAndView login() {
        String page = isValidate() ? "redirect:/ui/home":"login.html";
        return mav(page);
    }

    @RequestMapping(value = "/site/signup")
    public ModelAndView signup() {
        String page = isValidate() ? "redirect:/ui/home":"signUp.html";
        return mav(page);
    }

    @RequestMapping(value = "/site/logout")
    public ModelAndView logout() {
        return mav("login.html");
    }

    @RequestMapping(value = "/site/pricing")
    public ModelAndView pricing() {
        return mav("pricing.html");
    }

    @RequestMapping(value = "/site/features")
    public ModelAndView features() {
        return mav("features.html");
    }

}
