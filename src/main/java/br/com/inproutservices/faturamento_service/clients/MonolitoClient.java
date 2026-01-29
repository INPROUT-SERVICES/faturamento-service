package br.com.inproutservices.faturamento_service.clients;

import br.com.inproutservices.faturamento_service.dtos.integration.ItemCandidatoDTO;
import br.com.inproutservices.faturamento_service.dtos.integration.OsLpuDetalheDTO;
import br.com.inproutservices.faturamento_service.dtos.integration.UsuarioDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@FeignClient(name = "monolito-client", url = "${APP_MONOLITH_URL}")
public interface MonolitoClient {

    @GetMapping("/api/integracao/faturamento/usuario/{id}")
    UsuarioDTO getUsuario(@PathVariable("id") Long id);

    @GetMapping("/api/integracao/faturamento/detalhe/{id}")
    OsLpuDetalheDTO getDetalhe(@PathVariable("id") Long id);

    @GetMapping("/api/integracao/faturamento/candidatos")
    List<ItemCandidatoDTO> getItensCandidatos(@RequestParam("usuarioId") Long usuarioId);

    @GetMapping("/api/integracao/faturamento/candidatos-adiantamento")
    List<ItemCandidatoDTO> getItensCandidatosAdiantamento(@RequestParam("usuarioId") Long usuarioId);

    @PutMapping("/api/integracao/faturamento/atualizar-lancamento/{osLpuDetalheId}")
    void atualizarStatusLancamento(@PathVariable("osLpuDetalheId") Long id, @RequestParam("status") String status);
}