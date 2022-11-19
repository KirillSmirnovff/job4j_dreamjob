package ru.job4j.dreamjob.service;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Service;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.store.CandidateDBStore;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;

@ThreadSafe
@Service
public class CandidateService {

    private final CandidateDBStore store;

    private final CityService service;

    public CandidateService(CandidateDBStore store, CityService service) {
        this.store = store;
        this.service = service;
    }

    public Collection<Candidate> findAll() {
        List<Candidate> candidates = store.findAll();
        candidates.forEach(
                candidate -> candidate.setCity(service.findById(candidate.getCity().getId()))
        );
        return candidates;
    }

    public void add(Candidate candidate) {
        store.add(candidate);
    }

    public Candidate findById(int id) {
        return store.findById(id).orElseThrow(NoSuchElementException::new);
    }

    public void replace(Candidate candidate) {
        store.replace(candidate);
    }
}
