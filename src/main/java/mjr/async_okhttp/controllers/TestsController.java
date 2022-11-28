package mjr.async_okhttp.controllers;

import mjr.async_okhttp.services.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestsController extends RestControllerBase
{
    @Autowired
    TestService testService;

    @GetMapping("/go")
    public ResponseEntity go() {
        return processServiceResult(testService.doTest());
    }

    @PostMapping("/post")
    public String post() {
        String token;

        try {
            token = testService.doPost();
        } catch (Exception ex) {
            return "Error: " + ex;
        }

        if (token != null) {
            return token;
        } else {
            return "Bad login through form";
        }
    }
}
