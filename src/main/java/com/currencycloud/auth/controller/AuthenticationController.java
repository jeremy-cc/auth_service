package com.currencycloud.auth.controller;

import com.currencycloud.auth.exception.DataAccessException;
import com.currencycloud.auth.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/*
* This controller encapsulates the crm engine's authentication routes -
* authentication for api, api v2, back office and apiproxy
*
* it is intended to be fast, performant and stateless
* */
@RestController
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService service) {
        this.authenticationService = service;
    }

    // get security question

    // post authenticate
    @RequestMapping(path = "/authenticate", method = RequestMethod.POST)
    public @ResponseBody
    Map<String,Object> authenticate(@RequestParam(value = "login_id") String loginId,
                                    @RequestParam(value = "password") String password,
                                    @RequestParam(value = "security_question_number") String qNo,
                                    @RequestParam(value = "security_answer") String answer) throws DataAccessException {
        return authenticationService.authenticate(loginId, password, Integer.parseInt(qNo), answer, "trading");
    }

    // post authenticate_api
    @RequestMapping(path = "/authenticate_api", method = RequestMethod.POST)
    public @ResponseBody Map<String,Object> authenticateApi(
            @RequestParam(value = "login_id") String loginId,
            @RequestParam(value = "api_key") String apiKey) throws DataAccessException {

        return authenticationService.authenticateApi(loginId, apiKey);
    }

    // get a security question from the system
    @RequestMapping(path = "/security_question", method = RequestMethod.GET)
    public @ResponseBody Map<String,Object> getSecurityQuestion(
            @RequestParam(value = "login_id") String loginId) throws DataAccessException {

        return authenticationService.getSecurityQuestion(loginId);
    }


    // post authenticate_for_back_office
    @RequestMapping(path = "/authenticate_for_back_office", method = RequestMethod.POST)
    public @ResponseBody
    Map<String,Object> authenticateForBackOffice(@RequestParam(value = "login_id") String loginId,
                                    @RequestParam(value = "password") String password,
                                    @RequestParam(value = "security_question_number") String qNo,
                                    @RequestParam(value = "security_answer") String answer) throws DataAccessException {
        return authenticationService.authenticate(loginId, password, Integer.parseInt(qNo), answer, "back_office");
    }

    // apiproxy get details for contact by login id
    @RequestMapping(path = "/apiproxy_authorisation_details", method = RequestMethod.GET)
    public @ResponseBody
    Map<String,Object> getApiProxyAuthorisationDetails(@RequestParam(value = "login_id") String loginId) throws DataAccessException {
        return authenticationService.getApiProxyAuthorisationDetails(loginId);
    }

}
