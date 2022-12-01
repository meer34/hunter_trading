package com.hunter.data.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Component;

@Component
public class RepositoryConfigurer {
    private Repositories repositories;

    @Autowired
    public RepositoryConfigurer(ApplicationContext applicationContext) {
        this.repositories = new Repositories(applicationContext);
    }

    public Repositories getRepositories() {
        return this.repositories;
    }
}