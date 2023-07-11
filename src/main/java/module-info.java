module fr.epita.assistants.icbz_ide {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires lombok;
    requires java.validation;
    requires org.eclipse.jgit;
    requires org.fxmisc.richtext;
    requires org.fife.RSyntaxTextArea;
    requires autocomplete;
    requires javafx.swing;
    requires com.google.googlejavaformat;
    requires org.eclipse.jdt.core;
    requires org.eclipse.text;
    requires java.compiler;

    opens fr.epita.assistants.frontend to javafx.fxml;
    exports fr.epita.assistants.frontend;
}