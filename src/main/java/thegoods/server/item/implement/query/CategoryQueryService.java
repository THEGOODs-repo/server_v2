package thegoods.server.item.implement.query;

import thegoods.server.item.domain.Category;

public interface CategoryQueryService {
    public Category findCategoryById(Long categoryId);

}
