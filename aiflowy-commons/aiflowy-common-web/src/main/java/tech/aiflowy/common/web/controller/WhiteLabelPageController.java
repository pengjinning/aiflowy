package tech.aiflowy.common.web.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.aiflowy.common.domain.Result;

import javax.servlet.http.HttpServletRequest;

/**
 * @ignore
 */
@RestController
@RequestMapping
public class WhiteLabelPageController implements ErrorController {

    /**
     * 解决 Whitelabel Error Page
     */
    @RequestMapping("/error")
    public Result<Void> error(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        HttpStatus httpStatus = HttpStatus.valueOf(statusCode);
        return Result.fail(httpStatus.value(), httpStatus.getReasonPhrase());
    }
}
