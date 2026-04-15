package com.chernobyl.explorer.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.chernobyl.explorer.entidades.NivelPeligro;

@Repository
public interface NivelPeligroRepository extends JpaRepository<NivelPeligro, Integer>{
	
}
