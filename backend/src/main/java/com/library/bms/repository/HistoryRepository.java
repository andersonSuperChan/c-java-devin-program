package com.library.bms.repository;

import com.library.bms.entity.History;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class HistoryRepository {
    private final Map<Long, History> histories = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    
    public List<History> findAll() {
        return new ArrayList<>(histories.values());
    }
    
    public Optional<History> findById(Long id) {
        return Optional.ofNullable(histories.get(id));
    }
    
    public History save(History history) {
        if (history.getId() == null) {
            history.setId(idGenerator.getAndIncrement());
        }
        histories.put(history.getId(), history);
        return history;
    }
    
    public List<History> findByStudentNo(String studentNo) {
        return histories.values().stream()
                .filter(h -> h.getStudentNo().equals(studentNo))
                .collect(Collectors.toList());
    }
    
    public List<History> findByIssn(String issn) {
        return histories.values().stream()
                .filter(h -> h.getIssn().equals(issn))
                .collect(Collectors.toList());
    }
    
    public Optional<History> findOpenLoan(String studentNo, String issn) {
        return histories.values().stream()
                .filter(h -> h.getStudentNo().equals(studentNo) 
                        && h.getIssn().equals(issn) 
                        && h.getReturnDate() == null)
                .findFirst();
    }
    
    public List<History> findOpenLoans() {
        return histories.values().stream()
                .filter(h -> h.getReturnDate() == null)
                .collect(Collectors.toList());
    }
    
    public void deleteAll() {
        histories.clear();
        idGenerator.set(1);
    }
}
