package com.thiago.demo_park_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.thiago.demo_park_api.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {



}
