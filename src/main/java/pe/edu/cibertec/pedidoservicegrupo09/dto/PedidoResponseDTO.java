package pe.edu.cibertec.pedidoservicegrupo09.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoResponseDTO {
    private Long idPedido;
    private String codigoSeguimiento;
    private LocalDateTime fechaEmision;
    private Long clienteId;
    private String estadoPedido;
    private List<DetallePedidoDTO> listaDetalles;
    private Double montoTotalCalculado;
}
