package pe.edu.cibertec.pedidoservicegrupo09.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

// @RefreshScope: al llamar POST /actuator/refresh se vuelven a leer los @Value desde el Config Server
@RefreshScope
@RequestMapping("/api/v1/pedidos")
@RestController
public class ConfiguracionController {

    @Value("${ecom.impuesto-igv:sin-config-server}")
    private String impuestoIgv;

    @Value("${ecom.monto-minimo-envio-gratis:sin-config-server}")
    private String montoMinimoEnvioGratis;

    @Value("${ecom.mensaje-bienvenida:sin-config-server}")
    private String mensajeBienvenida;

    @Value("${spring.datasource.url:sin-config-server}")
    private String datasourceUrl;

    @GetMapping("/configuracion")
    public ResponseEntity<Map<String, String>> obtenerConfiguracion() {
        Map<String, String> configuracion = new LinkedHashMap<>();
        configuracion.put("impuesto-igv", impuestoIgv);
        configuracion.put("monto-minimo-envio-gratis", montoMinimoEnvioGratis);
        configuracion.put("mensaje-bienvenida", mensajeBienvenida);
        configuracion.put("datasource.url", datasourceUrl);
        return ResponseEntity.ok(configuracion);
    }
}
