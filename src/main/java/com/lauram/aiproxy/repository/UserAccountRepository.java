package com.lauram.aiproxy.repository;

import com.lauram.aiproxy.domain.PlanType;
import com.lauram.aiproxy.domain.UserAccount;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory storage of user accounts. New users start on the FREE plan.
 */
@Repository
public class UserAccountRepository {

    private final Map<String, UserAccount> accounts = new ConcurrentHashMap<>();

    public UserAccount findOrCreate(String userId) {
        return accounts.computeIfAbsent(userId, id -> new UserAccount(id, PlanType.FREE));
    }

    public Collection<UserAccount> findAll() {
        return accounts.values();
    }
}
