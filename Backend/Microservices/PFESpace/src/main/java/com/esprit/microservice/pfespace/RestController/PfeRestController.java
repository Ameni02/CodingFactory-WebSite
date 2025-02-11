package com.esprit.microservice.pfespace.RestController;

import com.esprit.microservice.pfespace.Services.IPfeService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PfeRestController {
    IPfeService pfeService;
}
