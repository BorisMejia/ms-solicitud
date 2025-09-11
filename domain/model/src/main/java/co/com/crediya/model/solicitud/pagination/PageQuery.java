package co.com.crediya.model.solicitud.pagination;

public record PageQuery(int page, int size, String sortBy, boolean asc) {

    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;
    public static final String DEFAULT_SORT = "id_solicitud";

    public static PageQuery of(Integer page, Integer size, String sortBy, Boolean asc) {
        int p = page == null || page < 0 ? DEFAULT_PAGE : page;
        int s = size == null || size < 1 ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);
        String sb = (sortBy == null || sortBy.isBlank()) ? DEFAULT_SORT : sortBy;
        boolean a = asc != null && asc;
        return new PageQuery(p, s, sb, a);
    }
}
