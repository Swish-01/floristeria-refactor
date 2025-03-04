package com.floristeria.blomst.controller.health;

import com.floristeria.blomst.domain.Response;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.floristeria.blomst.utils.RequestUtils.getResponse;
import static java.util.Collections.emptyMap;
import static org.springframework.http.HttpStatus.OK;

@RequiredArgsConstructor
@RestController
@RequestMapping()
public class HealthCheckController {
    @GetMapping("/")
    public ResponseEntity<Response> checkHealthStatus(HttpServletRequest request) {
        return ResponseEntity.ok().body(getResponse(request, emptyMap(), "Estado de salud correcto", OK)
        );
    }
}
