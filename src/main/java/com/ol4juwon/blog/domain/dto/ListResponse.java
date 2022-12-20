package com.ol4juwon.blog.domain.dto;

import java.util.List;

public record ListResponse<T>(
    List<T> items
) {
    public ListResponse {

    }
    public ListResponse() {
        this(List.of());
      }
}
