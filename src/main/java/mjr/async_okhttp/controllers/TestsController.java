package mjr.async_okhttp.controllers;

import lombok.extern.slf4j.Slf4j;
import mjr.async_okhttp.models.Cvi;
import mjr.async_okhttp.services.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestsController
{
    @Autowired
    TestService testService;

    @GetMapping("/go")
    public Cvi go() throws Exception {
        return testService.doTest();
    }
}
