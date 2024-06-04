package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.DataBase;
import guru.qa.niffler.data.entity.CategoryEntity;
import guru.qa.niffler.data.entity.SpendEntity;
import guru.qa.niffler.data.jdbc.DataSourceProvider;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpendRepositoryJdbc implements SpendRepository {

    private static final DataSource spendDataSource = DataSourceProvider.dataSource(DataBase.SPEND);

    @Override
    public CategoryEntity createCategory(CategoryEntity category) {
        try (Connection connection = spendDataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "INSERT INTO category(category,username) VALUES (?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS
             )) {
            ps.setString(1, category.getCategory());
            ps.setString(2, category.getUsername());
            ps.executeUpdate();

            UUID generatedId;
            try (ResultSet resultSet = ps.getGeneratedKeys()) {
                if (resultSet.next()) {
                    generatedId = UUID.fromString(resultSet.getString("id"));
                } else throw new IllegalStateException("Can't access to id");
            }
            category.setId(generatedId);
            return category;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CategoryEntity editCategory(CategoryEntity category) {
        try (Connection connection = spendDataSource.getConnection();
             PreparedStatement us = connection.prepareStatement(
                     "UPDATE category SET category=?, username=? WHERE id=?");
             PreparedStatement ss = connection.prepareStatement(
                     "SELECT * FROM category WHERE id=?")) {

            us.setString(1, category.getCategory());
            us.setString(2, category.getUsername());
            us.setObject(3, category.getId());

            int rowsAffected = us.executeUpdate();
            if (rowsAffected > 0) {
                ss.setObject(1, category.getId());
                try (ResultSet resultSet = ss.executeQuery()) {
                    if (resultSet.next()) {
                        category.setId(UUID.fromString(resultSet.getString("id")));
                        category.setCategory(resultSet.getString("category"));
                        category.setUsername(resultSet.getString("username"));
                    }
                }
            }
            return category;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public CategoryEntity getCategoryByUsernameAndCategory(String categoryName, String username) {
        CategoryEntity category = new CategoryEntity();
        try (Connection connection = spendDataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM category WHERE category = ? AND username = ?"
             )) {
            ps.setString(1, categoryName);
            ps.setString(2, username);
            ps.executeQuery();

            try (ResultSet resultSet = ps.getResultSet()) {
                if (resultSet.next()) {
                    category.setId(UUID.fromString(resultSet.getString("id")));
                    category.setCategory(resultSet.getString("category"));
                    category.setUsername(resultSet.getString("username"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return category;
    }

    public CategoryEntity getCategoryById(UUID id) {
        CategoryEntity category = new CategoryEntity();
        try (Connection connection = spendDataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM category WHERE id = ?"
             )) {
            ps.setObject(1, id);
            ps.executeQuery();
            try (ResultSet resultSet = ps.getResultSet()) {
                if (resultSet.next()) {
                    category.setId(UUID.fromString(resultSet.getString("id")));
                    category.setCategory(resultSet.getString("category"));
                    category.setUsername(resultSet.getString("username"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return category;
    }

    @Override
    public void removeCategory(CategoryEntity category) {
        try (Connection connection = spendDataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "DELETE FROM category WHERE id = ?")) {
            ps.setObject(1, category.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public SpendEntity createSpend(SpendEntity spend) {
        try (Connection connection = spendDataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "INSERT INTO spend(username,spend_date,currency,amount,description,category) VALUES (?, ?, ?, ?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS
             )) {
            ps.setString(1, spend.getUsername());
            ps.setObject(2, new java.sql.Date(spend.getSpendDate().getTime()));
            ps.setObject(3, spend.getCurrency().name());
            ps.setDouble(4, spend.getAmount());
            ps.setString(5, spend.getDescription());
            ps.setObject(6, spend.getCategory());
            ps.executeUpdate();
            UUID generatedId;
            try (ResultSet resultSet = ps.getGeneratedKeys()) {
                if (resultSet.next()) {
                    generatedId = UUID.fromString(resultSet.getString("id"));
                } else throw new IllegalStateException("Can't access to id");
            }
            spend.setId(generatedId);
            return spend;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SpendEntity editSpend(SpendEntity spend) {
        try (Connection connection = spendDataSource.getConnection();
             PreparedStatement us = connection.prepareStatement(
                     "UPDATE spend SET username=?, spend_date=?, currency=?, amount=?, description=?, category_id=? WHERE id=?")) {
            us.setString(1, spend.getUsername());
            us.setObject(2, new java.sql.Date(spend.getSpendDate().getTime()));
            us.setObject(3, spend.getCurrency().name());
            us.setDouble(4, spend.getAmount());
            us.setString(5, spend.getDescription());
            us.setObject(6, spend.getCategory());
            us.setObject(7, spend.getId());
            int rowsAffected = us.executeUpdate();
            if (rowsAffected > 0) {
                return getSpendById(spend.getId());
            } else {
                throw new RuntimeException("Failed to update SpendEntity with id: " + spend.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeSpend(SpendJson spend) {
        try (Connection connection = spendDataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "DELETE FROM spend WHERE id = ?")) {
            ps.setObject(1, spend.getClass());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public SpendEntity getSpendById(UUID id) {
        try (Connection connection = spendDataSource.getConnection();
             PreparedStatement ss = connection.prepareStatement(
                     "SELECT * FROM spend WHERE id = ?")) {
            ss.setObject(1, id);
            try (ResultSet resultSet = ss.executeQuery()) {
                if (resultSet.next()) {
                    return mapResultSetToSpendEntity(resultSet);
                } else {
                    throw new SQLException("No SpendEntity found with id: " + id);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private SpendEntity mapResultSetToSpendEntity(ResultSet resultSet) throws SQLException {
        SpendEntity spend = new SpendEntity();
        spend.setId(UUID.fromString(resultSet.getString("id")));
        spend.setUsername(resultSet.getString("username"));
        spend.setSpendDate(resultSet.getDate("spend_date"));
        spend.setCurrency(CurrencyValues.valueOf(resultSet.getString("currency")));
        spend.setAmount(resultSet.getDouble("amount"));
        spend.setDescription(resultSet.getString("description"));
        spend.setCategory(UUID.fromString(resultSet.getString("category_id")));
        return spend;
    }

    @Override
    public List<SpendEntity> findAllByUsername(String username) {
        List<SpendEntity> spends = new ArrayList<>();
        try (Connection connection = spendDataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(
                     "SELECT * FROM spend WHERE username = ?")) {
            ps.setString(1, username);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next()) {
                SpendEntity spend = new SpendEntity();
                spend.setId(UUID.fromString(resultSet.getString("id")));
                spend.setUsername(resultSet.getString("username"));
                spend.setSpendDate(resultSet.getDate("spend_date"));
                spend.setCurrency(CurrencyValues.valueOf(resultSet.getString("currency")));
                spend.setAmount(resultSet.getDouble("amount"));
                spend.setDescription(resultSet.getString("description"));
                spend.setCategory(UUID.fromString(resultSet.getString("category_id")));
                spends.add(spend);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return spends;
    }


}

