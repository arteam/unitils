package org.unitils.database.util.spring;

import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.unitils.core.Unitils;
import org.unitils.core.UnitilsException;
import org.unitils.spring.SpringModule;

public class DatabaseSpringSupportImpl implements DatabaseSpringSupport {


	@Override
	public boolean isTransactionManagerConfiguredInSpring(Object testObject) {
		if (!getSpringModule().isApplicationContextConfiguredFor(testObject)) {
            return false;
        }
		ApplicationContext context = getSpringModule().getApplicationContext(testObject);
		return context.getBeansOfType(PlatformTransactionManager.class).size() != 0;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public PlatformTransactionManager getPlatformTransactionManager(Object testObject) {
		ApplicationContext context = getSpringModule().getApplicationContext(testObject);
		Map<String, PlatformTransactionManager> platformTransactionManagers = context.getBeansOfType(PlatformTransactionManager.class);
		if (platformTransactionManagers.size() == 0) {
			throw new UnitilsException("Could not find a bean of type " + PlatformTransactionManager.class.getSimpleName() 
					+ " in the spring ApplicationContext for this class");
		}
		if (platformTransactionManagers.size() > 1) {
			throw new UnitilsException("Found more than one bean of type " + PlatformTransactionManager.class.getSimpleName()
					+ " in the spring ApplicationContext for this class");
		}
		return platformTransactionManagers.values().iterator().next();
	}


    /**
     * @return The Spring module, not null
     */
    protected SpringModule getSpringModule() {
        return Unitils.getInstance().getModulesRepository().getModuleOfType(SpringModule.class);
    }
}
