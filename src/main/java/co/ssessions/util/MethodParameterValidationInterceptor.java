package co.ssessions.util;

import java.lang.reflect.Method;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class MethodParameterValidationInterceptor implements MethodInterceptor {

	public MethodParameterValidationInterceptor() {
		int a = 1;
	}
	
	public Object invoke(MethodInvocation invocation) throws Throwable {

		System.out.println("Hello!");
		
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		ExecutableValidator executableValidator = factory.getValidator().forExecutables();
		
		
		Method method = invocation.getMethod();
		Object[] arguments = invocation.getArguments();
		Object theThis = invocation.getThis();
		
		
		Set<ConstraintViolation<Object>> violations = executableValidator.validateParameters(theThis, method, arguments);

		if ((violations != null) && (violations.size() > 0)) {
			throw new IllegalArgumentException(violations.toString());
		}
		
		
		
		
		return invocation.proceed();
	}

}
