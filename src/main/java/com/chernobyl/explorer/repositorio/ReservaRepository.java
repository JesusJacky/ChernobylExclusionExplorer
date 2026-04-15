package com.chernobyl.explorer.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chernobyl.explorer.entidades.Reserva;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Integer>{

}
