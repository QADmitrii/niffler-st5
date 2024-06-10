package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.data.entity.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.jupiter.annotation.GenerateSpend;
import guru.qa.niffler.model.SpendJson;

import java.util.Date;

public class SpendJdbcExtension extends AbstractSpendExtension {
    private final SpendRepository spendRepository = SpendRepository.getInstance();

    @Override
    protected SpendJson createSpend(GenerateSpend spend) {
        SpendEntity spendEntity = new SpendEntity();
        spendEntity.setSpendDate(new Date());
        spendEntity.getCategory().getClass();
        spendEntity.setCurrency(spend.currency());
        spendEntity.setAmount(spend.amount());
        spendEntity.setDescription(spend.description());
        spendEntity.setUsername(spend.username());

        return SpendJson.fromEntity(spendRepository.createSpend(spendEntity));
    }

    @Override
    protected void removeSpend(SpendJson spend) {
        spendRepository.removeSpend(spend);

    }
}
