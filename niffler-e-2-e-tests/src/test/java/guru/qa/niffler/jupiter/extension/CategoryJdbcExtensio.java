package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.data.entity.CategoryEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.jupiter.annotation.GenerateCategory;
import guru.qa.niffler.model.CategoryJson;

public class CategoryJdbcExtensio extends AbstractCategoryExtension{
    private final SpendRepository spendRepository = SpendRepository.getInstance();

    @Override
    protected CategoryJson createCategory(GenerateCategory category) {
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setCategory(category.category());
        categoryEntity.setUsername(category.username());

        return CategoryJson.fromEntity(spendRepository.createCategory(categoryEntity));
    }

    @Override
    protected void removeCategory(CategoryJson category) {
        spendRepository.removeCategory(CategoryEntity.fromJson(category));
    }
}
