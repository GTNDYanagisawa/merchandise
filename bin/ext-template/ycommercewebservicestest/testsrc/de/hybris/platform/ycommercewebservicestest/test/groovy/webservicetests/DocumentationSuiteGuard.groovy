package de.hybris.platform.ycommercewebservicestest.test.groovy.webservicetests

class DocumentationSuiteGuard {
    private static boolean isSuiteRunning = false;
    public static boolean isSuiteRunning() {
        return isSuiteRunning;
    }

    public static void setSuiteRunning(boolean isSuiteRunning) {
        this.isSuiteRunning = isSuiteRunning;
    }
}
