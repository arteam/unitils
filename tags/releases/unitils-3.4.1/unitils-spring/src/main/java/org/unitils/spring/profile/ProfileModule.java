package org.unitils.spring.profile;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.unitils.core.Module;
import org.unitils.core.TestListener;
import org.unitils.core.UnitilsException;
import org.unitils.spring.annotation.ConfigureProfile;
import org.unitils.spring.annotation.SpringApplicationContext;



/**
 * ProfileModule - Since Spring 3 their is the ability to use a profile.
 * But without Unitils you still need to do some configuration.
 * 
 * This module configures the Spring profile and reload the {@link SpringApplicationContext}.
 * 
 * @author Jeroen Horemans
 * @author Thomas De Rycke
 * @author Willemijn Wouters
 * 
 * @since 3.4
 * 
 */
public class ProfileModule implements Module {

    private static final Log LOGGER = LogFactory.getLog(ProfileModule.class);

    private GenericApplicationContext ctx; 

    /**
     * @see org.unitils.core.Module#init(java.util.Properties)
     */
    public void init(Properties configuration) {
        // do nothing
    }

    /**
     * @see org.unitils.core.Module#afterInit()
     */

    public void afterInit() {
        //do nothing
    }

    /**
     * First it will look if the {@link ConfigureProfile} is present. This 
     * annotation contains the profilename. 
     * 
     * The {@link Profile} is set active and all the beans will be loaded.
     * 
     * @param testClass
     */
    protected void getProfile(Class<?> testClass) {
        if (checkIfEverythingIsInPlace(testClass)) {
            ConfigureProfile profile = testClass.getAnnotation(ConfigureProfile.class);
            if (profile.configuration().equals(TypeConfiguration.CONFIGURATION)) {
                AnnotationConfigApplicationContext temp = new AnnotationConfigApplicationContext();
                ctx = temp;
                setConfigurationAsTypeConfiguration(temp, profile);
            } else {
                GenericXmlApplicationContext temp = new GenericXmlApplicationContext();
                ctx = temp;
                setConfigurationAsTypeSpringApplicationContext(temp, profile, testClass.getAnnotation(SpringApplicationContext.class).value());
            }
        }
    }

    /**
     * How to do the configuration when you use {@link Configuration}
     * @param ctx
     * @param profile
     */
    protected void setConfigurationAsTypeConfiguration(AnnotationConfigApplicationContext ctx, ConfigureProfile profile) {
        ctx.getEnvironment().setActiveProfiles(profile.value());
        ctx.scan(profile.packageProfile()); // register all @Configuration classes
        ctx.refresh();
    }

    protected void setConfigurationAsTypeSpringApplicationContext(GenericXmlApplicationContext ctx, ConfigureProfile profile, String[] placeContext) {
        ConfigurableEnvironment env = ctx.getEnvironment(); 
        env.setActiveProfiles(profile.value()); 
        ctx.load(placeContext); 
        ctx.refresh(); 
    }

    /**
     * The injection of beans doesn't happen automatically anymore when you use {@link SpringBeanByName} or {@link SpringBeanByType}
     * @param testObject
     */
    public boolean injectBeans(Object testObject) {
        //inject beans
        boolean everythingOk = true;
        for (Field field : testObject.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                if (!field.isAccessible()) {
                    //set accessible
                    field.setAccessible(true);
                }
                try {
                    field.set(testObject, ctx.getBean(field.getName()));
                } catch (BeansException e) {
                    LOGGER.error(e.getMessage(), e);
                    everythingOk = false;
                } catch (IllegalArgumentException e) {
                    LOGGER.error(e.getMessage(), e);
                    everythingOk = false;
                } catch (IllegalAccessException e) {
                    LOGGER.error(e.getMessage(), e);
                    everythingOk = false;
                }
            }
        }
        if (everythingOk) {
            return true;
        }
        return false;
    }

    /**
     * This method checks if the testclass contains a {@link SpringApplicationContext} and a {@link ConfigureProfile}.
     * 
     * @param testClass
     * @return boolean
     */
    protected boolean checkIfEverythingIsInPlace(Class<?> testClass) {
        if (!testClass.isAnnotationPresent(ConfigureProfile.class)) {
            LOGGER.warn("The annotation 'ConfigureProfile' is not present.");
            return false;
        }
        ConfigureProfile configProfile = testClass.getAnnotation(ConfigureProfile.class);
        if (StringUtils.isEmpty(configProfile.value())) {
            throw new UnitilsException("The name of the profile should be filled in.");
        }

        //type of configuration is ApplicationContext
        if (configProfile.configuration().equals(TypeConfiguration.APPLICATIONCONTEXT)) {
            if (!testClass.isAnnotationPresent(SpringApplicationContext.class)) {
                throw new UnitilsException("The annotation 'SpringApplicationContext' is not present.");
            }
        } else {
            //type of the configuration is the annotation Configuration.
            if (StringUtils.isEmpty(configProfile.packageProfile())) {
                throw new UnitilsException("You should fill in the name of the package of the profile.");
            }
        }

        return true;
    }



    /**
     * @param ctx the ctx to set
     */
    protected void setCtx(GenericApplicationContext ctx) {
        this.ctx = ctx;
    }

    /**
     * @return the ctx
     */
    protected GenericApplicationContext getCtx() {
        return ctx;
    }
    /**
     * The context will be closed.
     */
    protected void closeContext() {
        if (ctx != null) {
            ctx.close();
        }
    }



    /**
     * @see org.unitils.core.Module#getTestListener()
     */
    public TestListener getTestListener() {
        return new TestListener() {

            /**
             * @see org.unitils.core.TestListener#beforeTestClass(java.lang.Class)
             */
            @Override
            public void beforeTestClass(Class<?> testClass) {
                getProfile(testClass);
            }

            /**
             * @see org.unitils.core.TestListener#beforeTestMethod(java.lang.Object, java.lang.reflect.Method)
             */
            @Override
            public void beforeTestMethod(Object testObject, Method testMethod) {
                injectBeans(testObject);
            }

            /**
             * @see org.unitils.core.TestListener#afterTestTearDown(java.lang.Object, java.lang.reflect.Method)
             */
            @Override
            public void afterTestTearDown(Object testObject, Method testMethod) {
                closeContext();
            }

        };
    }



}
