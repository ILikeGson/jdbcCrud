package main.java.ZhenyaShvyrkov.javacore.jdbc.repository;

import java.util.List;

public interface GenericRepository<T, ID> {
    T save(T t);
    List<T> read();
    T readById(ID id);
    T update(T t, ID id);
    void delete(T t);
    void deleteByID(ID id);
}
