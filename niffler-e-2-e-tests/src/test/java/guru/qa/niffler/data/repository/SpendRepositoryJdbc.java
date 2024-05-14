package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.DataBase;
import guru.qa.niffler.data.entity.CategoryEntity;
import guru.qa.niffler.data.entity.SpendEntity;
import guru.qa.niffler.data.jdbc.DataSourceProvider;
import guru.qa.niffler.model.CurrencyValues;

import javax.sql.DataSource;
import java.sql.*;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SpendRepositoryJdbc implements SpendRepository {
    private static final DataSource dataSource = DataSourceProvider.dataSource(DataBase.SPEND);

    @Override
    public CategoryEntity createCategory(CategoryEntity category) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement PreparedStatement = conn.prepareStatement("INSERT INTO \"category\" (category, username) VALUES (?, ?)",
                     RETURN_GENERATED_KEYS)) {
            PreparedStatement.setString(1, category.getCategory());
            PreparedStatement.setString(2, category.getUsername());
            PreparedStatement.executeUpdate();

            UUID generatedId;
            try (ResultSet resultSet = PreparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    generatedId = UUID.fromString(resultSet.getString("id"));
                } else {
                    throw new IllegalStateException("Can`t access to id");
                }
            }
            category.setId(generatedId);
            return category;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CategoryEntity editCategory(CategoryEntity category) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(
                     "UPDATE \"category\" SET category = ?, username = ? WHERE category = ?"
             )) {
            preparedStatement.setString(1, category.getCategory());
            preparedStatement.setString(2, category.getUsername());
            preparedStatement.setObject(3, category.getCategory());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return category;
    }

    @Override
    public void removeCategory(CategoryEntity category) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(
                     "DELETE FROM \"category\" WHERE id = ?"
             )) {
            preparedStatement.setObject(1, category.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SpendEntity createSpend(SpendEntity spend) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("INSERT INTO public.spend(username, spend_date, currency, amount, description, category_id)" +
                                     " VALUES (?, ?, ?, ?, ?, ?);",
                             PreparedStatement.RETURN_GENERATED_KEYS
                     )) {

            preparedStatement.setString(1, spend.getUsername());
            preparedStatement.setDate(2, (java.sql.Date) spend.getSpendDate());
            preparedStatement.setString(3, spend.getCurrency().toString());
            preparedStatement.setDouble(4, spend.getAmount());
            preparedStatement.setString(5, spend.getDescription());
            preparedStatement.setObject(6, spend.getCategory());
            preparedStatement.executeUpdate();

            UUID uuid = null;
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    uuid = UUID.fromString(resultSet.getString("id"));
                } else {
                    throw new IllegalArgumentException("Не удалось получить данные по трате");
                }
            }
            spend.setId(uuid);
            return spend;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SpendEntity editSpend(SpendEntity spend) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement("UPDATE \"spend\" SET username = ?, currency = ?, " +
                     "spend_date = ?, amount = ?, description = ?, category_id = ? WHERE id = ?")) {
            ps.setString(1, spend.getUsername());
            ps.setString(2, String.valueOf(spend.getCurrency()));
            ps.setDate(3, new Date(System.currentTimeMillis()));
            ps.setDouble(4, spend.getAmount());
            ps.setString(5, spend.getDescription());
            ps.setObject(6, spend.getCategory().getId());
            ps.setObject(7, spend.getId());
            ps.executeUpdate();
            return spend;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeSpend(SpendEntity spend) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("DELETE FROM public.spend where id =?"
                     )) {

            preparedStatement.setObject(1, spend.getId());
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

