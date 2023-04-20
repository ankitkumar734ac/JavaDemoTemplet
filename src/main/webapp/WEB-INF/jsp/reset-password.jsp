<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html>
<head>
	<title>Password Reset</title>
</head>
<body>
	<h2>Password Reset Form</h2>
	<form:form method="POST" modelAttribute="passwordResetForm" action="/reset-password">
		<div>
			<label for="password">New Password:</label>
			<form:password path="password" id="password" />
			<form:errors path="password" cssClass="error" />
		</div>
		<div>
			<label for="confirmPassword">Confirm Password:</label>
			<form:password path="conformPassword" id="conformPassword" />
			<form:errors path="conformPassword" cssClass="error" />
		</div>
		<input type="hidden" name="token" value="${token}" />
		<input type="submit" value="Reset Password" />
	</form:form>
</body>
</html>
