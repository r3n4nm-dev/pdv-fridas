package com.renanmateus.vendafridas.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SplitItemIdFromString {

	public static List<Long> splitString(String stringItensId) {

		List<Long> itensId = new ArrayList<Long>();
		String[] result = stringItensId.split(",");
		// convert array to List
		List<String> targetList = Arrays.asList(result);
		targetList.forEach(t -> {
			itensId.add(Long.parseLong(t));
		});
		return itensId;
	}
}
