package com.github.funnygopher.crowddj.service;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class CrowdDJApplication extends Application<CrowdDJConfiguration> {

    public static final String NAME = "crowddj-service";

    public static void main(String[] args) throws Exception {
        Application app = new CrowdDJApplication();
        app.run(args);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void initialize(Bootstrap<CrowdDJConfiguration> bootstrap) {
        super.initialize(bootstrap);
    }

    @Override
    public void run(CrowdDJConfiguration config, Environment environment) throws Exception {
        // Do run stuff
    }
}
