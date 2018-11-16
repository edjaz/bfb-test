package com.bforbank.testing.cucumber.stepdefs;

import com.bforbank.testing.TfTestApp;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;

import org.springframework.boot.test.context.SpringBootTest;

@WebAppConfiguration
@SpringBootTest
@ContextConfiguration(classes = TfTestApp.class)
public abstract class StepDefs {

    protected ResultActions actions;

}
