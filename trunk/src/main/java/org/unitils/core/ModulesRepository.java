package org.unitils.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * todo javadoc
 */
public class ModulesRepository {


    //todo javadoc
    private List<UnitilsModule> modules;

    private Map<UnitilsModule, TestListener> testListeners;


    public ModulesRepository(List<UnitilsModule> modules) {
        this.modules = modules;
        this.testListeners = createTestListeners(modules);
    }


    public List<UnitilsModule> getModules() {
        return modules;
    }


    @SuppressWarnings({"unchecked"})
    public <T extends UnitilsModule> T getModule(Class<T> type) {

        for (UnitilsModule module : modules) {

            if (type.isAssignableFrom(module.getClass())) {
                return (T) module;
            }
        }
        return null;
    }

    @SuppressWarnings({"unchecked"})
    public <T extends UnitilsModule> List<T> getModules(Class<T> type) {

        List<T> result = new ArrayList<T>();

        for (UnitilsModule module : modules) {

            if (type.isAssignableFrom(module.getClass())) {
                result.add((T) module);
            }
        }
        return result;
    }


    public Map<UnitilsModule, TestListener> getTestListeners() {
        return testListeners;
    }


    public TestListener getTestListener(UnitilsModule module) {

        return testListeners.get(module);
    }


    private Map<UnitilsModule, TestListener> createTestListeners(List<UnitilsModule> modules) {
        Map<UnitilsModule, TestListener> result = new HashMap<UnitilsModule, TestListener>(modules.size());

        for (UnitilsModule module : modules) {

            result.put(module, module.createTestListener());
        }
        return result;
    }

}
