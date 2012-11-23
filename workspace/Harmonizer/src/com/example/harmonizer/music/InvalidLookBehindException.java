package com.example.harmonizer.music;

public class InvalidLookBehindException extends Exception {

	public InvalidLookBehindException(int lookBehind) {
		super("Cannot look " + lookBehind + 
				" back. See History.MAX_LOOKBEHIND.");
	}

}
