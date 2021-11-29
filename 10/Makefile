all:
	mvn -f ../Fish/Common/pom.xml compile assembly:single \
		-Djar.name=xclients -Djar.mainClass=com.cs4500.fish.runnables.ClientsProgram

	mvn -f ../Fish/Common/pom.xml compile assembly:single \
		-Djar.name=xserver -Djar.mainClass=com.cs4500.fish.runnables.ServerProgram