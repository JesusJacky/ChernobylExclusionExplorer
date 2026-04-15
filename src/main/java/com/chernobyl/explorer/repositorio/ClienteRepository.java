package com.chernobyl.explorer.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chernobyl.explorer.entidades.Cliente;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer>{

}
