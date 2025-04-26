package com.thiago.demo_park_api;


import com.thiago.demo_park_api.web.dto.EstacionamentoCreateDto;
import com.thiago.demo_park_api.web.dto.PageableDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/estacionamentos/estacionamentos-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/estacionamentos/estacionamentos-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class EstacionamentoIT {

    @Autowired
    WebTestClient testClient;

    @Test
    public void CriarCheckIn_ComDadosValidos_RetornarCreatedAndLocation() {
        EstacionamentoCreateDto createDto = EstacionamentoCreateDto.builder()
                .placa("WER-1111").marca("FIAT").modelo("PALIO 1.0")
                .cor("AZUL").clienteCpf("81170071007")
                .build();

        testClient.post().uri("/api/v1/estacionamentos/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@gmail.com", "123456"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectBody()
                .jsonPath("placa").isEqualTo("WER-1111")
                .jsonPath("marca").isEqualTo("FIAT")
                .jsonPath("modelo").isEqualTo("PALIO 1.0")
                .jsonPath("cor").isEqualTo("AZUL")
                .jsonPath("clienteCpf").isEqualTo("81170071007")
                .jsonPath("recibo").exists()
                .jsonPath("dataEntrada").exists()
                .jsonPath("vagaCodigo").exists();

    }

    @Test
    public void CriarCheckIn_ComRoleCliente_RetornarErrorComStatus403() {
        EstacionamentoCreateDto createDto = EstacionamentoCreateDto.builder()
                .placa("WER-1111").marca("FIAT").modelo("PALIO 1.0")
                .cor("AZUL").clienteCpf("81170071007")
                .build();

        testClient.post().uri("/api/v1/estacionamentos/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bia@gmail.com", "123456"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("status").isEqualTo("403")
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos/check-in")
                .jsonPath("method").isEqualTo("POST");

    }

    @Test
    public void CriarCheckIn_ComDadosInvalidos_RetornarErrorComStatus422() {
        EstacionamentoCreateDto createDto = EstacionamentoCreateDto.builder()
                .placa("").marca("").modelo("")
                .cor("").clienteCpf("")
                .build();

        testClient.post().uri("/api/v1/estacionamentos/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bia@gmail.com", "123456"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody()
                .jsonPath("status").isEqualTo("422")
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos/check-in")
                .jsonPath("method").isEqualTo("POST");

    }

    @Test
    public void CriarCheckIn_ComCpfInexistente_RetornarErrorComStatus404() {
        EstacionamentoCreateDto createDto = EstacionamentoCreateDto.builder()
                .placa("WER-1111").marca("FIAT").modelo("PALIO 1.0")
                .cor("AZUL").clienteCpf("51105819086")
                .build();

        testClient.post().uri("/api/v1/estacionamentos/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@gmail.com", "123456"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo("404")
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos/check-in")
                .jsonPath("method").isEqualTo("POST");

    }

    @Sql(scripts = "/estacionamentos/estacionamento-insert-vagas-ocupadas.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "/estacionamentos/estacionamento-delete-vagas-ocupadas.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    public void CriarCheckIn_ComVagasOcupadas_RetornarErrorComStatus404() {
        EstacionamentoCreateDto createDto = EstacionamentoCreateDto.builder()
                .placa("WER-1111").marca("FIAT").modelo("PALIO 1.0")
                .cor("AZUL").clienteCpf("81170071007")
                .build();

        testClient.post().uri("/api/v1/estacionamentos/check-in")
                .contentType(MediaType.APPLICATION_JSON)
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@gmail.com", "123456"))
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo("404")
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos/check-in")
                .jsonPath("method").isEqualTo("POST");

    }



    @Test
    public void buscarCheckIn_ComPerfilAdmin_RetornarDadosComStatus200() {
        testClient.get()
                .uri("/api/v1/estacionamentos/check-in/{recibo}", "20250313-101300")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@gmail.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("placa").isEqualTo("FIT-1020")
                .jsonPath("marca").isEqualTo("FIAT")
                .jsonPath("modelo").isEqualTo("PALIO")
                .jsonPath("cor").isEqualTo("VERDE")
                .jsonPath("clienteCpf").isEqualTo("62352379016")
                .jsonPath("recibo").isEqualTo("20250313-101300")
                .jsonPath("dataEntrada").isEqualTo("2025-03-13 10:15:00")
                .jsonPath("vagaCodigo").isEqualTo("A-01");


    }

    @Test
    public void buscarCheckIn_ComPerfilCliente_RetornarDadosComStatus200() {
        testClient.get()
                .uri("/api/v1/estacionamentos/check-in/{recibo}", "20250313-101300")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@gmail.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("placa").isEqualTo("FIT-1020")
                .jsonPath("marca").isEqualTo("FIAT")
                .jsonPath("modelo").isEqualTo("PALIO")
                .jsonPath("cor").isEqualTo("VERDE")
                .jsonPath("clienteCpf").isEqualTo("62352379016")
                .jsonPath("recibo").isEqualTo("20250313-101300")
                .jsonPath("dataEntrada").isEqualTo("2025-03-13 10:15:00")
                .jsonPath("vagaCodigo").isEqualTo("A-01");


    }

    @Test
    public void buscarCheckIn_ComChekInInexistente_RetornarErrorComStatus404() {
        testClient.get()
                .uri("/api/v1/estacionamentos/check-in/{recibo}", "20250313-999999")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@gmail.com", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo("404")
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos/check-in/20250313-999999")
                .jsonPath("method").isEqualTo("GET");


    }

    @Test
    public void criarCheckOut_ComReciboExistente_RetornarSucesso() {
        testClient.put()
                .uri("/api/v1/estacionamentos/check-out/{recibo}", "20250313-101300")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@gmail.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("placa").isEqualTo("FIT-1020")
                .jsonPath("marca").isEqualTo("FIAT")
                .jsonPath("modelo").isEqualTo("PALIO")
                .jsonPath("cor").isEqualTo("VERDE")
                .jsonPath("dataEntrada").isEqualTo("2025-03-13 10:15:00")
                .jsonPath("recibo").isEqualTo("20250313-101300")
                .jsonPath("clienteCpf").isEqualTo("62352379016")
                .jsonPath("vagaCodigo").isEqualTo("A-01")
                .jsonPath("dataEntrada").exists()
                .jsonPath("valor").exists()
                .jsonPath("desconto").exists();

    }

    @Test
    public void criarCheckOut_ComReciboInexistente_RetornarErrorStatus404() {
        testClient.put()
                .uri("/api/v1/estacionamentos/check-out/{recibo}", "20250313-000000")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@gmail.com", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("status").isEqualTo("404")
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos/check-out/20250313-000000")
                .jsonPath("method").isEqualTo("PUT");

    }

    @Test
    public void criarCheckOut_ComRoleCliente_RetornarErrorStatus403() {
        testClient.put()
                .uri("/api/v1/estacionamentos/check-out/{recibo}", "20250313-101300")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bia@gmail.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("status").isEqualTo("403")
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos/check-out/20250313-101300")
                .jsonPath("method").isEqualTo("PUT");

    }

    @Test
    public void BuscarEstacionamentos_PorClienteCpf_RetornarSucesso() {
        PageableDto responseBody = testClient.get()
                .uri("/api/v1/estacionamentos/cpf/{cpf}?size=1&page=0", "62352379016")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@gmail.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getContent().size()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(responseBody.getTotalPages()).isEqualTo(2);
        org.assertj.core.api.Assertions.assertThat(responseBody.getNumber()).isEqualTo(0);
        org.assertj.core.api.Assertions.assertThat(responseBody.getSize()).isEqualTo(1);

        responseBody = testClient.get()
                .uri("/api/v1/estacionamentos/cpf/{cpf}?size=1&page=1", "62352379016")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@gmail.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getContent().size()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(responseBody.getTotalPages()).isEqualTo(2);
        org.assertj.core.api.Assertions.assertThat(responseBody.getNumber()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(responseBody.getSize()).isEqualTo(1);

    }

    @Test
    public void buscarEstacionamentos_ComClienteCpfPorPerfilCliente_RetornarErrorStatus403() {
        testClient.get()
                .uri("/api/v1/estacionamentos/cpf/{cpf}", "62352379016")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bia@gmail.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("status").isEqualTo("403")
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos/cpf/62352379016")
                .jsonPath("method").isEqualTo("GET");

    }

    @Test
    public void BuscarEstacionamentos_DoClienteLogado_RetornarSucesso() {
        PageableDto responseBody = testClient.get()
                .uri("/api/v1/estacionamentos?size=1&page=0")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@gmail.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getContent().size()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(responseBody.getTotalPages()).isEqualTo(2);
        org.assertj.core.api.Assertions.assertThat(responseBody.getNumber()).isEqualTo(0);
        org.assertj.core.api.Assertions.assertThat(responseBody.getSize()).isEqualTo(1);

        responseBody = testClient.get()
                .uri("/api/v1/estacionamentos?size=1&page=1")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bob@gmail.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PageableDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responseBody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responseBody.getContent().size()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(responseBody.getTotalPages()).isEqualTo(2);
        org.assertj.core.api.Assertions.assertThat(responseBody.getNumber()).isEqualTo(1);
        org.assertj.core.api.Assertions.assertThat(responseBody.getSize()).isEqualTo(1);

    }

    @Test
    public void buscarEstacionamentos_doClienteLogadoPorPerfilCliente_RetornarErrorStatus403() {
        testClient.get()
                .uri("/api/v1/estacionamentos")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@gmail.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("status").isEqualTo("403")
                .jsonPath("path").isEqualTo("/api/v1/estacionamentos")
                .jsonPath("method").isEqualTo("GET");

    }



}
