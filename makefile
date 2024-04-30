f:
	bunx prettier --write "frontend/src/**"
b:
	cd backend && mvn spring-javaformat:validate && mvn spring-javaformat:apply