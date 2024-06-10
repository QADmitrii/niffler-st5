package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.CategoryEntity;
import guru.qa.niffler.data.entity.SpendEntity;
import guru.qa.niffler.model.SpendJson;

import java.util.List;

public interface SpendRepository {
    static SpendRepository getInstance() {
        System.getProperty("repo");
        return new SpendRepositoryJdbc();
    }

    CategoryEntity createCategory(CategoryEntity category);

    CategoryEntity editCategory(CategoryEntity category);

    void removeCategory(CategoryEntity category);

    SpendEntity createSpend(SpendEntity category);

    SpendEntity editSpend(SpendEntity category);

    void removeSpend(SpendJson category);

    List<SpendEntity> findAllByUsername(String username);
}
