package net.rakugakibox.spring.boot.logback.access.test;

import lombok.RequiredArgsConstructor;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Parameterized;
import org.junit.runners.model.FrameworkField;
import org.junit.runners.model.InitializationError;
import org.junit.runners.parameterized.BlockJUnit4ClassRunnerWithParameters;
import org.junit.runners.parameterized.ParametersRunnerFactory;
import org.junit.runners.parameterized.TestWithParameters;
import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.cache.DefaultCacheAwareContextLoaderDelegate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DefaultBootstrapContext;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpringParametersRunnerFactory implements ParametersRunnerFactory {

    @RequiredArgsConstructor
    private static class InnerTestContextBootstrapper extends SpringBootTestContextBootstrapper {

        private final ContainerType currentContainer;

        /**
         * Specify additional properties.
         */
        @Override
        protected String[] getProperties(Class<?> testClass) {
            String[] properties = super.getProperties(testClass);
            if (currentContainer.isReactive()) {
                Set<String> set = new HashSet<>(Arrays.asList(properties));
                set.add("spring.main.web-application-type=REACTIVE");
                return set.toArray(new String[0]);
            }

            return properties;
        }

        /**
         * Specify additional sources for application: use container configuration
         */
        @Override
        protected Class<?>[] getClasses(Class<?> testClass) {
            Set<Class<?>> classes = new HashSet<>(Arrays.asList(super.getClasses(testClass)));
            classes.add(currentContainer.getConfigurationClass());

            return classes.toArray(new Class[0]);
        }
    }

    /**
     * Inheritance required for public method modifier.
     */
    private static class InnerSpringJUnit4ClassRunner extends SpringJUnit4ClassRunner {
        InnerSpringJUnit4ClassRunner(Class<?> clazz) throws InitializationError {
            super(clazz);
        }

        @Override
        public Object createTest() throws Exception {
            return super.createTest();
        }

        @Override
        protected TestContextManager createTestContextManager(Class<?> clazz) {
            return super.createTestContextManager(clazz);
        }
    }

    @Override
    public Runner createRunnerForTestWithParameters(TestWithParameters test) throws InitializationError {
        ContainerType currentContainer = (ContainerType) test.getParameters().iterator().next();
        Class<?> targetClass = test.getTestClass().getJavaClass();

        InnerSpringJUnit4ClassRunner springRunner = new InnerSpringJUnit4ClassRunner(targetClass) {
            @Override
            public TestContextManager createTestContextManager(Class<?> clazz) {
                DefaultBootstrapContext context = new DefaultBootstrapContext(clazz, new DefaultCacheAwareContextLoaderDelegate());
                InnerTestContextBootstrapper bootstrapper = new InnerTestContextBootstrapper(currentContainer);
                bootstrapper.setBootstrapContext(context);

                return new TestContextManager(bootstrapper);
            }
        };

        return new BlockJUnit4ClassRunnerWithParameters(test) {

            @Override
            public void run(RunNotifier notifier) {
                super.run(notifier);
                // clear current context
                TestContext testContext = springRunner.createTestContextManager(targetClass).getTestContext();
                testContext.markApplicationContextDirty(DirtiesContext.HierarchyMode.CURRENT_LEVEL);
            }

            @Override
            public Object createTest() throws Exception {
                Object instance = springRunner.createTest();
                return createTestUsingFieldInjection(instance);
            }

            /**
             * Copy/paste from parent class
             */
            private Object createTestUsingFieldInjection(Object testClassInstance) throws Exception {
                List<FrameworkField> annotatedFieldsByParameter = getTestClass().getAnnotatedFields(Parameterized.Parameter.class);
                Object[] parameters = test.getParameters().toArray(new Object[0]);

                if (annotatedFieldsByParameter.size() != parameters.length) {
                    throw new Exception(
                            "Wrong number of parameters and @Parameter fields."
                                    + " @Parameter fields counted: "
                                    + annotatedFieldsByParameter.size()
                                    + ", available parameters: " + parameters.length
                                    + ".");
                }
                
                for (FrameworkField each : annotatedFieldsByParameter) {
                    Field field = each.getField();
                    Parameterized.Parameter annotation = field.getAnnotation(Parameterized.Parameter.class);
                    int index = annotation.value();
                    try {
                        field.set(testClassInstance, parameters[index]);
                    } catch (IllegalArgumentException iare) {
                        throw new Exception(getTestClass().getName()
                                + ": Trying to set " + field.getName()
                                + " with the value " + parameters[index]
                                + " that is not the right type ("
                                + parameters[index].getClass().getSimpleName()
                                + " instead of " + field.getType().getSimpleName()
                                + ").", iare);
                    }
                }

                return testClassInstance;
            }
        };
    }

}
