package com.imchobo.sayren_back.domain.term.mapper;

import com.imchobo.sayren_back.domain.term.dto.TermResponseDTO;
import com.imchobo.sayren_back.domain.term.entity.Term;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TermMapper {

  TermResponseDTO toResponseDTO(Term term);
}
