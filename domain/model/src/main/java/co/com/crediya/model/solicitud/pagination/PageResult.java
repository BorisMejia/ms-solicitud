package co.com.crediya.model.solicitud.pagination;

import java.util.List;

public record PageResult<T>(
        List<T> items,
        int page,
        int size,
        long totalItems,
        int totalPages,
        boolean hasNext,
        boolean hasPrev
) {
    public static <T> PageResult<T> of(List<T> items, int page, int size, long totalItems) {
        int pages = (int) Math.max(1, Math.ceil(totalItems / (double) size));
        boolean hasNext = (page + 1) < pages;
        boolean hasPrev = page > 0;
        return new PageResult<>(items, page, size, totalItems, pages, hasNext, hasPrev);
    }
}
