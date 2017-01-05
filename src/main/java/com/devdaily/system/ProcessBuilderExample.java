package com.devdaily.system;

import java.io.IOException;
import java.util.*;

public class ProcessBuilderExample {

	public static void main(String[] args) throws Exception {
		new ProcessBuilderExample();
	}

	// can run basic ls or ps commands
	// can run command pipelines
	// can run sudo command if you know the password is correct
	public ProcessBuilderExample() throws IOException, InterruptedException {
		List<String> commands = getCommands2();
		// execute the command
		SystemCommandExecutor commandExecutor = new SystemCommandExecutor(commands);
		int result = commandExecutor.executeCommand();

		// get the stdout and stderr from the command that was run
		StringBuilder stdout = commandExecutor.getStandardOutputFromCommand();
		StringBuilder stderr = commandExecutor.getStandardErrorFromCommand();

		// print the stdout and stderr
		System.out.println("The numeric result of the command was: " + result);
		System.out.println("STDOUT:");
		System.out.println(stdout);
		System.out.println("STDERR:");
		System.out.println(stderr);
	}

	private List<String> getCommands() {
		// build the system command we want to run
		List<String> commands = new ArrayList<String>();
		commands.add("/bin/sh");
		commands.add("-c");
		commands.add(
				"netstat -ano | grep \"^tcp\" | grep ESTABLISHED | cut -d\":\" -f3 | cut -d\" \" -f1 | grep \"^443\" | wc -l");
		return commands;
	}

	private List<String> getCommands2() {
		// build the system command we want to run
		List<String> commands = new ArrayList<String>();
		commands.add("/bin/bash");
		commands.add("-c");
		commands.add("if ! which mqsilist; then source /opt/ibm/iib-10.0.0.6/server/bin/mqsiprofile; fi && mqsilist");
		return commands;
	}
}
