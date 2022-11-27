package mjr.async_okhttp.controllers;

import lombok.extern.slf4j.Slf4j;
import mjr.async_okhttp.http.HttpResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public abstract class RestControllerBase
{
    protected <T> ResponseEntity<T> processServiceResult(HttpResponse<T> result) {
        try {
            return reallyProcessServiceResult(result);
        } catch (Exception ex) {
            log.error("Error processing response", ex);
            return new ResponseEntity<>(HttpStatus.valueOf(500));
        }
    }

    private <T> ResponseEntity<T> reallyProcessServiceResult(HttpResponse<T> result) {
        var content = result.getContent();
        var headers = new HttpHeaders();
        ResponseEntity response = null;

        headers.add("Content-Type", "application/json");

        if (result.getStatusCode() >= 400) {
            Object errorContent = null;

            if (result.getError() != null) {
                errorContent = result.getError().getMessage();

                response = new ResponseEntity(errorContent, HttpStatus.valueOf(result.getStatusCode()));
            } else if (content != null) {
                if (isStringLike(content)) {
                    headers.add("Content-Type", "text/html; charset=UTF-8");
                }

                // Unlikely if there's an error status, but check for content
                response = new ResponseEntity(content, headers, HttpStatus.valueOf(result.getStatusCode()));
            }

            return response;
        }

        if (result.getHeaders() != null) {
            for (var entry : result.getHeaders().entrySet()) {
                headers.set(entry.getKey(), entry.getValue());
            }
        }

        if (content != null) {
            // We should probably do this for primitives and primitive wrapper classes as well
            if (isStringLike(content)) {
                headers.set("Content-Type", "text/html; charset=UTF-8");
            } else if (content instanceof byte[]) {
                headers.set("Content-Type", "application/octet-stream");
            }

            response = new ResponseEntity(content, headers, HttpStatus.valueOf(result.getStatusCode()));
        } else if (result.getStatusCode() == 200) {
            // If response is "Ok" and there's no content, return 204 (No content)
            response = new ResponseEntity(HttpStatus.NO_CONTENT);
        }

        return response;
    }

    private boolean isStringLike(Object object)
    {
        return object.getClass() == String.class;
    }
}

