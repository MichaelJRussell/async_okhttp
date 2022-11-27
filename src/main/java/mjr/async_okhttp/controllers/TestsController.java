package mjr.async_okhttp.controllers;

import mjr.async_okhttp.services.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestsController extends RestControllerBase
{
    @Autowired
    TestService testService;

    @GetMapping("/go")
    public ResponseEntity go() throws Exception {
        return processServiceResult(testService.doTest());
    }
}
