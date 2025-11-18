package com.codingsena.codingsena_backend.dtos;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PagedResponse<T> {
	private List<T> content;
	private Integer pageNumber;
	private Integer pageSize;
	private Long totalElements;
	private Integer totalPages;
	private Boolean isLastPage;
}

