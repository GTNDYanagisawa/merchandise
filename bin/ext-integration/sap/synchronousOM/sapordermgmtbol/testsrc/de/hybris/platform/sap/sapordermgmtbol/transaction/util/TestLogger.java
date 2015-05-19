/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2014 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package de.hybris.platform.sap.sapordermgmtbol.transaction.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Improve Readability of console output by indenting <br>
 */
public class TestLogger {

    private List<String> log = new ArrayList<String>();

    private static final String INDENT_CHAR = "  ";
    /** No Indentation */
    public static final int ROOT_LEVEL = 0;
    /** 1 Level Indentation for test methods */
    public static final int METHOD_LEVEL = 1;
    /** 2 Level Indentation for test steps in methods */
    public static final int STEP_LEVEL = 2;

    private int level = 0;

    /**
     * adds the String to the list considering the indentation<br>
     * 
     * @param entry element to be appended to this list
     * @return true (as specified by Collection.add)
     */
    public boolean add(String entry) {

        StringBuilder indented = new StringBuilder(entry);
        for (int i = 0; i < level; i++) {
            indented.insert(0, INDENT_CHAR);
        }
        return log.add(indented.toString());
    }

    /**
     * Adds a describing line to the output and increases indentation afterwards<br>
     * 
     * @param describe describing line
     */
    public void addChildLevel(String describe) {
        add(describe);
        indent();
    }

    /**
     * Unindents, adds a describing line to the output and increases indentation
     * afterwards<br>
     * 
     * @param describe describing line
     */
    public void addSiblingLevel(String describe) {
        unIndent();
        add(describe);
        indent();
    }

    /**
     * increase indentation
     */
    public void indent() {
        level++;
    }

    /**
     * decrease indentation
     */
    public void unIndent() {
        if (level > 0)
            level--;
    }

    /**
     * set specific indentation level.<br>
     * 
     * @param lvl Level of indentation
     */
    public void setIndentLevel(int lvl) {
        level = lvl;
    }

    /**
     * Prints all log entries to .<br>
     */
    public void sysout() {

        for (String entry : log) {
            System.out.println(entry);
        }

    }

    /**
     * append text to the last entry.<br>
     * 
     * @param string text to append
     */
    public void appendToLastEntry(String string) {
        String string2 = log.get(log.size() - 1);
        String string3 = string2 + string;
        log.set(log.size() - 1, string3);
    }

    /**
     * Add an empty line to the log.<br>
     */
    public void addEmptyLine() {
        log.add("");
    }

    /**
     * Delete all entries.<br>
     */
    public void reset() {
       log.clear();
       setIndentLevel(ROOT_LEVEL);
    }

}
