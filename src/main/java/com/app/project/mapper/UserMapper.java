package com.app.project.mapper;

public interface UserMapper<R, M, D> {
    M toModel(R requestDto);

    D toDto(M model);
}
