package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.CategoryEntity;
import guru.qa.niffler.data.entity.SpendEntity;

public interface SpendRepository {
    static SpendRepository getInstance() {
        if ("jdbc".equals(System.getProperty("repo"))) {
            return new SpendRepositoryJdbc();
        }
        return new SpendRepositoryJdbc();
    }

    CategoryEntity createCategory(CategoryEntity category);

    CategoryEntity editCategory(CategoryEntity category);

    void removeCategory(CategoryEntity category);

    SpendEntity createSpend(SpendEntity category);

    SpendEntity editSpend(SpendEntity category);

    void removeSpend(SpendEntity category);
}
