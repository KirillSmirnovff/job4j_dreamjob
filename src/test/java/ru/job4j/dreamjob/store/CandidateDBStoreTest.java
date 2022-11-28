package ru.job4j.dreamjob.store;

import org.junit.Test;
import ru.job4j.dreamjob.Main;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.service.CityService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CandidateDBStoreTest {

    CandidateDBStore store = new CandidateDBStore(new Main().loadPool());
    CityService service = new CityService();
    LocalDate date = LocalDate.now();

    @Test
    public void whenCreateCandidate() throws SQLException {
        Candidate candidate = new Candidate(0, "Kirill Smirnov", "Java Developer",
                date, service.findById(1));
        store.add(candidate);
        Candidate expected = store.findById(candidate.getId()).get();
        assertEquals(candidate.getName(), expected.getName());
        try (Connection connection = store.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("delete from candidate")) {
            statement.execute();
        }
    }

    @Test
    public void whenFindAll() throws SQLException {
        Candidate candidateOne = new Candidate(0, "Kirill Smirnov", "Java Developer",
                date, service.findById(1));
        Candidate candidateTwo = new Candidate(0, "Petr Petrov", "Python Developer",
                date, service.findById(2));
        store.add(candidateOne);
        store.add(candidateTwo);
        List<Candidate> candidates = store.findAll();
        assertEquals(candidates.get(0).getName(), candidateOne.getName());
        assertEquals(candidates.get(0).getDescription(), candidateOne.getDescription());
        assertEquals(candidates.get(1).getCity().getId(), candidateTwo.getCity().getId());
        assertEquals(candidates.get(1).getName(), candidateTwo.getName());
        try (Connection connection = store.getPool().getConnection();
             PreparedStatement statement = connection.prepareStatement("delete from candidate")) {
            statement.execute();
        }
    }

    @Test
    public void whenReplace() throws SQLException {
        Candidate candidate = new Candidate(0, "Kirill Smirnov", "Java Developer",
                date, service.findById(1));
        store.add(candidate);
        candidate.setName("Petr Petrov");
        candidate.setDescription("Python developer");
        store.replace(candidate);
        Candidate fromDB = store.findById(candidate.getId()).get();
        assertEquals(fromDB.getName(), candidate.getName());
        try (Connection connection = store.getPool().getConnection();
            PreparedStatement statement = connection.prepareStatement("delete from candidate")) {
            statement.execute();
        }
    }
}