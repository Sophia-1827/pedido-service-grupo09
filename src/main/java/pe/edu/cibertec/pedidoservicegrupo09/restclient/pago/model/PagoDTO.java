package pe.edu.cibertec.pedidoservicegrupo09.restclient.pago.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Copia del PagoDTO de pago-service-grupo09: debe tener los mismos campos para que Feign lo pueda leer
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagoDTO {
    private Long idPago;
    private Long idPedido;
    private String metodoPago;
    private Double montoPagado;
    private LocalDateTime fechaPago;
    private String codigoAutorizacion;
    private String estadoPago;
}
