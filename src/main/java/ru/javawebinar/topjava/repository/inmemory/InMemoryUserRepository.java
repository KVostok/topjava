package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.AbstractNamedEntity;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserRepository.class);
    private final Map<Integer, User> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        this.save(new User(null, "userName", "email@mail.ru", "password", Role.ADMIN));
        this.save(new User(null, "userName", "email1@mail.ru", "password", Role.USER));
        this.save(new User(null, "userName1", "email@mail.ru", "password", Role.USER));
    }

    @Override
    public boolean delete(int id) {
        log.info("delete {}", id);
        return repository.remove(id) != null;
    }

    @Override
    public User save(User user) {
        log.info("save {}", user);

        //todo Поле email должно быть уникально. Сделать проверку при создании записи
        if (user.isNew()) {
            user.setId(counter.incrementAndGet());
            repository.put(user.getId(), user);
            return user;
        }
        // handle case: update, but not present in storage
        return repository.computeIfPresent(user.getId(), (id, oldUser) -> user);
    }

    @Override
    public User get(int id) {
        log.info("get {}", id);
        return repository.get(id);
    }

    @Override
    public List<User> getAll() {
        log.info("getAll");
        //.sorted(comparing(integerUserEntry -> integerUserEntry.getValue().getName()))
        return repository.values().stream()
                .sorted(Comparator.comparing(AbstractNamedEntity::getName))
                .collect(toList());
    }

    @Override
    public User getByEmail(String email) {
        log.info("getByEmail {}", email);
        return (User) repository.entrySet().stream()
                .filter(userEntry -> userEntry.getValue().getEmail().equalsIgnoreCase(email)).findAny().orElse(null);
    }
}
