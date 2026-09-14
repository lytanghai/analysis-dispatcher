package com.finance.dispatch.worker.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PageResponse<T> {

    private List<T> content;

    private long totalElements;

    private int totalPages;

    private int size;

    private int numberOfElements;
}