package com.techfun.altrua.infra.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DevDataLoader implements ApplicationRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);

        if (count != null && count > 0)
            return;

        Resource resource = new ClassPathResource("db/seed/mock_data.sql");
        String sql = new String(resource.getInputStream().readAllBytes());
        jdbcTemplate.execute(sql);
    }
}
