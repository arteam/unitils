This source folder contains a number of integration tests for unitils.
All tests are executed from org.unitils.integrationtest.UnitilsIntegrationTest, the other test classes in this source folder are used by UnitilsIntegrationTest and are not meant to be run separately.

For the toplink tests to pass, following jvm parameter must be added:
-javaagent:/path-to-unitils/lib/spring-agent.jar
