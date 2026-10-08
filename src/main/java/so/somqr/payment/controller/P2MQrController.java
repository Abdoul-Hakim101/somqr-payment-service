package so.somqr.payment.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import so.somqr.payment.dto.Request.DecodeP2MQrRequest;
import so.somqr.payment.dto.Response.P2MQrResponse;
import so.somqr.payment.service.P2MQrDecoder;
import so.somqr.payment.util.RequestUtils;
import so.somqr.payment.util.Response;

import java.util.Map;

@RestController
@RequestMapping("/api/v2/somqr/p2m")
@RequiredArgsConstructor
public class P2MQrController {

    private final P2MQrDecoder p2mQrDecoder;

    @PostMapping("/decode")
    public ResponseEntity<Response> decode(
            @Valid @RequestBody DecodeP2MQrRequest request,
            HttpServletRequest httpRequest
    ) {
        var payload = p2mQrDecoder.decode(request.payload());
        Response response = RequestUtils.getResponse(
                httpRequest,
                Map.of("payload", P2MQrResponse.from(payload)),
                "P2M QR code decoded successfully",
                HttpStatus.OK
        );
        return ResponseEntity.ok(response);
    }
}
