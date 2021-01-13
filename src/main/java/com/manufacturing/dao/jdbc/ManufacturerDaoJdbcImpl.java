package com.manufacturing.dao.jdbc;

import com.manufacturing.dao.ManufacturerDao;
import com.manufacturing.lib.Dao;
import com.manufacturing.model.Manufacturer;
import com.manufacturing.util.ConnectionUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Dao
public class ManufacturerDaoJdbcImpl implements ManufacturerDao {
    private static final String MANUFACTURER_NAME = "manufacturer_name";
    private static final String MANUFACTURER_ID = "manufacturer_id";
    private static final String ORIGIN = "origin";

    @Override
    public Manufacturer create(Manufacturer manufacturer) {
        String query = "INSERT INTO manufacturers (manufacturer_name, origin) VALUE (?, ?)";
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query,
                    Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, manufacturer.getName());
            statement.setString(2, manufacturer.getCountry());
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                manufacturer.setId(resultSet.getObject(1, Long.class));
            }
            statement.close();
            resultSet.close();
            return manufacturer;
        } catch (SQLException e) {
            throw new DataProcessingException("This data can't be added to table", e);
        }
    }

    @Override
    public Optional<Manufacturer> get(Long manufacturerId) {
        String query = "SELECT * FROM manufacturers WHERE manufacturer_id = ? AND deleted = false";
        Manufacturer manufacturer = null;
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, manufacturerId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                manufacturer = getManufacturer(resultSet);
            }
            statement.close();
            resultSet.close();
        } catch (SQLException e) {
            throw new DataProcessingException("This data can't be added to table", e);
        }
        return Optional.ofNullable(manufacturer);
    }

    @Override
    public Manufacturer update(Manufacturer manufacturer) {
        String query = "UPDATE manufacturers SET manufacturer_name = ?, origin = ? "
                + " WHERE manufacturer_id = ? AND deleted = false";
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, manufacturer.getName());
            statement.setString(2, manufacturer.getCountry());
            statement.setLong(3, manufacturer.getId());
            statement.executeUpdate();
            statement.close();
            return manufacturer;
        } catch (SQLException e) {
            throw new DataProcessingException("This data can't be added to table", e);
        }
    }

    @Override
    public boolean delete(Long manufacturerId) {
        String query = "UPDATE manufacturers SET deleted = true WHERE manufacturer_id = ?";
        try (Connection connection = ConnectionUtil.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, manufacturerId);
            int deletedLines = statement.executeUpdate();
            statement.close();
            return deletedLines > 0;
        } catch (SQLException e) {
            throw new DataProcessingException("Delete of product with id="
                    + manufacturerId + " is failed", e);
        }
    }

    @Override
    public List<Manufacturer> getAll() {
        String query = "SELECT * FROM manufacturers WHERE deleted = false";
        try (Connection connection = ConnectionUtil.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            List<Manufacturer> manufacturers = new ArrayList<>();
            while (resultSet.next()) {
                manufacturers.add(getManufacturer(resultSet));
            }
            resultSet.close();
            statement.close();
            return manufacturers;
        } catch (SQLException e) {
            throw new DataProcessingException("Something going wrong", e);
        }
    }

    private Manufacturer getManufacturer(ResultSet resultSet) throws SQLException {
        long manufacturerId = resultSet.getObject(MANUFACTURER_ID, Long.class);
        String name = resultSet.getObject(MANUFACTURER_NAME, String.class);
        String country = resultSet.getObject(ORIGIN, String.class);
        Manufacturer manufacturer = new Manufacturer(name, country);
        manufacturer.setId(manufacturerId);
        return manufacturer;
    }
}