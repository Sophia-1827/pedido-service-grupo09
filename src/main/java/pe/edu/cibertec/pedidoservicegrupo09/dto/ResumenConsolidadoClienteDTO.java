package pe.edu.cibertec.pedidoservicegrupo09.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.edu.cibertec.pedidoservicegrupo09.restclient.pago.model.PagoDTO;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumenConsolidadoClienteDTO {
    private Long clienteId;
    private String nombreCliente;
    private Integer totalPedidosRealizados;
    private Double montoTotalAcumulado;
    private List<PedidoResponseDTO> historialPedidos;
    private List<PagoDTO> historialPagos;
    private String estadoCuenta;
}
