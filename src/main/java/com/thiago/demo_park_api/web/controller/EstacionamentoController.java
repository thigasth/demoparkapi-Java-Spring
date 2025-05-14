package com.thiago.demo_park_api.web.controller;

import com.thiago.demo_park_api.entity.ClienteVaga;
import com.thiago.demo_park_api.jwt.JwtUserDetails;
import com.thiago.demo_park_api.repository.projection.ClienteVagaProjection;
import com.thiago.demo_park_api.service.ClienteService;
import com.thiago.demo_park_api.service.ClienteVagaService;
import com.thiago.demo_park_api.service.EstacionamentoService;
import com.thiago.demo_park_api.service.JasperService;
import com.thiago.demo_park_api.web.dto.*;
import com.thiago.demo_park_api.web.dto.mapper.ClienteVagaMapper;
import com.thiago.demo_park_api.web.dto.mapper.PageableMapper;
import com.thiago.demo_park_api.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;
import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;

@Tag(name = "Estacionamento", description = "Operacões de registro de entrada e saída de um veiculo do estacionamento.")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/estacionamentos")
public class EstacionamentoController {

    private final EstacionamentoService estacionamentoService;
    private final ClienteVagaService clienteVagaService;
    private final ClienteService clienteService;
    private final JasperService jasperService;



    @Operation( summary = "Operacão de check-in", description = "Recurso para dar entrada de um veículo no estacionamento" +
            "Requisicão exige um uso de um Bearer token. Acesso restrito a Role='ADMIN'",
            security =  @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso",
                            headers = @Header(name = HttpHeaders.LOCATION, description = "URL de acesso ao recurso criado"),
                            content = @Content(mediaType = "applicarion/json;charset=UTF-8",
                                    schema = @Schema(implementation = EstacionamentoCreateDto.class))),
                    @ApiResponse(responseCode = "403", description = "Recurso não permitido ao perfil de CLIENTE",
                            content = @Content(mediaType = "applicarion/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "Causa possíveis: <br/>" +
                            "- CPF do cliente não cadastrado no sistema; <br/>" +
                            "- Nenhuma Vaga Localizada;",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Recurso não processado por falta de dados ou dados inválidos",
                            content = @Content(mediaType = "applicarion/json;charset=UTF-8",
                                    schema = @Schema(implementation = ErrorMessage.class)))
            })
    @PostMapping("/check-in")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EstacionamentoResponseDto> checkin(@RequestBody @Valid EstacionamentoCreateDto dto) {
        ClienteVaga clienteVaga = ClienteVagaMapper.toClienteVaga(dto);
        estacionamentoService.checkIn(clienteVaga);
        EstacionamentoResponseDto responseDto = ClienteVagaMapper.toDto(clienteVaga);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequestUri().path("/{recibo}")
                .buildAndExpand(clienteVaga.getRecibo())
                .toUri();

        return ResponseEntity.created(location).body(responseDto);
    }

    @Operation( summary = "Localizar um check-in pelo recibo", description = "Recurso para localizar um check-in pelo recibo" +
            "Requisicão exige um uso de um Bearer token. Acesso restrito a Role='ADMIN'",
            security =  @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Check-in localizado com sucesso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EstacionamentoResponseDto.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Recibo não encontrado",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))
                    )
            })
    @GetMapping("/check-in/{recibo}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENTE')")
    public ResponseEntity<EstacionamentoResponseDto> getByRecibo(@PathVariable String recibo) {
        ClienteVaga clienteVaga = clienteVagaService.BuscarPorRecibo(recibo);
        EstacionamentoResponseDto dto = ClienteVagaMapper.toDto(clienteVaga);
        return ResponseEntity.ok(dto);
    }


    @Operation( summary = "Realizar um check-out", description = "Recurso realizar um check-out de um veículo" +
            "Requisicão exige um uso de um Bearer token. Acesso restrito a Role='ADMIN'",
            security =  @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Check-out realizado com sucesso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = EstacionamentoResponseDto.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Recibo inválido ou inexistente",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))
                    ),
                    @ApiResponse(responseCode = "403", description = "Recurso não não permitido ao perfil de CLIENTE",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            })
    @PutMapping("/check-out/{recibo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EstacionamentoResponseDto> checkout(@PathVariable String recibo) {
        ClienteVaga clienteVaga = estacionamentoService.chechOut(recibo);
        EstacionamentoResponseDto dto = ClienteVagaMapper.toDto(clienteVaga);
        return ResponseEntity.ok(dto);
    }

    @Operation( summary = "Localizar os registros de estacionamento por CPF",
            description = "Localizar os registros de estacionamento de CLIENTE por CPF" +
                    "Requisicão exige um uso de um Bearer token. Acesso restrito a Role='ADMIN'",
            security =  @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = PATH, name = "cpf", description = "Representa a página retornada",
                            required = true
                    ),
                    @Parameter(in = QUERY, name = "page",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "0")),
                            description = "Representa a página retornada"
                    ),
                    @Parameter(in = QUERY, name = "size",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "5")),
                            description = "Representa o total de elementos por página"
                    ),
                    @Parameter(in = QUERY, name = "sort", hidden = true,
                            content = @Content(schema = @Schema(type = "string", defaultValue = "id,asc")),
                            description = "Campo padrão de ordenação 'dataEntrada, asc' ."
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso localizado com sucesso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PageableDto.class))
                    ),
                    @ApiResponse(responseCode = "403", description = "Recurso não não permitido ao perfil de CLIENTE",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class))
                    )
            })

    @GetMapping("/cpf/{cpf}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageableDto> getAllEstacionamentosPorCpf(@PathVariable String cpf,
                                                                   @PageableDefault(size = 5, sort = "dataEntrada",
                                                                   direction =  Sort.Direction.ASC) Pageable pageable) {
        Page<ClienteVagaProjection> projection = clienteVagaService.buscarTodosPorClienteCpf(cpf, pageable);
        PageableDto dto = PageableMapper.toDto(projection);
        return ResponseEntity.ok(dto);

    }

    @Operation( summary = "Localizar os registros de estacionamento do cliente logado",
            description = "Localizar os registros de estacionamento do cliente logado" +
                    "Requisicão exige um uso de um Bearer token. Acesso restrito a Role='CLIENTE'",
            security =  @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = QUERY, name = "page",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "0")),
                            description = "Representa a página retornada"
                    ),
                    @Parameter(in = QUERY, name = "size",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "5")),
                            description = "Representa o total de elementos por página"
                    ),
                    @Parameter(in = QUERY, name = "sort", hidden = true,
                            content = @Content(schema = @Schema(type = "string", defaultValue = "id,asc")),
                            description = "Campo padrão de ordenação 'dataEntrada, asc' ."
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso localizado com sucesso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = EstacionamentoResponseDto.class))
                    ),
                    @ApiResponse(responseCode = "403", description = "Recurso não não permitido ao perfil de CLIENTE",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorMessage.class))
                    )
            })
    @GetMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<PageableDto> getAllEstacionamentosDoCliente(@AuthenticationPrincipal JwtUserDetails user,
                                                                      @PageableDefault(size = 5, sort = "dataEntrada",
                                                                           direction =  Sort.Direction.ASC) Pageable pageable) {
        Page<ClienteVagaProjection> projection = clienteVagaService.buscarTodosPorUsuarioId(user.getId(), pageable);
        PageableDto dto = PageableMapper.toDto(projection);
        return ResponseEntity.ok(dto);

    }

    @GetMapping("/relatorio")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<Void> getRelatorio(HttpServletResponse response, @AuthenticationPrincipal JwtUserDetails user) throws IOException {
        String cpf = clienteService.buscarPorUsuarioId(user.getId()).getCpf();
        jasperService.addParams("CPF", cpf);

        byte[] bytes = jasperService.gerarPdf();

        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader("Content-disposition", "inline; filename=" + System.currentTimeMillis() + ".pdf");
        response.getOutputStream().write(bytes);

        return ResponseEntity.ok().build();
    }

}
