package fr.epita.assistants.frontend;

import javafx.embed.swing.SwingNode;
import javafx.scene.control.Control;
import javafx.scene.control.Tab;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.paint.Color;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;
import java.util.Random;

import static fr.epita.assistants.frontend.MyIdeApp.*;
import static fr.epita.assistants.frontend.Settings.saveSettings;

public class Themes {

    public static String actualFont = "OCR A Extended";

    // Random
    private static final Random random = new Random();

    // Border colors
    private static final Color SciFiBorder = Color.rgb(21, 244, 238);
    private static final Color SoftSciFiBorder = Color.rgb(213, 88, 255);
    private static final Color AggressiveSciFiBorder = Color.rgb(255, 75, 75);
    private static final Color PeacefulSciFiBorder = Color.rgb(255, 0, 222);
    private static final Color CyberpunkBorder = Color.rgb(255, 0, 213);
    private static final Color ClassicWhiteBorder = Color.rgb(50, 50, 50);
    private static final Color ClassicDarkBorder = Color.rgb(255, 255, 255);

    // Text colors
    private static final Color WhiteText = Color.rgb(229, 237, 239);
    private static final Color BlackText = Color.rgb(0, 0, 0);
    private static final Color YellowText = Color.rgb(255, 248, 0);

    // Theme colors

    private static final Color SciFiPrimary = Color.rgb(0, 32, 40);
    private static final Color SciFiSecond = Color.rgb(0, 48, 61);
    private static final Color SciFiThird = Color.rgb(172, 255, 250);

    private static final Color SoftSciFiPrimary = Color.rgb(35, 0, 61);
    private static final Color SoftSciFiSecond = Color.rgb(51, 0, 62);
    private static final Color SoftSciFiThird = Color.rgb(255, 211, 255);

    private static final Color PeacefulSciFiPrimary = Color.rgb(100, 0, 67);
    private static final Color PeacefulSciFiSecond = Color.rgb(117, 0, 104);
    private static final Color PeacefulSciFiThird = Color.rgb(255, 141, 250);

    private static final Color AggressiveSciFiPrimary = Color.rgb(66, 0, 7);
    private static final Color AggressiveSciFiSecond = Color.rgb(113, 0, 15);
    private static final Color AggressiveSciFiThird = Color.rgb(255, 176, 176);

    private static final Color CyberpunkPrimary = Color.rgb(0, 18, 56);
    private static final Color CyberpunkSecond = Color.rgb(95, 0, 80);
    private static final Color CyberpunkThird = Color.rgb(169, 78, 255);

    private static final Color ClassicWhitePrimary = Color.rgb(170, 170, 170);
    private static final Color ClassicWhiteSecond = Color.rgb(195, 195, 195);
    private static final Color ClassicWhiteThird = Color.rgb(220, 220, 220);

    private static final Color ClassicDarkPrimary = Color.rgb(10, 10, 10);
    private static final Color ClassicDarkSecond = Color.rgb(80, 80, 80);
    private static final Color ClassicDarkThird = Color.rgb(110, 110, 110);

    private static final Color ImposterColor = Color.rgb(255, 0, 15);

    // Fonts
    public static String OCR_FONT_FAMILY = "OCR A Extended";
    public static String RAVIE_FONT_FAMILY = "Ravie";
    public static String PROLIGHT_FONT_FAMILY = "Pro Light";


    protected static void applyTheme(String theme, int k) {

        if (k == 0) {
            saveSettings(Settings.getProjectPath(), theme);
        }

        savedTheme = theme;

        switch (theme) {
            case "Soft Sci/Fi Theme" -> applySoftSciFiTheme();
            case "Peaceful Sci/Fi Theme" -> applyPeacefulSciFiTheme();
            case "Aggressive Sci/Fi Theme" -> applyAggressiveSciFiTheme();
            case "Cyberpunk Theme" -> applyCyberpunkTheme();
            case "Classic White Theme" -> applyClassicWhiteTheme();
            case "Classic Dark Theme" -> applyClassicDarkTheme();
            case "Random Theme" -> applyRandomTheme();
            default -> applySciFiTheme();
        }
    }

    protected static void applySciFiTheme() {
        applyChosenTheme(SciFiPrimary, SciFiSecond, SciFiThird, SciFiBorder, WhiteText, BlackText, OCR_FONT_FAMILY, 0);
    }

    protected static void applySoftSciFiTheme() {
        applyChosenTheme(SoftSciFiPrimary, SoftSciFiSecond, SoftSciFiThird, SoftSciFiBorder, WhiteText, BlackText, OCR_FONT_FAMILY, 1);
    }

    protected static void applyAggressiveSciFiTheme() {
        applyChosenTheme(AggressiveSciFiPrimary, AggressiveSciFiSecond, AggressiveSciFiThird, AggressiveSciFiBorder, WhiteText, BlackText, OCR_FONT_FAMILY,2);
    }

    protected static void applyPeacefulSciFiTheme() {
        applyChosenTheme(PeacefulSciFiPrimary, PeacefulSciFiSecond, PeacefulSciFiThird, PeacefulSciFiBorder, WhiteText, BlackText, OCR_FONT_FAMILY, 3);
    }

    protected static void applyCyberpunkTheme() {
        applyChosenTheme(CyberpunkPrimary, CyberpunkSecond, CyberpunkThird, CyberpunkBorder, YellowText, BlackText, OCR_FONT_FAMILY, 4);
    }

    protected static void applyClassicWhiteTheme() {
        applyChosenTheme(ClassicWhitePrimary, ClassicWhiteSecond, ClassicWhiteThird, ClassicWhiteBorder, BlackText, WhiteText, PROLIGHT_FONT_FAMILY, 5);
    }

    protected static void applyClassicDarkTheme() {
        applyChosenTheme(ClassicDarkPrimary, ClassicDarkSecond, ClassicDarkThird, ClassicDarkBorder, WhiteText, BlackText, PROLIGHT_FONT_FAMILY, 6);
    }

    protected static void applyRandomTheme() {
        applyChosenTheme(generateRandomColor(), generateRandomColor(), generateRandomColor(), generateRandomColor(), generateRandomColor(), generateRandomColor(), OCR_FONT_FAMILY, 4);
    }

    protected static void applyImposter() {
        applyChosenTheme(ImposterColor, ImposterColor, ImposterColor, BlackText, WhiteText, BlackText, RAVIE_FONT_FAMILY, 100);
    }

    protected static void applyChosenTheme(Color primary, Color second, Color third, Color border, Color textColor, Color textColor2, String fontText, int t) {

        root.setStyle("-fx-background-color: " + toRGBCode(second));

        applyStyles(tabPane, primary, border, textColor, fontText);
        applyStyles(mainAndBuild, primary, border, textColor, fontText);
        applyStyles(buildArea, primary, border, textColor, fontText);
        applyStyles(saveButton, primary, border, textColor, fontText);

        applyStyles(fileTreeView, second, border, textColor, fontText);

        applyStyles(menuBar, third, border, textColor, fontText);
        applyStyles(themesBar, third, border, textColor, fontText);
        applyStyles(compileBar, textColor, border, textColor2, fontText);

        for (Tab tab : tabPane.getTabs()) {
            if (!Objects.equals(tab.getText(), "Welcome to ICBZ IDE !")) {
                applySingle((RSyntaxTextArea) ((RTextScrollPane) ((SwingNode) tab.getContent()).getContent()).getTextArea(), t);
                ((RTextScrollPane) (((SwingNode) tab.getContent()).getContent())).getGutter().setLineNumberFont(new Font(fontText, Font.PLAIN, 14));
            }
        }

        actualFont = fontText;

    }

    private static void applyStyles(Control control, Color innerColor, Color borderColor, Color textColor, String fontText) {
        String inner = toRGBCode(innerColor);
        String text = toRGBCode(textColor);

        control.setStyle(
                String.format("-fx-background-color: %s; -fx-control-inner-background: %s; -fx-text-fill: %s; -fx-font-family: '%s';", inner, inner, text, fontText)
        );

        control.setBorder(
                new Border(new BorderStroke(borderColor, BorderStrokeStyle.SOLID, null, new BorderWidths(1.5)))
        );
    }

    private static void applySingle(RSyntaxTextArea rSyntaxTextArea, int t) {

        switch (t) {
            case 1 -> {
                rSyntaxTextArea.setBackground(javafxToAWT(WhiteText));
                rSyntaxTextArea.setCurrentLineHighlightColor(javafxToAWT(SoftSciFiThird));
                rSyntaxTextArea.setSelectionColor(javafxToAWT(SoftSciFiThird));
                SwingUtilities.invokeLater(() -> {
                    rSyntaxTextArea.setFont(new Font(OCR_FONT_FAMILY, Font.PLAIN, 14));
                });
            }
            case 2 -> {
                rSyntaxTextArea.setBackground(javafxToAWT(WhiteText));
                rSyntaxTextArea.setCurrentLineHighlightColor(javafxToAWT(AggressiveSciFiThird));
                rSyntaxTextArea.setSelectionColor(javafxToAWT(AggressiveSciFiThird));
                SwingUtilities.invokeLater(() -> {
                    rSyntaxTextArea.setFont(new Font(OCR_FONT_FAMILY, Font.PLAIN, 14));
                });
            }
            case 3 -> {
                rSyntaxTextArea.setBackground(javafxToAWT(WhiteText));
                rSyntaxTextArea.setCurrentLineHighlightColor(javafxToAWT(PeacefulSciFiThird));
                rSyntaxTextArea.setSelectionColor(javafxToAWT(PeacefulSciFiThird));
                SwingUtilities.invokeLater(() -> {
                    rSyntaxTextArea.setFont(new Font(OCR_FONT_FAMILY, Font.PLAIN, 14));
                });
            }
            case 4 -> {
                rSyntaxTextArea.setBackground(javafxToAWT(WhiteText));
                rSyntaxTextArea.setCurrentLineHighlightColor(javafxToAWT(CyberpunkThird));
                rSyntaxTextArea.setSelectionColor(javafxToAWT(CyberpunkThird));
                SwingUtilities.invokeLater(() -> {
                    rSyntaxTextArea.setFont(new Font(OCR_FONT_FAMILY, Font.PLAIN, 14));
                });
            }
            case 5 -> {
                rSyntaxTextArea.setBackground(javafxToAWT(WhiteText));
                rSyntaxTextArea.setCurrentLineHighlightColor(javafxToAWT(ClassicWhiteThird));
                rSyntaxTextArea.setSelectionColor(javafxToAWT(ClassicWhiteThird));
                SwingUtilities.invokeLater(() -> {
                    rSyntaxTextArea.setFont(new Font(PROLIGHT_FONT_FAMILY, Font.PLAIN, 14));
                });
            }
            case 6 -> {
                rSyntaxTextArea.setBackground(javafxToAWT(WhiteText));
                rSyntaxTextArea.setCurrentLineHighlightColor(javafxToAWT(ClassicDarkThird));
                rSyntaxTextArea.setSelectionColor(javafxToAWT(ClassicDarkThird));
                SwingUtilities.invokeLater(() -> {
                    rSyntaxTextArea.setFont(new Font(PROLIGHT_FONT_FAMILY, Font.PLAIN, 14));
                });
            }
            case 100 -> {
                rSyntaxTextArea.setBackground(javafxToAWT(WhiteText));
                rSyntaxTextArea.setCurrentLineHighlightColor(javafxToAWT(ImposterColor));
                rSyntaxTextArea.setSelectionColor(javafxToAWT(ImposterColor));
                SwingUtilities.invokeLater(() -> {
                    rSyntaxTextArea.setFont(new Font(OCR_FONT_FAMILY, Font.PLAIN, 14));
                });
            }
            default -> {
                rSyntaxTextArea.setBackground(javafxToAWT(WhiteText));
                rSyntaxTextArea.setCurrentLineHighlightColor(javafxToAWT(SciFiThird));
                rSyntaxTextArea.setSelectionColor(javafxToAWT(SciFiThird));
                SwingUtilities.invokeLater(() -> {
                    rSyntaxTextArea.setFont(new Font(OCR_FONT_FAMILY, Font.PLAIN, 14));
                });
            }
        }

        rSyntaxTextArea.setCodeFoldingEnabled(true);

    }

    private static Color generateRandomColor()
    {
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);
        return Color.rgb(red, green, blue);
    }

    static String toRGBCode(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    protected static java.awt.Color javafxToAWT(Color javafxColor) {
        int red = (int) (javafxColor.getRed() * 255);
        int green = (int) (javafxColor.getGreen() * 255);
        int blue = (int) (javafxColor.getBlue() * 255);
        int alpha = (int) (javafxColor.getOpacity() * 255);

        return new java.awt.Color(red, green, blue, alpha);
    }

}