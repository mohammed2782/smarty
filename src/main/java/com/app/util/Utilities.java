package com.app.util;

import java.util.ArrayList;

public class Utilities {
    public StringBuilder getSingleQuoteCommaSeperated(ArrayList<String> list) {
        StringBuilder sb = new StringBuilder();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) sb.append(",");
                sb.append("'").append(list.get(i)).append("'");
            }
        }
        return sb;
    }

    public ArrayList<String> SplitStringToArrayList(String str, String separator) {
        ArrayList<String> list = new ArrayList<>();
        if (str != null && !str.isEmpty()) {
            String[] parts = str.split(separator);
            for (String part : parts) {
                if (!part.trim().isEmpty()) {
                    list.add(part.trim());
                }
            }
        }
        return list;
    }

    public StringBuilder getCommaSeperated(ArrayList<?> list) {
        StringBuilder sb = new StringBuilder();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) sb.append(",");
                sb.append(list.get(i));
            }
        }
        return sb;
    }
}
