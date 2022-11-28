package ru.job4j.dreamjob.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.City;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class CandidateDBStore {

    private final BasicDataSource pool;
    private static final Logger LOG = LoggerFactory.getLogger(PostDBStore.class.getName());
    private static final String SELECT_ALL = "SELECT * FROM candidate";
    private static final String SELECT_ID = "SELECT * FROM candidate WHERE id = ?";
    private static final String INSERT_CANDIDATE = "INSERT INTO candidate(name, city_id, description, created, photo) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_CANDIDATE = "UPDATE candidate SET name = ?, description = ?, photo = ?, city_id = ? where id = ?";

    public CandidateDBStore(BasicDataSource pool) {
        this.pool = pool;
    }

    private Candidate createCandidate(ResultSet it) throws SQLException {
        return new Candidate(it.getInt("id"), it.getString("name"),
                it.getString("description"),
                it.getTimestamp("created").toLocalDateTime().toLocalDate(),
                new City(it.getInt("city_id")),
                it.getBytes("photo"));
    }

    public List<Candidate> findAll() {
        List<Candidate> candidates = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(SELECT_ALL)
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    candidates.add(createCandidate(it));
                }
            }
        } catch (Exception e) {
            LOG.error("Error!", e);
        }
        return candidates;
    }


    public Candidate add(Candidate candidate) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(INSERT_CANDIDATE,
                     PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, candidate.getName());
            ps.setInt(2, candidate.getCity().getId());
            ps.setString(3, candidate.getDescription());
            ps.setTimestamp(4, Timestamp.valueOf(candidate.getCreated().atStartOfDay()));
            ps.setBytes(5, candidate.getPhoto());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    candidate.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            LOG.error("Error!", e);
        }
        return candidate;
    }

    public boolean replace(Candidate candidate) {
        boolean rsl = false;
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement(UPDATE_CANDIDATE)) {
            ps.setString(1, candidate.getName());
            ps.setString(2, candidate.getDescription());
            ps.setBytes(3, candidate.getPhoto());
            ps.setInt(4, candidate.getCity().getId());
            ps.setInt(5, candidate.getId());
            int affected =  ps.executeUpdate();
            rsl = affected > 0;
        } catch (Exception e) {
            LOG.error("Error!", e);
        }
        return rsl;
    }

    public Optional<Candidate> findById(int id) {
        Optional<Candidate> rsl = Optional.empty();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement(SELECT_ID)
        ) {
            ps.setInt(1, id);
            try (ResultSet it = ps.executeQuery()) {
                if (it.next()) {
                    rsl = Optional.of(createCandidate(it));
                }
            }
        } catch (Exception e) {
            LOG.error("Error!", e);
        }
        return rsl;
    }

    public BasicDataSource getPool() {
        return pool;
    }
}