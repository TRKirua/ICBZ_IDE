package fr.epita.assistants.backend.utils;

/**
 * All classes with this annotation are provided by the assistants.
 */
@Given()
public @interface Given {

    /**
     * Will this file be overwritten at compilation time by the moulinette?
     * Note: changing this field locally will not change the moulinette's
     * behavior, this is an indication of intent, not something you can
     * unilaterally change.
     *
     * @return `true` if the file will be overwritten, false otherwise.
     */
    boolean overwritten() default true;
}
