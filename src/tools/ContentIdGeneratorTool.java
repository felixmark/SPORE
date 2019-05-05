package tools;

import java.util.UUID;

public class ContentIdGeneratorTool {
	
	/**
	 * @param prefix The prefix for the User ID (i.e. prefix-0293859).
	 * @return Unique ID in the format of: prefix-UUID
	 */
	public static String generateContentId(String prefix) {
	     return String.format("%s-%s", prefix, UUID.randomUUID());
	}
}
