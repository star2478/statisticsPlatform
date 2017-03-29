package com.z.statisticsPlatform.controller;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatisticsPlatformController {
    private final Logger logger = Logger.getLogger(getClass());
    
    @Autowired
    private DiscoveryClient client;
    
    @RequestMapping(value = "/goverC", method = RequestMethod.GET)
    public Integer goverC(@RequestParam Integer a, @RequestParam Integer b) {
        ServiceInstance instance = client.getLocalServiceInstance();
        Integer r = a + b;
        logger.info("/goverC, host:" + instance.getHost() + ", service_id:" + instance.getServiceId() + ", result:" + r);
        return r;
    }
    
    @RequestMapping(value = "/hello" ,method = RequestMethod.GET)
    public String hello() {
//        ServiceInstance instance = client.getLocalServiceInstance();
//        logger.info("/hello, host:" + instance.getHost() + ", service_id:" + instance.getServiceId());
        return "GoverC: Hello World";
    }
}
