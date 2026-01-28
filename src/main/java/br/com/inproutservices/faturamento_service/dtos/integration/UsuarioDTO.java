package br.com.inproutservices.faturamento_service.dtos.integration;
import lombok.Data;
import java.util.Set;

@Data
public class UsuarioDTO {
    private Long id;
    private String nome;
    private String email;
    private String role;
    private Set<Long> segmentosIds;
}