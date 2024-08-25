package dev.jskw.tomorrowkey.persistance;

import org.springframework.data.jpa.repository.JpaRepository;

public interface KeyRepository extends JpaRepository<KeyEntity, String> {
}