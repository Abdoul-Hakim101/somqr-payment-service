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
import so.somqr.payment.dto.DecodeP2pQrRequest;
import so.somqr.payment.dto.P2PQrResponse;
import so.somqr.payment.service.P2pQrDecoder;
import so.somqr.payment.util.RequestUtils;
import so.somqr.payment.util.Response;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/somqr/p2p")
@RequiredArgsConstructor
public class P2PQrController {

    private final P2pQrDecoder p2pQrDecoder;

    @PostMapping("/decode")
    public ResponseEntity<Response> decode(@Valid @RequestBody DecodeP2pQrRequest request, HttpServletRequest httpRequest) {
        var payload = p2pQrDecoder.decode(request.payload());
        Response response = RequestUtils.getResponse(
                httpRequest,
                Map.of("payload", P2PQrResponse.from(payload)),
                "P2P QR code decoded successfully",
                HttpStatus.OK
        );
        return ResponseEntity.ok(response);
    }

}
