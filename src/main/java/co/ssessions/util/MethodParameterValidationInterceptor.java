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

	private ExecutableValidator executableValidator;
	
	public MethodParameterValidationInterceptor() {
		ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
		this.executableValidator = validatorFactory.getValidator().forExecutables();
	}
	
	public Object invoke(MethodInvocation invocation) throws Throwable {

		// TODO: Replace this with proper logging
		System.out.println("Hello!");
		
		Method method = invocation.getMethod();
		Object[] arguments = invocation.getArguments();
		Object theThis = invocation.getThis();
		
		Set<ConstraintViolation<Object>> violations = executableValidator.validateParameters(theThis, method, arguments);

		if ((violations != null) && (violations.size() > 0)) {
			throw new IllegalArgumentException("ERROR: In " + method.getName() + " some parameters failed validation. " + violations.toString());
		}
		
		return invocation.proceed();
	}

}
