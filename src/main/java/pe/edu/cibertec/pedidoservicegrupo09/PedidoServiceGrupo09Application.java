package pe.edu.cibertec.pedidoservicegrupo09;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class PedidoServiceGrupo09Application {

    public static void main(String[] args) {
        SpringApplication.run(PedidoServiceGrupo09Application.class, args);
    }

}
