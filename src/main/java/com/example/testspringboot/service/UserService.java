package com.example.testspringboot.service;

import com.example.testspringboot.model.User;
import com.example.testspringboot.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> search(String keyword) {
        List<User> users = userRepository.findAllByOrderByIdAsc();
        if (keyword == null || keyword.isBlank()) {
            return users;
        }
        String kw = keyword.trim().toLowerCase();
        return users.stream()
                .filter(user -> matches(user, kw))
                .toList();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public boolean isNameTaken(String name, Long currentId) {
        if (name == null || name.isBlank()) {
            return false;
        }
        if (currentId == null) {
            return userRepository.existsByNameIgnoreCase(name.trim());
        }
        return userRepository.existsByNameIgnoreCaseAndIdNot(name.trim(), currentId);
    }

    @Transactional
    public User save(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found");
        }
        userRepository.deleteById(id);
    }

    private boolean matches(User user, String keyword) {
        if (user.getName() != null && user.getName().toLowerCase().contains(keyword)) {
            return true;
        }
        if (user.getId() != null && String.valueOf(user.getId()).equals(keyword)) {
            return true;
        }
        if (user.getAge() != null && String.valueOf(user.getAge()).contains(keyword)) {
            return true;
        }
        return user.getSalary() != null && String.valueOf(user.getSalary()).contains(keyword);
    }
}
