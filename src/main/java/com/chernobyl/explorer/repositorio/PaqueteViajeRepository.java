package com.chernobyl.explorer.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chernobyl.explorer.entidades.PaqueteViaje;

@Repository
public interface PaqueteViajeRepository extends JpaRepository<PaqueteViaje, Integer>{

}
