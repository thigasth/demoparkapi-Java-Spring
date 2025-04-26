package com.thiago.demo_park_api.repository;

import com.thiago.demo_park_api.entity.Vaga;
import com.thiago.demo_park_api.service.VagaService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VagaRepository extends JpaRepository<Vaga, Long> {

    Optional<Vaga> findByCodigo(String codigo);

    Optional<Vaga> findFirstByStatus(Vaga.StatusVaga statusVaga);
}
