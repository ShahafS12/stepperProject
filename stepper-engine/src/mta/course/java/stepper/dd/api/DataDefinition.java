package mta.course.java.stepper.dd.api;

public interface DataDefinition {
    String getName();
    boolean isUserFriendly();
    Class<?> getType();
    abstract <T> T getValue(String name);
}
