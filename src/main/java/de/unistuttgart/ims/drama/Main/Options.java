package de.unistuttgart.ims.drama.Main;

import java.io.File;

import com.lexicalscope.jewel.cli.Option;

public interface Options {
	@Option
	File getInput();

	@Option
	File getOutput();
}